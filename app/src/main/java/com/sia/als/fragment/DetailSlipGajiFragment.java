package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.SlipGajiBonusAdapter;
import com.sia.als.adapter.SlipGajiBonusLainAdapter;
import com.sia.als.adapter.SlipGajiPotonganAdapter;
import com.sia.als.adapter.SlipGajiTjPerdinAdapter;
import com.sia.als.adapter.SlipGajiTunjanganAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.SlipGaji;
import com.sia.als.model.SlipGajiPerdin;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class DetailSlipGajiFragment extends Fragment {
    private View view;
    TextView totalGajiTxt, rangePeriodeTxt, nomorSlipGajiTxt, jabatanTxt, gajiPokokTxt,totalPenghasilanTxt, totalPotonganTxt, gajiPphTxt;
    StatefulLayout statefulLayout;
    TextView toolbarTitle, bonusLainTxt;
    ImageButton backButton;
    SlipGajiPotonganAdapter slipGajiPotonganAdapter;
    SlipGajiTjPerdinAdapter slipGajiTjPerdinAdapter;
    SlipGajiTunjanganAdapter slipGajiTunjanganAdapter;
    SlipGajiBonusAdapter slipGajiBonusAdapter;
    SlipGajiBonusLainAdapter slipGajiBonusLainAdapter;
    Locale localeID;
    NumberFormat numberFormat;
    Button notif;
    LinearLayout layReport, layBonus, layPerdin, layGajiPokok, layTunjangan, layPph;

    boolean isLoading = false;
    boolean isFirst = true;
    String id;
    List<SlipGaji> dataPotongan = new ArrayList<>(), dataTunjangan = new ArrayList<>(), dataBonus = new ArrayList<>(), dataBonusLain = new ArrayList<>();
    List<SlipGajiPerdin> dataPerdin = new ArrayList<>();
    RecyclerView rvPotongan, rvTjPerdin, rvTunjangan, rvBonus, rvBonusLain;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_slip_gaji_detail, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        totalGajiTxt = (TextView) view.findViewById(R.id.total_gaji_txt);
        rangePeriodeTxt = (TextView) view.findViewById(R.id.range_periode_txt);
        nomorSlipGajiTxt = (TextView) view.findViewById(R.id.no_slip_gaji_txt);
        jabatanTxt = (TextView) view.findViewById(R.id.jabatan_txt);
        totalPenghasilanTxt = (TextView) view.findViewById(R.id.total_penghasilan_txt);
        totalPotonganTxt = (TextView) view.findViewById(R.id.total_potongan_txt);
        gajiPokokTxt = (TextView) view.findViewById(R.id.gaji_pokok_txt);
        gajiPphTxt = (TextView) view.findViewById(R.id.gaji_pph_txt);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        localeID = new Locale("in", "ID");
        numberFormat = NumberFormat.getNumberInstance(localeID);
        rvPotongan = (RecyclerView) view.findViewById(R.id.potongan_list_recycler);
        rvTjPerdin = (RecyclerView) view.findViewById(R.id.tunjangan_perdin_list_recycler);
        rvTunjangan = (RecyclerView) view.findViewById(R.id.tunjangan_list_recycler);
        rvBonus = (RecyclerView) view.findViewById(R.id.bonus_list_recycler);
        rvBonusLain = (RecyclerView) view.findViewById(R.id.bonus_lain_list_recycler);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        layReport = (LinearLayout) view.findViewById(R.id.share_layout);
        bonusLainTxt = (TextView) view.findViewById(R.id.bonus_lain_obj);
        layBonus = (LinearLayout) view.findViewById(R.id.bonus_obj);
        layPerdin = (LinearLayout) view.findViewById(R.id.tunjangan_perdin_obj);
        layGajiPokok = (LinearLayout) view.findViewById(R.id.gaji_pokok_obj);
        layTunjangan = (LinearLayout) view.findViewById(R.id.tunjangan_obj);
        layPph = (LinearLayout) view.findViewById(R.id.pph_obj);

        toolbarTitle.setText("Detail Slip Gaji");
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_share_24);
        notif.setWidth(24);
        notif.setHeight(24);
        backButton.setVisibility(View.GONE);
        rvBonusLain.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBonus.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTunjangan.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPotongan.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTjPerdin.setLayoutManager(new LinearLayoutManager(getContext()));
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        if (getArguments() != null){
            id = getArguments().getString("slip_id");
            makeDataRequest(id);
        }
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResault();
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

    private void shareResault() {
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

    private Uri getImageUri(Context context, Bitmap bitmapShare) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapShare.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmapShare, "", "");
            return Uri.parse(path);
        }catch (Exception e){
            e.getMessage();
        }
        return null;
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

    private void showFragment() {
        SlipGajiFragment slipGajiFragment = new SlipGajiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, slipGajiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void makeDataRequest(String id) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait..");
        dialog.show();
        String tag_json_obj = "json_data_task_req";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_SLIP_GAJI_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (isFirst){
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }
                    Boolean status = response.getBoolean("success");
                    if (status){
                        JSONObject obj = response.getJSONObject("data");
                        JSONObject objSlip = response.getJSONObject("slip");
                        String gajiBersih = objSlip.getString("gaji_bersih");
                        String noSlipGaji = obj.getString("no_ref");
                        String gajiPokok = obj.getString("gaji_pokok");
                        String rangePeriode = obj.getString("namaperiode");
                        String jabatan = obj.getString("jbt");
                        String jumlahPenghasilan = obj.getString("gaji_kotor");
                        String jumlahPotongan = objSlip.getString("total_potongan");
                        String totalBonusLain = obj.getString("total_bonus_tambahan");
                        String totalBonus = obj.getString("total_bonus");
                        String totalTjPerdin = obj.getString("total_tunjangan_perdin");
                        String totalTunjangan = obj.getString("total_tunjangan");
                        String gajiPph = objSlip.getString("pph");


                        jumlahPotongan = jumlahPotongan.replace(",",".");
                        gajiBersih = gajiBersih.replace(",",".");
                        gajiPph = gajiPph.replace(",",".");
//                        totalGajiTxt.setText(numberFormat.format(Double.parseDouble(gajiBersih)));
                        totalGajiTxt.setText(gajiBersih);
                        nomorSlipGajiTxt.setText(noSlipGaji);
                        gajiPokokTxt.setText(numberFormat.format(Double.parseDouble(gajiPokok)));
                        rangePeriodeTxt.setText(rangePeriode);
                        jabatanTxt.setText(jabatan);
                        totalPenghasilanTxt.setText(numberFormat.format(Double.parseDouble(jumlahPenghasilan)));
                        totalPotonganTxt.setText(jumlahPotongan);
                        gajiPphTxt.setText(gajiPph);

                        statefulLayout.setState(StatefulLayout.State.CONTENT);

                        if (totalBonusLain.equals("0")) {
                            bonusLainTxt.setVisibility(View.GONE);
                        } else {
                            bonusLainTxt.setVisibility(View.VISIBLE);
                        }if (totalBonus.equals("0")) {
                            layBonus.setVisibility(View.GONE);
                        } else {
                            layBonus.setVisibility(View.VISIBLE);
                        }if (totalTjPerdin.equals("0")) {
                            layPerdin.setVisibility(View.GONE);
                        } else {
                            layPerdin.setVisibility(View.VISIBLE);
                        }if (gajiPokok.equals("0")) {
                            layGajiPokok.setVisibility(View.GONE);
                        } else {
                            layGajiPokok.setVisibility(View.VISIBLE);
                        }if (totalTunjangan.equals("0")) {
                            layTunjangan.setVisibility(View.GONE);
                        } else {
                            layTunjangan.setVisibility(View.VISIBLE);
                        }if (gajiPph.equals("0")) {
                            layPph.setVisibility(View.GONE);
                        } else {
                            layPph.setVisibility(View.VISIBLE);
                        }

                        isLoading = false;

                        try {
                            JSONArray array = obj.getJSONArray("detail_perdin");
                            for (int i = 0; i < array.length(); i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                SlipGajiPerdin slipGajiPerdin = new SlipGajiPerdin();

                                slipGajiPerdin.setNamaTjPerdin(jsonObject.getString("klien_name"));
                                slipGajiPerdin.setJumlahTjPerdin(jsonObject.getInt("nominal_perdin"));
                                dataPerdin.add(slipGajiPerdin);

                            }
                            if (slipGajiTjPerdinAdapter == null){
                                isFirst = false;
                                slipGajiTjPerdinAdapter = new SlipGajiTjPerdinAdapter(getContext(),dataPerdin);

                                rvTjPerdin.setAdapter(slipGajiTjPerdinAdapter);
                                rvTjPerdin.smoothScrollToPosition(0);
                            }else {
                                slipGajiTjPerdinAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e) {
                            TastyToast.makeText(getContext(), "" + e, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
                        }

                        try {
                            JSONArray array = obj.getJSONArray("bonustambahan");
                            for (int i = 0; i < array.length(); i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                SlipGaji slipGaji = new SlipGaji();

                                slipGaji.setNamaBonusLainGaji(jsonObject.getString("nama_field"));
                                slipGaji.setJumlahBonusLainGaji(jsonObject.getInt("nominal"));
                                dataBonusLain.add(slipGaji);
                            }
                            if (slipGajiBonusLainAdapter == null){
                                isFirst = false;
                                slipGajiBonusLainAdapter = new SlipGajiBonusLainAdapter(getContext(), dataBonusLain);

                                rvBonusLain.setAdapter(slipGajiBonusLainAdapter);
                                rvBonusLain.smoothScrollToPosition(0);
                            }else {
                                slipGajiBonusLainAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e) {
                            TastyToast.makeText(getContext(), "" + e, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
                        }

                        try {
                            JSONArray array = obj.getJSONArray("payrolldetail");
                            for (int i = 0; i < array.length(); i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                JSONObject objDetailField = jsonObject.getJSONObject("fielddetail");
                                String kategori = objDetailField.getString("jenis");
                                SlipGaji slipGaji = new SlipGaji();

                                if (kategori.equals("potongan")){
                                    slipGaji.setNamaPotonganGaji(objDetailField.getString("nama_field"));
                                    slipGaji.setJumlahPotonganGaji(jsonObject.getInt("total"));
                                    dataPotongan.add(slipGaji);
                                }
                                if (kategori.equals("tunjangan")){
                                    slipGaji.setNamaTunjanganGaji(objDetailField.getString("nama_field"));
                                    slipGaji.setJumlahTunjanganGaji(jsonObject.getInt("total"));
                                    dataTunjangan.add(slipGaji);
                                }
                                if (kategori.equals("bonus")){
                                    slipGaji.setNamaBonusGaji(objDetailField.getString("nama_field"));
                                    slipGaji.setJumlahBonusGaji(jsonObject.getInt("total"));
                                    dataBonus.add(slipGaji);
                                }

                            }
                            if (slipGajiPotonganAdapter == null){
                                isFirst = false;
                                slipGajiPotonganAdapter = new SlipGajiPotonganAdapter(getContext(),dataPotongan);

                                rvPotongan.setAdapter(slipGajiPotonganAdapter);
                                rvPotongan.smoothScrollToPosition(0);
                            }else {
                                slipGajiPotonganAdapter.notifyDataSetChanged();
                            }

                            if (slipGajiTunjanganAdapter == null){
                                isFirst = false;
                                slipGajiTunjanganAdapter = new SlipGajiTunjanganAdapter(getContext(), dataTunjangan);

                                rvTunjangan.setAdapter(slipGajiTunjanganAdapter);
                                rvTunjangan.smoothScrollToPosition(0);
                            }else {
                                slipGajiTunjanganAdapter.notifyDataSetChanged();
                            }

                            if (slipGajiBonusAdapter == null){
                                isFirst = false;
                                slipGajiBonusAdapter = new SlipGajiBonusAdapter(getContext(), dataBonus);

                                rvBonus.setAdapter(slipGajiBonusAdapter);
                                rvBonus.smoothScrollToPosition(0);
                            }else {
                                slipGajiTunjanganAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;

                        }catch (JSONException e) {
                            TastyToast.makeText(getContext(), "" + e, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
                        }

                    }else {
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                        if(isFirst)
                        {
                            statefulLayout.setState(Config.STATE_EMPTY);
                        }
                        else
                        {
                            isLoading = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
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
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }
}
