package com.michael.email.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.michael.email.R;


/**
 * Created by zeyiwu on 14-6-3.
 */
public class AlertDialogFragment extends BaseDialogFragment {

    private static final String TAG = "AlertDialogFragment";
    private static final String ARG_CANCEL_OK = "arg_cancel_ok";
    private static final String ARG_SHOW_TITLE = "arg_show_title";
    private static final String ARG_OK = "arg_ok";
    private static final String ARG_CANCEL = "arg_cancel";
    private static final String ARG_NEUTRAL = "arg_neutral";
    private static final String ARG_CANCELABLE = "arg_cancelable";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        boolean showTitle = bundle.getBoolean(ARG_SHOW_TITLE);
        boolean hasCancelOk = bundle.getBoolean(ARG_CANCEL_OK);
        boolean cancelable = bundle.getBoolean(ARG_CANCELABLE, true);
        String cancel = bundle.getString(ARG_CANCEL);
        String neutral = bundle.getString(ARG_NEUTRAL);
        String ok = bundle.getString(ARG_OK);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(getMessage());
//        builder.setCancelable(cancelable);//这里是没有作用的！换成全局的setCancelable(cancelable);
        setCancelable(cancelable);
        if (showTitle) {
            builder.setTitle(getTitle());
        }
        if (hasCancelOk) {
            if (ok != null) {
                builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_OK);
                    }
                });
            } else {
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_OK);
                    }
                });
            }

            if (cancel != null) {
                builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_CANCELED);
                    }
                });
            } else {
                builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_CANCELED);
                    }
                });
            }
        } else {
            if (ok != null) {
                builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_OK);
                    }
                });
            } else {
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResult(Activity.RESULT_OK);
                    }
                });
            }

        }
        if(neutral != null)
        {
            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    onResult(Activity.RESULT_FIRST_USER);
                }
            });
        }
        Dialog dialog = builder.create();
        if (!showTitle) {
            dialog.requestWindowFeature(STYLE_NO_TITLE);
        }
        return dialog;
    }

    public static class AlertParams {
        final Context context;
        DialogResultListener listener;
        String title;
        String message;
        String ok;
        String cancel;
        String neutral;
        int requestCode;
        boolean showTitle;
        boolean hasCancelOk;
        boolean cancelAble;

        AlertParams(Context context) {
            this.context = context;
            ok = context.getString(R.string.ok);
            cancel = context.getString(R.string.cancel);
            showTitle = true;
            hasCancelOk = true;
        }
    }

    public static class Builder {
        private final AlertParams P;
        private final FragmentActivity activity;

        public Builder(FragmentActivity activity) {
            P = new AlertParams(activity);
            this.activity = activity;
        }

        public Builder setRequestCode(int code) {
            P.requestCode = code;
            return this;
        }

        public Builder setTitle(String title) {
            P.title = title;
            return this;
        }

        public Builder setTitle(int id) {
            P.title = activity.getString(id);
            return this;
        }

        public Builder setMessage(String message) {
            P.message = message;
            return this;
        }

        public Builder setMessage(int id) {
            P.message = activity.getString(id);
            return this;
        }

        public Builder setListener(DialogResultListener listener) {
            P.listener = listener;
            return this;
        }

        public Builder setOk(String ok) {
            P.ok = ok;
            return this;
        }

        public Builder setOk(int id) {
            P.ok = activity.getString(id);
            return this;
        }

        public Builder setCancel(String cancel) {
            P.cancel = cancel;
            return this;
        }

        public Builder setCancel(int id) {
            P.cancel = activity.getString(id);
            return this;
        }

        public Builder setNeutral(String neutral) {
            P.neutral = neutral;
            return this;
        }

        public Builder setNeutral(int id) {
            P.neutral = activity.getString(id);
            return this;
        }

        public Builder setShowTitle(boolean show) {
            P.showTitle = show;
            return this;
        }

        public Builder setHasCancelOk(boolean hasCancelOk) {
            P.hasCancelOk = hasCancelOk;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            P.cancelAble = cancelable;
            return this;
        }

        public AlertDialogFragment create() {
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setDialogResultListener(P.listener);
            Bundle bundle = createArguments(P.title, P.message, P.requestCode);
            bundle.putBoolean(ARG_CANCEL_OK, P.hasCancelOk);
            bundle.putBoolean(ARG_SHOW_TITLE, P.showTitle);
            bundle.putString(ARG_OK, P.ok);
            bundle.putString(ARG_CANCEL, P.cancel);
            bundle.putString(ARG_NEUTRAL, P.neutral);
            bundle.putBoolean(ARG_CANCELABLE, P.cancelAble);
            fragment.setArguments(bundle);

            return fragment;
        }
    }

    public void show(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment current = manager.findFragmentByTag(TAG);
        if (current != null) {
            transaction.remove(current);
        }

        show(manager, TAG);
    }
}
