package com.michael.email.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;

import com.michael.email.R;
import com.michael.email.util.Consts;
import com.michael.email.util.L;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-29
 * Time: 下午4:00
 */
public class ListItemDialogFragment extends BaseDialogFragment {
    private static final String TAG = ListItemDialogFragment.class.getSimpleName();

    private static final String ARG_ITEM_ID_ARRAY = "item_ids";
    private static final String ARG_ITEM_STRING_ARRAY = "item_strings";
    private static final String ARG_ITEM_SELECT_ID = "item_select";
    private static final String ARG_LIST_TYPE = "item_type";
    private int[] mItemIDs;

    public static ListItemDialogFragment show(FragmentActivity activity,
                                              int titleID,
                                              int itemArrayID,
                                              int itemStringArrayID,
                                              int requestCode) {
        return show(activity, activity.getString(titleID),
                itemArrayID, itemStringArrayID, requestCode);
    }

    public static ListItemDialogFragment show(FragmentActivity activity,
                                              String title,
                                              int itemArrayID,
                                              int itemStringArrayID,
                                              int requestCode) {
        Bundle bundle = createArguments(title, null, requestCode);
        bundle.putInt(ARG_ITEM_ID_ARRAY, itemArrayID);
        bundle.putInt(ARG_ITEM_STRING_ARRAY, itemStringArrayID);
        bundle.putInt(ARG_LIST_TYPE, 0);

        ListItemDialogFragment fragment = new ListItemDialogFragment();
        show(activity, fragment,
                bundle, ListItemDialogFragment.class.getSimpleName());
        return fragment;
    }

    public static ListItemDialogFragment showSingleChoice(FragmentActivity activity,
                                                          int titleID,
                                                          int selectId,
                                                          int itemArrayID,
                                                          int itemStringArrayID,
                                                          int requestCode) {
        Bundle bundle = createArguments(activity.getString(titleID), null, requestCode);
        bundle.putInt(ARG_ITEM_ID_ARRAY, itemArrayID);
        bundle.putInt(ARG_ITEM_STRING_ARRAY, itemStringArrayID);
        bundle.putInt(ARG_ITEM_SELECT_ID, selectId);
        bundle.putInt(ARG_LIST_TYPE, 1);

        ListItemDialogFragment fragment = new ListItemDialogFragment();
        show(activity, fragment,
                bundle, ListItemDialogFragment.class.getSimpleName());
        return fragment;
    }

    public static void dismiss(FragmentActivity activity) {
        dismiss(activity, ListItemDialogFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int itemArrayID = bundle.getInt(ARG_ITEM_ID_ARRAY, 0);
            int itemStringArrayID = bundle.getInt(ARG_ITEM_STRING_ARRAY, 0);
            int type = bundle.getInt(ARG_LIST_TYPE, 0);
            mItemIDs = getItemIDs(itemArrayID);
            if (itemStringArrayID > 0) {
                if (type == 0) {
                    return buildDialog(itemStringArrayID);
                } else if (type == 1) {
                    int selectId = bundle.getInt(ARG_ITEM_SELECT_ID, 0);
                    if (selectId < 0) {
                        selectId = 0;
                    }
                    return buildSingleChoiceDialog(itemStringArrayID, selectId);
                }
            }
        }
        return super.onCreateDialog(savedInstanceState);
    }

    private Dialog buildDialog(int id) {
        final String title = getTitle();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(id, this);
        Dialog dialog = builder.create();
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        } else {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    private Dialog buildSingleChoiceDialog(int id, int select) {
        final String title = getTitle();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(id, select, this);
        builder.setNegativeButton(R.string.cancel, null);
        Dialog dialog = builder.create();
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        } else {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    private int[] getItemIDs(int id) {
        TypedArray array = getActivity().getResources().obtainTypedArray(id);
        if (array != null) {
            int[] itemIDs = new int[array.length()];
            for (int i = 0; i < array.length(); i++) {
                itemIDs[i] = array.getResourceId(i, 0);
            }
            return itemIDs;
        }
        return null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (mItemIDs != null && which >= 0 && which < mItemIDs.length) {
            onResult(mItemIDs[which]);
        }
        if (Consts.DEBUG) {
            L.d(TAG, "click item :" + which);
        }
    }
}
