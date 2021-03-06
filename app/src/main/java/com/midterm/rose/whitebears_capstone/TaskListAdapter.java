package com.midterm.rose.whitebears_capstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class TaskListAdapter
        extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private final ArrayList<Task> mTaskList;
    private final LayoutInflater mInflater;

    class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTextView;
        public final TextView statusTextView;
        public final TextView dueDateTextView;
        public final TextView projectTextView;

        final TaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, TaskListAdapter adapter) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            statusTextView = (TextView) itemView.findViewById(R.id.status);
            dueDateTextView = (TextView) itemView.findViewById(R.id.duedate);
            projectTextView = (TextView) itemView.findViewById(R.id.project);
            this.mAdapter = adapter;
        }
    }

    public TaskListAdapter(Context context, ArrayList<Task> taskList) {
        mInflater = LayoutInflater.from(context);
        this.mTaskList = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate an item view.
        View itemView = mInflater.inflate(R.layout.activity_tasklist_item, parent, false);
        return new TaskViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        // Retrieve the data for that position.
        Task currentTask = mTaskList.get(position);
        // Add the data to the view holder.

        StringBuilder sb = new StringBuilder();

        ArrayList<User> allUsers = currentTask.getUsers();
        for(User user : allUsers){
            if(allUsers.indexOf(user) == allUsers.size() -1){
                sb.append(user.getFullName());
            }else{
                sb.append(user.getFullName() + ", ");
            }
        }

        holder.titleTextView.setText("Title: " + currentTask.getTitle());
        holder.statusTextView.setText("Status: " + currentTask.getStatus());
        if(currentTask.getFormattedDueDate() == null){
            holder.dueDateTextView.setText("Due Date: ");
        }
        else{
            holder.dueDateTextView.setText("Due Date: " + currentTask.getFormattedDueDate());
        }
        holder.projectTextView.setText("Project: " + currentTask.getProjectName());
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}