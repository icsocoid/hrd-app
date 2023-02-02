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
import com.sia.als.model.PengajuanAbsensi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PengajuanAbsensiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PengajuanAbsensi> modelList;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, PengajuanAbsensi obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PengajuanAbsensiAdapter(Context context, List<PengajuanAbsensi> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pengajuan_absensi, parent, false);
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
            final PengajuanAbsensi mList = modelList.get(position);
            myViewHolder.jenisPengajuanTxt.setText(mList.getJenis_pengajuan());
            myViewHolder.statusPengajuanTxt.setText(mList.getStatus_pengajuan());

            String oldFormat = "yyyy-MM-dd";
            String pattern = "EEEE, dd MMMM yyyy";
            String patternPengajuan = "dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat, new Locale("id", "ID"));
            //String tglAwal = mList.getTanggal_awal();
            //String tglSelesai = mList.getTanggal_akhir();
            try {
                //tgl pengajuan
                Date d = sdf.parse(mList.getTanggal());
                sdf.applyPattern(pattern);
                String newDateString = sdf.format(d);
                myViewHolder.waktuTxt.setText(newDateString);
                //tgl absensi
                sdf = new SimpleDateFormat(oldFormat, new Locale("id", "ID"));
                Date tgl = sdf.parse(mList.getTanggal_absensi());
                sdf.applyPattern(patternPengajuan);
                String tglAbsensi = sdf.format(tgl);
                myViewHolder.waktuIzinTxt.setText(tglAbsensi);
            } catch (ParseException p) {

            }


            if (mList.getStatus_pengajuan().equals("PENDING")) {
                myViewHolder.signView.setBackgroundResource(R.drawable.orange_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
                myViewHolder.statusPengajuanTxt.setTextColor(context.getResources().getColor(R.color.colorOrange));
            } else if (mList.getStatus_pengajuan().equals("DISAPPROVE")) {
                myViewHolder.signView.setBackgroundResource(R.drawable.red_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.active_color));
                myViewHolder.statusPengajuanTxt.setTextColor(context.getResources().getColor(R.color.active_color));
            }
            else
            {
                myViewHolder.signView.setBackgroundResource(R.drawable.gree_circle);
                myViewHolder.leftView.setBackgroundColor(context.getResources().getColor(R.color.main_green_color));
                myViewHolder.statusPengajuanTxt.setTextColor(context.getResources().getColor(R.color.main_green_color));
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
        public TextView jenisPengajuanTxt,waktuIzinTxt,statusPengajuanTxt,waktuTxt;
        public View signView,leftView;
        public MyViewHolder(View view) {
            super(view);
            jenisPengajuanTxt = (TextView) view.findViewById(R.id.jenis_pengajuan_txt);
            waktuIzinTxt = (TextView) view.findViewById(R.id.waktu_absensi);
            statusPengajuanTxt = (TextView) view.findViewById(R.id.status_pengajuan_txt);
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
