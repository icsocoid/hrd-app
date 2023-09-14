package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Pengajuan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PengajuanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Pengajuan> modelList;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, Pengajuan obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PengajuanAdapter(Context context,List<Pengajuan> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_izin, parent, false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final Pengajuan mList = modelList.get(position);
            myViewHolder.namaIzinTxt.setText(mList.getNama_izin());
            myViewHolder.statusIzinTxt.setText(mList.getStatus_izin());

            String oldFormat = "yyyy-MM-dd";
            String pattern = "EEEE, dd MMMM yyyy";
            String patternIzin = "dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat, new Locale("id", "ID"));
            //String tglAwal = mList.getTanggal_awal();
            //String tglSelesai = mList.getTanggal_akhir();
            try {
                Date d = sdf.parse(mList.getTanggal());
                sdf.applyPattern(pattern);
                String newDateString = sdf.format(d);
                myViewHolder.waktuTxt.setText(newDateString);
                sdf = new SimpleDateFormat(oldFormat, new Locale("id", "ID"));
                Date tglMulai = sdf.parse(mList.getTanggal_awal());
                sdf.applyPattern(patternIzin);
                String tglAwal = sdf.format(tglMulai);
                sdf = new SimpleDateFormat(oldFormat, new Locale("id", "ID"));
                Date tglSls = sdf.parse(mList.getTanggal_akhir());
                sdf.applyPattern(patternIzin);
                String tglSelesai = sdf.format(tglSls);
                myViewHolder.waktuIzinTxt.setText(tglAwal + "-" + tglSelesai);
            } catch (ParseException p) {

            }


            if (mList.getStatus_izin().equals("PENDING")) {
                myViewHolder.signView.setBackgroundResource(R.drawable.orange_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
                myViewHolder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.colorOrange));
            } else if (mList.getStatus_izin().equals("DISAPPROVE")) {
                myViewHolder.signView.setBackgroundResource(R.drawable.red_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.active_color));
                myViewHolder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.active_color));
            }
            else
            {
                myViewHolder.signView.setBackgroundResource(R.drawable.gree_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.main_green_color));
                myViewHolder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.main_green_color));
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
        public TextView namaIzinTxt,waktuIzinTxt,statusIzinTxt,waktuTxt;
        public View signView,leftView;
        public MyViewHolder(View view) {
            super(view);
            namaIzinTxt = (TextView) view.findViewById(R.id.nama_izin_txt);
            waktuIzinTxt = (TextView) view.findViewById(R.id.waktu_izin);
            statusIzinTxt = (TextView) view.findViewById(R.id.status_izin_txt);
            waktuTxt = (TextView) view.findViewById(R.id.time_txt);
            signView = (View) view.findViewById(R.id.sign_status);
            leftView = (View) view.findViewById(R.id.left_view);
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
