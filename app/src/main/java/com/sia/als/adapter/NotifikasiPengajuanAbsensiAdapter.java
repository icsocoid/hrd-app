package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdsmdg.tastytoast.ErrorToastView;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.R;
import com.sia.als.model.PengajuanAbsensi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifikasiPengajuanAbsensiAdapter extends RecyclerView.Adapter<NotifikasiPengajuanAbsensiAdapter.MyViewHolder> {

    List<PengajuanAbsensi> modelList;
    Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, PengajuanAbsensi obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public NotifikasiPengajuanAbsensiAdapter(Context context,List<PengajuanAbsensi> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public NotifikasiPengajuanAbsensiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_partner_pengajuan, parent, false);
        context = parent.getContext();
        return new NotifikasiPengajuanAbsensiAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotifikasiPengajuanAbsensiAdapter.MyViewHolder holder, final int position) {
        final PengajuanAbsensi mList = modelList.get(position);
        holder.namaKaryawanTxt.setText(mList.getNama_karyawan());
        holder.statusIzinTxt.setText(mList.getStatus_pengajuan());
        holder.jenisTxt.setText(mList.getJenis_pengajuan());

        String oldFormat = "yyyy-MM-dd";
        String pattern = "EEEE, dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
        try {
            Date d = sdf.parse(mList.getTanggal());
            sdf.applyPattern(pattern);
            String newDateString = sdf.format(d);
            holder.waktuTxt.setText(newDateString);
        }
        catch (ParseException p)
        {
            TastyToast.makeText(context,p.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
        }


        if(mList.getStatus_pengajuan().equals("PENDING"))
        {
            holder.signView.setBackgroundResource(R.drawable.orange_circle);
            holder.leftView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
            holder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.colorOrange));
        }
        else if(mList.getStatus_pengajuan().equals("DISAPPROVE"))
        {
            holder.signView.setBackgroundResource(R.drawable.red_circle);
            holder.leftView.setBackgroundColor(context.getResources().getColor(R.color.active_color));
            holder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.active_color));
        }
        else
        {
            holder.signView.setBackgroundResource(R.drawable.gree_circle);
            holder.leftView.setBackgroundColor(context.getResources().getColor(R.color.main_green_color));
            holder.statusIzinTxt.setTextColor(context.getResources().getColor(R.color.main_green_color));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,mList,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView namaKaryawanTxt,waktuIzinTxt,statusIzinTxt,waktuTxt,jenisTxt;
        public View signView,leftView;
        public MyViewHolder(View view) {
            super(view);
            namaKaryawanTxt = (TextView) view.findViewById(R.id.nama_karyawan_txt);
            waktuIzinTxt = (TextView) view.findViewById(R.id.waktu_izin);
            statusIzinTxt = (TextView) view.findViewById(R.id.status_izin_txt);
            jenisTxt = (TextView) view.findViewById(R.id.jenis_txt);
            waktuTxt = (TextView) view.findViewById(R.id.time_txt);
            signView = (View) view.findViewById(R.id.sign_status);
            leftView = (View) view.findViewById(R.id.left_view);

        }
    }

    public void insertData(List<PengajuanAbsensi> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.modelList.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        for (int i = 0; i < getItemCount(); i++) {
            if (modelList.get(i) == null) {
                modelList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }
    public void resetListData() {
        this.modelList = new ArrayList<>();
        notifyDataSetChanged();
    }
}
