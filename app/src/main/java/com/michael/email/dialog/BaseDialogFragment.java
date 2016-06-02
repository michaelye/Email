package com.michael.email.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-29
 * Time: 下午12:32
 */
public class BaseDialogFragment extends DialogFragment implements
        DialogInterface.OnClickListener {

    protected static final String ARG_TITLE = "title";
    protected static final String ARG_MESSAGE = "message";
    protected static final String ARG_REQUESTCODE = "request_code";
    private DialogResultListener mListener;
    protected String title;
    protected String message;
    protected int requestCode;

    public static void show(FragmentActivity activity,
                            BaseDialogFragment fragment,
                            Bundle arguments,
                            String tag) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment current = manager.findFragmentByTag(tag);
        if (current != null) {
            transaction.remove(current);
        }

        fragment.setArguments(arguments);
        fragment.show(manager, tag);
    }

    public static void dismiss(FragmentActivity activity, String tag) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment current = manager.findFragmentByTag(tag);
        if (current != null) {
            transaction.remove(current);
        }

        transaction.commitAllowingStateLoss();
    }

    public static BaseDialogFragment getFragment(FragmentActivity activity, String tag) {
        FragmentManager manager = activity.getSupportFragmentManager();
        return (BaseDialogFragment)(manager.findFragmentByTag(tag));
    }

    public void setDialogResultListener(DialogResultListener listener) {
        mListener = listener;
    }

    protected static Bundle createArguments(final String title,
                                            final String message,
                                            final int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_MESSAGE, message);
        bundle.putInt(ARG_REQUESTCODE, requestCode);
        return bundle;
    }

    protected void onResult(int resultCode) {
        onResult(resultCode, null);
    }

    protected void onResult(int resultCode, Bundle bundle) {
        if (mListener != null) {
            mListener.onDialogResult(requestCode, resultCode, bundle);
        }
    }

    protected String getMessage() {
        return message;
    }

    protected String getTitle() {
        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(ARG_TITLE);
            message = bundle.getString(ARG_MESSAGE);
            requestCode = bundle.getInt(ARG_REQUESTCODE);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onResult(Activity.RESULT_CANCELED);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
