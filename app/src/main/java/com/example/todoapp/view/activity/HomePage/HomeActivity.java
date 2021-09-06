package com.example.todoapp.view.activity.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.R;
import com.example.todoapp.databinding.ActivityHomeBinding;
import com.example.todoapp.databinding.ActivityInputFileBinding;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.databinding.ActivityRetrievedLayoutBinding;
import com.example.todoapp.databinding.ActivityUpdateDataBinding;
import com.example.todoapp.model.data;
import com.example.todoapp.view.activity.Login.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //data binding

        ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        //setContentView(R.layout.activity_home);


       toolbar = binding.homeToolbar;
         setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo List App");
        mAuth = FirebaseAuth.getInstance();

        recyclerView = binding.recyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        binding.recyclerView.setHasFixedSize(true);
       binding.recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);


        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
//data binding


        binding.floatingPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        //ActivityInputFileBinding bindInput=DataBindingUtil.setContentView(this, R.layout.activity_input_file,null);

        ActivityInputFileBinding bindInput = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_input_file, null, false);
       // setContentView(bindInput.getRoot());
        //  View myView = inflater.inflate(R.layout.activity_input_file, null);
       myDialog.setView(bindInput.getRoot());

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        bindInput.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bindInput.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = bindInput.task.getText().toString().trim();
                String mDescription = bindInput.description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());


                if (TextUtils.isEmpty(mTask)) {
                   bindInput.task.setError("Task Required");
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {
                   bindInput.description.setError("Description Required");
                    return;
                } else {
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    data data = new data(mTask, mDescription, id, date);
                    reference.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Task has been inserted successfully", Toast.LENGTH_SHORT).show();
                              } else {
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                              }
                            loader.dismiss();
                        }
                    });

                }
                //dialog.show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<data> options = new FirebaseRecyclerOptions.Builder<data>()
                .setQuery(reference, data.class)
                .build();

        FirebaseRecyclerAdapter<data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final data data) {

                holder.setDate(data.getId());
                holder.setTask(data.getTask());
                holder.setDesc(data.getDescription());

                holder.activityRetrievedLayoutBind.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        task = data.getTask();
                        description = data.getDescription();

                        updateTask();
                    }
                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ActivityRetrievedLayoutBinding bindRetrieved=
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                R.layout.activity_retrieved_layout, parent, false);
                //ActivityRetrievedLayoutBinding bindRetrieved=DataBindingUtil.setContentView(this, R.layout.activity_retrieved_layout);

            //    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieved_layout, parent, false);
                return new MyViewHolder(bindRetrieved);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ActivityRetrievedLayoutBinding activityRetrievedLayoutBind;
        public MyViewHolder(@NonNull ActivityRetrievedLayoutBinding activityRetrievedLayoutBinding) {
            super(activityRetrievedLayoutBinding.getRoot());
            activityRetrievedLayoutBind=activityRetrievedLayoutBinding;
            //mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView= activityRetrievedLayoutBind.taskTv;
            taskTextView.setText(task);
        }

        public void setDesc(String desc) {
            TextView descTextView = activityRetrievedLayoutBind.descriptionTv;
            descTextView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = activityRetrievedLayoutBind.dateTv;

        }
    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        //LayoutInflater inflater = LayoutInflater.from(this);

        ActivityUpdateDataBinding bindUpdate=DataBindingUtil.setContentView(this, R.layout.activity_update_data);
        AlertDialog dialog = myDialog.create();

      //  View view = inflater.inflate(R.layout.activity_update_data, null);
        myDialog.setView(bindUpdate.getRoot());


        bindUpdate.mEditTextTask.setText(task);
        bindUpdate.mEditTextTask.setSelection(task.length());

        bindUpdate.mEditTextDescription.setText(description);
        bindUpdate.mEditTextDescription.setSelection(description.length());

        bindUpdate.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = bindUpdate.mEditTextTask.getText().toString().trim();
                description = bindUpdate.mEditTextDescription.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                data  dat= new data(task, description, key, date);

                reference.child(key).setValue(dat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "update failed "+err, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        bindUpdate.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Failed to delete task "+ err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Logout:
                mAuth.signOut();
                Intent intent  = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    }
