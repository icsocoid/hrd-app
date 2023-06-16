package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sia.als.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SlipGajiFragment extends Fragment {

    private View view;
    ImageButton close;
    TextView toolbarTitle, today;
    ImageView imageView;
    Button btFilter;
    LinearLayout filterDialogBtn, calender;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM");

    private int mMonth, mYear;
    private String dariTanggal;





    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_development, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        imageView = (ImageView) view.findViewById(R.id.img_choose_date);
        filterDialogBtn = (LinearLayout) view.findViewById(R.id.layoutJournalChooseDate);

        toolbarTitle.setText("My Salary");
//        filterDialogBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){showFilterDialog(); }
//        });
        return view;
    }
//    private void showFilterDialog()
//    {
//        Dialog dialog = new Dialog(getContext());
//        dialog.setContentView(R.layout.dialog_filter_bulan);
//
//        close = dialog.findViewById(R.id.bt_close);
//        close.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view ){dialog.dismiss();}
//        });
//
//        btFilter = (Button) dialog.findViewById(R.id.bt_filter);
//        btFilter.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                dialog.dismiss();
//            }
//        });

//        today = dialog.findViewById(R.id.today);
//        today.setText(simpleDateFormat.format(new Date()));
//        calender = dialog.findViewById(R.id.calender);
//
//        calender.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                int month = month+1;
//
//                            }
//                        }
//                );
//
//            }
//        });
//
//        dialog.show();
//    }
}

