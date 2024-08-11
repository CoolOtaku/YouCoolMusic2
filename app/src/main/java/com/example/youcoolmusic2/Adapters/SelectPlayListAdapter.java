package com.example.youcoolmusic2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.youcoolmusic2.Obg.PlayListItem;
import com.example.youcoolmusic2.R;
import java.util.ArrayList;

public class SelectPlayListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<PlayListItem> objects;

    public SelectPlayListAdapter(Context context, ArrayList<PlayListItem> list) {
        ctx = context;
        objects = list;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_play_list_2, parent, false);
        }

        PlayListItem p = getPlayListItem(position);

        ((TextView) view.findViewById(R.id.title)).setText(p.getTitle());

        return view;
    }

    PlayListItem getPlayListItem(int position) {
        return ((PlayListItem) getItem(position));
    }

}