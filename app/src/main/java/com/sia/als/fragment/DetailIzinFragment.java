package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.dialog.AlasanDialog;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailIzinFragment extends Fragment {
    View view;
    ImageButton backButton;
    TextView izinIdTxt,tanggalTxt,jenisTxt,tanggalAwalTxt,tanggalAkhirTxt,alasanTxt,statusTxt,tolakTxt;
    ImageView previewImg;
    Bitmap bitmap;
    String oldFormat = "yyyy-MM-dd";
    String patternIzin = "dd MMMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    FloatingActionButton shareBtn;
    LinearLayout layReport,layTolak;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    SessionManagement sessionManagement;
    String id;
    Button notif;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        view = inflater.inflate(R.layout.fragment_izin_detail, container, false);
        sessionManagement = new SessionManagement(getContext());
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
        izinIdTxt = (TextView) view.findViewById(R.id.izin_id_txt);
        tanggalTxt = (TextView) view.findViewById(R.id.tanggal_txt);
        jenisTxt = (TextView) view.findViewById(R.id.jenis_txt);
        tanggalAwalTxt = (TextView) view.findViewById(R.id.tanggal_awal_txt);
        tanggalAkhirTxt = (TextView) view.findViewById(R.id.tanggal_akhir_txt);
        alasanTxt = (TextView) view.findViewById(R.id.alasan_txt);
        statusTxt = (TextView) view.findViewById(R.id.status_txt);
        tolakTxt = (TextView) view.findViewById(R.id.alasan_tolak_txt);
        previewImg = (ImageView) view.findViewById(R.id.preview_img);
        layReport = (LinearLayout) view.findViewById(R.id.share_layout);
        layTolak = (LinearLayout) view.findViewById(R.id.lay_ditolak);
        shareBtn = (FloatingActionButton) view.findViewById(R.id.fab_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               shareResult();
            }
        });
        if(getArguments() != null)
        {
            id = getArguments().getString("id_izin");
            makeDataRequest(id);
        }

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.rotate_backward);
        if(sessionManagement.isAdmin())
        {
            fab.show();
            shareBtn.hide();
        }
        else
        {
            shareBtn.show();
            fab.hide();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlasanDialog alasanDialog = new AlasanDialog(DetailIzinFragment.this);
                alasanDialog.show(getFragmentManager(),"");
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Anda akan setujui izin ini!")
                        .setConfirmText("Ya!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                makeUpdateStatusRequest("APPROVE","");
                                sDialog.hide();
                            }
                        })
                        .show();
              //
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    private void showFragment() {
        IzinFragment izinFragment = new IzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, izinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showTolakLayout(String tolak)
    {
        layTolak.setVisibility(View.VISIBLE);
        tolakTxt.setVisibility(View.VISIBLE);
        tolakTxt.setText(tolak);
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;


        }
    }

    private void shareResult(){
        try {
            Bitmap bitmapShare = getBitmapFromView();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getContext(), bitmapShare));
            shareIntent.setType("image/jpeg");
            // shareActionProvider.setShareIntent(shareIntent);
            startActivity(Intent.createChooser(shareIntent, "Share"));

        }catch (Exception e){
            e.getMessage();
        }

    }

    private Bitmap getBitmapFromView() {


        //Assign a size and position to the view and all of its descendants
        layReport.layout(0, 0, layReport.getMeasuredWidth(), layReport.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmapLay = Bitmap.createBitmap(layReport.getMeasuredWidth(),
                layReport.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmapLay);
        Drawable background = layReport.getBackground();

        if (background != null) {
            background.draw(c);
        }
        //Render this view (and all of its children) to the given Canvas
        layReport.draw(c);
        return bitmapLay;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "", "");
            return Uri.parse(path);
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }

    public void makeUpdateStatusRequest(String status,String alasan)
    {
        //status setuju APPROVE, tolak DISAPPROVE
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_status_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_izin", id);
        params.put("status", status);
        params.put("alasan", alasan);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_STATUS_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("izin");
                        String namaIzin = obj.getString("nama_izin");
                        String tanggal = obj.getString("tanggal");
                        String tanggalAwal = obj.getString("tanggal_awal");
                        String tanggalAkhir = obj.getString("tanggal_akhir");
                        String keterangan = obj.getString("keterangan");
                        String statusIzin = obj.getString("status_izin");
                        String noIzin = obj.getString("izin_id");
                        String alasanReject = obj.getString("alasan_reject");

                        if(statusIzin.equals("APPROVE"))
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.green));
                        }
                        else if(statusIzin.equals("PENDING"))
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.colorOrange));
                        }
                        else
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.active_color));
                            layTolak.setVisibility(View.VISIBLE);
                            tolakTxt.setVisibility(View.VISIBLE);
                            tolakTxt.setText(alasanReject);
                        }

                        try {
                            Date d = sdf.parse(tanggal);
                            sdf.applyPattern(patternIzin);
                            tanggal = sdf.format(d);

                            sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
                            Date tglMulai = sdf.parse(tanggalAwal);
                            sdf.applyPattern(patternIzin);
                            tanggalAwal = sdf.format(tglMulai);

                            sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
                            Date tglSls = sdf.parse(tanggalAkhir);
                            sdf.applyPattern(patternIzin);
                            tanggalAkhir = sdf.format(tglSls);
                        }
                        catch (ParseException p)
                        {

                        }
                        String photo = obj.getString("photo");
                        bitmap = ImageUtil.convert(photo);
                        jenisTxt.setText(namaIzin);
                        tanggalTxt.setText(tanggal);
                        tanggalAwalTxt.setText(tanggalAwal);
                        tanggalAkhirTxt.setText(tanggalAkhir);
                        alasanTxt.setText(keterangan);
                        statusTxt.setText(statusIzin);
                        izinIdTxt.setText(noIzin);
                        previewImg.setImageBitmap(bitmap);
                       // TastyToast.makeText(getActivity(), "" + response.getString("message"), Toast.LENGTH_LONG,TastyToast.SUCCESS).show();
                        new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sukses")
                                .setContentText(response.getString("message"))
                                .show();
                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT,TastyToast.ERROR).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void makeDataRequest(String idIzin) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_izin", idIzin);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("izin");
                        String namaIzin = obj.getString("nama_izin");
                        String tanggal = obj.getString("tanggal");
                        String tanggalAwal = obj.getString("tanggal_awal");
                        String tanggalAkhir = obj.getString("tanggal_akhir");
                        String keterangan = obj.getString("keterangan");
                        String statusIzin = obj.getString("status_izin");
                        String noIzin = obj.getString("izin_id");
                        String alasanReject = obj.getString("alasan_reject");

                        if(statusIzin.equals("APPROVE"))
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.green));
                        }
                        else if(statusIzin.equals("PENDING"))
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.colorOrange));
                        }
                        else
                        {
                            statusTxt.setTextColor(getContext().getResources().getColor(R.color.active_color));
                            layTolak.setVisibility(View.VISIBLE);
                            tolakTxt.setVisibility(View.VISIBLE);
                            tolakTxt.setText(alasanReject);
                        }

                        try {
                            Date d = sdf.parse(tanggal);
                            sdf.applyPattern(patternIzin);
                            tanggal = sdf.format(d);

                            sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
                            Date tglMulai = sdf.parse(tanggalAwal);
                            sdf.applyPattern(patternIzin);
                            tanggalAwal = sdf.format(tglMulai);

                            sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
                            Date tglSls = sdf.parse(tanggalAkhir);
                            sdf.applyPattern(patternIzin);
                            tanggalAkhir = sdf.format(tglSls);
                        }
                        catch (ParseException p)
                        {

                        }
                        String photo = obj.getString("photo");
                        bitmap = ImageUtil.convert(photo);
                        jenisTxt.setText(namaIzin);
                        tanggalTxt.setText(tanggal);
                        tanggalAwalTxt.setText(tanggalAwal);
                        tanggalAkhirTxt.setText(tanggalAkhir);
                        alasanTxt.setText(keterangan);
                        statusTxt.setText(statusIzin);
                        izinIdTxt.setText(noIzin);
                        previewImg.setImageBitmap(bitmap);

                    } else {
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
