# Android仿微信朋友圈发布动态功能

# 一、前言

应工作上的要求，需要有一个类似于微信朋友圈发动态上传图片的功能，想起曾经已经做过了，但奈何不忍看自己以前写的代码的惨状，觉得重新封装一个使用方便，易于维护的类似功能的类，自己之后用起来也顺手，当然也方便一下大家，这样可以加快我们工作的效率，让我们有更多的时间学习（划水）。

![image](https://upload-images.jianshu.io/upload_images/20395467-c268719eacb5e847.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

功能上的话，目前有添加图片、查看大图、删除图片

# 二、效果图
先贴一下效果图吧

![image](https://upload-images.jianshu.io/upload_images/20395467-fcb699250e107c2d.gif?imageMogr2/auto-orient/strip)

# 三、实现功能
库有用到butterknife和显示图片的glide
适配器用的rvadapter，加一个删除功能的气泡弹窗
引入一下相关依赖：
```
    api 'com.jakewharton:butterknife:10.2.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.1'

    implementation 'com.github.bumptech.glide:glide:4.5.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.5.0'

    //气泡弹窗
    implementation 'me.kareluo.ui:popmenu:1.1.0'
    implementation 'com.zhy:base-rvadapter:3.0.3'
```
####（一）布局文件
主页面上的话，只有一个RecyclerView，他的LayoutManager设为GridLayoutManager，一行为3个
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_images"
        android:paddingTop="15dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```

子布局的话，有两个ImageView，一个是普通的图片，另外一个固定为那个添加的按钮
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="90dp"
    android:gravity="center"
    android:layout_marginBottom="10dp"
    android:layout_marginTop="10dp"
    android:orientation="horizontal">

    <ImageView
        android:id="@+id/iv_thum"
        android:visibility="gone"
        android:scaleType="centerCrop"
        android:layout_width="90dp"
        android:layout_height="match_parent" />

    <ImageView
        android:visibility="gone"
        android:id="@+id/iv_add"
        android:src="@mipmap/add_icon"
        android:scaleType="centerCrop"
        android:layout_width="90dp"
        android:layout_height="match_parent" />

</LinearLayout>

```

#### （二）Activity代码

先来看一下如何在我们的页面里使用我已经写好的这个adapter

首先初始化一下控件和adapter，给adapter设置一个点击添加图片的监听
```
    private void initView() {
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new NineGridAdapter(MainActivity.this, mSelectList, rvImages);
        adapter.setMaxSize(maxNum);
        rvImages.setAdapter(adapter);
        adapter.setOnAddPicturesListener(new OnAddPicturesListener() {
            @Override
            public void onAdd() {
                pickImage();
            }
        });
    }
```

选择图片页面的启动
```
    private void pickImage() {
        MultiImageSelector selector = MultiImageSelector.create(context);
        selector.showCamera(true);
        selector.count(maxNum);
        selector.multi();
        selector.origin(mSelectList);
        selector.start(instans, REQUEST_IMAGE);
    }
```

拿到选择图片页面的返回数据，添加至List中
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> select = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                mSelectList.clear();
                mSelectList.addAll(select);
                adapter.notifyDataSetChanged();
            }
        }
    }
```

#### （三）适配器代码

首先在构造函数中，往list里添加一个空串，为添加按钮占位。之后初始化一下删除气泡按钮跟大图显示的控件
```
    public NineGridAdapter(Context context, List<String> selectPath, RecyclerView rvImages) {
        super(context, R.layout.item_img, selectPath);
        this.context = context;

        selectPath.add("");
        initDeleteMenu();
        initTransfer(rvImages);
    }
```
两个控件初始化的代码
```
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
```
在item填充的函数中完成图片的显示，点击和长按的监听
```
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
```
点击事件代码
```
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
```
#四、关于老v7项目的问题
前段时间也是刚把自己的项目从v7手动改为androidx的，忙活了半天，后来发现android studio有一键将v7项目改为新的androidx的项目，算是给自己记个笔记
![78M(FMFGHPU%CF1GDL_POBM.png](https://upload-images.jianshu.io/upload_images/20395467-72f05aa4782d0393.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
