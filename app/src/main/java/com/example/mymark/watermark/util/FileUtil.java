package com.example.mymark.watermark.util;



import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.apache.commons.io.FileUtils.deleteDirectory;

public class FileUtil {


    public static void unzip (File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    public static String loadJsonFromFile(File file) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            InputStream inputStream = FileUtils.openInputStream(file);
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public static boolean copyDirFromAssetsToDataData (Context context, String dirName,
                                                       boolean deleteExisting){
        if (dirName == null) return false;
        try {
            File dstDir = new File(context.getFilesDir().getAbsolutePath() + "/" + dirName);
            if (dstDir.exists() && dstDir.isDirectory()) {
                if (deleteExisting) {
                    deleteDirectory(dstDir);
                    dstDir.mkdir();
                }
            } else {
                dstDir.mkdir();
            }

            String[] fileList = context.getAssets().list(dirName);
            if (fileList == null) return false;
            for (String fileName : fileList) {
                if (!copyFileFromAssetsToDataData(context, dirName, fileName))
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将文件copy到data目录下
     * @param context
     * @param dirName
     * @param fileName
     */
    public static boolean copyFileFromAssetsToDataData (Context context, String dirName, String
            fileName){
        boolean copySuccessful = false;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte buffer[] = new byte[8192];
        int length = 0;
        try {
            inputStream = context.getAssets().open(dirName + "/" + fileName);
            File dstDir = new File(context.getFilesDir().getAbsolutePath() + "/" + dirName);
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }

            File dstFile = new File(context.getFilesDir().getAbsolutePath() + "/" + dirName, fileName);
            outputStream = new FileOutputStream(dstFile);
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            copySuccessful = true;
        } catch (Exception e) {
            copySuccessful = false;
            e.printStackTrace();
        }
        return copySuccessful;
    }
}
