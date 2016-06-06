package com.michael.email.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.michael.email.R;
import com.michael.email.db.DBManagerContact;
import com.michael.email.model.Contact;
import com.michael.email.util.EmailBus;
import com.michael.email.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 16/6/2.
 */
public class ContactsFragment extends Fragment
{

    private ListView lvContact;

    private ContactsFragmentAdapter contactsFragmentAdapter;

    private List<Contact> contactList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EmailBus.getInstance().register(this);
    }

    public void onEventMainThread(EmailBus.BusEvent busEvent)
    {
        if (busEvent.eventId == EmailBus.BUS_ID_REFRESH_CONTACT)
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
        View parentView = inflater.inflate(R.layout.fragment_contacts, container, false);
        lvContact = (ListView)parentView.findViewById(R.id.lvContact);
        contactList = new ArrayList<>();
        contactsFragmentAdapter = new ContactsFragmentAdapter(getActivity(), contactList);
        lvContact.setAdapter(contactsFragmentAdapter);
        lvContact.setEmptyView(parentView.findViewById(R.id.tvEmptyView));
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                UIUtil.startNewLetterActivity(getActivity(), contactList.get(position).emailAddress);
            }
        });
        addEmptyFooter();
        iniData();
        return parentView;
    }

    private void addEmptyFooter()
    {
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.layout_empty_footer, null);
        lvContact.addFooterView(emptyView);
    }

    private void iniData()
    {
        contactList.clear();
        contactList.addAll(DBManagerContact.getInstance().getContacts());
        contactsFragmentAdapter.notifyDataSetChanged();
    }

}
