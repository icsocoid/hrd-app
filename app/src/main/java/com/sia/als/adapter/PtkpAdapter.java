package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sia.als.R;
import com.sia.als.model.Izin;
import com.sia.als.model.Ptkp;

import java.util.List;

public class PtkpAdapter extends BaseAdapter {
    Context context;
    List<Ptkp> item;
    private LayoutInflater inflater;

    public PtkpAdapter(Context context,List<Ptkp> item)
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_izin, null);

        TextView izinTxt = (TextView) convertView.findViewById(R.id.izin);

        Ptkp data;
        data = item.get(position);

        izinTxt.setText(data.getNamaPtkp());

        return convertView;
    }

}
