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
import com.sia.als.model.Perdin;
import com.sia.als.model.PerdinPartner;

import java.util.List;

public class PerdinPartnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PerdinPartner> modelList;
    Context context;
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener mOnEditClickListener;
    private OnDetailClickListener mOnDetailClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheet;
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

    public interface OnItemClickListener{
        void onItemClick(View view, PerdinPartner obj, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void showBottomSheetDialog(String val, String statusId) {
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
        if (statusId.equals("0") || statusId.equals("1")){
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

    public PerdinPartnerAdapter(Context context, List<PerdinPartner> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_perdin_partner, parent, false);
            context = parent.getContext();
            return new MyViewHolder(itemView);
        }else
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final PerdinPartner mList = modelList.get(position);
            myViewHolder.nomorPerdinTxt.setText(mList.getNomorPerdin());
            myViewHolder.namaKlien.setText(mList.getNamaKlien());
            myViewHolder.tglAwal.setText(mList.getTanggalAwal());
            myViewHolder.tglAkhir.setText(mList.getTanggalAkhir());
            myViewHolder.namaKlien.setText(mList.getNamaKlien());
            myViewHolder.jumlahEmp.setText(mList.getJumlahKaryawan());

            if (mList.getStatusId().equals("0")){
                myViewHolder.statusTxt.setText("PROSES");
                myViewHolder.statusTxt.setTextColor(context.getResources().getColor(R.color.md_orange_700));
                myViewHolder.statusTxt.setBackground(context.getDrawable(R.drawable.bg_status_0));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialog(mList.getId().toString(), mList.getStatusId().toString());
                    }
                });
            }else if (mList.getStatusId().equals("1")){
                myViewHolder.statusTxt.setText("NEED APPROVAL");
                myViewHolder.statusTxt.setTextColor(context.getResources().getColor(R.color.md_yellow_700));
                myViewHolder.statusTxt.setBackground(context.getDrawable(R.drawable.bg_status_1));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v,mList,position);
                    }
                });
            }else {
                myViewHolder.statusTxt.setText("APPROVE");

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

        public TextView nomorPerdinTxt, statusTxt, tglAwal, tglAkhir, namaKlien, jumlahEmp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomorPerdinTxt = (TextView) itemView.findViewById(R.id.no_perdin_txt);
            tglAwal = (TextView) itemView.findViewById(R.id.tgl_awal_txt);
            tglAkhir = (TextView) itemView.findViewById(R.id.tgl_akhir_txt);
            statusTxt = (TextView) itemView.findViewById(R.id.status_perdin_txt);
            jumlahEmp = (TextView) itemView.findViewById(R.id.jumlah_karyawan_txt);
            namaKlien = (TextView) itemView.findViewById(R.id.nama_klien_txt);
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
