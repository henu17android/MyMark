package com.example.mymark.watermark.util;

import com.example.mymark.watermark.model.FontItem;

import java.util.List;

public class FontUtil {

    public static final String FONT_DIR = "font";
    public static final String FONT_CONFIG_FILE = "fonts.json";
    public static final String DST_LANG_CONFIG = "languages.json";

    public static final long DEFAULT_FONT_ID = 0;
    public static final String DEFAULT_FONT_NAME = "FandolHei";
    public static final String DEFAULT_FONT_FILE_NAME = "default.otf";

    public static final String FONT_FILE_POSTFIX_OTF = ".otf";
    public static final String FONT_FILE_POSTFIX_TTF = ".ttf";

    private static final double DEFAULT_FONT_ASCENT = 0.1719;
    private static final double DEFAULT_FONT_DESCENT = 0.0781;

    private static FontUtil mInstance = null;
    //private List<FontFamilyEntity> mFontFamilyEntityList = null;
    private List<FontItem> mFontItemList = null;
    private List<FontItem> mDstLangFontItemList = null;

    private final static String TAG = "FontUtil";

    public FontUtil() {}

    public static synchronized FontUtil getInstance() {
        if (mInstance == null) {
            mInstance = new FontUtil();
        }
        return mInstance;
    }

}
