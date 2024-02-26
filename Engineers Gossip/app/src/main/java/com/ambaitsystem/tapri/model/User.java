package com.ambaitsystem.tapri.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class User implements Serializable {
    String id, name, email,collegename;
    String department_index,admission_index;

    public User() {
    }

    public User(String id, String name, String email,String collegename,String department_index,String admission_index) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.collegename = collegename;
        this.department_index = department_index;
        this.admission_index = admission_index;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getcollegename() {
        return collegename;
    }

    public String getdepartment_index() {
        return department_index;
    }

    public String getadmission_index() {
        return admission_index;
    }

}
