package com.sia.als.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sia.als.R;
import com.sia.als.fragment.CompletionPerdinFragment;
import com.sia.als.model.Penyelesaian;

import java.util.ArrayList;
import java.util.List;

public class PerdinPenyelesaianAdapter extends RecyclerView.Adapter<PerdinPenyelesaianAdapter.MyViewHolder> {
    List<Penyelesaian> modelList;
    CompletionPerdinFragment appFragment;
    Context context;
    Bitmap bitmap;
    private  OnFileClickListener onFileClickListener;

    int REQUEST_CAMERA = 100;
    int REQUEST_GALLERY = 20;
    int bitmap_size = 60; // range 1 - 100

    public PerdinPenyelesaianAdapter(Context context, ArrayList<Penyelesaian> penyelesaianList) {
        this.context = context;
        this.modelList = penyelesaianList;
    }

    public interface OnFileClickListener{
        void onItemClick(View view, String val);
    }
    public void setOnFileClickListener (final OnFileClickListener onFileClickListener){
        this.onFileClickListener = onFileClickListener;
    }



    public PerdinPenyelesaianAdapter(Context context, List<Penyelesaian> modelList, CompletionPerdinFragment appFragment){
        this.appFragment = appFragment;
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public PerdinPenyelesaianAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_perdin_completion, parent, false);
        context = parent.getContext();
        return new PerdinPenyelesaianAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PerdinPenyelesaianAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Penyelesaian mList = modelList.get(position);
        holder.imageAttachBs.setImageBitmap(null);

        if (mList.getBitmap() != null) {
            holder.imageAttachBs.setImageBitmap(mList.getBitmap());
        }
        holder.loadImageBs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appFragment.showAddImageDialog(REQUEST_CAMERA, REQUEST_GALLERY, position);
            }
        });

        holder.noteTxt.setText(mList.getNotePerdin());
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<modelList.size();i++){
                    Penyelesaian penyelesaian = new Penyelesaian();
                    penyelesaian.setNotePerdin(modelList.get(i).getNotePerdin());
                    penyelesaian.setBitmap(modelList.get(i).getBitmap());
                    modelList.set(i,penyelesaian);
                }
                modelList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.noteTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mList.setNotePerdin(s.toString());
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAttachBs;
        ImageButton removeBtn;
        ConstraintLayout loadImageBs;
        EditText noteTxt;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAttachBs = (ImageView) itemView.findViewById(R.id.imageview_add_picture_bs_placeholder);
            loadImageBs = (ConstraintLayout) itemView.findViewById(R.id.constraint_add_picture_bs_placeholder);
            noteTxt = (EditText) itemView.findViewById(R.id.catatan_txt);
            removeBtn = (ImageButton) itemView.findViewById(R.id.remove_btn);


        }
    }


}
