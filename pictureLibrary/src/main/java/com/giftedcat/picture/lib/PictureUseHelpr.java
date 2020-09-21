package com.giftedcat.picture.lib;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.giftedcat.picture.lib.photoview.GlideImageLoader;
import com.giftedcat.picture.lib.photoview.style.index.NumberIndexIndicator;
import com.giftedcat.picture.lib.photoview.style.progress.ProgressBarIndicator;
import com.giftedcat.picture.lib.photoview.transfer.TransferConfig;
import com.giftedcat.picture.lib.photoview.transfer.Transferee;
import com.giftedcat.picture.lib.selector.MultiImageSelector;

import java.util.List;

public class PictureUseHelpr {

    private Context context;

    private Activity instans;

    /** 选择图片的集合*/
    private List<String> mSelect;

    /** 最大图片数量*/
    private int maxNum = 9;

    protected Transferee transferee;
    protected TransferConfig config;

    public PictureUseHelpr(Activity instans) {
        this.instans = instans;
        this.context = instans;
    }

    public static PictureUseHelpr init(Activity instans){
        return new PictureUseHelpr(instans);
    }

    public PictureUseHelpr setMaxNum(int maxNum){
        this.maxNum = maxNum;
        return this;
    }

    public PictureUseHelpr origin(List<String> images){
        this.mSelect = images;
        return this;
    }

    public void show(int position){
        config.setNowThumbnailIndex(position);
        config.setSourceImageList(mSelect);
        transferee.apply(config).show();
    }

    /**
     * 初始化大图查看控件
     */
    public PictureUseHelpr bindRecyclerView(RecyclerView recyclerView, int imageId){
        transferee = Transferee.getDefault(context);
        config = TransferConfig.build()
                .setSourceImageList(mSelect)
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(GlideImageLoader.with(context.getApplicationContext()))
                .setJustLoadHitImage(true)
                .bindRecyclerView(recyclerView, imageId);
        return this;
    }


    /**
     * 选择添加图片
     */
    public void pickImage(int requestCode) {
        MultiImageSelector selector = MultiImageSelector.create(context);
        selector.showCamera(true);
        selector.count(maxNum);
        selector.multi();
        selector.origin(mSelect);
        selector.start(instans, requestCode);
    }

}
