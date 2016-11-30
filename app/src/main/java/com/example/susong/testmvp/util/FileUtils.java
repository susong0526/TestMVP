package com.example.susong.testmvp.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.example.susong.testmvp.C;
import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.cache.SecondLevelCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public final class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String IMAGE_FILE_SUFFIX = ".jpeg";
    private static final String IMAGE_GIF_SUFFIX = ".gif";
    private static final String VIDEO_FILE_SUFFIX = ".mp4";
    private static final String AUDIO_FILE_SUFFIX = ".amr";
    public static final String TEXT_FILE_SUFFIX = ".txt";
    private static final Md5FileNameGenerator MD5_FILE_NAME_GENERATOR = new Md5FileNameGenerator();
    private static final String VIDEO_DIR_NAME = MD5_FILE_NAME_GENERATOR.generate("video"); //video
    private static final String AUDIO_DIR_NAME = MD5_FILE_NAME_GENERATOR.generate("audio"); //audio
    private static final String TEXT_DIR_NAME = MD5_FILE_NAME_GENERATOR.generate("text"); //text
    private static final String SPLASH_DIR_NAME = MD5_FILE_NAME_GENERATOR.generate("splash"); //splash
    private static final String RC_CAMERA_PHOTO = MD5_FILE_NAME_GENERATOR.generate("rc_camera_photo"); //splash
    private static final String TEMP_DIR_NAME = MD5_FILE_NAME_GENERATOR.generate("temp"); //temp
    private static String CROP_FILENAME = "cropped.png";

    /**
     * 获取应用图片缓存目录
     */
    public static String getImageCacheDir() {
        return ImageLoader.getInstance().getDiskCache().getDirectory().getAbsolutePath();
    }

    /**
     * 获取闪屏页图片
     *
     * @return
     */
    public static String getSplashCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f = null;
        if (C.DEBUG) {
            f = new File(cacheDir + "/splash");
        } else {
            f = new File(cacheDir + "/" + SPLASH_DIR_NAME);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取融云拍摄照片目录
     *
     * @return
     */
    public static String getRcCameraPhotoCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f = null;
        if (C.DEBUG) {
            f = new File(cacheDir + "/rc_camera_photo");
        } else {
            f = new File(cacheDir + "/" + RC_CAMERA_PHOTO);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取应用音频缓存目录
     */
    public static String getAudioCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f = null;
        if (C.DEBUG) {
            f = new File(cacheDir + "/audio");
        } else {
            f = new File(cacheDir + "/" + AUDIO_DIR_NAME);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取应用视频缓存目录
     */
    public static String getVideoCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f = null;
        if (C.DEBUG) {
            f = new File(cacheDir + "/video");
        } else {
            f = new File(cacheDir + "/" + VIDEO_DIR_NAME);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取应用文本文件缓存目录
     */
    public static String getTextCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f;
        if (C.DEBUG) {
            f = new File(cacheDir + "/text");
        } else {
            f = new File(cacheDir + "/" + TEXT_DIR_NAME);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 临时缓存文件夹，自己拍的照片和视频
     */
    public static String getTempCacheDir() {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory().getParentFile();
        File f;
        if (C.DEBUG) {
            f = new File(cacheDir + "/temp");
        } else {
            f = new File(cacheDir + "/" + TEMP_DIR_NAME);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * 创建临时文件的文件名； 文件名由时间戳组成 + 文件后缀组成
     *
     * @param fileType 文件类型
     * @return
     */
    public static String generateTempFilePath(C.FileType fileType) {
        String filePath = "";
        switch (fileType) {
            case AUDIO:
                filePath = getTempCacheDir() + File.separator + System.currentTimeMillis() + AUDIO_FILE_SUFFIX;
                break;
            case IMAGE:
                filePath = getTempCacheDir() + File.separator + System.currentTimeMillis() + IMAGE_FILE_SUFFIX;
                break;
            case VIDEO:
                filePath = getTempCacheDir() + File.separator + System.currentTimeMillis() + VIDEO_FILE_SUFFIX;
                break;
            case TEXT:
                filePath = getTempCacheDir() + File.separator + System.currentTimeMillis() + TEXT_FILE_SUFFIX;
                break;
        }
        return filePath;
    }

    /**
     * 根据文件完整路径获取文件名
     *
     * @param tempFilePath 临时文件的完整文件名
     */
    public static String getTempFileName(String tempFilePath) {
        String fileName = tempFilePath.substring(tempFilePath.lastIndexOf(File.separator) + 1);
        return fileName;
    }

    /**
     * 根据文件获取文件名
     *
     * @param tempFile 临时文件对象
     */
    public static String getTempFileName(File tempFile) {
        if (tempFile != null) {
            return getTempFileName(tempFile.getAbsolutePath());
        } else {
            return null;
        }
    }

    /**
     * 复制文件
     *
     * @param srcPath   源文件路径
     * @param destPath  目标文件路径
     * @param deleteSrc 是否删除源文件
     */
    public static boolean copyFile(String srcPath, String destPath, boolean deleteSrc) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        return copyFile(srcFile, destFile, deleteSrc);
    }

    /**
     * 复制文件
     *
     * @param srcFile   源文件
     * @param destFile  目标文件
     * @param deleteSrc 是否删除源文件
     */
    public static boolean copyFile(File srcFile, File destFile, boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
            return false;
        } finally {
            close(out);
            close(in);
        }
        return true;
    }

    /**
     * 根据给定的Uri字符串获取对应的文件路径
     *
     * @param uriString 文件的uri字符
     */
    public static String getFilePathByUriString(String uriString) {
        return getFilePathByUri(Uri.parse(uriString));
    }

    /**
     * 根据给定的Uri获取对应的文件路径
     */
    public static String getFilePathByUri(Uri uri) {
        String path = uri.getPath();
        File f = new File(path);
        if (f.exists()) {
            return path;
        }
        return null;
    }

    /**
     * 用字符串生成文件
     */
    public static boolean string2File(String content, String targetFile) {
        return bytesArray2File(content.getBytes(), targetFile);
    }

    /**
     * 字节数组转文件
     *
     * @param content  字节内容
     * @param filePath 目标文件路径
     * @return
     */
    public static boolean bytesArray2File(byte[] content, String filePath) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(content);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.e(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 文件转字符串
     *
     * @param sourceFilePath
     * @return
     */
    public static String file2String(String sourceFilePath) {
        byte[] fileData = null;
        FileInputStream fileInputStream = null;
        File file = new File(sourceFilePath);
        try {
            fileInputStream = new FileInputStream(sourceFilePath);
            int length = (int) file.length();
            fileData = new byte[length];
            int readCount = 0; // 已经成功读取的字节的个数
            while (readCount < length) {
                readCount += fileInputStream.read(fileData, readCount, length - readCount);
            }
            fileInputStream.close();
            return new String(fileData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件转字符串
     *
     * @return
     */
    public static String stream2String(InputStream is) {
        byte[] buffer = new byte[1024];
        int len = -1;
        StringBuffer sb = new StringBuffer();
        try {
            while ((len = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存文件到内部存储器
     */
    public static void saveFileToInternalStorage(String fileName, String data) {
        SecondLevelCache.sharedInstance().put(fileName, data);
//        if (StringUtil.isEmpty(data)) {
//            return;
//        }
//        File f = new File(FileUtils.getTempCacheDir(), fileName);
//        try {
//            data = URLEncoder.encode(data);
//            FileOutputStream fos = new FileOutputStream(f);
//            fos.write(data.getBytes());
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            if (f != null && f.exists()) {
//                f.delete();
//            }
//        }
    }

    /**
     * 从内部存储器删除文件
     */
    public static void deleteFileFromInternalStorage(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            File f = new File(FileUtils.getTempCacheDir(), fileName);
            if (null != f && f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * 从文件中获取字符串
     */
    public static String getStringFromInternalStorage(String fileName) {
//        File f = new File(FileUtils.getTempCacheDir(), fileName);
//        if (f.exists()) {
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream(f);
//                byte[] buffer = new byte[1024];
//                int len;
//                StringBuffer sb = new StringBuffer();
//                while ((len = fis.read(buffer)) != -1) {
//                    sb.append(new String(buffer, 0, len));
//                }
//                return URLDecoder.decode(sb.toString());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (null != fis) {
//                    try {
//                        fis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return null;
        Object tempObj = SecondLevelCache.sharedInstance().get(fileName);
        if (null != tempObj && !TextUtils.isEmpty((String)tempObj)) {
            return tempObj.toString();
        }
        return null;
    }

    /**
     * 关闭流
     */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                Logger.e(e.getMessage());
            }
        }
        return true;
    }

    public static void renameFile(File sourceFile, String newFileName) {
        if (sourceFile != null && sourceFile.exists()) {
            String parent = sourceFile.getParent();
            File newFile = new File(parent, newFileName);
            sourceFile.renameTo(newFile);
        }
    }

    /**
     * 截图文件
     */
    public static File generateCropFile(Context context) {
        return new File(context.getCacheDir(), CROP_FILENAME);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean deleteFiles(File file) {
        return false;
    }

    public synchronized static Object readSerObjectFromFile(String fileName) {
        Object b;
        ObjectInputStream in = null;
        File file = new File(fileName);
        if (!file.exists()) return null;
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));
            b = in.readObject();
            return b;
        } catch (Exception e) {
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

    public static boolean saveSerObjectToFile(Object object, String fileName) {
        ObjectOutputStream out = null;
        try {
            createFile(fileName);
            out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public synchronized static File createFile(String filePath) throws Exception {
        File file = new File(filePath);
        String parent = file.getParent();
        File parentFile = new File(parent);
        if (!parentFile.exists()) {
            if (parentFile.mkdirs()) {
                file.createNewFile();
            } else {
                throw new IOException("文件创建失败");
            }
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }

    // 统一将文件保存到缓存文件夹中
    // 原因: Android 6.0必须先获取写入内存卡权限才能往内存卡中写入数据
    public static String getRootPath() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return A.instance.getCacheDir().getAbsolutePath();
    }
}
