package com.example.lattice;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Add data base Entity
@Database(entities  = {UserData.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    //Create database instance
    private  static  RoomDB database;
    //Define database name
    private static  String DATABASE_NAME = "database";

    public synchronized  static RoomDB getInstance(Context context){
        //Check condition
        if (database == null){
            //when database is null
            // Initialize database
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class,DATABASE_NAME).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        // Return database
        return database;
    }

    public abstract UserDao userDao();
}
