package com.michael.email.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.michael.email.R;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.model.Email;
import com.michael.email.util.EmailBus;
import com.michael.email.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 16/6/2.
 */
public class PendingFragment extends Fragment
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
        addEmptyFooter();
        emailList.addAll(DBManagerEmail.getInstance().getEmailPending());
        sendFragmentAdapter.notifyDataSetChanged();

        lvPending.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(emailList.get(position) != null)
                {
                    UIUtil.startEmailDetailActivity(getActivity(), emailList.get(position).id);
                }
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
}
