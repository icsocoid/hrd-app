package com.sia.als.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import android.graphics.BitmapFactory;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.an.deviceinfo.device.model.Device;
import com.an.deviceinfo.permission.PermissionManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.BuildConfig;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SubmitFragment extends Fragment {

    private View view;
    private ImageView imgView;
    TextView toolbarTitle;
    private Bitmap bitmap,decoded;
    ImageButton backButton;
    Button cameraBtn,submitBtn;
    Device empDevice;
    Button saveBtn;
    SessionManagement sessionManagement;
    int REQUEST_CAMERA = 120;
    int bitmap_size = 60;
    private boolean mockLocationsEnabled;

    // Mock location rejection
    private Location lastMockLocation;
    private int numGoodReadings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_submit, container, false);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Submit Absen");
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        imgView = view.findViewById(R.id.imgView);
        sessionManagement = new SessionManagement(getContext());
        cameraBtn = view.findViewById(R.id.picture_btn);
        submitBtn = view.findViewById(R.id.submit_btn);
        submitBtn.setEnabled(false);
        empDevice = new Device(getContext());
        if(getArguments() != null)
        {
            bitmap = getArguments().getParcelable("img");
            imgView.setImageBitmap(bitmap);
            submitBtn.setEnabled(true);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPicture();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAttemptData();
            }
        });

        return view;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void launchPicture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public boolean isMockLocationEnabled() {
        final boolean isMockLocation = false;

        return isMockLocation;
    }

    public void submitAttemptData()
    {
        if(bitmap == null)
        {
            Toast.makeText(getContext(),"Anda belum melakukan pengambilan gambar",Toast.LENGTH_LONG).show();
        }
        else
        {
            FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        double lat = location.getLatitude();
                        double lang = location.getLongitude();
                        String alamatMaps = "";
                        try {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addressList = geocoder.getFromLocation(lat, lang, 1);
                            if (addressList != null && addressList.size() > 0) {
                                Address address = addressList.get(0);
                                alamatMaps = address.getAddressLine(0)+ ", " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                            }
                        }
                        catch (IOException e) {

                        }
                        boolean isMock = (Build.VERSION.SDK_INT >= 18 && location.isFromMockProvider());
                        if (isMock) {
//                            Toast.makeText(getContext(),"Memakai Fake",Toast.LENGTH_LONG).show();
                            String jenis_fake = "fake";
                            makeRequestData(lat,lang,alamatMaps,jenis_fake);
                        }
                        else {
//                            Toast.makeText(getContext(),"Tidak memakai Fake",Toast.LENGTH_LONG).show();
                            String jenis_fake = "tidak";
                            makeRequestData(lat,lang,alamatMaps,jenis_fake);

                        }
                        //String alamatMaps = location.get(0)+ ", " + location.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                    }
                }
            });
        }

    }

    public void makeRequestData(double lat,double longg,String alamat,String jenisFake)
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_absen_req";
        //  WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
        // WifiInfo info = manager.getConnectionInfo();
        String deviceInfo = empDevice.getManufacturer()+" "+empDevice.getHardware()+" "+empDevice.getDevice();
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        String macAddress = sessionManagement.getUserDetails().get(Config.KEY_MAC);
        Map<String, String> params = new HashMap<String, String>();
        params.put("photo", ImageUtil.convert(bitmap));
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(longg));
        params.put("mac_address", macAddress);
        params.put("device", deviceInfo);
        params.put("user_id", userId);
        params.put("alamat_map", alamat);
        params.put("status_fake", String.valueOf(jenisFake));
        params.put("qr_code", Config.RES_QRCODE);
        Log.i("cek param", params.toString());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ABSENSI_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    Boolean status = response.getBoolean("status");
                    Bundle bundle = new Bundle();
                    if (status) {
                        bundle.putString("status","sukses");
                        bundle.putString("page","absensi");
                        bundle.putString("body_message",response.getString("message"));

                    } else {

                        bundle.putString("status","fail");
                        bundle.putString("page","absensi");
                        bundle.putString("body_message",response.getString("message"));



                    }
                    SuksesFragment suksesFragment = new SuksesFragment();
                    suksesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.m_frame, suksesFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
                catch (JSONException e)
                {
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void launchSelfieCam(){

        int id = getFrontCameraId();


        if(id==-1){

        }else {
            CameraFragment cameraFragment = CameraFragment.newInstance(this);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.m_frame, cameraFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }


    }

    int getFrontCameraId(){

        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i =0; i<Camera.getNumberOfCameras();i++){

            Camera.getCameraInfo(i,ci);

            if(ci.facing== Camera.CameraInfo.CAMERA_FACING_FRONT)
                return i;
        }
        return -1;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            setToImageView(getResizedBitmap(bitmap, 512));
            imgView.setImageBitmap(bitmap);
            submitBtn.setEnabled(true);


        }
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imgView.setImageBitmap(decoded);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
