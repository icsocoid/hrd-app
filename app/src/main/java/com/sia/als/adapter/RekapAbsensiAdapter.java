package com.sia.als.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Absensi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RekapAbsensiAdapter extends RecyclerView.Adapter<RekapAbsensiAdapter.MyViewHolder> {

    List<Absensi> modelList;
    Context context;

    public RekapAbsensiAdapter(Context context,List<Absensi> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public RekapAbsensiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_absensi, parent, false);
            context = parent.getContext();
            return new RekapAbsensiAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RekapAbsensiAdapter.MyViewHolder holder, int position) {
        final Absensi mList = modelList.get(position);
        holder.jamMasukTxt.setText(mList.getJam_masuk());
        holder.jamPulangTxt.setText(mList.getJam_pulang());
        holder.statusMasukTxt.setText(mList.getStatus_masuk());
        holder.statusPulangTxt.setText(mList.getStatus_pulang());
        holder.accountImg.setImageResource(R.drawable.ic_account1);
        holder.waktuTxt.setText(mList.getEmployeeName());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView jamMasukTxt,jamPulangTxt,statusMasukTxt,statusPulangTxt,waktuTxt;
        public ImageView accountImg;
        public MyViewHolder(View view) {
            super(view);
            jamMasukTxt = (TextView) view.findViewById(R.id.jam_masuk_txt);
            jamPulangTxt = (TextView) view.findViewById(R.id.jam_pulang_txt);
            statusMasukTxt = (TextView) view.findViewById(R.id.status_masuk_txt);
            statusPulangTxt = (TextView) view.findViewById(R.id.status_pulang_txt);
            waktuTxt = (TextView) view.findViewById(R.id.time_txt);
            accountImg = (ImageView) view.findViewById(R.id.clock);

        }
    }

}
