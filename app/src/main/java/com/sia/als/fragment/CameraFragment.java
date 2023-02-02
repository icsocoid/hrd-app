package com.sia.als.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sia.als.R;
import com.sia.als.util.CameraPreview;

import java.lang.ref.WeakReference;

public class CameraFragment extends Fragment {
    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView btnSnap;
    Button saveBtn;
    public static final int IMAGE_MEDIA_TYPE = 1;
    private View view;
    SubmitFragment submitFragment;
    AccountFragment accountFragment;

    Bitmap bitmap;
    private static final String TAG = CameraFragment.class.getSimpleName();

    public static CameraFragment newInstance(SubmitFragment submitFragment)
    {
        CameraFragment st = new CameraFragment();
        st.submitFragment = submitFragment;
        return st;
    }

    public static CameraFragment newInstance(AccountFragment accountFragment)
    {
        CameraFragment st = new CameraFragment();
        st.accountFragment = accountFragment;
        return st;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        btnSnap = (ImageView) view.findViewById(R.id.btnSnap);
        LinearLayout cameraView = view.findViewById(R.id.cameraView);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getContext(), mCamera);
        cameraView.addView(mPreview);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCamera.takePicture(null, null, picture);
            }
        });
        return view;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        Log.d(TAG, "numbers of cameras" + Camera.getNumberOfCameras());
        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camNo, camInfo);

            if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                c = Camera.open(camNo);
                c.setDisplayOrientation(90);
            }
        }
        return c;
    }

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            System.gc();
            bitmap = null;
            BitmapWorkerTask task = new BitmapWorkerTask(data);
            task.execute(0);
        }

    };


    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<byte[]> dataf;
        private int data = 0;

        public BitmapWorkerTask(byte[] imgdata) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            dataf = new WeakReference<byte[]>(imgdata);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            ResultActivity(dataf.get());
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {

                Bundle bundle = new Bundle();
                bundle.putParcelable("img", bitmap);
                if(submitFragment != null)
                {
                   // SubmitFragment submitFragment = new SubmitFragment();
                    submitFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.m_frame, submitFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }

                if(accountFragment != null)
                {
                    // SubmitFragment submitFragment = new SubmitFragment();
                    accountFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.m_data_frame, accountFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }

            }
        }
    }


    public void ResultActivity(byte[] data) {
        bitmap = null;
        bitmap = decodeSampledBitmapFromResource(data, 250, 250);
        bitmap = RotateBitmap(bitmap, 270);
        bitmap = flip(bitmap);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    // rotate the bitmap to portrait
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    //the front camera displays the mirror image, we should flip it to its original
    Bitmap flip(Bitmap d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d;
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }



}
