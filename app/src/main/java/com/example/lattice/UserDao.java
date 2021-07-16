package com.example.lattice;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.gson.JsonArray;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    //Insert query
    @Insert(onConflict = REPLACE)
    void insert(UserData userData);

    //Delete query
    @Delete
    void delete(UserData userData);

    //Delete all query
    @Delete
    void reset(List<UserData> userData);

    //Update query
    @Query("UPDATE user_data SET name = :sName ," +
            " phone_no = :sPhoneNo, email = :sEmail, " +
            "address = :sAddress, password = :sPassword" +
            " WHERE ID = :sID")
    void update(int sID, String sName, String sPhoneNo,
                String sEmail, String sAddress, String sPassword);

    //get all data query
    @Query("SELECT * FROM user_data")
    List<UserData> getAll();
}
