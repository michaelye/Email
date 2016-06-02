package com.michael.email.dialog;

import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-29
 * Time: 下午12:33
 */
public interface DialogResultListener
{

    void onDialogResult(int requestCode, int resultCode, Bundle arguments);
}
