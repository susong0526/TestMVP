package com.example.susong.testmvp.util.encrypt;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.jlhm.personal.utils.FileUtils;
import com.jlhm.personal.utils.LogUtils;
import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * A class for helping deal the bitmap,
 * like: get the orientation of the bitmap, compress bitmap etc.
 */
public class BitmapHelper {

    private static final String TAG = BitmapHelper.class.getSimpleName();

    public static int getDegress(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static int caculateInSampleSize(Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > height && width > rqsW) {//如果宽度大的话根据宽度固定大小缩放
            inSampleSize = (options.outWidth / rqsW);
        } else if (width < height && height > rqsH) {//如果高度高的话根据宽度固定大小缩放
            inSampleSize = (options.outHeight / rqsH);
        }
        if (inSampleSize <= 0) {
            inSampleSize = 1;
        }
        return inSampleSize;
    }

    /**
     * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
     * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int maxKb) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / 1024 > maxKb) {
                if (options <= 0) {
                    break;
                }
                baos.reset();
                bitmap.compress(CompressFormat.PNG, options, baos);
                options -= 10;
            }
            byte[] bts = baos.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
            baos.close();
            return bmp;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap compressBitmap(Bitmap bitmap, int rqsW, int rqsH) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        if(baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            bitmap.compress(CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Options newOpts = new Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > rqsW) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / rqsW);
        } else if (w < h && h > rqsH) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / rqsH);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap1;
    }

    public static String compressBitmap(String srcPath, int rqsW, int rqsH) {
        if (TextUtils.isEmpty(srcPath)) return null;
        FileOutputStream fos = null;
        File srcFile = new File(srcPath);
        String fileName = FileUtils.getTempCacheDir() + "/" + srcFile.getName();
        try {
            Options newOpts = new Options();
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float wRate = w / rqsW;
            float hRate = h / rqsH;
            int be = wRate > hRate ? Math.round(wRate) : Math.round(hRate);
            if (be < 1) be = 1;
            newOpts.inSampleSize = be;
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (null != bitmap) {
                bitmap.compress(CompressFormat.JPEG, 50, baos);
            }
            FileUtils.createFile(fileName);
            fos = new FileOutputStream(new File(fileName));
            fos.write(baos.toByteArray());
            fos.flush();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        } finally {
            try {
                if (null != fos) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 压缩某个输入流中的图片，可以解决网络输入流压缩问题，并得到图片对象
     *
     * @return Bitmap {@link Bitmap}
     */
    public static Bitmap compressBitmap(InputStream is, int reqsW, int reqsH) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ReadableByteChannel channel = Channels.newChannel(is);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) baos.write(buffer.get());
                buffer.clear();
            }
            byte[] bts = baos.toByteArray();
            Bitmap bitmap = compressBitmap(bts, reqsW, reqsH);
            is.close();
            channel.close();
            baos.close();
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap compressBitmap(byte[] bts, int reqsW, int reqsH) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
        options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
    }

    public static byte[] compressBitmap(int maxNumOfPixels, String imgpath) {
        double maxSize = 100.00;
        Bitmap bitmap = loadBitmap(maxNumOfPixels, imgpath);
        if (null != bitmap) {
            byte[] bBitmap = bitmap2Bytes(bitmap);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            double mid = bBitmap.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bBitmap = compressBitmap((int) (maxNumOfPixels / Math.abs(i)), imgpath);
            }
            return bBitmap;
        } else {
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        if (null == bitmap) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.size() / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            bitmap.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static Bitmap loadBitmap(int maxNumOfPixels, String imgpath) {
        Bitmap bitmap = null;
        try {
            FileInputStream f = new FileInputStream(new File(imgpath));
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgpath, options);
            // 调用上面定义的方法计算inSampleSize值
            if (0 == maxNumOfPixels) {
                maxNumOfPixels = 128 * 128;
            }
            options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(f, null, options);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String saveBitmapToFile(Bitmap bm, String fileName) throws IOException {
        try {
            String path = FileUtils.getTempCacheDir() + "/";
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File myCaptureFile = new File(path + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return myCaptureFile.getPath();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return null;
    }

    public static Bitmap resourcesToBitmap(Resources res, int resID, int reqsW, int reqsH) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resID, options);
        options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }

    /**
     * 压缩指定路径的图片，并得到图片对象
     */
    public static Bitmap fileToBitmap(String path, int rqsW, int rqsH) {
        try {
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return null;
    }

    /**
     * 压缩指定路径的图片，并得到图片对象
     */
    public static Bitmap fileToBitmap(String path) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

}
