package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Izin;

import java.util.List;

public class RekapHomeAdapter extends  RecyclerView.Adapter<RekapHomeAdapter.MyViewHolder> {
    List<Izin> modelList;
    Context context;
    private OnItemClickListener mOnItemClickListener;

    public RekapHomeAdapter(Context context,List<Izin> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Izin obj, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RekapHomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rekap, parent, false);
        context = parent.getContext();
        return new RekapHomeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RekapHomeAdapter.MyViewHolder holder,final int position) {
        final Izin mList = modelList.get(position);
        holder.namaIzinTxt.setText(mList.getNamaIzin());
        holder.jumlahIzinTxt.setText(String.valueOf(mList.getJumlah()));
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
        public TextView namaIzinTxt,jumlahIzinTxt;
        public MyViewHolder(View view) {
            super(view);
            namaIzinTxt = (TextView) view.findViewById(R.id.nama_izin_txt);
            jumlahIzinTxt = (TextView) view.findViewById(R.id.jumlah_izin_txt);

        }
    }

}
