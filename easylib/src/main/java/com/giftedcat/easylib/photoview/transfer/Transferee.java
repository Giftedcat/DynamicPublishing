package com.giftedcat.easylib.photoview.transfer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.KeyEvent;
import android.widget.AbsListView;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giftedcat.easylib.photoview.style.index.CircleIndexIndicator;
import com.giftedcat.easylib.photoview.style.progress.ProgressBarIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main workflow: <br/>
 * 1、点击缩略图展示缩略图到 transferee 过渡动画 <br/>
 * 2、显示下载高清图片进度 <br/>
 * 3、加载完成显示高清图片 <br/>
 * 4、高清图支持手势缩放 <br/>
 * 5、关闭 transferee 展示 transferee 到原缩略图的过渡动画 <br/>
 * Created by hitomi on 2017/1/19.
 * <p>
 * email: 196425254@qq.com
 */
public class Transferee implements DialogInterface.OnShowListener,
        DialogInterface.OnKeyListener,
        TransferLayout.OnLayoutResetListener {

    private Context context;
    private Dialog transDialog;

    private TransferLayout transLayout;
    private TransferConfig transConfig;
    private OnTransfereeStateChangeListener transListener;

    // 因为Dialog的关闭有动画延迟，固不能使用 dialog.isShowing, 去判断 transferee 的显示逻辑
    private boolean shown;

    /**
     * 构造方法私有化，通过{@link #getDefault(Context)} 创建 transferee
     *
     * @param context 上下文环境
     */
    private Transferee(Context context) {
        this.context = context;
        createLayout();
        createDialog();
    }

    /**
     * @param context
     * @return {@link Transferee}
     */
    public static Transferee getDefault(Context context) {
        return new Transferee(context);
    }

    private void createLayout() {
        transLayout = new TransferLayout(context);
        transLayout.setOnLayoutResetListener(this);
    }

    private void createDialog() {
        transDialog = new AlertDialog.Builder(context, getDialogStyle())
                .setView(transLayout)
                .create();
        transDialog.setOnShowListener(this);
        transDialog.setOnKeyListener(this);
    }

    /**
     * 兼容4.4以下的全屏 Dialog 样式
     *
     * @return The style of the dialog
     */
    private int getDialogStyle() {
        int dialogStyle;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            dialogStyle = android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
        } else {
            dialogStyle = android.R.style.Theme_Translucent_NoTitleBar;
        }
        return dialogStyle;
    }

    /**
     * 检查参数，如果必须参数缺少，就使用缺省参数或者抛出异常
     */
    private void checkConfig() {
        if (transConfig.isSourceEmpty())
            throw new IllegalArgumentException("The parameter sourceImageList can't be empty");

        if (transConfig.getImageLoader() == null)
            throw new IllegalArgumentException("Need to specify an ImageLoader");

        transConfig.setNowThumbnailIndex(transConfig.getNowThumbnailIndex() < 0
                ? 0 : transConfig.getNowThumbnailIndex());

        transConfig.setOffscreenPageLimit(transConfig.getOffscreenPageLimit() <= 0
                ? 1 : transConfig.getOffscreenPageLimit());

        transConfig.setDuration(transConfig.getDuration() <= 0
                ? 300 : transConfig.getDuration());

        transConfig.setProgressIndicator(transConfig.getProgressIndicator() == null
                ? new ProgressBarIndicator() : transConfig.getProgressIndicator());

        transConfig.setIndexIndicator(transConfig.getIndexIndicator() == null
                ? new CircleIndexIndicator() : transConfig.getIndexIndicator());
    }

    private void fillOriginImages() {
        List<ImageView> originImageList = new ArrayList<>();
        if (transConfig.getRecyclerView() != null) {
            fillByRecyclerView(originImageList);
        } else if (transConfig.getListView() != null) {
            fillByListView(originImageList);
        } else if (transConfig.getImageView() != null) {
            originImageList.add(transConfig.getImageView());
        }
        transConfig.setOriginImageList(originImageList);
    }

    private void fillByRecyclerView(final List<ImageView> originImageList) {
        RecyclerView recyclerView = transConfig.getRecyclerView();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView originImage = recyclerView.getChildAt(i)
                    .findViewById(transConfig.getImageId());
            if (originImage != null)
                originImageList.add(originImage);
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPos = 0, lastPos = 0;
        int totalCount = layoutManager.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayMan = (GridLayoutManager) layoutManager;
            firstPos = gridLayMan.findFirstVisibleItemPosition();
            lastPos = gridLayMan.findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linLayMan = (LinearLayoutManager) layoutManager;
            firstPos = linLayMan.findFirstVisibleItemPosition();
            lastPos = linLayMan.findLastVisibleItemPosition();
        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos);
    }

    private void fillByListView(final List<ImageView> originImageList) {
        AbsListView absListView = transConfig.getListView();
        int childCount = absListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView originImage = ((ImageView) absListView.getChildAt(i)
                    .findViewById(transConfig.getImageId()));
            originImageList.add(originImage);
        }

        int firstPos = absListView.getFirstVisiblePosition();
        int lastPos = absListView.getLastVisiblePosition();
        int totalCount = absListView.getCount();
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos);
    }

    private void fillPlaceHolder(List<ImageView> originImageList, int totalCount, int firstPos, int lastPos) {
        if (firstPos > 0) {
            for (int pos = firstPos; pos > 0; pos--) {
                originImageList.add(0, null);
            }
        }
        if (lastPos < totalCount) {
            for (int i = (totalCount - 1 - lastPos); i > 0; i--) {
                originImageList.add(null);
            }
        }
    }

    /**
     * 配置 transferee 参数对象
     *
     * @param config 参数对象
     * @return transferee
     */
    public Transferee apply(TransferConfig config) {
        if (!shown) {
            transConfig = config;
            fillOriginImages();
            checkConfig();
            transLayout.apply(config);
        }
        return this;
    }

    /**
     * transferee 是否显示
     *
     * @return true ：显示, false ：关闭
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * 显示 transferee
     */
    public void show() {
        if (shown) return;
        transDialog.show();
        if (transListener != null)
            transListener.onShow();

        shown = true;
    }

    /**
     * 显示 transferee, 并设置 OnTransfereeChangeListener
     *
     * @param listener {@link OnTransfereeStateChangeListener}
     */
    public void show(OnTransfereeStateChangeListener listener) {
        if (shown) return;
        transDialog.show();
        transListener = listener;
        transListener.onShow();

        shown = true;
    }

    /**
     * 关闭 transferee
     */
    public void dismiss() {
        if (!shown) return;
        transLayout.dismiss(transConfig.getNowThumbnailIndex());
        shown = false;
    }

    /**
     * 获取图片文件
     */
    public File getImageFile(String imageUri) {
        return transConfig.getImageLoader().getCache(imageUri);
    }

    /**
     * 清除 transferee 缓存
     */
    public void clear() {
        if (transConfig != null && transConfig.getImageLoader() != null) {
            transConfig.getImageLoader().clearCache();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        transLayout.show();
    }

    @Override
    public void onReset() {
        transDialog.dismiss();
        if (transListener != null)
            transListener.onDismiss();

        shown = false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP &&
                !event.isCanceled()) {
            dismiss();
        }
        return true;
    }

    /**
     * 设置 Transferee 显示和关闭的监听器
     *
     * @param listener {@link OnTransfereeStateChangeListener}
     */
    public void setOnTransfereeStateChangeListener(OnTransfereeStateChangeListener listener) {
        transListener = listener;
    }

    /**
     * Transferee 显示的时候调用 {@link OnTransfereeStateChangeListener#onShow()}
     * <p>
     * Transferee 关闭的时候调用 {@link OnTransfereeStateChangeListener#onDismiss()}
     */
    public interface OnTransfereeStateChangeListener {
        void onShow();

        void onDismiss();
    }

    public interface OnTransfereeLongClickListener {
        void onLongClick(ImageView imageView, String imageUri, int pos);
    }

}
