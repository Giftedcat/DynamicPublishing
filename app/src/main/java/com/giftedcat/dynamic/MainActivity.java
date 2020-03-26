package com.giftedcat.dynamic;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.giftedcat.dynamic.activity.BaseActivity;
import com.giftedcat.dynamic.adapter.NineGridAdapter;
import com.giftedcat.dynamic.listener.OnAddPicturesListener;
import com.giftedcat.easylib.selector.MultiImageSelector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_IMAGE = 2;
    private int maxNum = 9;

    Unbinder unbinder;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    NineGridAdapter adapter;

    List<String> mSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        mSelect = new ArrayList<>();
        initView();
    }

    private void initView() {
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new NineGridAdapter(MainActivity.this, mSelect, rvImages);
        adapter.setMaxSize(maxNum);
        rvImages.setAdapter(adapter);
        adapter.setOnAddPicturesListener(new OnAddPicturesListener() {
            @Override
            public void onAdd() {
                pickImage();
            }
        });
    }

    /**
     * 选择添加图片
     */
    private void pickImage() {
        MultiImageSelector selector = MultiImageSelector.create(context);
        selector.showCamera(true);
        selector.count(maxNum);
        selector.multi();
        selector.origin(mSelect);
        selector.start(instans, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> select = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                mSelect.clear();
                mSelect.addAll(select);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }
}
