package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Perdin;

import java.util.List;

public class PerdinDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Perdin> modelList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public PerdinDetailAdapter(android.content.Context context, List<Perdin> modelList){
        this.context = context;
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_perdin_detail, parent, false);
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
            final Perdin mList = modelList.get(position);
            myViewHolder.catatanTxt.setText(mList.getCatatanPerdin());
            if (mList.getBitmapPerdin() != null){
                ((MyViewHolder) holder).previewImg.setImageBitmap(mList.getBitmapPerdin());
            }
        } else
        {
            showLoadingView((PerdinDetailAdapter.LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() { return modelList.size(); }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView previewImg;
        TextView catatanTxt;
        public MyViewHolder(View itemView) {
            super(itemView);
            previewImg = (ImageView) itemView.findViewById(R.id.preview_img);
            catatanTxt = (TextView) itemView.findViewById(R.id.catatan_txt);
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
