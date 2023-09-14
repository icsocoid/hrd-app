package com.sia.als.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.TaskName;

import java.util.List;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.MyViewHolder> {
    List<TaskName> modelList;
    Context context;

    public TaskItemAdapter(Context context,List<TaskName> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public TaskItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_task, parent, false);
        context = parent.getContext();
        return new TaskItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskItemAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final TaskName mList = modelList.get(position);
        holder.namaTxt.setText(mList.getTaskName());
        holder.removeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<modelList.size();i++){
                    TaskName taskModel = new TaskName();
                    taskModel.setTaskName(modelList.get(i).getTaskName());
                    modelList.set(i,taskModel);
                }
                modelList.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.namaTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mList.setTaskName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView namaTxt;
        ImageView removeRow;

        public MyViewHolder(View view) {
            super(view);
            namaTxt = (TextView) view.findViewById(R.id.add_task_name);
            removeRow = (ImageView) view.findViewById(R.id.remove_row);
        }
    }
}
