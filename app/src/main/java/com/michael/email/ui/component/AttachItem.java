package com.michael.email.ui.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michael.email.R;


/**
 *
 * 附件
 *
 * Created by michael on 15/8/31.
 */
public class AttachItem extends LinearLayout
{

    public AttachItem(Context context)
    {
        super(context);
        ini(context);
    }

    public AttachItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        ini(context);
    }

    public AttachItem(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        ini(context);
    }

    private TextView tvAttachPath;
    private ImageView ivDeleteAttach;

    private void ini(Context context)
    {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_attach_item, null, false);
        tvAttachPath = (TextView)view.findViewById(R.id.tvAttachPath);
        ivDeleteAttach = (ImageView)view.findViewById(R.id.ivDeleteAttach);
        ivDeleteAttach.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(onAttachDeleteListener != null)
                {
                    onAttachDeleteListener.onDelete(position);
                }
            }
        });
        this.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private int position;

    /**
     * 设置地址
     * */
    public void setAttach(int position, String path)
    {
        this.position = position;
        tvAttachPath.setText(path);
    }

    /**
     * 删除按钮是否可见
     * */
    public void setDeleteIconVisibility(int visibility)
    {
        ivDeleteAttach.setVisibility(visibility);
    }

    private OnAttachDeleteListener onAttachDeleteListener;

    public void setOnAttachDeleteListener(OnAttachDeleteListener onAttachDeleteListener)
    {
        this.onAttachDeleteListener = onAttachDeleteListener;
    }

    public interface OnAttachDeleteListener
    {
        public void onDelete(int position);
    }
}
