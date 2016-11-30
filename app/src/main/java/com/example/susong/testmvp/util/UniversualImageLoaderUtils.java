package com.example.susong.testmvp.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.susong.testmvp.R;
import com.example.susong.testmvp.widget.TopRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Android-Universal-ImageLoader 工具类
 */
public class UniversualImageLoaderUtils {

    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/imgs");
        BaseDiskCache baseDiscCache = new UnlimitedDiskCache(cacheDir);
        BaseImageDownloader baseImageDownloader = new BaseImageDownloader(context);
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration
                .Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) //50Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(baseDiscCache)
                .imageDownloader(baseImageDownloader); //Remove for release app
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .considerExifParams(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public static DisplayImageOptions getDisplayImageOptions(boolean considerExifParams) {
        return new DisplayImageOptions
                .Builder()
                .considerExifParams(considerExifParams)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static DisplayImageOptions getDisplayImageOptions(final ImageSize targetImageSize) {
        return new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .considerExifParams(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return Bitmap.createScaledBitmap(bitmap, targetImageSize.getWidth(), targetImageSize.getHeight(), false);
                    }
                }).build();
    }

    public static DisplayImageOptions getRoundCornerImageOptions(final ImageSize targetImageSize, int dpCorners) {
        return new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .considerExifParams(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return Bitmap.createScaledBitmap(bitmap, targetImageSize.getWidth(), targetImageSize.getHeight(), false);
                    }
                })
                .displayer(new RoundedBitmapDisplayer(DensityUtils.dip2px(dpCorners), 0)).build();
    }

    public static DisplayImageOptions getRoundCornerImageOptions(int dpCorners) {
        return new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .considerExifParams(false)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(DensityUtils.dip2px(dpCorners), 0)).build();
    }

    public static DisplayImageOptions getTopRoundCornerImageOptions(int dpCorners) {
        return new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnLoading(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .considerExifParams(false)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .displayer(new TopRoundedBitmapDisplayer(DensityUtils.dip2px(dpCorners), 0)).build();
    }

    /**
     * 获取本地资源图片的Uri地址 Android-Universal-Image-Loader支持
     */
    public static String generateLocalResImgUri(int resID) {
        return "drawable://" + resID;
    }

    public static String generateAssetsImgUri(String assetFileName) {
        return "assets://" + assetFileName;
    }
}