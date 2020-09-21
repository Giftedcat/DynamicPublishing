package com.giftedcat.picture.lib.selector.utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.easylib.R;

public class PermissionUtils {

    /**
     * 请求权限
     * */
    public static void requestPermission(final Activity context, final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }

}
