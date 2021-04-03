package com.example.mymark.watermark.util;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.mymark.MyApplication;
import com.example.mymark.watermark.WatermarkUtil;
import com.example.mymark.watermark.model.WatermarkItem;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.mymark.watermark.WatermarkUtil.WATERMARK_CONFIG_FILE_NAME;
import static com.example.mymark.watermark.WatermarkUtil.WATERMARK_ICON_FILE_NAME;
import static com.example.mymark.watermark.WatermarkUtil.WATERMARK_NORMAL_FILE_NAME;
import static com.example.mymark.watermark.WatermarkUtil.WATERMARK_ROOT_DIR;
import static com.example.mymark.watermark.WatermarkUtil.WATERMARK_VIP_FILE_NAME;

public class WatermarkManager {
    private static WatermarkManager mInstance = null;
    public List<WatermarkItem> mVipWatermarkList = null;
    public List<WatermarkItem> mNormalWatermarkList = null;
    private HashMap<Long, Bitmap> mStickerHashMap = null;
    private static String TAG = "WatermarkManager";

    public static synchronized WatermarkManager getInstance() {
        if (mInstance == null) {
            mInstance = new WatermarkManager();
        }
        return mInstance;
    }

    public WatermarkManager() {
        mStickerHashMap = new HashMap<>();
    }

    public List<WatermarkItem> getVipWatermarkList() {
        if (mVipWatermarkList == null || mVipWatermarkList.size() == 0) {
            mVipWatermarkList = getWatermarkList(WATERMARK_VIP_FILE_NAME);
        }
        return mVipWatermarkList;
    }

    public List<WatermarkItem> getNormalWaterItemList() {
        if (mNormalWatermarkList == null || mNormalWatermarkList.size() == 0) {
            mNormalWatermarkList = getWatermarkList(WATERMARK_NORMAL_FILE_NAME);
        }
        return mNormalWatermarkList;
    }


    public List<WatermarkItem> getWatermarkList(String watermarkFileName){
        List<WatermarkItem> watermarkItemList = new ArrayList<>();
        String rootDir = MyApplication.appContext.getFilesDir().getAbsolutePath() + "/" + WATERMARK_ROOT_DIR + "/" +
                watermarkFileName + "/";
        Log.d(TAG, "getWatermarkList: " + rootDir);
        File watermarkRootDir = new File(rootDir);
        for (String fileName : watermarkRootDir.list()) {
            File watermarkFile = new File(rootDir + fileName);
            if (watermarkFile.isDirectory()) {
                File icon = new File(watermarkFile, WATERMARK_ICON_FILE_NAME);
                File config = new File(watermarkFile, WATERMARK_CONFIG_FILE_NAME);
                if (icon.exists() && config.exists()) {
                    String configStr = FileUtil.loadJsonFromFile(config);
                    WatermarkItem watermarkItem = new Gson().fromJson(
                            configStr, WatermarkItem.class);
                    Bitmap iconBitmap = BitmapFactory.decodeFile(icon.getAbsolutePath());
                    watermarkItem.setIcon(iconBitmap);
                    watermarkItemList.add(watermarkItem);
                }
            }
        }
        Collections.sort(watermarkItemList);
        return watermarkItemList;
    }


    public Bitmap getStickerBitmap(WatermarkItem item, String fileName) {
        Long key = item.getId();
        String fileDir = WatermarkUtil.getFileName(item);
        if (mStickerHashMap.containsKey(key)) {
            return mStickerHashMap.get(key);
        } else {
            String filePath = MyApplication.appContext.getFilesDir() + "/" + WATERMARK_ROOT_DIR + "/" + fileDir + "/" + item.getId() + "/";
            File file = new File(filePath, fileName);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                mStickerHashMap.put(key, bitmap);
                return bitmap;
            }
        }
        return null;
    }


    public WatermarkItem getWaterItemById(long id) {
        String itemId = String.valueOf(id);
        if (itemId.charAt(0) == '2') {
            for (int i = 0; i < getVipWatermarkList().size(); i++) {
                if (getVipWatermarkList().get(i).getId() == id) {
                    return getVipWatermarkList().get(i);
                }
            }
        } else if (itemId.charAt(0) == '1') {
            for (int i = 0; i < getNormalWaterItemList().size(); i++) {
                if (getNormalWaterItemList().get(i).getId() == id) {
                    return getNormalWaterItemList().get(i);
                }
            }
        }

        return getNormalWaterItemList().get(0);
    }
}
