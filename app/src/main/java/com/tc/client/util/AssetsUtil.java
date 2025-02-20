package com.tc.client.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtil {

    public static String readAssetFileAsString(Context context, String path) {
        InputStream in = null;
        try {
            in = context.getAssets().open(path);
            if (in == null) {
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readSize = 0;
            readSize = in.read(buffer);
            while (readSize != -1) {
                baos.write(buffer, 0, readSize);
                readSize = in.read(buffer);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void copyData(Context context, String srcPath, String targetPath) {
        AssetManager am = context.getAssets();
        InputStream in = null;
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(targetPath);
            in = am.open(srcPath);
            byte[] buffer = new byte[1024];
            int readSize = 0;
            while ((readSize = in.read(buffer)) != -1) {
                fos.write(buffer, 0, readSize);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Closer.close(in);
            Closer.close(fos);
        }
    }
}
