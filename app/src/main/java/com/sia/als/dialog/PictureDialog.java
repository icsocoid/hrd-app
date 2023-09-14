package com.sia.als.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.sia.als.R;
import com.sia.als.adapter.PerdinPenyelesaianAdapter;

@SuppressLint("ValidFragment")
public class PictureDialog extends DialogFragment {
    View view;
    Button cameraBtn,fileBtn;
   // ProfileFragment profileFragment;
    int REQUEST_CAMERA = 100;
    int SELECT_FILE = 20;
    public PictureDialog(int reqCamera,int reqFile)
    {
     //   this.profileFragment = profileFragment;
        this.REQUEST_CAMERA = reqCamera;
        this.SELECT_FILE = reqFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picture_dialog, container,
                false);
        cameraBtn = (Button) view.findViewById(R.id.picture_btn);
        fileBtn = (Button) view.findViewById(R.id.gallery_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getTargetFragment().startActivityForResult(intent, REQUEST_CAMERA);
                getDialog().dismiss();

            }
        });
        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                getTargetFragment().startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                getDialog().dismiss();
            }
        });


        return view;
    }

    public void setTargetFragment(PerdinPenyelesaianAdapter penyelesaianAdapter, int request_camera) {
    }
}
