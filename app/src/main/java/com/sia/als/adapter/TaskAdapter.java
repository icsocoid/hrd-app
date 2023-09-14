package com.sia.als.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sia.als.R;
import com.sia.als.model.Task;
import com.sia.als.util.Utility;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Task> modelList;
    Context context;
    private OnEditClickListener mOnEditClickListener;
    private OnDetailClickListener mOnDetailClickListener;
    private OnItemClickListener onItemClickListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheet;

    public interface OnItemClickListener{
        void onItemClick(View view, Task obj, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnDetailClickListener{
        void onItemClick(View view, String val);
    }

    public interface OnEditClickListener{
        void onItemClick(View view, String val);
    }

    public void setmOnDetailClickListener (final OnDetailClickListener mOnDetailClickListener){
        this.mOnDetailClickListener = mOnDetailClickListener;
    }

    public void setmOnEditClickListener(final OnEditClickListener mOnEditClickListener){
        this.mOnEditClickListener = mOnEditClickListener;
    }
    public TaskAdapter(android.content.Context context, List<Task> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent, false);
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

    private void showBottomSheetDialog(String val, String statusTask) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View viewDialog = LayoutInflater.from(this.context).inflate(R.layout.sheet_task_menu, null);
        mBottomSheetDialog = new BottomSheetDialog((this.context));
        mBottomSheetDialog.setContentView(viewDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
        Button editBtn = (Button) viewDialog.findViewById(R.id.edit_task_btn);
        if (statusTask.equals("0")){
            editBtn.setVisibility(View.VISIBLE);
        }else {
            editBtn.setVisibility(View.GONE);

        }
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEditClickListener.onItemClick(v, val);
                mBottomSheetDialog.dismiss();
            }
        });

        Button detailBtn = (Button) viewDialog.findViewById(R.id.detail_btn);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDetailClickListener.onItemClick(v, val);
                mBottomSheetDialog.dismiss();
            }
        });
    }

    public int getItemViewType(int position){
        return modelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final Task mList = modelList.get(position);
            myViewHolder.bulanTxt.setText(mList.getBulan());
            myViewHolder.tahunTxt.setText(mList.getTahun());
            myViewHolder.jumlahTaskTxt.setText(mList.getJumlah());
            bottomSheet = ((Activity)context).findViewById(R.id.bottom_sheet);
            mBehavior = BottomSheetBehavior.from(bottomSheet);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(mList.getId().toString(), mList.getStatus().toString());
                }
            });

            myViewHolder.bulanTxt.setText(Utility.convertBulanIndo(mList.getBulan()));

            if (mList.getStatus().equals("0")){
                myViewHolder.statusTxt.setText("PROSES");
                myViewHolder.statusTxt.setTextColor(context.getResources().getColor(R.color.md_orange_700));
                myViewHolder.statusTxt.setBackground(context.getDrawable(R.drawable.bg_status_0));
            }
            else {
                myViewHolder.statusTxt.setText("SELESAI");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v,mList,position);
                    }
                });
            }
        }
        else if (holder instanceof LoadingViewHolder){
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView bulanTxt, tahunTxt, jumlahTaskTxt, statusTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            bulanTxt = (TextView) itemView.findViewById(R.id.bulan_task_txt);
            tahunTxt = (TextView) itemView.findViewById(R.id.tahun_task_txt);
            jumlahTaskTxt = (TextView) itemView.findViewById(R.id.jumlah_task_txt);
            statusTxt = (TextView) itemView.findViewById(R.id.status_txt);

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
