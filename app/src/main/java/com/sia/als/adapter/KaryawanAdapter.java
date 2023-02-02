package com.sia.als.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Karyawan;
import com.sia.als.util.ImageUtil;

import java.util.List;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.MyViewHolder> {

    List<Karyawan> modelList;
    Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Karyawan obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public KaryawanAdapter(Context context,List<Karyawan> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public KaryawanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_karyawan, parent, false);
        context = parent.getContext();
        return new KaryawanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KaryawanAdapter.MyViewHolder holder, final int position) {
        final Karyawan mList = modelList.get(position);
        holder.namaTxt.setText(mList.getEmployeeName());
        holder.jabatanTxt.setText(mList.getJabatan());
        Bitmap pic = ImageUtil.convert(mList.getPhoto());
        holder.profileImage.setImageBitmap(pic);
        if(mList.getStatusEmployee().equals("0"))
        {
            holder.statusTxt.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            holder.statusTxt.setText("VERIFIKASI");
        }
        else if(mList.getStatusEmployee().equals("2"))
        {
            holder.statusTxt.setBackgroundColor(context.getResources().getColor(R.color.active_color));
            holder.statusTxt.setText("TIDAK AKTIF");
        }
        else if(mList.getStatusEmployee().equals("1"))
        {
            holder.statusTxt.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.statusTxt.setText("AKTIF");
        }
        else if(mList.getStatusEmployee().equals("3"))
        {
            holder.statusTxt.setBackgroundColor(context.getResources().getColor(R.color.orange));
            holder.statusTxt.setText("RESET");
        }

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

        TextView statusTxt,namaTxt,jabatanTxt;
        ImageView profileImage;

        public MyViewHolder(View view) {
            super(view);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            statusTxt = (TextView) view.findViewById(R.id.status_txt);
            namaTxt = (TextView) view.findViewById(R.id.nama_txt);
            jabatanTxt = (TextView) view.findViewById(R.id.jabatan_txt);
        }
    }

}

