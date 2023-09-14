package com.sia.als.adapter;

import android.annotation.SuppressLint;
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
import com.sia.als.model.SlipGaji;
import com.sia.als.model.Task;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SlipGajiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<SlipGaji> modelList;
    Context context;
    public Locale localeID;
    public NumberFormat numberFormat;
    private OnDetailClickListener onDetailClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public interface OnDetailClickListener{
        void onItemClick(View view, SlipGaji obj, int position);
    }
    public void setOnDetailClickListener(final OnDetailClickListener onDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener;
    }

    public SlipGajiAdapter(Context context, List<SlipGaji> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slip_gaji, parent, false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof MyViewHolder){
            localeID = new Locale("in", "ID");
            numberFormat = NumberFormat.getNumberInstance(localeID);
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final SlipGaji mList = modelList.get(position);
            myViewHolder.periodeTxt.setText(mList.getPeriode());
            myViewHolder.rangePeriodeTxt.setText(mList.getRangeperiode());
            myViewHolder.gajiBersihTxt.setText(numberFormat.format(Double.parseDouble(mList.getGajibersih())));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDetailClickListener.onItemClick(v,mList,position);
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
        public TextView periodeTxt,rangePeriodeTxt, gajiBersihTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            periodeTxt = (TextView) itemView.findViewById(R.id.periode_txt);
            rangePeriodeTxt = (TextView) itemView.findViewById(R.id.range_periode_txt);
            gajiBersihTxt = (TextView) itemView.findViewById(R.id.gaji_bersih_txt);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_circular);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position){
        //ProgressBar would be displayed
    }
}
