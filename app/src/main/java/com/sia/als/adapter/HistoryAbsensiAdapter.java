package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Absensi;
import com.sia.als.model.Pengajuan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAbsensiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Absensi> modelList;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, Absensi obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
    public HistoryAbsensiAdapter(Context context,List<Absensi> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_absensi, parent, false);
            context = parent.getContext();
            return new MyViewHolder(itemView);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            context = parent.getContext();
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return modelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final Absensi mList = modelList.get(position);
            myViewHolder.jamMasukTxt.setText(mList.getJam_masuk());
            myViewHolder.jamPulangTxt.setText(mList.getJam_pulang());
            myViewHolder.statusMasukTxt.setText(mList.getStatus_masuk());
            myViewHolder.statusPulangTxt.setText(mList.getStatus_pulang());
            String oldFormat = "yyyy-MM-dd";
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
            try {
                Date d = sdf.parse(mList.getTanggal());
                sdf.applyPattern(pattern);
                String newDateString = sdf.format(d);
                myViewHolder.waktuTxt.setText(newDateString);
            }
            catch (ParseException p)
            {

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,mList,position);
                }
            });
        }
        else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView jamMasukTxt,jamPulangTxt,statusMasukTxt,statusPulangTxt,waktuTxt;
        public MyViewHolder(View view) {
            super(view);
            jamMasukTxt = (TextView) view.findViewById(R.id.jam_masuk_txt);
            jamPulangTxt = (TextView) view.findViewById(R.id.jam_pulang_txt);
            statusMasukTxt = (TextView) view.findViewById(R.id.status_masuk_txt);
            statusPulangTxt = (TextView) view.findViewById(R.id.status_pulang_txt);
            waktuTxt = (TextView) view.findViewById(R.id.time_txt);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_circular);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

}
