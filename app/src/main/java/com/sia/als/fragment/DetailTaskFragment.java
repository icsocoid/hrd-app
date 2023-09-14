package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.TaskDetailAdapter;
import com.sia.als.config.Config;
import com.sia.als.mail.asynTasks.Login;
import com.sia.als.model.TaskDetail;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;
import com.sia.als.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class DetailTaskFragment extends Fragment {
    private View view;
    TextView toolbarTitle, bulanTxt, tahunTxt, jumlahTaskTxt, statusTxt;
    SessionManagement sessionManagement;
    RecyclerView rvTaskDetail;
    TaskDetailAdapter taskDetailAdapter;
    StatefulLayout statefulLayout;
    Spinner statusSpin;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoading = false;
    boolean isFirst = true;
    List<TaskDetail> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;


    String id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Detail Program Kerja");
        bulanTxt = (TextView) view.findViewById(R.id.bulan_task_txt);
        tahunTxt = (TextView) view.findViewById(R.id.tahun_task_txt);
        statusTxt = (TextView) view.findViewById(R.id.status_task_txt);
        jumlahTaskTxt = (TextView) view.findViewById(R.id.jumlah_task_txt);
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error,null);
        rvTaskDetail = (RecyclerView) view.findViewById(R.id.task_detail_list_recycler);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTaskDetail.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
            }
        });

        if (getArguments() != null){
            id = getArguments().getString("task_id");
            makeDataRequest(id);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showTask();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    private void resetData() {
        isFirst = true;
        data = new ArrayList<>();
        taskDetailAdapter = null;
        makeDataRequest(id);
    }

    public void showTask() {
        TaskFragment taskFragment = new TaskFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, taskFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void makeDataRequest(String taskId) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait..");
        dialog.show();
        String tag_json_obj = "json_data_task_req";
        Map<String, String> params = new HashMap<>();
        params.put("task_id", taskId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_TASK_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if (isFirst) {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }
                    Boolean status = response.getBoolean("success");
                    if (status){
                        JSONObject obj = response.getJSONObject("task");
                        String bulan = obj.getString("bulan");
                        String tahun = obj.getString("tahun");
                        Integer statusTask = obj.getInt("status_task");
                        String jumlahTask = obj.getString("taskitem_count");

                        if (statusTask == 0) { statusTxt.setText("Progress"); }
                        else if (statusTask == 1) { statusTxt.setText("Finished"); }
                        else {
                            statusTxt.setText("Approve");
                            statusTxt.setTextColor(getResources().getColor(R.color.main_green_color));
                        }

                        bulanTxt.setText(Utility.convertBulanIndo(bulan));
                        tahunTxt.setText(tahun);
                        jumlahTaskTxt.setText(jumlahTask);

                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray Jarray = obj.getJSONArray("taskitem");
                            for (int i = 0; i < Jarray.length(); i++){
                                JSONObject jsonObject = Jarray.getJSONObject(i);
                                TaskDetail taskDetail = new TaskDetail();
                                taskDetail.setTaskId(jsonObject.getString("id"));
                                taskDetail.setTaskName(jsonObject.getString("task_name"));
                                data.add(taskDetail);
                            }
                            if (taskDetailAdapter == null){
                                isFirst = false;
                                taskDetailAdapter = new TaskDetailAdapter(getContext(), data);
                                rvTaskDetail.setAdapter(taskDetailAdapter);
                            }
                            else {
                                taskDetailAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e ){
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    } else {
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
                swipeRefreshLayout.setRefreshing(false);
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
