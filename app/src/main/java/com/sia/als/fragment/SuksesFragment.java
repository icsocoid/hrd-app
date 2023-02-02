package com.sia.als.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sia.als.R;
import com.sia.als.activity.LoginActivity;

public class SuksesFragment extends Fragment {

    private View view;
    ImageButton backButton;
    TextView toolbarTitle;
    Button okBtn,saveBtn;
    ImageView signImg;
    TextView headerTxt,bodyTxt;
    String page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sukses, container, false);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("SUKSES");
        backButton.setVisibility(View.GONE);
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        okBtn = (Button) view.findViewById(R.id.ok_btn);
        signImg = (ImageView) view.findViewById(R.id.sign_image);
        headerTxt = (TextView) view.findViewById(R.id.header_txt);
        bodyTxt = (TextView) view.findViewById(R.id.body_txt);
        headerTxt.setText("NOTIFIKASI");
        if(getArguments() != null)
        {
            String sign = getArguments().getString("status");
            if(sign.equals("fail"))
            {
                signImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fail));
                headerTxt.setText("FAILED");
            }

            bodyTxt.setText(getArguments().getString("body_message"));
            page = getArguments().getString("page");
        }
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page.equals("login"))
                {
                    Intent loginInten = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginInten);
                }
                else
                {
                    AbsensiFragment submitFragment = new AbsensiFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.m_frame, submitFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }

            }
        });
        return view;
    }
}
