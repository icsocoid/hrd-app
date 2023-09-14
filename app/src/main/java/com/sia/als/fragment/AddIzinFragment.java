package com.sia.als.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.BottomIzinAdapter;
import com.sia.als.adapter.IzinAdapter;
import com.sia.als.adapter.PengajuanAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.PictureDialog;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.Izin;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class AddIzinFragment extends Fragment {
    private View view;
    TextView toolbarTitle;
    ImageButton backButton;
    Button notif;
    SessionManagement sessionManagement;
    //Spinner izinCmb;
    BottomIzinAdapter izinAdapter;
    EditText jenisIzinTxt;
    ImageButton pilihanIzinBtn;
    //IzinAdapter izinAdapter;
    LinearLayout calender1, calender2;
    TextView today1, date;
    private int mMonth, mYear, mDay;
    List<Izin> izinList = new ArrayList<>();
    ImageView imageAttach;
    Bitmap bitmap;
    ConstraintLayout loadImage;
    int REQUEST_CAMERA = 100;
    int REQUEST_GALLERY = 20;
    int bitmap_size = 60; // range 1 - 100
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    EditText keteranganTxt;
    private String jenisIzinId;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheet;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    StatefulLayout statefulLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_add_izin, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Pengajuan Izin");
        backButton.setVisibility(View.GONE);
        sessionManagement = new SessionManagement(getContext());
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);
        bottomSheet = (View) view.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        linearLayoutManager = new LinearLayoutManager(getContext());
        //izinCmb = (Spinner) view.findViewById(R.id.spinner_izin);
        jenisIzinTxt = (EditText) view.findViewById(R.id.pilihan_izin_txt);
        pilihanIzinBtn = (ImageButton) view.findViewById(R.id.pilihan_izin_btn);
        imageAttach = (ImageView) view.findViewById(R.id.imageview_add_picture_placeholder);
        loadImage = (ConstraintLayout) view.findViewById(R.id.constraint_add_picture_placeholder);
        today1 = view.findViewById(R.id.today1);
        keteranganTxt = view.findViewById(R.id.keterangan_txt);
        today1.setText(simpleDateFormat.format(new Date()));
        calender1 = view.findViewById(R.id.calender1);
        calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // today1.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                int month = monthOfYear + 1;
                                String fm = "" + month;
                                String fd = "" + dayOfMonth;
                                if (month < 10) {
                                    fm = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    fd = "0" + dayOfMonth;
                                }
                                today1.setText(year + "-" + fm + "-" + fd);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        /*calender open onclick*/
        calender2 = view.findViewById(R.id.calender2);
        date = view.findViewById(R.id.date);
        date.setText(simpleDateFormat.format(new Date()));
        calender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                String fm = "" + month;
                                String fd = "" + dayOfMonth;
                                if (month < 10) {
                                    fm = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    fd = "0" + dayOfMonth;
                                }
                                date.setText(year + "-" + fm + "-" + fd);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialog(REQUEST_CAMERA, REQUEST_GALLERY);
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptData();
            }
        });
        pilihanIzinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        jenisIzinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        //makeIzinRequest();
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

    private void attemptData() {
        if (TextUtils.isEmpty(keteranganTxt.getText().toString())) {
            keteranganTxt.setTextColor(getResources().getColor(R.color.active_color));
            keteranganTxt.requestFocus();
        } else {
            FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lang = location.getLongitude();

                        //String alamatMaps = location.get(0)+ ", " + location.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                        makeRequestSubmitData(lat, lang);

                    } else {
                        TastyToast.makeText(getActivity(), "Gagal mendapatkan lokasi Anda..", Toast.LENGTH_LONG, TastyToast.ERROR).show();
                    }
                }
            });
        }
    }

    private void makeRequestSubmitData(double lat,double longg)
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        String photo = "";
        if(bitmap != null)
        {
            photo = ImageUtil.convert(bitmap);
        }
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        //int pos = izinCmb.getSelectedItemPosition();

        params.put("photo", photo);
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(longg));
        params.put("mulai", today1.getText().toString());
        params.put("akhir", date.getText().toString());
        params.put("user_id", userId);
        params.put("keterangan", keteranganTxt.getText().toString());
        params.put("izin_id", String.valueOf(getJenisIzinId()));
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADD_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {

                    Boolean status = response.getBoolean("status");
                    if (status) {
                        SuksesDialog suksesDialog = new SuksesDialog(AddIzinFragment.this);
                        suksesDialog.show(getFragmentManager(),"");

                    } else {

                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG,TastyToast.CONFUSING).show();

                    }
                }
                catch (JSONException e)
                {
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void showAddImageDialog(int reqCamera,int reqFile) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PictureDialog addPictureDialogFragment = new PictureDialog(reqCamera,reqFile);
        addPictureDialogFragment.setTargetFragment(this, reqCamera);
        addPictureDialogFragment.show(fm, getTag());
    }

   /* private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {

            //mengambil fambar dari Camera
            bitmap = (Bitmap) data.getExtras().get("data");

            imageAttach.setImageBitmap(bitmap);

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

                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                imageAttach.setImageBitmap(decoded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showListIzin()
    {
        IzinFragment izinFragment = new IzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, izinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View viewDialog = getLayoutInflater().inflate(R.layout.sheet_izin_list, null);
        statefulLayout = (StatefulLayout) viewDialog.findViewById(R.id.stateful);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        statefulLayout.setStateView(Config.STATE_ERROR, errorView);
        recyclerView = (RecyclerView) viewDialog.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        makeIzinRequest();

        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(viewDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void makeIzinRequest()
    {
        statefulLayout.setState(Config.STATE_PROGRESS);
        String tag_json_obj = "json_izin_req";
        Map<String, String> params = new HashMap<String, String>();

        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        params.put("user_id", userId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    statefulLayout.setState(Config.STATE_EMPTY);
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        izinList = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("izin");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Izin izin = new Izin();
                                izin.setId(json_data.getString("id"));
                                izin.setNamaIzin(json_data.getString("nama_izin"));
                                izin.setDescription(json_data.getString("description"));
                                izinList.add(izin);
                            }
                            izinAdapter = new BottomIzinAdapter(getContext(),izinList);
                            izinAdapter.setOnItemClickListener(new BottomIzinAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Izin obj, int position) {
                                    setJenisIzinId(obj.getId());
                                    jenisIzinTxt.setText(obj.getNamaIzin());
                                    mBottomSheetDialog.dismiss();
                                }
                            });
                            recyclerView.setAdapter(izinAdapter);
                            recyclerView.refreshDrawableState();
                            recyclerView.smoothScrollToPosition(0);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public String getJenisIzinId() {
        return jenisIzinId;
    }

    public void setJenisIzinId(String jenisIzinId) {
        this.jenisIzinId = jenisIzinId;
    }
}
