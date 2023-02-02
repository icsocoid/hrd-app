package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Izin;

import java.util.List;

public class BottomIzinAdapter extends RecyclerView.Adapter<BottomIzinAdapter.MyViewHolder> {

    List<Izin> modelList;
    Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Izin obj, int position);
    }
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
    public BottomIzinAdapter(Context context,List<Izin> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_jenis_izin, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Izin mList = modelList.get(position);
        holder.namaTxt.setText(mList.getNamaIzin());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,mList,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView namaTxt;

        public MyViewHolder(View view) {
            super(view);
            namaTxt = (TextView) view.findViewById(R.id.nama_izin_txt);
        }
    }
}
