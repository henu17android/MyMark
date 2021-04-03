package com.example.mymark.watermark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mymark.R;
import com.example.mymark.watermark.model.WatermarkItem;

import java.util.List;

public class WatermarkAdapter extends BaseAdapter {
    private List<WatermarkItem> mWaterItemList;
    private Context mContext;
    private int mSelectPosition = -1;

    public WatermarkAdapter(Context context, List<WatermarkItem> itemList) {
        this.mContext = context;
        this.mWaterItemList = itemList;
    }

    @Override
    public int getCount() {
        return mWaterItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWaterItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WatermarkItem watermarkItem = mWaterItemList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_watermark_item, parent, false);
        ImageView icon = view.findViewById(R.id.watermark_icon);
        icon.setImageBitmap(watermarkItem.getIcon());

        if (position == mSelectPosition) {
            icon.setSelected(true);
        }
        return view;
    }

    public void setSelectPosition(int position) {
        this.mSelectPosition = position;

    }

    public List<WatermarkItem> getWaterItemList() {
        return mWaterItemList;
    }
}
