package com.giftedcat.dynamic;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.giftedcat.dynamic.activity.BaseActivity;
import com.giftedcat.dynamic.adapter.NineGridAdapter;
import com.giftedcat.dynamic.listener.OnPicturesClickListener;
import com.giftedcat.picture.lib.PictureUseHelpr;
import com.giftedcat.picture.lib.selector.MultiImageSelector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_IMAGE = 2;

    Unbinder unbinder;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    NineGridAdapter adapter;

    List<String> mSelect;

    PictureUseHelpr helpr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        mSelect = new ArrayList<>();
        initView();
        helpr = PictureUseHelpr.init(this).
                setMaxNum(9).
                origin(mSelect).
                bindRecyclerView(rvImages, R.id.iv_thum);
    }

    private void initView() {
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new NineGridAdapter(MainActivity.this, mSelect, rvImages);
        rvImages.setAdapter(adapter);
        adapter.setOnAddPicturesListener(new OnPicturesClickListener() {
            @Override
            public void onClick(int position) {
                helpr.show(position);
            }

            @Override
            public void onAdd() {
                helpr.pickImage(REQUEST_IMAGE);
            }
        });
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
