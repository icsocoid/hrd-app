package com.sia.als.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sia.als.R;


public class TaskFragment extends Fragment {

    private View view;
    TextView toolbarTitle;
    FloatingActionButton addTaskBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task, container, false);
        addTaskBtn = (FloatingActionButton) view.findViewById(R.id.task_float_buttom);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("My Task");

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskFragment();
            }
        });
        return view;
    }

    private void showAddTaskFragment() {
        AddTaskFragment addTaskFragment = new AddTaskFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, addTaskFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}

