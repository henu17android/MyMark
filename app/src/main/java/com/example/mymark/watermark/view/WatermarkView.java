package com.example.mymark.watermark.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mymark.MyApplication;
import com.example.mymark.util.StringUtils;
import com.example.mymark.watermark.WatermarkUtil;
import com.example.mymark.watermark.model.FontItem;
import com.example.mymark.watermark.model.WatermarkItem;
import com.example.mymark.watermark.style.CaptionBgSticker;
import com.example.mymark.watermark.style.CaptionFgStrokeLayer;
import com.example.mymark.watermark.style.CaptionStickerLayer;
import com.example.mymark.watermark.style.CaptionStrokeLayer;
import com.example.mymark.watermark.util.FontUtil;
import com.example.mymark.watermark.util.TemplateParams;
import com.example.mymark.watermark.util.WatermarkManager;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.AppCompatTextView;

import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_NONE;
import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_REPEAT;
import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_SCALE;

public class WatermarkView extends AppCompatTextView {


    private WatermarkItem mWaterItem;
    private String mTextColor = "#ffffff";
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private float mShadowRadius = (float) 1.6;
    private float mShadowDx = (float) 1.5;
    private float mShadowDy = (float) 1.3;
    private String mShadowColor = "#000000";
    private List<CaptionStrokeLayer> mStorkeLayerList;
    private List<CaptionStickerLayer> mStickerList;
    private int mStickerBaseHeight;
    private int mTextPosX;
    private int mTextPosY;
    private double mAscent;
    private double mDescent;
    private int mTextViewWidth;
    // TextView的最大宽度 in pixel
    private int mTextViewMaxWidth;
    private List mTextLines = new ArrayList();
    private int mLineCount;
    private boolean isForDrag;
    private float mDefaultWidth;

    // for drawing
    private Rect mTileRect = new Rect();
    private Rect mSrcLeftTileRect = new Rect();
    private Rect mDstLeftTileRect = new Rect();
    private Rect mSrcMiddleTileRect = new Rect();
    private Rect mDstMiddleTileRect = new Rect();
    private Rect mSrcRightTileRect = new Rect();
    private Rect mDstRightTileRect = new Rect();
    private static final String TAG = "WatermarkUtil";
    public WatermarkView(Context context) {
        this(context, null);
    }

    public WatermarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatermarkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mStorkeLayerList = new ArrayList<>();
        mStickerList = new ArrayList<>();
    }

    public void setMaxWidth(int maxWidth) {
        mTextViewMaxWidth = maxWidth;
    }

//    public void setFontFamily(long fontId) {
//        FontItem item = FontUtil.getInstance().getFontItemById(getContext(), fontId);
//        String fontFilePath = FontUtil.getInstance().getFontFilePathById(MyApplication.appContext, fontId);
//        Typeface tf;
//        try {
//            tf = Typeface.createFromFile(fontFilePath);
//            mAscent = item.getAscent();
//            mDescent = item.getDescent();
//            super.setTypeface(tf, Typeface.NORMAL);
//        } catch (Exception e) {
//            item = FontUtil.getInstance().getFontItemById(MyApplication.appContext, FontUtil.DEFAULT_FONT_ID);
//            fontFilePath = FontUtil.getInstance().getFontFilePathById(MyApplication.appContext, FontUtil.DEFAULT_FONT_ID);
//            tf = Typeface.createFromFile(fontFilePath);
//            mAscent = item.getAscent();
//            mDescent = item.getDescent();
//            super.setTypeface(tf, Typeface.NORMAL);
//        }
//    }


    public void setWatermarkStyle(WatermarkItem watermarkItem) {
        mWaterItem = watermarkItem;
        if (isForDrag) {
            setVisibility(GONE);
        } else if (watermarkItem.isHide()) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }

        if (watermarkItem.isEditable()) {
            setTextSize(StringUtils.parseInteger(watermarkItem.getForeground().getFontSize(), WatermarkUtil.DEFAULT_FONT_SIZE));
        } else {
            setTextSize(WatermarkUtil.DEFAULT_FONT_SIZE);
            Paint paint = getPaint();
            mDefaultWidth = paint.measureText("自定义");
        }

        String fileName = WatermarkUtil.getFileName(watermarkItem);
        mBitmapShader = null;
        mTextColor = mWaterItem.getForeground().getTextColor();
        if (!mWaterItem.getForeground().getBitmapShader().isEmpty()) {
            mBitmap = BitmapFactory.decodeFile(
                    getContext().getFilesDir().getAbsolutePath()
                            + "/"
                            + WatermarkUtil.WATERMARK_ROOT_DIR
                            + "/"
                            + fileName
                            + "/"
                            + watermarkItem.getName()
                            + "/"
                            + watermarkItem.getForeground().getBitmapShader());
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        mShadowRadius = mWaterItem.getForeground().getShadow().getRadius();
        mShadowColor = mWaterItem.getForeground().getShadow().getShadowColor();
        mShadowDx = mWaterItem.getForeground().getShadow().getDx();
        mShadowDy = mWaterItem.getForeground().getShadow().getDy();

        mStorkeLayerList.clear();   //获取所有的描边参数
        for (int i = 0; i < mWaterItem.getForeground().getStrokeLayers().size(); i++) {
            CaptionFgStrokeLayer layer = mWaterItem.getForeground().getStrokeLayers().get(i);
            CaptionStrokeLayer strokeLayer = new CaptionStrokeLayer(layer);
            mStorkeLayerList.add(strokeLayer);
        }
        mStickerList.clear();  //获取所有的贴图
        for (int i = 0; i < mWaterItem.getBackground().getStickerList().size(); i++) {
            CaptionBgSticker sticker = mWaterItem.getBackground().getStickerList().get(i);
            CaptionStickerLayer layer = new CaptionStickerLayer(sticker);
            mStickerList.add(layer);
        }

        //
        mStickerBaseHeight = mWaterItem.getBackground().getBaseHeight();
    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLineCount = getTextLines();
        int textWidth = getMeasuredWidth();
        if (mWaterItem != null && mWaterItem.getId() == WatermarkUtil.DEFAULT_WATERMARK_ID) {
            textWidth = (int)mDefaultWidth;
        }

        double rawTextHeight = getPaint().getFontMetrics().bottom - getPaint().getFontMetrics().top; //
        double realTextHeight = (1 - mAscent - mDescent) * rawTextHeight; //(1 - Ascent - Descent)行
        LOGD("[before] textHeight = "+rawTextHeight+", realTextHeight = "+realTextHeight);
        float maxTopExtra = 0f;
        float maxBottomExtra = 0f;
        // foreground   计算文字描边
        for(int i = 0; mStorkeLayerList != null && i < mStorkeLayerList.size(); i++) {
            textWidth += getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            textWidth += Math.abs(mStorkeLayerList.get(i).strokeParams.getDx() * getTextSize());
            float topExtra = getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            float bottomExtra = getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            float dy = mStorkeLayerList.get(i).strokeParams.getDy() * getTextSize();
            topExtra -= dy;
            bottomExtra += dy;
            maxTopExtra = Math.max(topExtra, maxTopExtra);
            maxBottomExtra = Math.max(bottomExtra, maxBottomExtra);
        }
        mTextViewWidth = textWidth;
        realTextHeight = realTextHeight + maxTopExtra + maxBottomExtra;
        LOGD("[after] maxTopExtra = "+maxTopExtra+", maxBottomExtra = "+maxBottomExtra+", realTextHeight = "+realTextHeight);
        //LOGD("[FontMetrics] "+getPaint().getFontMetrics().leading +"|"+ getPaint().getFontMetrics().top + "," + getPaint().getFontMetrics().bottom + "|" + getPaint().getFontMetrics().ascent + "," + getPaint().getFontMetrics().descent);

        // background
        double totalMarginTop = mAscent * rawTextHeight;
        double totalMarginBottom = mDescent * rawTextHeight;
        LOGD("[!!!] realTextHeight = "+realTextHeight+", totalMarginTop = "+totalMarginTop+", totalMarginBottom = "+totalMarginBottom);
        double totalMarginLeft = 0;
        double totalMarginRight = 0;
        for (int i = 0; i < mStickerList.size(); i++) {
            double marginTop = 0;
            double marginBottom = 0;
            double marginLeft = 0;
            double marginRight = 0;
            double relativeX = 0;
            double relativeY = 0;
            double ratio =  realTextHeight / (double) mStickerBaseHeight; //文字和贴图的比例
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            CaptionBgSticker sticker = stickerLayer.getSticker();
            double stickerHeight = sticker.getLayout().getHeight().getType() == TemplateParams.LAYOUT_TYPE_ABSOLUTE ?
                    (int)(sticker.getLayout().getHeight().getValue() * ratio) :
                    realTextHeight + (int)(sticker.getLayout().getHeight().getValue() * ratio);
            double stickerWidth = sticker.getLayout().getWidth().getType() == TemplateParams.LAYOUT_TYPE_ABSOLUTE ?
                    (int)(sticker.getLayout().getWidth().getValue() * ratio) :
                    textWidth + (int)(sticker.getLayout().getWidth().getValue() * ratio);
            stickerWidth = Math.max(0, stickerWidth);
            stickerHeight = Math.max(0, stickerHeight);
            int biasV = (int) (sticker.getConstraints().getBias().getV() * ratio); //垂直偏移
            int biasH = (int) (sticker.getConstraints().getBias().getH() * ratio);  //水平偏移
            // 计算垂直的margin
            //和文字的约束关系
            if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT) &&
                    sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
                //贴图在文字后面 垂直居中
                marginBottom = Math.max(0, stickerHeight / 2 + biasV - realTextHeight / 2);
                marginTop = Math.max(0, stickerHeight / 2 - biasV - realTextHeight / 2);
                relativeY = stickerHeight / 2 - biasV - realTextHeight / 2;
            } else if (!sticker.getConstraints().getTop().isEmpty()) {
                if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT)) {
                    //图片的顶部在文字顶部上方
                    if (biasV < 0) {
                        marginTop = Math.abs(biasV); //以贴图为背景，文字距离view顶部的距离
                        marginBottom = Math.max(0, stickerHeight + biasV - realTextHeight);
                    } else {
                        marginBottom = Math.max(0, stickerHeight + biasV - realTextHeight);
                    }
                    relativeY = 0 - biasV;//距离文字顶部的距离
                } else if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
                    marginBottom = Math.max(0, stickerHeight + biasV);
                    relativeY = 0 - (biasV + realTextHeight);
                }
            } else if (!sticker.getConstraints().getBottom().isEmpty()) {
                //图片位于文字上方
                if (sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT)) {
                    marginTop = Math.max(0, stickerHeight + biasV); //文字距离view顶部的距离
                    relativeY = stickerHeight + biasV;//贴图顶部到文字顶部的距离
                } else if (sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
                    if (biasV < 0) {
                        marginBottom = Math.abs(biasV);
                        marginTop = Math.max(0, stickerHeight + biasV - realTextHeight);
                    } else {
                        marginTop = Math.max(0, stickerHeight + biasV - realTextHeight);
                    }
                    relativeY = stickerHeight + biasV - realTextHeight;
                }
            }
            // 计算水平的margin
            //水平居中
            if (sticker.getConstraints().getLeft().equals(TemplateParams.CONSTRAINTS_TO_LEFT_OF_TEXT) &&
                    sticker.getConstraints().getRight().equals(TemplateParams.CONSTRAINTS_TO_RIGHT_OF_TEXT)) {
                // center in horizontal
                marginLeft = Math.max(0, stickerWidth / 2 - biasH - textWidth / 2); //左为负，右为正
                marginRight = Math.max(0, stickerWidth / 2 + biasH - textWidth / 2);
                relativeX = stickerWidth / 2 - biasH - textWidth / 2;
            } else if (!sticker.getConstraints().getLeft().isEmpty()) {
                if (sticker.getConstraints().getLeft().equals(TemplateParams.CONSTRAINTS_TO_LEFT_OF_TEXT)) {
                    if (biasH < 0) {
                        marginLeft = Math.abs(biasH);
                        marginRight = Math.max(0, stickerWidth + biasH - textWidth);
                    } else {
                        marginRight = Math.max(0, stickerWidth + biasH - textWidth);
                    }
                    relativeX = 0 - biasH;
                } else if (sticker.getConstraints().getLeft().equals(TemplateParams.CONSTRAINTS_TO_RIGHT_OF_TEXT)) {
                    marginRight = Math.max(0, biasH + stickerWidth);
                    relativeX = 0 - (biasH + textWidth);
                }
            } else if (!sticker.getConstraints().getRight().isEmpty()) {
                if (sticker.getConstraints().getRight().equals(TemplateParams.CONSTRAINTS_TO_LEFT_OF_TEXT)) {
                    marginLeft = Math.max(0, biasH + stickerWidth);
                    relativeX = biasH + stickerWidth;
                } else if (sticker.getConstraints().getRight().equals(TemplateParams.CONSTRAINTS_TO_RIGHT_OF_TEXT)) {
                    if (biasH < 0) {
                        marginRight = Math.abs(biasH);
                        marginLeft = Math.max(0, stickerWidth + biasH - textWidth);
                    } else {
                        marginLeft = Math.max(0, stickerWidth + biasH - textWidth);
                    }
                    relativeX = stickerWidth + biasH - textWidth;
                }
            }
            // 设置贴图的高宽和相对文字的位置
            stickerLayer.setHeight((int) stickerHeight);
            stickerLayer.setWidth((int) stickerWidth);
            stickerLayer.setX((int) relativeX);
            stickerLayer.setY((int) relativeY);
            // 调整总体margin
            int mBorder = 12;
            totalMarginLeft = Math.max(marginLeft, totalMarginLeft) + mBorder;
            totalMarginRight = Math.max(marginRight, totalMarginRight) + mBorder;
            totalMarginTop = Math.max(marginTop, totalMarginTop) + mBorder;
            totalMarginBottom = Math.max(marginBottom, totalMarginBottom) + mBorder;
        }
        for (int i = 0; i < mStickerList.size(); i++) {
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            Log.d(TAG, "onMeasure: " + totalMarginLeft + " " + stickerLayer.getX());
            Log.d(TAG, "onMeasure:top " + totalMarginTop + " " + stickerLayer.getY());
            //总体margin，减去贴图对文字的距离，得到贴图相对于整个view左上角的距离
            stickerLayer.setX((int) totalMarginLeft - stickerLayer.getX());
            stickerLayer.setY((int) totalMarginTop - stickerLayer.getY());
        }
        int totalHeight = (int) (realTextHeight + totalMarginTop + totalMarginBottom);
        int totalWidth = (int) (textWidth + totalMarginLeft + totalMarginRight);
        // textView 距离整体left的距离
        mTextPosX = (int) totalMarginLeft;
        // textView 距离整体top的距离
        mTextPosY = (int) (totalMarginTop - mAscent * rawTextHeight);
        LOGD("mTextPosX = "+mTextPosX+", mTextPosY = "+mTextPosY);
        totalHeight = totalHeight * mLineCount;
        LOGD("[after] totalWidth = "+totalWidth+", totalHeight = "+totalHeight);
        this.setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.save();
        TextPaint paint = getPaint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        String text = getText().toString();

        // draw below fg sticker layers
        if (!mWaterItem.isHide()) {
            drawStickerLayers(canvas, false);
        }

        // calculate line height
        float maxTopExtra = 0f;
        float maxBottomExtra = 0f;
        for(int i = 0; mStorkeLayerList != null && i < mStorkeLayerList.size(); i++) {
            float topExtra = getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            float bottomExtra = getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            float dy = mStorkeLayerList.get(i).strokeParams.getDy() * getTextSize();
            topExtra -= dy;
            bottomExtra += dy;
            maxTopExtra = Math.max(topExtra, maxTopExtra);
            maxBottomExtra = Math.max(bottomExtra, maxBottomExtra);
        }
        float fontHeightExtra = maxTopExtra + maxBottomExtra;
        int fontLineHeight = (int) ((paint.getFontMetrics().bottom - paint.getFontMetrics().top) + fontHeightExtra);

        // draw foreground layers
        for(int i = 0; i < mStorkeLayerList.size(); i++) {
            TextPaint textPaint = mStorkeLayerList.get(i).strokePaint;
            textPaint.setAntiAlias(true);
            textPaint.setDither(true);
//            textPaint.setLetterSpacing((float) 0.1);
            textPaint.setTextSize(paint.getTextSize());
            textPaint.setFlags(paint.getFlags());
            textPaint.setAlpha(paint.getAlpha());
            textPaint.setTypeface(paint.getTypeface());
            // 自定义描边效果
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setColor(Color.parseColor(mStorkeLayerList.get(i).strokeParams.getTextColor()));
            textPaint.setStrokeWidth(getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth());
            float dx = mStorkeLayerList.get(i).strokeParams.getDx() * getTextSize();
            float dy = mStorkeLayerList.get(i).strokeParams.getDy() * getTextSize();
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            LOGD(fontMetrics.top + "," + fontMetrics.bottom + "|" + fontMetrics.ascent + "," + fontMetrics.descent);
            // 在文本底层画出带描边的文本
            if (mWaterItem != null && mWaterItem.isEditable()) {
                // 单行
                canvas.drawText(text,
                        dx + mTextPosX + (mTextViewWidth - textPaint.measureText(text)) / 2,
                        dy - fontMetrics.top + mTextPosY,
                        textPaint);
            }
        }

        // draw text shadow
        getPaint().setShadowLayer(
                mShadowRadius * getTextSize(),
                mShadowDx * getTextSize(),
                mShadowDy * getTextSize(),
                Color.parseColor(mShadowColor)); // or whatever shadow you use
        getPaint().setShader(null);

        // draw text picture texture
        if (mBitmapShader != null) {
            getPaint().clearShadowLayer();
            getPaint().setShader(mBitmapShader);
        } else {
            paint.setColor(Color.parseColor(mTextColor));
        }

        // draw text
        if (mWaterItem != null && mWaterItem.getForeground().getHighlightText() != null && mWaterItem.isEditable()) {
            Log.d(TAG, "onDraw: mTextViewWidth - paint.measureText(text) " + (mTextViewWidth - paint.measureText(text)));
            float textPaddingLeft = mTextPosX + (mTextViewWidth - paint.measureText(text)) / 2;
            float textPaddingTop = mTextPosY - paint.getFontMetrics().top;
            if (!text.isEmpty()) {
                int highlightType = mWaterItem.getForeground().getHighlightText().getType();
                int highlightColor = Color.parseColor(mWaterItem.getForeground().getHighlightText().getColor());
                int normalColor = Color.parseColor(mTextColor);
                float highlightRand = mWaterItem.getForeground().getHighlightText().getRandom();
                if (highlightType == 1) {
                    // 随机高亮
                    float startX = textPaddingLeft;
                    for (int i = 0; i < text.length(); i++) {
                        float rand = (float) Math.random();
                        if (rand < highlightRand) {
                            paint.setColor(highlightColor);
                        } else {
                            paint.setColor(normalColor);
                        }
                        canvas.drawText(text.substring(i, i+1), startX, textPaddingTop, paint);
                        startX += paint.measureText(text.substring(i, i+1));
                    }
                } else if (highlightType == 2) {
                    // 指定位置高亮
                    float startX = textPaddingLeft;
                    for (int i = 0; i < text.length(); i++) {
                        int indexF = i + 1;
                        int indexB = i - text.length();
                        if (mWaterItem.getForeground().getHighlightText().getIndex().contains(indexF)
                                || mWaterItem.getForeground().getHighlightText().getIndex().contains(indexB)) {
                            paint.setColor(highlightColor);
                        } else {
                            paint.setColor(normalColor);
                        }
                        canvas.drawText(text.substring(i, i+1), startX, textPaddingTop, paint);
                        startX += paint.measureText(text.substring(i, i+1));
                    }
                }
            }
        } else {
            Log.d(TAG, "onDraw: mTextViewWidth - paint.measureText(text) " + (mTextViewWidth - paint.measureText(text)));
            if (mLineCount == 1 && mWaterItem.isEditable()) {
                canvas.drawText(text,
                        mTextPosX + (mTextViewWidth - paint.measureText(text)) / 2,
                        mTextPosY - paint.getFontMetrics().top, paint); //从baseline左侧开始绘制文字
            } else {
                // 多行
                for (int i = 0; i < mTextLines.size(); i++) {
                    canvas.drawText((String) mTextLines.get(i),
                            mTextPosX + (Math.min(mTextViewMaxWidth, mTextViewWidth) - paint.measureText((String)(mTextLines.get(i)))) / 2,
                            mTextPosY - paint.getFontMetrics().top + i * fontLineHeight,
                            paint);
                }
            }

        }

        // draw above fg sticker layers
        if (!mWaterItem.isHide()) {
            drawStickerLayers(canvas, true);
        }
    }

    /**
     * 计算行数
     * @return
     */
    private int getTextLines() {
        if (mTextViewMaxWidth <= 0)
            return 1;
        if (mWaterItem == null || mWaterItem.isSingleLine()) {
//            setText(getText().toString().replaceAll("\n", ""));
            return 1;
        }

        mTextLines.clear();
        int lineCount = 0;

        if (getText().toString().contains("\n")) {
            mTextLines = StringUtils.split(getText().toString(), "\n");
            lineCount = mTextLines.size();
        } else {
            float textTotalWidth = getPaint().measureText(getText().toString());
            if (textTotalWidth <= mTextViewMaxWidth) {
                mTextLines.add(getText().toString());
                return 1;
            }

            StringBuilder sb = new StringBuilder();
            String[] wordsList = getText().toString().split(" ");
            for (String word : wordsList) {
                if (getPaint().measureText(word) > mTextViewMaxWidth) {
                    for (int i = 0; i < word.length(); i++) {
                        sb.append(word.substring(i, i + 1));
                        if (getPaint().measureText(sb.toString()) > mTextViewMaxWidth) {
                            lineCount++;
                            mTextLines.add(sb.subSequence(0, sb.length() - 1));
                            sb.delete(0, sb.length() - 1);
                        }
                    }
                } else {
                    sb.append(word);
                    if (getPaint().measureText(sb.toString()) > mTextViewMaxWidth) {
                        lineCount++;
                        mTextLines.add(sb.subSequence(0, sb.length() - word.length()));
                        sb.delete(0, sb.length() - word.length());
                    }
                }
                sb.append(" ");
            }
            if (sb.length() > 0) {
                lineCount++;
                mTextLines.add(sb.subSequence(0, sb.length() - 1));
            }
        }
        return lineCount;
    }

    private void drawStickerLayers(Canvas canvas, boolean drawLayersAboveFg) {
        for (int i = 0; i < mStickerList.size(); i++) {
            if (mStickerList.get(i).getSticker().isAboveFg() != drawLayersAboveFg)
                continue;
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            Bitmap stickerBitmap = WatermarkManager.getInstance().getStickerBitmap(
                    mWaterItem,
                    stickerLayer.getSticker().getDrawable().getFile());
            int drawingMode = stickerLayer.getSticker().getDrawable().getTileMode();
            double tileStart = stickerLayer.getSticker().getDrawable().getTileStart();
            double tileEnd = stickerLayer.getSticker().getDrawable().getTileEnd();
            double ratio = stickerLayer.getHeight() / (double)stickerBitmap.getHeight();
            int tileWidth = stickerLayer.getWidth() - (int)(stickerBitmap.getWidth() * (tileStart + 1 - tileEnd) * ratio);
            switch (drawingMode) {
                case TILE_MODE_NONE:
                    // 等比缩放
                    mTileRect.set(
                            stickerLayer.getX(),
                            stickerLayer.getY(),
                            stickerLayer.getX()+stickerLayer.getWidth(),
                            stickerLayer.getY()+stickerLayer.getHeight()
                    );
                    canvas.drawBitmap(stickerBitmap, null, mTileRect, null);
                    break;

                case TILE_MODE_SCALE:
                    // 拉伸
                    mSrcLeftTileRect.set(
                            0,
                            0,
                            (int) (stickerBitmap.getWidth() * tileStart),
                            stickerBitmap.getHeight()
                    );  //bitmap
                    mDstLeftTileRect.set(
                            stickerLayer.getX(),
                            stickerLayer.getY(),
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio),
                            stickerLayer.getY() + stickerLayer.getHeight()
                    ); //在view中的位置
                    mSrcMiddleTileRect.set(
                            (int) (stickerBitmap.getWidth() * tileStart),
                            0,
                            (int) (stickerBitmap.getWidth() * tileEnd),
                            stickerBitmap.getHeight()
                    );
                    mDstMiddleTileRect.set(
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio),
                            stickerLayer.getY(),
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + tileWidth,
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
                    mSrcRightTileRect.set(
                            (int) (stickerBitmap.getWidth() * tileEnd),
                            0,
                            stickerBitmap.getWidth(),
                            stickerBitmap.getHeight()
                    );
                    mDstRightTileRect.set(
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + tileWidth,
                            stickerLayer.getY(),
                            stickerLayer.getX() + stickerLayer.getWidth(),
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
                    canvas.drawBitmap(stickerBitmap, mSrcLeftTileRect, mDstLeftTileRect, null);
                    canvas.drawBitmap(stickerBitmap, mSrcMiddleTileRect, mDstMiddleTileRect, null);
                    canvas.drawBitmap(stickerBitmap, mSrcRightTileRect, mDstRightTileRect, null);
                    break;

                case TILE_MODE_REPEAT:
                    // 平铺
                    // 平铺左侧
                    mSrcLeftTileRect.set(
                            0,
                            0,
                            (int) (stickerBitmap.getWidth() * tileStart),
                            stickerBitmap.getHeight()
                    );
                    mDstLeftTileRect.set(
                            stickerLayer.getX(),
                            stickerLayer.getY(),
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio),
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
                    canvas.drawBitmap(stickerBitmap, mSrcLeftTileRect, mDstLeftTileRect, null);
                    mSrcMiddleTileRect.set(
                            (int) (stickerBitmap.getWidth() * tileStart),
                            0,
                            (int) (stickerBitmap.getWidth() * tileEnd),
                            stickerBitmap.getHeight()
                    );
                    // 需要被平铺的宽度
                    int widthTobeTiled = tileWidth;
                    // 每次平铺的宽度
                    int widthRepeated = (int) (stickerBitmap.getWidth() * (tileEnd - tileStart) * ratio);
                    // 已平铺次数
                    int repeatTimes = 0;
                    while (widthTobeTiled > widthRepeated) {
                        mDstMiddleTileRect.set(
                                stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + widthRepeated * repeatTimes,
                                stickerLayer.getY(),
                                stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + widthRepeated * (repeatTimes + 1),
                                stickerLayer.getY() + stickerLayer.getHeight()
                        );
                        canvas.drawBitmap(stickerBitmap, mSrcMiddleTileRect, mDstMiddleTileRect, null);
                        repeatTimes = repeatTimes + 1;
                        widthTobeTiled = widthTobeTiled - widthRepeated;
                    }
                    // 平铺不足widthRepeated的区域
                    mSrcMiddleTileRect.set(
                            (int) (stickerBitmap.getWidth() * tileStart),
                            0,
                            (int) (stickerBitmap.getWidth() * (tileStart + widthTobeTiled / (stickerBitmap.getWidth() * ratio))),
                            stickerBitmap.getHeight()
                    );
                    mDstMiddleTileRect.set(
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + widthRepeated * repeatTimes,
                            stickerLayer.getY(),
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + widthRepeated * repeatTimes + widthTobeTiled,
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
                    canvas.drawBitmap(stickerBitmap, mSrcMiddleTileRect, mDstMiddleTileRect, null);
                    // 平铺右侧
                    mSrcRightTileRect.set(
                            (int) (stickerBitmap.getWidth() * tileEnd),
                            0,
                            stickerBitmap.getWidth(),
                            stickerBitmap.getHeight()
                    );
                    mDstRightTileRect.set(
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio) + tileWidth,
                            stickerLayer.getY(),
                            stickerLayer.getX() + stickerLayer.getWidth(),
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
                    canvas.drawBitmap(stickerBitmap, mSrcRightTileRect, mDstRightTileRect, null);
                    break;
            }
        }
    }


    public void setForDrag(boolean forDrag) {
        isForDrag = forDrag;
    }

    public WatermarkItem getWaterItem() {
        return mWaterItem;
    }

    public void setWaterItem(WatermarkItem waterItem) {
        this.mWaterItem = waterItem;
    }

    private void LOGD(String s) {
//        Log.d(TAG, "[CaptionTextView] "+s);
    }
}
