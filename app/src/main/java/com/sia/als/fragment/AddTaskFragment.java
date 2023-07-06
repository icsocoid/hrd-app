package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import androidx.fragment.app.Fragment;
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
import com.sia.als.adapter.TaskItemAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Izin;
import com.sia.als.model.TaskName;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskFragment extends Fragment {
    private View view, rowView;

    private int mMonth, mYear, mDay;

    LinearLayout dateLayout;
    SessionManagement sessionManagement;
    Button buttonAdd, notif;
    ImageButton backButton;
    EditText taskName;
    TextView toolbarTitle;
    TextView dateTaskTxt;
    String statusId, nameTask, nameTaskArray[];
    Spinner statusSpin;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<String> statusTask = new ArrayList<>();
    ArrayList<String> List = new ArrayList<String>();
    LinearLayoutManager linearLayoutManager;

    ArrayList<TaskName> taskList = new ArrayList<>();
    RecyclerView rvList;
    TaskItemAdapter taskItemAdapter;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_task, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        sessionManagement = new SessionManagement(getContext());
        buttonAdd = (Button) view.findViewById(R.id.add_row);
        statusSpin = (Spinner) view.findViewById(R.id.spinner_status);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        dateTaskTxt = view.findViewById(R.id.date_task_txt);
        dateLayout = view.findViewById(R.id.date_layout);
        rvList = (RecyclerView) view.findViewById(R.id.layout_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(linearLayoutManager);
        taskItemAdapter = new TaskItemAdapter(getContext(), taskList);
        rvList.setAdapter(taskItemAdapter);
        toolbarTitle.setText("Add Task");
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);
        dateTaskTxt.setText(simpleDateFormat.format(new Date()));
        statusTask.add("Process");
        statusTask.add("Finished");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, statusTask);
        statusSpin.setAdapter(dataAdapter);
        statusSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), "The planet is " + id, Toast.LENGTH_LONG).show();
                parent.getItemAtPosition(position);
                statusId = String.valueOf(parent.getItemIdAtPosition((int) id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
            }
        });
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                dateTaskTxt.setText(year + "-" + fm + "-" + fd);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequestSubmitTask();
            }
        });

        return view;
    }

    private void addView() {
        TaskName taskName1 = new TaskName();
        taskName1.setTaskName("");
        taskList.add(taskName1);
        taskItemAdapter.notifyDataSetChanged();
    }

    private void addTaskName() {

    }

    private void removeView(View view) {
        rvList.removeView(view);
    }

//    private void taskAttemptData() {
//
//        if (rowView != null) {
//            nameTask = (String) taskName.getText().toString().trim();
//            if (TextUtils.isEmpty(taskName.getText().toString())){
//                taskName.requestFocus();
//                Toast.makeText(getContext(),"Isi kolom nama task",Toast.LENGTH_LONG).show();
//            }else {
//                Toast.makeText(getContext(), List.toString(),Toast.LENGTH_LONG).show();
////                makeRequestSubmitTask();
//            }
//        }else {
//            Toast.makeText(getContext(),"Tambahkan task",Toast.LENGTH_LONG).show();
//        }
//
//    }

    private void makeRequestSubmitTask() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_password_req";
        Map<String, String> params = new HashMap<String, String>();

        JSONArray nameTask = new JSONArray();
        if (taskList.size()>0){
            for (int i = 0; i < taskList.size(); i++){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("taskname", taskList.get(i).getTaskName());
                    nameTask.put(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        params.put("tanggal", dateTaskTxt.getText().toString());
        params.put("user_id", userId);
        params.put("status_task", statusId);
        params.put("task_name", nameTask.toString());
        Log.i ("info infpo ", nameTask.toString());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADD_TASK_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status) {
                        String pesan = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + pesan, TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_SHORT,TastyToast.CONFUSING).show();
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
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                }
            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
