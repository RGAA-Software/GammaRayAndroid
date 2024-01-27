//package com.tc.client;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ImageLoader {
//
//    private static final String TAG = "Image";
//
//    static {
//        //OpenCVLoader.initDebug();
//    }
//
//    private Context mContext;
//
//    public ImageLoader(Context context) {
//        mContext = context;
//    }
//
//    public Image loadFromAssets(String path, boolean flipY) {
////        return loadFromAssets(path, flipY, new Size(1.0f, 1.0f), 0);
//        return null;
//    }
//
//    public Image loadFromAssets(String path) {
////        return loadFromAssets(path, true, new Size(1.0f, 1.0f), 0);
//        return null;
//    }
//
////    public Image loadFromAssets(String path, boolean flipY, Size scale, float blurKernel) {
////        InputStream in = null;
////        try {
////            in = mContext.getAssets().open(path);
////        } catch (IOException e) {
////            e.printStackTrace();
////            return null;
////        }
////        return loadImage(in, flipY, scale, blurKernel);
////    }
//
////    public Image loadFromInternalStorage(String path) {
////        return loadFromInternalStorage(path, true, new Size(1,1), 0);
////    }
//
//    public Image loadFromInternalStorage(String path, boolean flipY, Size scale, float blurKernel) {
//        InputStream in = null;
//        try {
//            in = new FileInputStream(path);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return loadImage(in, flipY, scale, blurKernel);
//    }
//
//    private Image loadImage(InputStream in, boolean flipY, Size scale, float blurKernel) {
//        try {
//            Bitmap bitmap = BitmapFactory.decodeStream(in);
//            if (bitmap == null) {
//                return null;
//            }
//
//            int width = 0;
//            int height = 0;
//            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
//            if (flipY) {
//                Mat mat = new Mat();
//                Utils.bitmapToMat(bitmap, mat);
//                Core.flip(mat, mat, 0);
//
//                Mat resizeMat = new Mat();
//                Imgproc.resize(mat, resizeMat, new Size(), scale.width, scale.height);
//
//                if (blurKernel != 0) {
//                    blur(resizeMat, blurKernel);
//                }
//
//                Bitmap flipBitmap = Bitmap.createBitmap(
//                        resizeMat.cols(), resizeMat.rows(), Bitmap.Config.ARGB_8888);
//
//                Utils.matToBitmap(resizeMat, flipBitmap);
//
//                width = flipBitmap.getWidth();
//                height = flipBitmap.getHeight();
//
//                flipBitmap.copyPixelsToBuffer(buffer);
//                flipBitmap.recycle();
//            } else {
//                width = bitmap.getWidth();
//                height = bitmap.getHeight();
//                bitmap.copyPixelsToBuffer(buffer);
//            }
//            buffer.position(0);
//
//            bitmap.recycle();
//
//            return new Image(width, height, 4, buffer);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
//
//    public List<Image> loadAnimFromAssets(String path, int cols, int rows) {
//        List<Image> images = new ArrayList<>();
//        InputStream in = null;
//        try {
//            in = mContext.getAssets().open(path);
//            if (in == null) {
//                return null;
//            }
//
//            Bitmap bitmap = BitmapFactory.decodeStream(in);
//            if (bitmap == null) {
//                return null;
//            }
//
//            int originWidth = bitmap.getWidth();
//            int originHeight = bitmap.getHeight();
//            int sliceWidth = originWidth/cols;
//            int sliceHeight = originHeight/rows;
//
//            for (int r = 0; r < rows; r++) {
//                for (int c = 0; c < cols; c++) {
//                    Bitmap item = Bitmap.createBitmap(bitmap, c*sliceWidth, r*sliceHeight, sliceWidth, sliceHeight);
//
//                    Mat mat = new Mat();
//                    Utils.bitmapToMat(item, mat);
//                    Core.flip(mat, mat, 0);
//                    Bitmap flipBitmap = Bitmap.createBitmap(
//                            sliceWidth, sliceHeight, Bitmap.Config.ARGB_8888);
//
//                    Utils.matToBitmap(mat, flipBitmap);
//
//                    ByteBuffer buffer = ByteBuffer.allocate(flipBitmap.getByteCount());
//                    flipBitmap.copyPixelsToBuffer(buffer);
//                    buffer.position(0);
//
//                    item.recycle();
//                    flipBitmap.recycle();
//
//                    Image image = new Image(sliceWidth, sliceHeight, 4, buffer);
//                    images.add(image);
//                }
//            }
//            bitmap.recycle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        return images;
//    }
//
//    private void blur(Mat in, float radius) {
//        Imgproc.blur(in, in, new Size(radius, radius));
//    }
//
//}
