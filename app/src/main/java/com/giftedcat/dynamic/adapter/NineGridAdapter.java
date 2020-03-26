package com.giftedcat.dynamic.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.giftedcat.dynamic.R;
import com.giftedcat.dynamic.listener.OnAddPicturesListener;
import com.giftedcat.easylib.photoview.GlideImageLoader;
import com.giftedcat.easylib.photoview.style.index.NumberIndexIndicator;
import com.giftedcat.easylib.photoview.style.progress.ProgressBarIndicator;
import com.giftedcat.easylib.photoview.transfer.TransferConfig;
import com.giftedcat.easylib.photoview.transfer.Transferee;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;

public class NineGridAdapter extends CommonAdapter<String> {

    /**
     * 删除小弹窗
     */
    PopupMenuView menuView;

    private Context context;

    OnAddPicturesListener listener;

    private int deletePosition;

    protected Transferee transferee;

    protected TransferConfig config;

    public NineGridAdapter(Context context, List<String> selectPath, RecyclerView rvImages) {
        super(context, R.layout.item_img, selectPath);
        this.context = context;

        selectPath.add("");
        initDeleteMenu();
        initTransfer(rvImages);
    }

    /**
     * 设置最大图片数量
     * */
    public void setMaxSize(int maxNum) {
        config.setMax(maxNum);
    }

    /**
     * 设置点击添加按钮的监听
     */
    public void setOnAddPicturesListener(OnAddPicturesListener listener) {
        this.listener = listener;
    }


    /**
     * 初始化大图查看控件
     */
    private void initTransfer(RecyclerView rvImages) {
        transferee = Transferee.getDefault(context);
        config = TransferConfig.build()
                .setSourceImageList(getDatas())
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(GlideImageLoader.with(context.getApplicationContext()))
                .setJustLoadHitImage(true)
                .bindRecyclerView(rvImages, R.id.iv_thum);
    }

    /**
     * 初始化图片删除小弹窗
     */
    private void initDeleteMenu() {
        menuView = new PopupMenuView(context, R.menu.menu_pop, new MenuBuilder(context));
        menuView.setSites(PopupView.SITE_TOP);
        menuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {
                getDatas().remove(deletePosition);
                if (!getDatas().get(getDatas().size() - 1).equals("")) {
                    //列表最后一张不是添加按钮时，加入添加按钮
                    getDatas().add("");
                }
                notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    protected void convert(ViewHolder viewHolder, String item, final int position) {
        ImageView ivThum = viewHolder.getView(R.id.iv_thum);
        ImageView ivAdd = viewHolder.getView(R.id.iv_add);
        if (item.equals("")) {
            //item为添加按钮
            ivThum.setVisibility(View.GONE);
            ivAdd.setVisibility(View.VISIBLE);
        } else {
            //item为普通图片
            ivThum.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(item).into(ivThum);
        ivThum.setOnClickListener(new PicturesClickListener(position));
        ivAdd.setOnClickListener(new PicturesClickListener(position));

        ivThum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deletePosition = position;
                //最上面的三个删除按钮是往下的  其他的都是往上的
                if (position < 3) {
                    menuView.setSites(PopupView.SITE_BOTTOM);
                } else {
                    menuView.setSites(PopupView.SITE_TOP);
                }
                menuView.show(view);
                return false;
            }
        });
    }

    /**
     * 图片点击事件
     */
    private class PicturesClickListener implements View.OnClickListener {

        int position;

        public PicturesClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_thum:
                    //点击图片
                    config.setNowThumbnailIndex(position);
                    config.setSourceImageList(getDatas());
                    transferee.apply(config).show();
                    break;
                case R.id.iv_add:
                    //点击添加按钮
                    if (listener != null)
                        listener.onAdd();
                    break;
            }
        }
    }

}
