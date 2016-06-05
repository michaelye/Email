package com.michael.email.ui.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michael.email.R;
import com.michael.email.base.BaseCompatableAdapter;
import com.michael.email.model.Email;
import com.michael.email.util.TimeUtils;

import java.util.List;

/**
 * Created by michael on 16/6/5.
 */
public class SendFragmentAdapter extends BaseCompatableAdapter<Email>
{


    public SendFragmentAdapter(Context context, List<Email> list)
    {
        super(context, list);
    }

    class ViewHolder
    {
        TextView tvSubject;
        TextView tvContent;
        TextView tvTime;
        ImageView ivStarLogo;
        ImageView ivAttachLogo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = super.layoutInflater.inflate(R.layout.list_item_send, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSubject = (TextView)convertView.findViewById(R.id.tvSubject);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivStarLogo = (ImageView) convertView.findViewById(R.id.ivStarLogo);
            viewHolder.ivAttachLogo = (ImageView) convertView.findViewById(R.id.ivAttachLogo);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Email email = super.getItem(position);

        String subject = email.subject;
        viewHolder.tvSubject.setText(subject == null ? "" : subject);
        String content = email.content;
        viewHolder.tvContent.setText(content == null ? "" :content);
        String time = email.sendTime+"";
        viewHolder.tvTime.setText(time == null ? "" : TimeUtils.getFormatTime(email.sendTime));

        List<String> attachPaths = email.attachPaths;
        if(attachPaths == null || attachPaths.isEmpty())
        {
            viewHolder.ivAttachLogo.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.ivAttachLogo.setVisibility(View.VISIBLE);
        }
        viewHolder.ivStarLogo.setVisibility(email.isStar ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

//    private OnItemClickListener onItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
//    {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener
//    {
//        public void OnItemClick(int position);
//    }
//
//    private OnItemLongClickListener onItemLongClickListener;
//
//    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener)
//    {
//        this.onItemLongClickListener = onItemLongClickListener;
//    }
//
//    public interface OnItemLongClickListener
//    {
//        public void OnItemLongClick(int position);
//    }
}
