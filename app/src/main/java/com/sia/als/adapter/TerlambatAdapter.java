package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sia.als.R;
import com.sia.als.model.Terlambat;

import java.util.List;

public class TerlambatAdapter extends BaseAdapter {

    Context context;
    List<Terlambat> item;
    private LayoutInflater inflater;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public TerlambatAdapter(Context context, List<Terlambat> item)
    {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        //return item.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_terlambat, null);

            TextView terlambatTxt = (TextView) convertView.findViewById(R.id.terlambat);

            Terlambat data;
            data = item.get(position);

            terlambatTxt.setText(data.getNamaAbsensi());

            return convertView;
    }

}
