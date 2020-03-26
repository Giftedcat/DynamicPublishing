package com.giftedcat.dynamic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends Activity {

    public Context context;
    public Activity instans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        instans = this;
        setTranslucentStatus(true);
    }

    /**
     * 设置沉浸式状态栏
     * */
    public void setTranslucentStatus(boolean b) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //版本小于4.4
            return;
        }
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bit = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (b) {
            winParams.flags |= bit;
        } else {
            winParams.flags &= ~bit;
        }
    }

}
