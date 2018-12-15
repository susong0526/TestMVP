package com.example.susong.testmvp.widget;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.susong.testmvp.C;
import com.example.susong.testmvp.R;
import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.base.activity.ActivityBaseCompat;
import com.example.susong.testmvp.cache.MemoryCache;
import com.example.susong.testmvp.entity.domain.ImageVO;
import com.example.susong.testmvp.util.CrashReporterUtils;
import com.example.susong.testmvp.util.DensityUtil;
import com.example.susong.testmvp.util.FileUtils;
import com.example.susong.testmvp.util.UniversualImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 图片选择Activity
 *
 * @author scott
 */
public class ImageSelector extends ActivityBaseCompat implements AdapterView.OnItemClickListener {
    private GridView mGridView;
    // 每行展示列数
    private final int NUMBER_COLUMNS = 3;
    // 图片间隔(单位: dp)
    private final int IMAGE_SPACE = 5;
    // 图片适配器
    private ImageAdapter mAdapter;
    // 选中的图片
    public static final String KEY_SELECTED_IMAGE = "selected_image";
    // 临时照片保存路径
    private final String PATH_TEMP_CAPTURE_IMAGE = FileUtils.getTempCacheDir();
    private File mCaptureFile;
    // 拍照
    private final int REQUEST_CODE_TAKE_PICTURE = 0x100;
    // 图片任务
    private Looper mImageLooper;
    private ImageHandler mImageHandler;
    // 删除缓存图片
    private final int MESSAGE_WHAT_DELETE_TEMP_IMG = 1;
    // 从数据库获取图片
    private final int MESSAGE_WHAT_READ_FROM_PROVIDER = 2;
    // 刷新图片数据
    private final int MESSAGE_WHAT_REFRESH_IMAGE = 3;
    // 申请权限
    private final int REQUEST_PERMISSION_CAMERA = 0x1;

    private final class ImageHandler extends Handler {
        public ImageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_WHAT_DELETE_TEMP_IMG: {
                    File tempFileDir = new File(PATH_TEMP_CAPTURE_IMAGE);
                    File[] tempFiles = tempFileDir.listFiles();
                    if (null != tempFiles && tempFileDir.length() > 0) {
                        for (File tempFile : tempFiles) {
                            if (System.currentTimeMillis() - tempFile.lastModified() > 1000 * 60 * 60) {
                                tempFile.delete();
                            }
                        }
                    }
                    break;
                }
                case MESSAGE_WHAT_READ_FROM_PROVIDER: {
                    final ArrayList<ImageVO> images = readFromProvider();
                    MemoryCache.sharedInstance().put(C.KEY_GALLERY_IMAGES, images);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == mAdapter) {
                                mAdapter = new ImageAdapter(images);
                                mGridView.setAdapter(mAdapter);
                            } else {
                                mAdapter.setImages(images);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                }
                case MESSAGE_WHAT_REFRESH_IMAGE: {
                    readFromProvider();
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mImageLooper) {
            mImageLooper.quit();
            mImageLooper = null;
        }
    }

    // 图片数据初始化
    public void initialize() {
        HandlerThread handlerThread = new HandlerThread("Image Task");
        handlerThread.start();
        mImageLooper = handlerThread.getLooper();
        mImageHandler = new ImageHandler(mImageLooper);
        mImageHandler.sendEmptyMessage(MESSAGE_WHAT_DELETE_TEMP_IMG);
    }

    private void initView() {
        if (null == mGridView) {
            mGridView = new GridView(this);
        }
        mGridView.setNumColumns(NUMBER_COLUMNS);
        mGridView.setVerticalSpacing(DensityUtil.dip2px(IMAGE_SPACE));
        mGridView.setHorizontalSpacing(DensityUtil.dip2px(IMAGE_SPACE));
        mGridView.setOnItemClickListener(this);
        setContentView(mGridView);
        setTitle(R.string.selecte_image);
        loadImages();
    }

    // 加载系统图片到缩略图
    private void loadImages() {
        ArrayList<ImageVO> images = (ArrayList<ImageVO>) MemoryCache.sharedInstance().get(C.KEY_GALLERY_IMAGES);
        if (null == images) {
            mImageHandler.sendEmptyMessage(MESSAGE_WHAT_READ_FROM_PROVIDER);
        } else {
            mAdapter = new ImageAdapter(images);
            mGridView.setAdapter(mAdapter);
            mImageHandler.sendEmptyMessage(MESSAGE_WHAT_REFRESH_IMAGE);
        }
    }

    // 从数据库获取
    private ArrayList<ImageVO> readFromProvider() {
        ContentResolver resolver = getContentResolver();
        String[] columns;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            columns = new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT};
        } else {
            columns = new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN};
        }
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        final ArrayList<ImageVO> images = new ArrayList<>();
        while (null != cursor && cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            long date_taken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
            int width = 0;
            int height = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
                height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
            } else {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    width = options.outWidth;
                    height = options.outHeight;
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReporterUtils.postCatchedException(e);
                }
            }
            ImageVO img = new ImageVO();
            img.setDateTaken(date_taken);
            img.setPath(path);
            img.setWidth(width);
            img.setHeight(height);
            if (onImageFilter(img)) {
                images.add(img);
            }
        }
        if (null != cursor) {
            cursor.close();
        }
        MemoryCache.sharedInstance().put(C.KEY_GALLERY_IMAGES, images);
        return images;
    }

    // 图片过滤
    protected boolean onImageFilter(ImageVO image) {
        if (null == image || TextUtils.isEmpty(image.getPath())) return false;
        if (image.getPath().endsWith(".gif")) return false;
        // 过滤掉小图片
        if (image.getWidth() < 200 && image.getHeight() < 200) return false;
        return true;
    }

    // 拍摄照片
    private void takePicture() {
        // 拍摄照片
        String fileName = "temp_" + System.currentTimeMillis() + ".jpeg";
        try {
            FileUtils.createFile(PATH_TEMP_CAPTURE_IMAGE + File.separator + fileName);
            mCaptureFile = new File(PATH_TEMP_CAPTURE_IMAGE, fileName);
            Uri uri = Uri.fromFile(mCaptureFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (0 == position) {
            // Android 6.0需要进行权限检查
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!PermissionUtil.isGranted(this, android.Manifest.permission.CAMERA)) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
//                        showYesNoDialog(getString(R.string.to_setting), getString(R.string.cancel), getString(R.string.tip_grant_camera_permission), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (DialogInterface.BUTTON_POSITIVE == which) {
//                                    try {
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + Application.instance.getPackageName()));
//                                        startActivity(intent);
//                                    } catch (Exception e) {
//
//                                    }
//                                } else {
//                                    dialog.dismiss();
//                                }
//                            }
//                        });
//                    } else {
//                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
//                    }
//                    return;
//                }
            }
            takePicture();
        } else {
            ImageVO image = (ImageVO) mAdapter.getItem(position);
            Intent data = new Intent();
            data.putExtra(KEY_SELECTED_IMAGE, image);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    // 构建Convert BaseView
    private ImageView buildConvertView() {
        ImageView imageView = new ImageView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setBackground(getDrawable(R.drawable.setting_item_view_bg));
        } else {
            imageView.setBackgroundResource(R.drawable.setting_item_view_bg);
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setClickable(true);
        int padding = DensityUtil.dip2px(5);
        imageView.setPadding(padding, padding, padding, padding);
        DisplayMetrics dm = A.instance.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        GridView.LayoutParams lp = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (width - (NUMBER_COLUMNS + 1) * IMAGE_SPACE) / NUMBER_COLUMNS);
        imageView.setLayoutParams(lp);
        return imageView;
    }

    private class ImageAdapter extends BaseAdapter {
        private ArrayList<ImageVO> images;

        public ImageAdapter(ArrayList<ImageVO> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = buildConvertView();
            }
            ImageVO img = images.get(position);
            if (0 == position) {
                ((ImageView) convertView).setImageResource(R.drawable.image_selector_camera);
            } else {
                loadThumbnail(img, (ImageView) convertView);
            }
            return convertView;
        }

        // 显示缩略图
        private void loadThumbnail(ImageVO img, ImageView imageView) {
            String path = img.getPath();
            if (!TextUtils.isEmpty(path)) {
                DisplayMetrics dm = A.instance.getResources().getDisplayMetrics();
                int width = dm.widthPixels;
                float sizeMultiplier = 0.1f;
                int targetWidth = (width - (NUMBER_COLUMNS + 1) * IMAGE_SPACE) / NUMBER_COLUMNS;
                int targetHeight = targetWidth;
                int imgWidth = img.getWidth();
                int imgHeight = img.getHeight();
                if (imgWidth > 0 && imgHeight > 0) {
                    float widthMultiplier = (float) targetWidth / (float) imgWidth;
                    float heightMultiplier = (float) targetHeight / (float) imgHeight;
                    sizeMultiplier = widthMultiplier > heightMultiplier ? widthMultiplier : heightMultiplier;
                    if (sizeMultiplier > 1.0f) sizeMultiplier = 1.0f;
                }
//                Glide.with(ImageSelector.this)
//                        .load(path)
//                        .thumbnail(sizeMultiplier)
//                        .bitmapTransform(new RoundedCornersTransformation(ImageSelector.this, DensityUtil.dip2px(10), 0))
//                        .centerCrop()
//                        .crossFade()
//                        .into(imageView);
                ImageLoader.getInstance().displayImage("file://" + path, imageView, UniversualImageLoaderUtils.getRoundCornerImageOptions(DensityUtil.dip2px(5)));
            }
        }

        public void setImages(ArrayList<ImageVO> images) {
            this.images = images;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_TAKE_PICTURE == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null != mCaptureFile && mCaptureFile.exists()) {
//                    try {
//                        MediaStore.Images.Media.insertImage(Application.instance.getContentResolver(), mCaptureFile.getAbsolutePath(), mCaptureFile.getName(), null);
//                        Application.instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mCaptureFile.getName())));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    ImageVO img = new ImageVO();
                    img.setPath(mCaptureFile.getAbsolutePath());
                    img.setDateTaken(mCaptureFile.lastModified());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCaptureFile.getPath(), options);
                    img.setWidth(options.outWidth);
                    img.setHeight(options.outHeight);
                    Intent resultData = new Intent();
                    resultData.putExtra(KEY_SELECTED_IMAGE, img);
                    setResult(RESULT_OK, resultData);
                    finish();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 申请摄像头权限
        if (REQUEST_PERMISSION_CAMERA == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }
    }
}
