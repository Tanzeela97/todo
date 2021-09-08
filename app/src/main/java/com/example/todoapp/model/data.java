package com.example.todoapp.model;

public class data {

    public data() {
    }

    private String task, description, id, date;


    public data(String task, String id, String date, String description) {
        this.task = task;
        this.id = id;
        this.date = date;
        this.description=description;
    }

//    public String getDescription() {
//        return description;
//    }

    public String getDescription() {
        return description;
   }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }}