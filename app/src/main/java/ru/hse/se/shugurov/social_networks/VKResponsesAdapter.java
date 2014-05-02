package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;

/**
 * Created by Иван on 26.02.14.
 */
public class VKResponsesAdapter extends BaseAdapter//TODO does not show pictures
{
    private LayoutInflater inflater;
    private VKAbstractItem[] comments;

    public VKResponsesAdapter(Context context, VKAbstractItem[] comments)
    {
        this.comments = comments;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return comments.length;
    }

    @Override
    public VKAbstractItem getItem(int position)
    {
        return comments[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.vk_comment, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.vk_comment_author_name)).setText(comments[position].getAuthor().getFullName());
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) convertView.findViewById(R.id.vk_comment_date)).setText(format.format(comments[position].getDate()));
        ((TextView) convertView.findViewById(R.id.vk_comment_text)).setText(Html.fromHtml(comments[position].getText()));
        return convertView;
    }
}