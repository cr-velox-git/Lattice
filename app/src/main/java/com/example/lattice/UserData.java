package com.example.lattice;

import java.io.Serializable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName= "user_data")
public class UserData implements Serializable {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name= "name")
    private String name;

    @ColumnInfo(name= "phone_no")
    private String phone_no;

    @ColumnInfo(name= "email")
    private String email;

    @ColumnInfo(name= "address")
    private String address;

    @ColumnInfo(name= "password")
    private String password;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
