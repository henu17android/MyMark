package com.example.mymark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mymark.watermark.WatermarkAdapter;
import com.example.mymark.watermark.WatermarkUtil;
import com.example.mymark.watermark.model.WatermarkItem;
import com.example.mymark.watermark.util.WatermarkManager;
import com.example.mymark.watermark.view.WatermarkView;

import java.io.IOException;
import java.util.List;

public class WatermarkActivity extends AppCompatActivity {

    private Button selectImageBtn;
    private ImageView imageView;
    private static final int SELECT_PICTURE = 100;
    private Uri filepath;
    //Bitmap
    private Bitmap original_image;

    //水印拖动相关
    private TextView mWatermarkView;
    private TextView mWatermarkViewForDrag; //约束布局下需要两个view才能实现拖动效果
    private int[] mDownPosInWatermark = new int[2];
    private int mLeftLocationOnScreenWatermark; //水印左侧的距离
    private int mVerticalMarginWatermarkTop; //水印顶部的距离

    //水印列表
    private RelativeLayout mAddMarkLayout;
    private GridView mGridViewMark;
    private WatermarkAdapter mWatermarkAdapter;
    private ConstraintLayout rootLayout;
    private final int mDefaultBorder = WatermarkUtil.WATERMARK_DEFAULT_MARGIN;
    private static String TAG = "WatermarkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);

        initView();



    }

    private void initView() {
        selectImageBtn = findViewById(R.id.choose_img);
        imageView = findViewById(R.id.image_view);
        rootLayout = findViewById(R.id.rootLayout);
        mWatermarkView = findViewById(R.id.watermark);
        mWatermarkViewForDrag = findViewById(R.id.watermark_for_drag);
//        mWatermarkView.setForDrag(true);

        initMarkList();
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        mWatermarkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onWatermarkTouched(v, event);
            }
        });
    }

    private void initMarkList() {
        mAddMarkLayout = findViewById(R.id.add_mark_layout);
        mGridViewMark = mAddMarkLayout.findViewById(R.id.gridview_watermark);
        final List<WatermarkItem> markList = WatermarkManager.getInstance().getVipWatermarkList();
        mWatermarkAdapter = new WatermarkAdapter(this, markList);
        mGridViewMark.setAdapter(mWatermarkAdapter);
    }

    /**
     * 水印拖动
     * @param v
     * @param event
     * @return
     */
    private boolean onWatermarkTouched(View v, MotionEvent event) {
        TextView tvWatermark = this.mWatermarkView;
        TextView tvForDrag = this.mWatermarkViewForDrag;
        int rIdWatermark = R.id.watermark;
        int rIdForDrag = R.id.watermark_for_drag;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownPosInWatermark[0] = (int) event.getX();
            mDownPosInWatermark[1] = (int) event.getY();
            int[] locationInWindow = new int[2];
            imageView.getLocationInWindow(locationInWindow);
            int[] locationInScreen = new int[2];
            mWatermarkView.getLocationOnScreen(locationInScreen);
            mLeftLocationOnScreenWatermark = locationInScreen[0];
            mVerticalMarginWatermarkTop = locationInScreen[1] - locationInWindow[1];

            int marginH = mLeftLocationOnScreenWatermark;
            int marginV = mVerticalMarginWatermarkTop;
            ConstraintSet set = new ConstraintSet();
            set.clone(rootLayout);
            set.clear(rIdWatermark, ConstraintSet.END);
            set.clear(rIdWatermark, ConstraintSet.TOP);
            set.clear(rIdWatermark, ConstraintSet.BOTTOM);
            set.connect(rIdWatermark, ConstraintSet.START, R.id.image_view, ConstraintSet.START, marginH);
            set.connect(rIdWatermark, ConstraintSet.TOP, R.id.image_view, ConstraintSet.TOP, marginV);
            set.clear(rIdForDrag, ConstraintSet.END);
            set.clear(rIdForDrag, ConstraintSet.TOP);
            set.clear(rIdForDrag, ConstraintSet.BOTTOM);
            set.connect(rIdForDrag, ConstraintSet.START, R.id.image_view, ConstraintSet.START, marginH);
            set.connect(rIdForDrag, ConstraintSet.TOP, R.id.image_view, ConstraintSet.TOP, marginV);
            set.applyTo(rootLayout);

            tvWatermark.setVisibility(View.GONE);
//            tvForDrag.setText(tvWatermark.getText());
            tvForDrag.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_rectangle_white_storke_1dp));
            tvForDrag.setVisibility(View.VISIBLE);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int marginH = (int) event.getX() - mDownPosInWatermark[0] + mLeftLocationOnScreenWatermark;
            int marginV = mVerticalMarginWatermarkTop + (int) event.getY() - mDownPosInWatermark[1];
            marginH = Math.max((rootLayout.getWidth() - imageView.getWidth()) / 2, marginH);
            marginH = Math.min(marginH, (rootLayout.getWidth() + imageView.getWidth()) / 2 - mWatermarkView.getWidth());
            marginV = Math.max(0, marginV);
            marginV = Math.min(marginV, imageView.getHeight() - mWatermarkView.getHeight());

            ConstraintSet set = new ConstraintSet();
            set.clone(rootLayout);
            set.connect(rIdForDrag, ConstraintSet.START, R.id.image_view, ConstraintSet.START, marginH);
            set.connect(rIdForDrag, ConstraintSet.TOP, R.id.image_view, ConstraintSet.TOP, marginV);
            set.connect(rIdWatermark, ConstraintSet.START, R.id.image_view, ConstraintSet.START, marginH);
            set.connect(rIdWatermark, ConstraintSet.TOP, R.id.image_view, ConstraintSet.TOP, marginV);
            set.applyTo(rootLayout);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // update position
            int marginH = (int) event.getX() - mDownPosInWatermark[0] + mLeftLocationOnScreenWatermark;
            int marginV = mVerticalMarginWatermarkTop + (int) event.getY() - mDownPosInWatermark[1];
            marginH = Math.max((rootLayout.getWidth() - imageView.getWidth()) / 2, marginH);
            marginH = Math.min(marginH, (rootLayout.getWidth() + imageView.getWidth()) / 2 - mWatermarkView.getWidth());
            marginV = Math.max(0, marginV);
            marginV = Math.min(marginV, imageView.getHeight() - mWatermarkView.getHeight());

            mVerticalMarginWatermarkTop = marginV;
            double ratioMarginH = (marginH - (rootLayout.getWidth() - imageView.getWidth()) / 2 + mDefaultBorder) / (double) imageView.getWidth();
            double ratioMarginV = (marginV + mDefaultBorder) / (double) imageView.getHeight();

//            mWatermarkSettings.setMarginH(ratioMarginH);
//            mWatermarkSettings.setMarginV(ratioMarginV);
            tvForDrag.setVisibility(View.GONE);
            tvWatermark.setVisibility(View.VISIBLE);
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(original_image);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }

    }
}
