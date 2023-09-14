package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.sia.als.dialog.SuksesDialog;
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

import cz.kinst.jakub.view.StatefulLayout;

public class AddTaskFragment extends Fragment {
    private View view;
    private int mMonth, mYear, mDay;
    LinearLayout dateLayout;
    SessionManagement sessionManagement;
    Button buttonAdd, notif;
    ImageButton backButton;
    TextView toolbarTitle, dateTaskTxt;
    String id;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    LinearLayoutManager linearLayoutManager;
    ArrayList<TaskName> taskList = new ArrayList<>();
    RecyclerView rvList;
    TaskItemAdapter taskItemAdapter;

    Boolean isFirst = true;
    Boolean isLoading = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_add, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        sessionManagement = new SessionManagement(getContext());
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        buttonAdd = (Button) view.findViewById(R.id.add_row);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        dateTaskTxt = view.findViewById(R.id.date_task_txt);
        dateLayout = view.findViewById(R.id.date_layout);
        rvList = (RecyclerView) view.findViewById(R.id.layout_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        taskItemAdapter = new TaskItemAdapter(getContext(), taskList);

        rvList.setLayoutManager(linearLayoutManager);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(taskItemAdapter);
        toolbarTitle.setText("Program Kerja");
        backButton.setVisibility(View.GONE);
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);
        dateTaskTxt.setText(simpleDateFormat.format(new Date()));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTask();
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showTask();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);



        if (getArguments() != null){
            id = getArguments().getString("task_id");
            makeDataRequestTask(id);
            if (id != null){
                dateLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }


    private void addView() {
        for(int i=0;i<taskList.size();i++){
            TaskName taskModel = new TaskName();
            taskModel.setTaskName(taskList.get(i).getTaskName());
            taskList.set(i,taskModel);
        }
        TaskName taskName1 = new TaskName();
        taskName1.setTaskName("");
        taskList.add(taskName1);
        taskItemAdapter.notifyDataSetChanged();
    }


    private void makeDataRequestTask(String id) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait..");
        dialog.show();
        String tag_json_obj = "json_data_task_req";
        Map<String, String> params = new HashMap<>();
        params.put("task_id", id);
        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_TASK_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status){
                        JSONObject jsonObject = response.getJSONObject("task");

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("taskitem");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                TaskName taskName = new TaskName();
                                taskName.setTaskName(jsonObject1.getString("task_name"));
                                taskList.add(taskName);
                            }

                            if (taskItemAdapter == null){
                                isFirst = false;
                                taskItemAdapter = new TaskItemAdapter(getContext(), taskList);
                                rvList.setAdapter(taskItemAdapter);
                            }else {
                                taskItemAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e){
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);

    }

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
        if (id != null){
            params.put("id", id);
            params.put("user_id", userId);
            params.put("task_name", nameTask.toString());
        }else{
            params.put("tanggal", dateTaskTxt.getText().toString());
            params.put("user_id", userId);
            params.put("task_name", nameTask.toString());
        }
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADD_TASK_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status) {
                        SuksesDialog suksesDialog = new SuksesDialog(AddTaskFragment.this);
                        suksesDialog.show(getFragmentManager(),"");

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

    public void showTask() {
        TaskFragment taskFragment = new TaskFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, taskFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}

