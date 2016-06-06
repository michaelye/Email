package com.michael.email.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.michael.email.R;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.dialog.AlertDialogFragment;
import com.michael.email.dialog.DialogResultListener;
import com.michael.email.model.Email;
import com.michael.email.receiver.AlarmClockManager;
import com.michael.email.util.EmailBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 16/6/2.
 */
public class PendingFragment extends Fragment implements DialogResultListener
{

    private ListView lvPending;

    private SendFragmentAdapter sendFragmentAdapter;

    private List<Email> emailList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EmailBus.getInstance().register(this);
    }

    public void onEventMainThread(EmailBus.BusEvent busEvent)
    {
        if (busEvent.eventId == EmailBus.BUS_ID_REFRESH_PENDING)
        {
            iniData();//刷新一下
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EmailBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_pending, container, false);
        lvPending = (ListView)parentView.findViewById(R.id.lvPending);
        emailList = new ArrayList<>();
        sendFragmentAdapter = new SendFragmentAdapter(getActivity(), emailList);
        lvPending.setAdapter(sendFragmentAdapter);
        lvPending.setEmptyView(parentView.findViewById(R.id.tvEmptyView));
        addEmptyFooter();
        iniData();

        lvPending.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                showConfirmDeleteDialog(emailList.get(position).id);
                return true;
            }
        });

        return parentView;
    }

    private void addEmptyFooter()
    {
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.layout_empty_footer, null);
        lvPending.addFooterView(emptyView);
    }

    private void iniData()
    {
        emailList.clear();
        emailList.addAll(DBManagerEmail.getInstance().getEmailPending());
        sendFragmentAdapter.notifyDataSetChanged();
    }

    private int REQUEST_CODE_DELETE = 1;

    private String emailIdToBeDelete;

    private void showConfirmDeleteDialog(String id)
    {
        emailIdToBeDelete = id;
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getActivity())
                .setRequestCode(REQUEST_CODE_DELETE)
                .setMessage(R.string.dialog_delete_and_cancel_email_tip)
                .setHasCancelOk(true)
                .setShowTitle(false)
                .setCancel(R.string.cancel)
                .setOk(R.string.ok)
                .setCancelable(false)
                .setListener(this);
        builder.create().show(getActivity());
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        if (requestCode == REQUEST_CODE_DELETE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                AlarmClockManager.cancelClock(getActivity(), Integer.parseInt(emailIdToBeDelete));
                DBManagerEmail.getInstance().deleteEmail(emailIdToBeDelete);
                EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_PENDING));
            }
        }
    }
}
