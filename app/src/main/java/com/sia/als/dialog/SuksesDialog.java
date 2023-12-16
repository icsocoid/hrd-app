package com.sia.als.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.sia.als.R;
import com.sia.als.activity.ForgotActivity;
import com.sia.als.activity.ResetDeviceActivity;
import com.sia.als.fragment.AbsensiFragment;
import com.sia.als.fragment.AccountFragment;
import com.sia.als.fragment.AddIzinFragment;
import com.sia.als.fragment.AddOfficeFragment;
import com.sia.als.fragment.AddTaskFragment;
import com.sia.als.fragment.AddTerlambatFragment;
import com.sia.als.fragment.CompletionPerdinFragment;
import com.sia.als.fragment.DetailNotifPengajuanAbsensiFragment;
import com.sia.als.fragment.DetailPerdinPartnerFragment;
import com.sia.als.fragment.NotifyIzinFragment;

@SuppressLint("ValidFragment")
public class SuksesDialog extends DialogFragment {
    private View view;
    public TextView headerTxt,bodyTxt;
    Button okBtn;
    AddIzinFragment addIzinFragment;
    AddTaskFragment addTaskFragment;
    NotifyIzinFragment notifyIzinFragment;
    ForgotActivity forgotActivity;
    AddOfficeFragment addOfficeFragment;
    AbsensiFragment absensiFragment;
    AddTerlambatFragment addTerlambatFragment;
    AccountFragment accountFragment;
    CompletionPerdinFragment completionPerdinFragment;
    DetailPerdinPartnerFragment detailPerdinPartnerFragment;

    public SuksesDialog(AddIzinFragment addIzinFragment)
    {
        this.addIzinFragment = addIzinFragment;
    }
    public SuksesDialog(AddTerlambatFragment addTerlambatFragment)
    {
        this.addTerlambatFragment = addTerlambatFragment;
    }
    public SuksesDialog(AbsensiFragment absensiFragment)
    {
        this.absensiFragment = absensiFragment;
    }
    public SuksesDialog(NotifyIzinFragment notifyIzinFragment)
    {
        this.notifyIzinFragment = notifyIzinFragment;
    }
    public SuksesDialog(AddOfficeFragment addOfficeFragment)
    {
        this.addOfficeFragment = addOfficeFragment;
    }
    public SuksesDialog(AccountFragment accountFragment)
    {
        this.accountFragment = accountFragment;
    }

    public SuksesDialog(ForgotActivity forgotActivity)
    {
        this.forgotActivity = forgotActivity;
    }

    public SuksesDialog(AddTaskFragment addTaskFragment) {
        this.addTaskFragment = addTaskFragment;
    }
    public SuksesDialog(CompletionPerdinFragment completionPerdinFragment) {
        this.completionPerdinFragment = completionPerdinFragment;
    }
    public SuksesDialog(DetailPerdinPartnerFragment detailPerdinPartnerFragment) {
        this.detailPerdinPartnerFragment = detailPerdinPartnerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sukses_dialog, container,
                false);
        headerTxt = (TextView) view.findViewById(R.id.header_txt);
        bodyTxt = (TextView) view.findViewById(R.id.body_txt);
        okBtn = (Button) view.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addIzinFragment != null) {
                    addIzinFragment.showListIzin();
                }
                if(notifyIzinFragment != null)
                {
                    notifyIzinFragment.showListIzin();
                }
                if(forgotActivity != null)
                {
                    forgotActivity.showLogin();
                }
                if(addOfficeFragment != null)
                {
                    addOfficeFragment.backToList();
                }
                if(addTerlambatFragment != null)
                {
                    addTerlambatFragment.showListTerlambat();
                }
                if(accountFragment != null)
                {
                    accountFragment.showAccount();
                }
                if(addTaskFragment != null)
                {
                    addTaskFragment.showTask();
                }
                if(completionPerdinFragment != null)
                {
                    completionPerdinFragment.showTask();
                }if(detailPerdinPartnerFragment != null)
                {
                    detailPerdinPartnerFragment.showTask();
                }
                SuksesDialog.this.dismiss();
            }
        });

        return view;
    }
}
