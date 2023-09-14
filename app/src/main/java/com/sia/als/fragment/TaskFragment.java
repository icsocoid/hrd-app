package com.sia.als.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.KodePerusahaanActivity;
import com.sia.als.adapter.TaskAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Task;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;


public class TaskFragment extends Fragment {
    private View view, bottomSheet;
    SessionManagement sessionManagement;
    TextView toolbarTitle;
    RecyclerView rvTask;
    TaskAdapter taskAdapter;
    StatefulLayout statefulLayout;
    Button retryBtn, detailBtn, notif;
    FloatingActionButton addTaskBtn;
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<Task> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior mBehavior;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task, container, false);
        addTaskBtn = (FloatingActionButton) view.findViewById(R.id.task_float_buttom);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Program Kerja");
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        rvTask = (RecyclerView) view.findViewById(R.id.task_list_recycler);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        notif = (Button) getActivity().findViewById(R.id.button_general);

        notif.setVisibility(View.GONE);
        rvTask.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        bottomSheet = (View) view.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        detailBtn = (Button) view.findViewById(R.id.detail_btn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDataRequest(0);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
                initScrollListener();
                addTaskBtn.hide();
            }
        });
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskFragment();
                makeDataRequest(0);
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

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View viewDialog = getLayoutInflater().inflate(R.layout.sheet_task_menu, null);
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

    private void showAddTaskFragment() {
        AddTaskFragment addTaskFragment = new AddTaskFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, addTaskFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void initScrollListener() {
        rvTask.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int hal, int totalItemsCount) {
                if (!isLoading)
                {
                    page = page + limit;
                    makeDataRequest(page);
                }
            }
        });
    }

    private void resetData() {
        isFirst = true;
        page = 0;
        data = new ArrayList<>();
        taskAdapter = null;
        makeDataRequest(page);
    }
    private void makeDataRequest(int halaman) {
        if (isFirst)
        {
            statefulLayout.setState(Config.STATE_PROGRESS);
            addTaskBtn.hide();
        }
        String tag_json_obj = "json_data_task_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("halaman", String.valueOf(halaman));
        params.put("limit", String.valueOf(limit));
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.TASK_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if (isFirst) {
                        addTaskBtn.show();
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }
                    Boolean status = response.getBoolean("status");
                    if (status){
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray Jarray = response.getJSONArray("task");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Task task = new Task();
                                task.setId(json_data.getString("id"));
                                task.setBulan(json_data.getString("bulan"));
                                task.setTahun(json_data.getString("tahun"));
                                task.setJumlah(json_data.getString("taskitem_count"));
                                task.setStatus(json_data.getString("status_task"));
                                data.add(task);
                            }
                            if (taskAdapter == null) {
                                isFirst = false;
                                taskAdapter = new TaskAdapter(getContext(), data);
                                taskAdapter.setmOnEditClickListener(new TaskAdapter.OnEditClickListener() {
                                    @Override
                                    public void onItemClick(View view, String val) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("task_id", val);
                                        AddTaskFragment addTaskFragment = new AddTaskFragment();
                                        addTaskFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, addTaskFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });
                                taskAdapter.setmOnDetailClickListener(new TaskAdapter.OnDetailClickListener() {
                                    @Override
                                    public void onItemClick(View view, String val) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("task_id", val);
                                        DetailTaskFragment detailTaskFragment = new DetailTaskFragment();
                                        detailTaskFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailTaskFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });

                                taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Task obj, int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("task_id", obj.getId());
                                        DetailTaskFragment detailTaskFragment = new DetailTaskFragment();
                                        detailTaskFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailTaskFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });
                                addTaskBtn.show();
                                rvTask.setAdapter(taskAdapter);
                                rvTask.smoothScrollToPosition(0);

                            } else {
                                taskAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                            addTaskBtn.show();

                        } catch (JSONException e) {
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    }
                    else
                    {
                        if(isFirst)
                        {
                            statefulLayout.setState(Config.STATE_EMPTY);
                            addTaskBtn.show();
                        }
                        else
                        {
                            isLoading = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
                    retryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeDataRequest(0);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                addTaskBtn.show();
                if (error instanceof TimeoutError || error instanceof NoConnectionError)
                {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    @Override
    public void onResume() {
        super.onResume();
        resetData();
    }

    private void showFragment() {
        NewProfileFragment newProfileFragment = new NewProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, newProfileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}

