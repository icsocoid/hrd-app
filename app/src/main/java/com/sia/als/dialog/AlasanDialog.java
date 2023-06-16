package com.sia.als.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.sia.als.R;
import com.sia.als.fragment.DetailIzinFragment;
import com.sia.als.fragment.DetailNotifPengajuanAbsensiFragment;
import com.sia.als.fragment.DetailPengajuanAbsensiFragment;

import org.w3c.dom.Text;

@SuppressLint("ValidFragment")
public class AlasanDialog extends DialogFragment {
    View view;
    EditText alasanTxt;
    TextView cancelBtn,saveBtn;
    DetailIzinFragment detailIzinFragment;
    DetailPengajuanAbsensiFragment detailPengajuanAbsensiFragment;
    DetailNotifPengajuanAbsensiFragment detailNotifPengajuanAbsensiFragment;

    public AlasanDialog(DetailIzinFragment detailIzinFragment)
    {
        super();
        this.detailIzinFragment = detailIzinFragment;
    }

    public AlasanDialog(DetailPengajuanAbsensiFragment detailPengajuanAbsensiFragment)
    {
        super();
        this.detailPengajuanAbsensiFragment = detailPengajuanAbsensiFragment;
    }

    public AlasanDialog(DetailNotifPengajuanAbsensiFragment detailNotifPengajuanAbsensiFragment)
    {
        super();
        this.detailNotifPengajuanAbsensiFragment = detailNotifPengajuanAbsensiFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reject_dialog, container,
                false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alasanTxt = (EditText) view.findViewById(R.id.alasan_tolak_txt);
        saveBtn = (TextView) view.findViewById(R.id.manage_product_form_category_button_save);
        cancelBtn = (TextView) view.findViewById(R.id.manage_product_form_category_button_cancel);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detailIzinFragment != null){
                    detailIzinFragment.makeUpdateStatusRequest("DISAPPROVE",alasanTxt.getText().toString());
                }
                if(detailPengajuanAbsensiFragment != null){
                    detailPengajuanAbsensiFragment.makeUpdateStatusRequest("DISAPPROVE",alasanTxt.getText().toString());
                }
                if(detailNotifPengajuanAbsensiFragment != null){
                    detailNotifPengajuanAbsensiFragment.makeUpdateStatusRequest("DISAPPROVE",alasanTxt.getText().toString());
                }
                dismiss();
            }
        });

        return view;
    }

}
