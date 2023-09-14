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
import com.sia.als.model.Task;
import com.sia.als.model.TaskDetail;

import java.util.List;

public class TaskDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<TaskDetail> modelList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public TaskDetailAdapter(android.content.Context context, List<TaskDetail> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task_detail, parent, false);
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
        if (holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final TaskDetail mList = modelList.get(position);
            myViewHolder.nameTxt.setText(mList.getTaskName());
        }else if (holder instanceof  LoadingViewHolder){
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }


    @Override
    public int getItemCount() { return modelList.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTxt;
        public MyViewHolder( View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.task_name);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View view) {
            super(view);
            progressBar = view.findViewById(R.id.progress_circular);
        }
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {
    }

}
