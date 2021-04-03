package com.example.mymark.watermark;

import android.app.Application;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.mymark.MyApplication;
import com.example.mymark.util.StringUtils;
import com.example.mymark.watermark.util.FileUtil;
import com.example.mymark.watermark.model.FontItem;
import com.example.mymark.watermark.model.WatermarkItem;
import com.example.mymark.watermark.style.CaptionBgSticker;
import com.example.mymark.watermark.style.CaptionFgStrokeLayer;
import com.example.mymark.watermark.style.CaptionStickerLayer;
import com.example.mymark.watermark.style.CaptionStrokeLayer;
import com.example.mymark.watermark.util.FontUtil;
import com.example.mymark.watermark.util.TemplateParams;
import com.example.mymark.watermark.util.WatermarkManager;

import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_NONE;
import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_REPEAT;
import static com.example.mymark.watermark.util.TemplateParams.TILE_MODE_SCALE;

public class WatermarkUtil {

    public static final String WATERMARK_ROOT_DIR = "watermark";
    public static final String WATERMARK_ICON_FILE_NAME = "icon.png";
    public static final String WATERMARK_NORMAL_FILE_NAME = "normal";
    public static final String WATERMARK_VIP_FILE_NAME = "vip";
    public static final String WATERMARK_FILE_POSTFIX = ".bpwt";
    public static final String WATERMARK_FILE_ZIP = ".bpwts";
    public static final String WATERMARK_CONFIG_FILE_NAME = "config.json";
    public static final int WATERMARK_DEFAULT_MARGIN = 12;
    public static final long NON_WATERMARK_ID = 200001;
    public static final long DEFAULT_WATERMARK_ID = 0;
    private static String TAG = "WatermarkUtil";
    public static final int DEFAULT_FONT_SIZE = 14;

    /**
     * 解压水印包
     * @param watermarkZipPath
     * @return
     */
    public static boolean extractWatermark(String watermarkZipPath) {
        try {
            File watermarkRootDir = new File(MyApplication.appContext.getFilesDir().getAbsolutePath() + "/" + WATERMARK_ROOT_DIR + "/");
            if (!watermarkRootDir.exists() || !watermarkRootDir.isDirectory()) return false;
            File watermarkZip = new File(watermarkZipPath);
            FileUtil.unzip(watermarkZip, watermarkRootDir);
            String packageName = watermarkZip.getName().substring(0, watermarkZip.getName().lastIndexOf("."));
            File watermarkPackageDir = new File(watermarkRootDir, packageName);
            if (!watermarkPackageDir.exists() || !watermarkPackageDir.isDirectory()) return false;

            String[] watermarkFileList = watermarkPackageDir.list();
            if (watermarkFileList == null) return false;
            for (String fileName : watermarkFileList) {
                if (!fileName.endsWith(WATERMARK_FILE_POSTFIX)) continue;
                //unzip bpwt file
                File bpwtFile = new File(watermarkPackageDir, fileName);
                FileUtil.unzip(bpwtFile, watermarkPackageDir);
                bpwtFile.delete();
            }
            watermarkZip.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Bitmap createWatermarkAsPng(Context context, WatermarkItem watermarkItem, String content) {
        if (watermarkItem == null || watermarkItem.getId() == WatermarkUtil.NON_WATERMARK_ID) {
            return null;
        }
        if (watermarkItem.getId() == WatermarkUtil.DEFAULT_WATERMARK_ID) {
            // 默认绘影字幕水印，设置占位字符，用于计算宽度
            //不超过4个汉字
            content = " ";
        }
        String fileName = getFileName(watermarkItem);
        final float scale = context.getResources().getDisplayMetrics().density;
        Bitmap bitmap = null;
        BitmapShader bitmapShader = null;
        String textColor = watermarkItem.getForeground().getTextColor();
        if (!watermarkItem.getForeground().getBitmapShader().isEmpty()) {
            bitmap = BitmapFactory.decodeFile(
                    context.getFilesDir().getAbsolutePath()
                            + "/"
                            + WatermarkUtil.WATERMARK_ROOT_DIR
                            + "/"
                            + fileName
                            + "/"
                            + watermarkItem.getName()
                            + "/"
                            + watermarkItem.getForeground().getBitmapShader());
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        float shadowRadius = watermarkItem.getForeground().getShadow().getRadius();
        String shadowColor = watermarkItem.getForeground().getShadow().getShadowColor();
        float shadowDx = watermarkItem.getForeground().getShadow().getDx();
        float shadowDy = watermarkItem.getForeground().getShadow().getDy();

        // 准备foreground stroke相关
        List<CaptionStrokeLayer> mStorkeLayerList = new ArrayList<>();
        for (int i = 0; i < watermarkItem.getForeground().getStrokeLayers().size(); i++) {
            CaptionFgStrokeLayer layer = watermarkItem.getForeground().getStrokeLayers().get(i);
            CaptionStrokeLayer strokeLayer = new CaptionStrokeLayer(layer);
            mStorkeLayerList.add(strokeLayer);
        }

        // 准备background贴图相关
        List<CaptionStickerLayer> mStickerList = new ArrayList<>();
        for (int i = 0; i < watermarkItem.getBackground().getStickerList().size(); i++) {
            CaptionBgSticker sticker = watermarkItem.getBackground().getStickerList().get(i);
            CaptionStickerLayer layer = new CaptionStickerLayer(sticker);
            mStickerList.add(layer);
        }
        int mStickerBaseHeight = watermarkItem.getBackground().getBaseHeight();

//        FontItem fontItem = FontUtil.getInstance().getFontItemById(context, watermarkItem.getForeground().getFontId());
//        String fontFilePath = FontUtil.getInstance().getFontFilePathById(context, watermarkItem.getForeground().getFontId());
//        if (fontFilePath == null) {
//            // font dose not exist, change to default font
//            fontFilePath = FontUtil.getInstance().getFontFilePathById(context, FontUtil.DEFAULT_FONT_ID);
//            fontItem = FontUtil.getInstance().getFontItemById(context, FontUtil.DEFAULT_FONT_ID);
//        }
        double mAscent = 0;
        double mDescent = 0;

        int watermarkFontSize = StringUtils.parseInteger(watermarkItem.getForeground().getFontSize(), DEFAULT_FONT_SIZE);
        float fontSize =  watermarkFontSize * scale + 0.5f;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(fontSize);
//        paint.setTypeface(Typeface.createFromFile(fontFilePath));
        //获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();


        // 计算水印bitmap的高度
        double rawTextHeight = (int)(fontMetrics.bottom - fontMetrics.top);
        double realTextHeight =  (int)((1 - mAscent - mDescent) * rawTextHeight);
        float maxTopExtra = 0;
        float maxBottomExtra = 0;
        for (int i = 0; mStorkeLayerList != null && i < mStorkeLayerList.size(); i++) {
            int topExtra = (int) (fontSize * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2);
            int bottomExtra = (int) (fontSize * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2);
            int dy = (int) (mStorkeLayerList.get(i).strokeParams.getDy() * fontSize);
            topExtra -= dy;
            bottomExtra += dy;
            maxTopExtra = topExtra > maxTopExtra ? topExtra : maxTopExtra;
            maxBottomExtra = bottomExtra > maxBottomExtra ? bottomExtra : maxBottomExtra;
        }
        realTextHeight = realTextHeight + maxTopExtra + maxBottomExtra;
        double totalMarginTop = mAscent * rawTextHeight;
        double totalMarginBottom = mDescent * rawTextHeight;
//        for (int i = 0; i < mStickerList.size(); i++) {
//            double marginTop = 0;
//            double marginBottom = 0;
//            double relativeY = 0;
//            double ratio =  realTextHeight / (double) mStickerBaseHeight;
//            CaptionStickerLayer stickerLayer = mStickerList.get(i);
//            CaptionBgSticker sticker = stickerLayer.getSticker();
//            double stickerHeight = sticker.getLayout().getHeight().getType() == TemplateParams.LAYOUT_TYPE_ABSOLUTE ?
//                    (int)(sticker.getLayout().getHeight().getValue() * ratio) :
//                    realTextHeight + (int)(sticker.getLayout().getHeight().getValue() * ratio);
//            stickerHeight = Math.max(0, stickerHeight);
//            int biasV = (int) (sticker.getConstraints().getBias().getV() * ratio);
//            // 计算垂直的margin
//            if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT) &&
//                    sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
//                // center in vertical
//                marginBottom = Math.max(0, stickerHeight / 2 + biasV - realTextHeight / 2);
//                marginTop = Math.max(0, stickerHeight / 2 - biasV - realTextHeight / 2);
//                relativeY = stickerHeight / 2 - biasV - realTextHeight / 2;
//            } else if (!sticker.getConstraints().getTop().isEmpty()) {
//                if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT)) {
//                    if (biasV < 0) {
//                        marginTop = Math.abs(biasV);
//                        marginBottom = Math.max(0, stickerHeight + biasV - realTextHeight);
//                    } else {
//                        marginBottom = Math.max(0, stickerHeight + biasV - realTextHeight);
//                    }
//                    relativeY = 0 - biasV;
//                } else if (sticker.getConstraints().getTop().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
//                    marginBottom = Math.max(0, stickerHeight + biasV);
//                    relativeY = 0 - (biasV + realTextHeight);
//                }
//            } else if (!sticker.getConstraints().getBottom().isEmpty()) {
//                if (sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_TOP_OF_TEXT)) {
//                    marginTop = Math.max(0, stickerHeight + biasV);
//                    relativeY = stickerHeight + biasV;
//                } else if (sticker.getConstraints().getBottom().equals(TemplateParams.CONSTRAINTS_TO_BOTTOM_OF_TEXT)) {
//                    if (biasV < 0) {
//                        marginBottom = Math.abs(biasV);
//                        marginTop = Math.max(0, stickerHeight + biasV - realTextHeight);
//                    } else {
//                        marginTop = Math.max(0, stickerHeight + biasV - realTextHeight);
//                    }
//                    relativeY = stickerHeight + biasV - realTextHeight;
//                }
//            }
//            // 设置贴图的高度和位置
//            stickerLayer.setHeight((int) stickerHeight);
//            stickerLayer.setY((int) relativeY);
//            // 调整总体margin
//            totalMarginTop = marginTop > totalMarginTop ? marginTop : totalMarginTop;
//            totalMarginBottom = marginBottom > totalMarginBottom ? marginBottom : totalMarginBottom;
//        }
        for (int i = 0; i < mStickerList.size(); i++) {
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            stickerLayer.setY((int) totalMarginTop - stickerLayer.getY());
        }
        // 计算单行字幕的高度
        int lineHeight = (int) (realTextHeight + totalMarginTop + totalMarginBottom);
        int maxLineCount = 1;
        int totalHeight = lineHeight * maxLineCount;

        // 文字距离位图left和top的距离, 为drawText做准备
        int mTextPosX = 0;
        int mTextPosY = (int) totalMarginTop - (int) (mAscent * rawTextHeight);
        int textWidth = 0;
        if (!StringUtils.isEmpty(content)) {
            textWidth = (int) (paint.measureText(content));
        }


        int mTextViewWidth;

        //计算位图宽度
        // 计算文本和背景贴图的位置
        for (int i = 0; mStorkeLayerList != null && i < mStorkeLayerList.size(); i++) {
            textWidth += fontSize * mStorkeLayerList.get(i).strokeParams.getStrokeWidth() / 2;
            textWidth += Math.abs(mStorkeLayerList.get(i).strokeParams.getDx() * fontSize);
        }
        mTextViewWidth = textWidth;
        double totalMarginLeft = 0;
        double totalMarginRight = 0;
        for (int i = 0; i < mStickerList.size(); i++) {
            double marginLeft = 0;
            double marginRight = 0;
            double relativeX = 0;
            double ratio = realTextHeight / (double) mStickerBaseHeight;
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            CaptionBgSticker sticker = stickerLayer.getSticker();
            double stickerHeight = sticker.getLayout().getHeight().getType() == TemplateParams.LAYOUT_TYPE_ABSOLUTE ?
                    (int) (sticker.getLayout().getHeight().getValue() * ratio) :
                    realTextHeight + (int) (sticker.getLayout().getHeight().getValue() * ratio);
            double stickerWidth = sticker.getLayout().getWidth().getType() == TemplateParams.LAYOUT_TYPE_ABSOLUTE ?
                    (int) (sticker.getLayout().getWidth().getValue() * ratio) :
                    textWidth + (int) (sticker.getLayout().getWidth().getValue() * ratio);
            stickerWidth = Math.max(0, stickerWidth);
            stickerHeight = Math.max(0, stickerHeight);
            int biasV = (int) (sticker.getConstraints().getBias().getV() * ratio);
            int biasH = (int) (sticker.getConstraints().getBias().getH() * ratio);
            // 计算水平的margin
            if (sticker.getConstraints().getLeft().equals(TemplateParams.CONSTRAINTS_TO_LEFT_OF_TEXT) &&
                    sticker.getConstraints().getRight().equals(TemplateParams.CONSTRAINTS_TO_RIGHT_OF_TEXT)) {
                // center in horizontal
                marginLeft = Math.max(0, stickerWidth / 2 - biasH - textWidth / 2);
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
            // 设置贴图的宽度和位置
            stickerLayer.setWidth((int) stickerWidth);
            stickerLayer.setX((int) relativeX);
            // 调整总体margin
            totalMarginLeft = marginLeft > totalMarginLeft ? marginLeft : totalMarginLeft;
            totalMarginRight = marginRight > totalMarginRight ? marginRight : totalMarginRight;
        }
        // 根据是否centerHorizontal计算水平的偏移
        int totalWidth = (int) (textWidth + totalMarginLeft + totalMarginRight);

        // 调整文字和贴图的X位置
        mTextPosX = (int) (totalMarginLeft);
        for (int i = 0; i < mStickerList.size(); i++) {
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            stickerLayer.setX((int) (totalMarginLeft - stickerLayer.getX()));
        }

        Bitmap resultBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // 开始画foreground和background
        Rect mTileRect = new Rect();
        Rect mSrcLeftTileRect = new Rect();
        Rect mDstLeftTileRect = new Rect();
        Rect mSrcMiddleTileRect = new Rect();
        Rect mDstMiddleTileRect = new Rect();
        Rect mSrcRightTileRect = new Rect();
        Rect mDstRightTileRect = new Rect();
        // draw below fg sticker layers
        for (int i = 0; i < mStickerList.size(); i++) {
            if (mStickerList.get(i).getSticker().isAboveFg())
                continue;
            CaptionStickerLayer stickerLayer = mStickerList.get(i);
            Bitmap stickerBitmap = WatermarkManager.getInstance().getStickerBitmap(watermarkItem,
                    getFileName(watermarkItem));
            int drawingMode = stickerLayer.getSticker().getDrawable().getTileMode();
            double tileStart = stickerLayer.getSticker().getDrawable().getTileStart();
            double tileEnd = stickerLayer.getSticker().getDrawable().getTileEnd();
            double ratio = stickerLayer.getHeight() / (double) stickerBitmap.getHeight();
            int tileWidth = stickerLayer.getWidth() - (int) (stickerBitmap.getWidth() * (tileStart + 1 - tileEnd) * ratio);
            switch (drawingMode) {
                case TILE_MODE_NONE:
                    // 等比缩放
                    mTileRect.set(
                            stickerLayer.getX(),
                            stickerLayer.getY(),
                            stickerLayer.getX() + stickerLayer.getWidth(),
                            stickerLayer.getY() + stickerLayer.getHeight()
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
                    );
                    mDstLeftTileRect.set(
                            stickerLayer.getX(),
                            stickerLayer.getY(),
                            stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio),
                            stickerLayer.getY() + stickerLayer.getHeight()
                    );
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

        // draw foreground layers
        for (int i = 0; i < mStorkeLayerList.size(); i++) {
            TextPaint textPaint = mStorkeLayerList.get(i).strokePaint;
            textPaint.setAntiAlias(true);
            textPaint.setDither(true);
            textPaint.setTextSize(paint.getTextSize());
            textPaint.setFlags(paint.getFlags());
            textPaint.setAlpha(paint.getAlpha());
            textPaint.setTypeface(paint.getTypeface());
            //自定义描边效果
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setColor(Color.parseColor(mStorkeLayerList.get(i).strokeParams.getTextColor()));
            textPaint.setStrokeWidth(paint.getTextSize() * mStorkeLayerList.get(i).strokeParams.getStrokeWidth());
            float dx = mStorkeLayerList.get(i).strokeParams.getDx() * fontSize;
            float dy = mStorkeLayerList.get(i).strokeParams.getDy() * fontSize;

            // 在文本底层画出带描边的文本
            canvas.drawText(content,
                    dx + mTextPosX + (mTextViewWidth - textPaint.measureText(content)) / 2,
                    dy - fontMetrics.top + mTextPosY,
                    textPaint);

        }

        // draw text shadow
        paint.setShadowLayer(shadowRadius * fontSize, shadowDx * fontSize, shadowDy * fontSize, Color.parseColor(shadowColor)); // or whatever shadow you use
        paint.setShader(null);

        // draw text picture texture
        if (bitmapShader != null) {
            paint.clearShadowLayer();
            paint.setShader(bitmapShader);
        } else {
            paint.setColor(Color.parseColor(textColor));
        }

        String text = content;
        if (watermarkItem != null && watermarkItem.getForeground().getHighlightText() != null) {
            float textPaddingLeft = mTextPosX + (mTextViewWidth - paint.measureText(text)) / 2;
            float textPaddingTop = mTextPosY - paint.getFontMetrics().top;
            if (!text.isEmpty()) {
                int highlightType = watermarkItem.getForeground().getHighlightText().getType();
                int highlightColor = Color.parseColor(watermarkItem.getForeground().getHighlightText().getColor());
                int normalColor = Color.parseColor(textColor);
                float highlightRand = watermarkItem.getForeground().getHighlightText().getRandom();
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
                        canvas.drawText(text.substring(i, i + 1), startX, textPaddingTop, paint);
                        startX += paint.measureText(text.substring(i, i + 1));
                    }
                } else if (highlightType == 2) {
                    // 指定位置高亮
                    float startX = textPaddingLeft;
                    for (int i = 0; i < text.length(); i++) {
                        int indexF = i + 1;
                        int indexB = i - text.length();
                        if (watermarkItem.getForeground().getHighlightText().getIndex().contains(indexF)
                                || watermarkItem.getForeground().getHighlightText().getIndex().contains(indexB)) {
                            paint.setColor(highlightColor);
                        } else {
                            paint.setColor(normalColor);
                        }
                        canvas.drawText(text.substring(i, i + 1), startX, textPaddingTop, paint);
                        startX += paint.measureText(text.substring(i, i + 1));
                    }
                }
            }
        } else {
            if (watermarkItem.isEditable()) {
                canvas.drawText(text,
                        mTextPosX + (mTextViewWidth - paint.measureText(text)) / 2,
                        mTextPosY - paint.getFontMetrics().top, paint);
            }

        }

        // draw above fg sticker layers
        if (!text.isEmpty()) {
            for (int i = 0; i < mStickerList.size(); i++) {
                if (!mStickerList.get(i).getSticker().isAboveFg())
                    continue;
                CaptionStickerLayer stickerLayer = mStickerList.get(i);
                Bitmap stickerBitmap = WatermarkManager.getInstance().getStickerBitmap(watermarkItem,
                        getFileName(watermarkItem));
                int drawingMode = stickerLayer.getSticker().getDrawable().getTileMode();
                double tileStart = stickerLayer.getSticker().getDrawable().getTileStart();
                double tileEnd = stickerLayer.getSticker().getDrawable().getTileEnd();
                double ratio = stickerLayer.getHeight() / (double) stickerBitmap.getHeight();
                int tileWidth = stickerLayer.getWidth() - (int) (stickerBitmap.getWidth() * (tileStart + 1 - tileEnd) * ratio);
                switch (drawingMode) {
                    case TILE_MODE_NONE:
                        // 等比缩放
                        mTileRect.set(
                                stickerLayer.getX(),
                                stickerLayer.getY(),
                                stickerLayer.getX() + stickerLayer.getWidth(),
                                stickerLayer.getY() + stickerLayer.getHeight()
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
                        );
                        mDstLeftTileRect.set(
                                stickerLayer.getX(),
                                stickerLayer.getY(),
                                stickerLayer.getX() + (int) (stickerBitmap.getWidth() * tileStart * ratio),
                                stickerLayer.getY() + stickerLayer.getHeight()
                        );
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

        return resultBitmap;
    }

    public static String getFileName(WatermarkItem item) {
        return WATERMARK_VIP_FILE_NAME;
    }
}
