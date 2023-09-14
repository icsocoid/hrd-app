package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.SlipGaji;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SlipGajiBonusLainAdapter extends RecyclerView.Adapter<SlipGajiBonusLainAdapter.MyViewHolder> {
    List<SlipGaji> modelList;
    Context context;
    public Locale localeID;
    public NumberFormat numberFormat;
    public SlipGajiBonusLainAdapter (Context context, List<SlipGaji> modelList){
        this.context = context;
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public SlipGajiBonusLainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slip_gaji_detail, parent, false);
        context = parent.getContext();
        return new SlipGajiBonusLainAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SlipGajiBonusLainAdapter.MyViewHolder holder, int position) {
        final SlipGaji mList = modelList.get(position);
        localeID = new Locale("in", "ID");
        numberFormat = NumberFormat.getNumberInstance(localeID);
        holder.namaGajiTxt.setText(mList.getNamaBonusLainGaji());
        holder.jumlahGajiTxt.setText(numberFormat.format(Double.parseDouble(String.valueOf(mList.getJumlahBonusLainGaji()))));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView namaGajiTxt, jumlahGajiTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namaGajiTxt = (TextView) itemView.findViewById(R.id.nama_gaji_txt);
            jumlahGajiTxt = (TextView) itemView.findViewById(R.id.jumlah_gaji_txt);
        }
    }
}
