package loucass.kfet.fragments.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import loucass.kfet.R;
import loucass.kfet.data.NewsData;

/**
 * Created by loucass003 on 11/29/17.
 */

public class NewsEditAdapter extends ArrayAdapter<NewsData>
{
    public List<NewsData> newsDatas;

    public NewsEditAdapter(Context context, List<NewsData> news)
    {
        super(context, 0, news);
        newsDatas = news;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.news_edit_element, parent, false);
        TextView title = rowView.findViewById(R.id.title);
        TextView date = rowView.findViewById(R.id.date);
        NewsData data = newsDatas.get(position);

        if(data != null)
        {
            title.setText(data.getTitle());
            date.setText(data.getDate());
        }

        return rowView;
    }
}