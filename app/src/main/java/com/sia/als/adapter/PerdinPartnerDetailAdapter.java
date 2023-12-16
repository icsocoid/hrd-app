package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.PerdinPartner;

import java.util.List;

public class PerdinPartnerDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<PerdinPartner> modelList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public PerdinPartnerDetailAdapter(Context context, List<PerdinPartner> modelList){
        this.context = context;
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_perdin_partner_detail, parent, false);
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

    public int getItemViewType(int position){
        return modelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder)
        {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final PerdinPartner mList = modelList.get(position);

            myViewHolder.namaKaryawanTxt.setText(mList.getNamaKaryawan());
            myViewHolder.nominalPerdinTxt.setText(mList.getTotalPerdin());
            myViewHolder.nominalBonSementara.setText(mList.getTotalBonSementara());

            if (mList.getTotalBonSementara().equals("0")){
                myViewHolder.nominalBsLay.setVisibility(View.GONE);
            }
            if (mList.getTotalPerdin().equals("0")){
                myViewHolder.nominalperdinLay.setVisibility(View.GONE);
            }
        }else
        {
            showLoadingView((PerdinPartnerDetailAdapter.LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namaKaryawanTxt, nominalPerdinTxt, nominalBonSementara;
        LinearLayout  nominalperdinLay, nominalBsLay;
        public MyViewHolder(View itemView) {
            super(itemView);
            namaKaryawanTxt = (TextView) itemView.findViewById(R.id.nama_karyawan_txt);
            nominalPerdinTxt = (TextView) itemView.findViewById(R.id.nominal_perdin_txt);
            nominalBonSementara = (TextView) itemView.findViewById(R.id.nominal_bs_txt);
            nominalperdinLay = (LinearLayout) itemView.findViewById(R.id.list_nominal_perdin);
            nominalBsLay = (LinearLayout) itemView.findViewById(R.id.list_nominal_bs);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progress_circular);

        }
    }
    private void showLoadingView(LoadingViewHolder holder, int position) {
    }
}
