package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
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
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.model.Notifikasi;
import com.sia.als.model.PrivacyPolicy;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivacyPolicyFragment extends Fragment {
    View view;
    TextView titleTxt, toolbarTitle;
    ImageButton backButton;
    Bitmap bitmap;
    //SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    WebView webview;
    SessionManagement sessionManagement;
    //List<PrivacyPolicy> data = new ArrayList<>();
    //String idPrivacyPolicy = "";




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        view = inflater.inflate(R.layout.fragment_privacy, container, false);
        sessionManagement = new SessionManagement(getContext());
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Privacy Policy");

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

        makeDataRequest();
        titleTxt = (TextView) view.findViewById(R.id.title_privacy);
        webview = (WebView) view.findViewById(R.id.description_txt);
        return view;
    }

    private void showFragment() {
        NewProfileFragment newProfileFragment = new NewProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, newProfileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_privacyPolicy_req";
        String id = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        //params.put(null, "");

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.PRIVACY_POLICY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {

                    Boolean status = response.getBoolean("status");
                    if (status) {
                        try {
                            JSONObject jObject = response.getJSONObject("data");
                            titleTxt.setText(jObject.getString("title"));
                            webview.setBackgroundColor(Color.TRANSPARENT);
                            webview.getSettings().setDefaultTextEncodingName("UTF-8");
                            webview.setFocusableInTouchMode(false);
                            webview.setFocusable(false);

                            webview.setOnLongClickListener(v -> true);
                            webview.setLongClickable(false);


                            webview.getSettings().setJavaScriptEnabled(true);

                            WebSettings webSettings = webview.getSettings();
                            Resources res = getResources();
                            int fontSize = 16;
                            webSettings.setDefaultFontSize(fontSize);

                            String mimeType = "text/html; charset=UTF-8";
                            String encoding = "utf-8";
                            String htmlText = jObject.getString("content");

                            String  bg_paragraph = "<style type=\"text/css\">body{color: #000000;}";


                            String text_default = "<html><head>"
                                    + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                                    + bg_paragraph
                                    + "</style></head>"
                                    + "<body>"
                                    + htmlText
                                    + "</body></html>";


                            webview.loadDataWithBaseURL(null, text_default, mimeType, encoding, null);

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    //dialog.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.hide();
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
