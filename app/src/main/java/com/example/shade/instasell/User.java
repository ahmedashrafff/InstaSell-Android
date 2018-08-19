package com.example.shade.instasell;

import java.util.ArrayList;

public class User {

private String name;
private String email;
private String id;
private ArrayList<String> posts;


    public User() {

        posts=new ArrayList<>();
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }


    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }

    public void addPost(String postId)
    {
        posts.add(postId);
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
