package com.michael.email.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.michael.email.R;
import com.michael.email.db.DBManagerContact;
import com.michael.email.model.Contact;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_contacts, container, false);
        lvContact = (ListView)parentView.findViewById(R.id.lvContact);
        contactList = new ArrayList<>();
        contactsFragmentAdapter = new ContactsFragmentAdapter(getActivity(), contactList);
        lvContact.setAdapter(contactsFragmentAdapter);

        contactList.addAll(DBManagerContact.getInstance().getContacts());
        contactsFragmentAdapter.notifyDataSetChanged();
        return parentView;
    }


}
