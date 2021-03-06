package com.midterm.rose.whitebears_capstone;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DashboardActivity extends AppCompatActivity implements HttpCall.AsyncResponse{
    private final String ON_SCHEDULE = "On Time";
    private final String OVERDUE = "Overdue";;

    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;
    private String userName;

    RadioGroup rg;

    ArrayList<Project> allProjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        HttpCall hc = new HttpCall();
        hc.delegate = this;
        userName = getIntent().getExtras().getString("userName");
        hc.execute("getTasks", userName);
    }
/*
    private void generateMultipleProjects(){
        Project p = new Project();

        p.setTitle("Try Catch");

        ArrayList<Task> tasks = new ArrayList<>();

        Task t1 = new Task(0, 0, 0, "High", "Task1", "Desc1", ON_SCHEDULE, "Try Catch", new Date(), new Date(), new Date());
        t1.addUser(new User("Joe", "Bob"));


        Task t2 = new Task(1, 1, 1, "Medium", "Task2", "Desc2", ON_SCHEDULE, "Try Catch", new Date(), new Date(), new Date());
        t2.addUser(new User("Billy", "Bob"));

        Task t3 = new Task(2, 2, 2, "High", "Task3", "Desc3", ON_SCHEDULE, "Try Catch", new Date(), new Date(), new Date());
        t3.addUser(new User("Jatin", "Singh"));
        t3.addUser(new User("Raf", "Sigwalt"));


        Task t4 = new Task(3, 3, 3, "Medium", "Task3", "Desc3", COMPLETED_ON_TIME, "Try Catch", new Date(), new Date(), new Date());
        t4.addUser(new User("Sup", "MahBrotha"));

        Task t5 =new Task(4, 4, 4, "Medium", "Task4", "Desc4", COMPLETED_OVERDUE, "Try Catch", new Date(), new Date(), new Date());
        t5.addUser(new User("Test", "User"));

        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        tasks.add(t5);
        p.setTasks(tasks);



        allProjects.add(p);



        Project p2 = new Project();

        p2.setTitle("SEEGAAAAAA");

        tasks = new ArrayList<>();

        t1 = new Task(0, 0, 0, "High", "Task1", "Desc1", ON_SCHEDULE, "SEEGAAAAAA", new Date(), new Date(), new Date());
        t1.addUser(new User("EA", "Sports"));


        t2 = new Task(1, 1, 1, "Medium", "Task2", "Desc2", OVERDUE, "SEEGAAAAAA", new Date(), new Date(), new Date());
        t2.addUser(new User("Intel", "99"));

        t3 = new Task(2, 2, 2, "High", "Task3", "Desc3", COMPLETED_OVERDUE, "SEEGAAAAAA", new Date(), new Date(), new Date());
        t3.addUser(new User("Johnny", "Dagger"));
        t3.addUser(new User("Lion", "Tiger"));

        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        p2.setTasks(tasks);

        allProjects.add(p2);


        Project p3 = new Project();

        p3.setTitle("WhiteBears");

        tasks = new ArrayList<>();

        t1 = new Task(0, 0, 0, "High", "Task1", "Desc1", OVERDUE, "WhiteBears", new Date(), new Date(), new Date());
        t1.addUser(new User("Love", "war"));


        t2 = new Task(1, 1, 1, "Medium", "Task2", "Desc2", OVERDUE, "WhiteBears", new Date(), new Date(), new Date());
        t2.addUser(new User("User", "2"));
        t2.addUser(new User("Scott", "Mcjagger"));

        t3 = new Task(2, 2, 2, "High", "Task3", "Desc3", , "WhiteBears", new Date(), new Date(), new Date());
        t3.addUser(new User("Johnny", "Dagger"));
        t3.addUser(new User("Sega", "Saturn"));
        t3.addUser(new User("Sega", "Dreams"));

        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        p3.setTasks(tasks);

        allProjects.add(p3);
    }
    */


    int completedOnTime, completedOverdue, overdue, onSchedule;

    private void updateProject(String projectName){
        completedOnTime = 0;
        completedOverdue = 0;
        overdue = 0;
        onSchedule = 0;

        ArrayList<Task> allTasks = new ArrayList<>();
        if(projectName.equals("All")) {
            for (Project project : allProjects) {
                allTasks.addAll(project.getTasks());
                incrementStatusCounters(project.getTasks());
            }
        }else {
            for (Project project : allProjects) {
                if (project.getTitle().equals(projectName)) {
                    allTasks.addAll(project.getTasks());
                    incrementStatusCounters(project.getTasks());
                    break;
                }
            }
        }

        PieChartHelper.populatePieChart(completedOnTime, completedOverdue, overdue, onSchedule);

        mAdapter = new TaskListAdapter(this, allTasks);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void incrementStatusCounters(ArrayList<Task> tasks){
        for(Task task : tasks){
            switch(task.getStatus()){
                case OVERDUE:
                    if(task.isCompleted()){
                        completedOverdue++;
                    }
                    else {
                        overdue++;
                    }
                    break;
                case ON_SCHEDULE:
                    if(task.isCompleted()){
                        completedOnTime++;
                    }
                    else {
                        onSchedule++;
                    }
                    break;
            }
        }
    }

    @Override
    public void processFinish(String output) {
        ArrayList<Task> alltasks = new ArrayList<>();
        try {
            JSONArray obj = new JSONArray(output);
            Gson gson = new Gson();
            for(int i = 0; i < obj.length(); i++){
                JSONObject task = obj.getJSONObject(i);
                SimpleDateFormat myDate= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                myDate.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
                if(Long.parseLong(task.get("CompletedDate").toString().replaceAll("\\D+","")) != 62135596800000L){
                    task.put("CompletedDate", myDate.format(new Date(Long.parseLong(task.get("CompletedDate").toString().replaceAll("\\D+","")))));
                }
                else{
                    task.remove("CompletedDate");
                }
                if(Long.parseLong(task.get("StartDate").toString().replaceAll("\\D+","")) != 62135596800000L){
                    task.put("StartDate", myDate.format(new Date(Long.parseLong(task.get("StartDate").toString().replaceAll("\\D+","")))));
                }
                else{
                    task.remove("StartDate");
                }
                if(Long.parseLong(task.get("DueDate").toString().replaceAll("\\D+","")) != 62135596800000L){
                    task.put("DueDate", myDate.format(new Date(Long.parseLong(task.get("DueDate").toString().replaceAll("\\D+","")))));
                }
                else{
                    task.remove("DueDate");
                }
                Task task1 = gson.fromJson(task.toString() , Task.class);
                alltasks.add(task1);
            }

            //Get all projects
            for(Task task : alltasks){
                boolean found = false;
                for(Project project : allProjects){

                    if(task.getProjectName().equals(project.getTitle())){
                        ArrayList<Task> pTasks =   project.getTasks();
                        pTasks.add(task);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Project project = new Project();
                    project.setTitle(task.getProjectName());
                    ArrayList<Task> pTask = new ArrayList<>();
                    pTask.add(task);
                    project.setTasks(pTask);
                    allProjects.add(project);
                }
            }

            rg = findViewById(R.id.projectGroup);

            PieChartHelper.pieChartView = findViewById(R.id.chart);

            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            ArrayList<Task> allTasks = new ArrayList<>();

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 50, 5);

            RadioButton rb = new RadioButton(this);
            rb.setText("All");
            rb.setLayoutParams(params);
            rg.addView(rb);

            for (Project p : allProjects) {
                rb = new RadioButton(this);
                rb.setText(p.getTitle());

                rb.setLayoutParams(params);
                rg.addView(rb);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton checkedRadioButton = group.findViewById(checkedId);
                        if (checkedRadioButton.isChecked()) {
                            updateProject(checkedRadioButton.getText() + "");
                        }
                    }
                });

                for (Task t : p.getTasks())
                    allTasks.add(t);
            }

            updateProject("All");

            ((RadioButton) rg.getChildAt(0)).setChecked(true);
        }
        catch (Exception e){
            String ex = e.getMessage();
        }
    }
}
