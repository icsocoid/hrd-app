package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.PerdinDetailAdapter;
import com.sia.als.adapter.PerdinPenyelesaianAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.PictureDialog;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.Penyelesaian;
import com.sia.als.model.TaskName;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CompletionPerdinFragment extends Fragment {
    private View view;
    ArrayList<Penyelesaian> penyelesaianList = new ArrayList<>();
    PerdinPenyelesaianAdapter penyelesaianAdapter;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;

    TextView toolbarTitle, nomorPerdinTxt, tglPerdinTxt, namaKlienTxt, nominalPerdinTxt, nominalBonSementaraTxt;
    String oldFormat = "yyyy-MM-dd";
    String patternIzin = "dd MMMM yyyy";
    Locale localeID;
    NumberFormat numberFormat;
    private String perdinId;
    SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    Bitmap bitmap;
    ImageButton backButton;
    SessionManagement sessionManagement;
    int REQUEST_CAMERA = 100;
    int REQUEST_GALLERY = 20;
    int bitmap_size = 60; // range 1 - 100
    Button buttonAdd, notif;
    private int position;
    String id;
    boolean isLoading = false;
    boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_penyelesaian, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Perjalanan Dinas");
        sessionManagement = new SessionManagement(getContext());
        nomorPerdinTxt = (TextView) view.findViewById(R.id.nomor_perdin_txt);
        namaKlienTxt = (TextView) view.findViewById(R.id.nama_klien_txt);
        tglPerdinTxt = (TextView) view.findViewById(R.id.tanggal_perdin_txt);
        nominalPerdinTxt = (TextView) view.findViewById(R.id.nominal_perdin_txt);
        nominalBonSementaraTxt = (TextView) view.findViewById(R.id.nominal_bs_txt);
        buttonAdd = (Button) view.findViewById(R.id.add_row);
        penyelesaianAdapter = new PerdinPenyelesaianAdapter(getContext(), penyelesaianList, this);
        rvList = (RecyclerView) view.findViewById(R.id.layout_list);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        localeID = new Locale("in", "ID");
        numberFormat = NumberFormat.getNumberInstance(localeID);

        rvList.setLayoutManager(linearLayoutManager);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(penyelesaianAdapter);
        backButton.setVisibility(View.GONE);
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
            }
        });


        if(getArguments() != null){
            id = getArguments().getString("perdin_id");
            makeDataRequest(id);
        }
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDataSubmit();
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

    public void showAddImageDialog(int request_camera, int request_gallery, int position) {
        setPosition(position);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PictureDialog addPictureDialogFragment = new PictureDialog(request_camera,request_gallery);
        addPictureDialogFragment.setTargetFragment( this, request_camera);
        addPictureDialogFragment.show(fm, addPictureDialogFragment.getTag());
    }

    private void addView() {
        for (int i=0;i<penyelesaianList.size();i++){
            Penyelesaian penyelesaianModel = new Penyelesaian();
            penyelesaianModel.setBitmap(penyelesaianList.get(i).getBitmap());
            penyelesaianModel.setPhotoPerdin(penyelesaianList.get(i).getPhotoPerdin());
            penyelesaianModel.setNotePerdin(penyelesaianList.get(i).getNotePerdin());
            penyelesaianList.set(i,penyelesaianModel);
        }
        Penyelesaian penyelesaianModel = new Penyelesaian();
        penyelesaianModel.setBitmap(null);
        penyelesaianModel.setPhotoPerdin("");
        penyelesaianModel.setNotePerdin("");
        penyelesaianList.add(penyelesaianModel);
        penyelesaianAdapter.notifyDataSetChanged();
    }

    private void showFragment() {
        PerdinFragment perdinFragment = new PerdinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {

            //mengambil fambar dari Camera
            String photo = "";
            bitmap = (Bitmap) data.getExtras().get("data");
            penyelesaianList.get(getPosition()).setBitmap(bitmap);
            if (bitmap != null)
            {
                photo = ImageUtil.convert(bitmap);
            }
            penyelesaianList.get(getPosition()).setPhotoPerdin(photo);
            penyelesaianAdapter.notifyDataSetChanged();

//            imageAttachBs.setImageBitmap(bitmap);

        }

        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                final int maxSize = 240;
                int outWidth;
                int outHeight;
                int inWidth = bitmap.getWidth();
                int inHeight = bitmap.getHeight();
                if (inWidth > inHeight) {
                    outWidth = maxSize;
                    outHeight = (inHeight * maxSize) / inWidth;
                } else {
                    outHeight = maxSize;
                    outWidth = (inWidth * maxSize) / inHeight;
                }

                bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight,
                        false);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                penyelesaianList.get(getPosition()).setBitmap(bitmap);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                String photo = "";
                if (bitmap != null)
                {
                    photo = ImageUtil.convert(decoded);
                }
                penyelesaianList.get(getPosition()).setPhotoPerdin(photo);
                penyelesaianAdapter.notifyDataSetChanged();
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
//                imageAttachBs.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeDataSubmit() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_perdin_req";
        Map<String, String> params = new HashMap<>();
        JSONArray photoArray = new JSONArray();
        if (penyelesaianList.size()>0){
            for (int i = 0; i < penyelesaianList.size(); i++){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("note", penyelesaianList.get(i).getNotePerdin());
                    jsonObject.put("photo", penyelesaianList.get(i).getPhotoPerdin());
                    photoArray.put(jsonObject);


                }catch (JSONException e){
                    throw new RuntimeException(e);
                }
            }
        }
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);

        params.put("photo_perdin", photoArray.toString());
        params.put("user_id", userId);
        params.put("perdin_id", String.valueOf(getPerdinId()));


        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.PENYELESAIAN_PERDIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status){
                        SuksesDialog suksesDialog = new SuksesDialog(CompletionPerdinFragment.this);
                        suksesDialog.show(getFragmentManager(),"");
                    }else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG,TastyToast.CONFUSING).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(getActivity(), "" + e, Toast.LENGTH_LONG,TastyToast.CONFUSING).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void makeDataRequest(String id) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("please wait...");
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        dialog.show();
        String tag_json_obj = "json_data_perdin_req";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("user_id", userId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_PERDIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status) {
                        JSONObject obj = response.getJSONObject("perdin");
                        JSONObject detailObj = obj.getJSONObject("detail");
                        String namaKlien = obj.getString("nama_klien");
                        String nomorPerdin = obj.getString("no_transaksi");
                        String tanggalPerdin = obj.getString("tanggal");
                        String nominalPerdin = detailObj.getString("nominal_perdin");
                        String nominalBs = detailObj.getString("nominal_bs");
                        String perdinId = detailObj.getString("perdin_id");

                        try {
                            Date d = sdf.parse(tanggalPerdin);
                            sdf.applyPattern(patternIzin);
                            tanggalPerdin = sdf.format(d);
                        }
                        catch (ParseException p)
                        {

                        }

                        namaKlienTxt.setText(namaKlien);
                        nomorPerdinTxt.setText(nomorPerdin);
                        tglPerdinTxt.setText(tanggalPerdin);
                        nominalPerdinTxt.setText(numberFormat.format(Double.parseDouble(nominalPerdin)));
                        nominalBonSementaraTxt.setText(numberFormat.format(Double.parseDouble(nominalBs)));
                        setPerdinId(perdinId);

                        try {
                            JSONArray jsonArray = obj.getJSONArray("detailcomplete");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Penyelesaian penyelesaian = new Penyelesaian();
                                String photoPerdin = jsonObject.getString("photo");
                                bitmap = ImageUtil.convert(photoPerdin);
                                penyelesaian.setBitmap(bitmap);
                                penyelesaian.setNotePerdin(jsonObject.getString("keterangan"));
                                penyelesaianList.add(penyelesaian);
                            }
                            if (penyelesaianAdapter == null){
                                isFirst = false;
                                penyelesaianAdapter = new PerdinPenyelesaianAdapter(getContext(), penyelesaianList);
                                rvList.setAdapter(penyelesaianAdapter);
                            }
                            else {
                                penyelesaianAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e ){
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }

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


    public String getPerdinId() {
        return perdinId;
    }

    public void setPerdinId(String perdinId) {
        this.perdinId = perdinId;
    }

    public void showTask() {
        PerdinFragment perdinFragment = new PerdinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
