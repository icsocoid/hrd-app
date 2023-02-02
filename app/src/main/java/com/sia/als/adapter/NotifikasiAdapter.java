package com.sia.als.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Absensi;
import com.sia.als.model.Izin;
import com.sia.als.model.Notifikasi;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.ImageUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifikasiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Notifikasi> mNotif;
    private OnItemClickListener mOnItemClickListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public NotifikasiAdapter(Context context, List<Notifikasi> mNotif)
    {
        this.context = context;
        this.mNotif = mNotif;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Notifikasi obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifikasi, parent, false);
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

    @Override
    public int getItemViewType(int position) {
        return mNotif.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final Notifikasi mNot = mNotif.get(position);
            myViewHolder.subjectTxt.setText(mNot.getSubject());
            myViewHolder.messageTxt.setText(mNot.getMessage());
            String oldFormat = "yyyy-MM-dd";
            String pattern = "EEEE, dd MMMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
            try {
                Date d = sdf.parse(mNot.getTanggal());
                sdf.applyPattern(pattern);
                String newDateString = sdf.format(d);
                myViewHolder.timeTxt.setText(newDateString);
            }
            catch (ParseException p)
            {

            }
            if(mNot.getStatusNotif().equals("0"))
            {
                //myViewHolder.signStatus.setBackgroundResource(R.drawable.ic_ceklist);
                //holder.statusTxt.setText("BELUM DIBACA");
                myViewHolder.signStatus.setVisibility(View.GONE);
            }
            else if(mNot.getStatusNotif().equals("1"))
            {
                myViewHolder.signStatus.setVisibility(View.VISIBLE);
                myViewHolder.signStatus.setBackgroundResource(R.drawable.ic_ceklist);
                //holder.statusTxt.setText("DIBACA");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,mNot,position);
                }
            });
        }
        else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mNotif.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTxt,subjectTxt,timeTxt;
        ImageView profileImage;
        View signStatus;
        public MyViewHolder(View view) {
            super(view);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            messageTxt = (TextView) view.findViewById(R.id.message_txt);
            subjectTxt = (TextView) view.findViewById(R.id.subject_txt);
            timeTxt = (TextView) view.findViewById(R.id.time_txt);
            signStatus = (View) view.findViewById(R.id.sign_status);

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
