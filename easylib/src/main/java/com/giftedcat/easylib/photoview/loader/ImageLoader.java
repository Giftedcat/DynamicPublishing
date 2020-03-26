package com.giftedcat.easylib.photoview.loader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.io.File;

/**
 * 图片加载器接口，实现 ImageLoader 可扩展自己的图片加载器
 * Created by hitomi on 2017/1/20.
 * <p>
 * email: 196425254@qq.com
 */
public interface ImageLoader {
    /**
     * 状态码，取消加载原图
     */
    int STATUS_DISPLAY_CANCEL = -1;
    /**
     * 状态码，加载原图失败
     */
    int STATUS_DISPLAY_FAILED = 0;
    /**
     * 状态码，加载原图成功
     */
    int STATUS_DISPLAY_SUCCESS = 1;

    /**
     * 加载并显示原图
     *
     * @param imageUrl       图片地址
     * @param imageView      用于图片加载成功后显示的 ImageView
     * @param placeholder    加载完成之前显示的占位图
     * @param sourceCallback 图片加载过程的回调
     */
    void showImage(String imageUrl, ImageView imageView, Drawable placeholder, @Nullable final SourceCallback sourceCallback);

    /**
     * 异步加载图片
     *
     * @param imageUrl 图片地址
     * @param callback 片加载完成的回调
     */
    void loadImageAsync(String imageUrl, final ThumbnailCallback callback);

    /**
     * 从本地同步加载图片（imageUrl 对应的图片是已经加载过的），返回 Bitmap
     */
    Bitmap loadImageSync(String imageUrl);

    /**
     * 获取 url 关联的图片缓存
     */
    File getCache(String url);

    /**
     * 清除 ImageLoader 缓存
     */
    void clearCache();

    interface SourceCallback {
        @UiThread
        void onStart();

        @UiThread
        void onProgress(int progress);

        @UiThread
        void onDelivered(int status, @Nullable File source);
    }

    interface ThumbnailCallback {
        @UiThread
        void onFinish(Bitmap bitmap);
    }
}
