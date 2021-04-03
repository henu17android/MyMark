package com.example.mymark.util;


public class Util {


//    public Bitmap processImage(Context context, String fileName) {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
//        Mat srcMat = new Mat();
//        Mat dstMat = new Mat();
//        Utils.bitmapToMat(bitmap, srcMat);
//        Imgproc.cvtColor(srcMat, dstMat, Imgproc.COLOR_BGRA2GRAY); //读取灰度像素矩阵
//        Utils.matToBitmap(dstMat, bitmap);
//        srcMat.release();
//        dstMat.release();
//        return bitmap;
//    }
//
//    //将字符串转为2进制，不足8位的补齐
//    public static String toBinary(String str) {
//        char[] strChar=str.toCharArray();
//        StringBuilder stream = new StringBuilder();
//        for(int i = 0;i < strChar.length;i++){
//            String temp = Integer.toBinaryString(strChar[i]);
//            if (temp.length() < 8) {
//                temp = plus(temp);
//            }
//            stream.append(temp);
//        }
//        return stream.toString();
//    }
//
//    public static String plus(String temp) {
//        int len = 8 - temp.length();
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i <len; i++) {
//            result.append("0");
//        }
//        return result.append(temp).toString();
//    }
}
