package com.michael.email.ui.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michael.email.R;
import com.michael.email.base.BaseCompatableAdapter;
import com.michael.email.model.Contact;

import java.util.List;

/**
 * Created by michael on 16/6/5.
 */
public class ContactsFragmentAdapter extends BaseCompatableAdapter<Contact>
{


    public ContactsFragmentAdapter(Context context, List<Contact> list)
    {
        super(context, list);
    }

    class ViewHolder
    {
        TextView tvContactName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = super.layoutInflater.inflate(R.layout.list_item_contact, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvContactName = (TextView)convertView.findViewById(R.id.tvContactName);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = super.getItem(position);
        viewHolder.tvContactName.setText(contact.emailAddress);
        return convertView;
    }
}
