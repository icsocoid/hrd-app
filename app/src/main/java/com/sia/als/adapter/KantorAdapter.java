package com.sia.als.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.model.Kantor;

import java.util.List;

public class KantorAdapter extends RecyclerView.Adapter<KantorAdapter.MyViewHolder> {

    List<Kantor> modelList;
    Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Kantor obj, int position);
    }
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public KantorAdapter(Context context,List<Kantor> modelList)
    {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public KantorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_office, parent, false);
        context = parent.getContext();
        return new KantorAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KantorAdapter.MyViewHolder holder, final int position) {
        final Kantor mList = modelList.get(position);
        holder.namaTxt.setText(mList.getNamaKantor());
        holder.statusTxt.setText(mList.getStatusKantor());
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

        TextView statusTxt,namaTxt;

        public MyViewHolder(View view) {
            super(view);
            statusTxt = (TextView) view.findViewById(R.id.status_office_txt);
            namaTxt = (TextView) view.findViewById(R.id.nama_kantor_txt);
        }
    }

}
