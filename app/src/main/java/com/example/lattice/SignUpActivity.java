package com.example.lattice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.intellij.lang.annotations.RegExp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText ed_name, ed_phone, ed_email, ed_address, ed_password, ed_confirm_password;
    private TextView er_name, er_phone, er_email, er_address, er_password;
    private ImageView im_name, im_phone, im_email, im_address, im_password;
    private Button signupBtn;

    private String name, phone, email, password, address;

    private Dialog loadingDialog;
    List<UserData> userDataList = new ArrayList<>();
    RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        ed_name = findViewById(R.id.ed_name);
        ed_phone = findViewById(R.id.ed_phone);
        ed_email = findViewById(R.id.ed_email);
        ed_address = findViewById(R.id.ed_address);
        ed_password = findViewById(R.id.ed_password);
        ed_confirm_password = findViewById(R.id.ed_confirm_password);

        er_name = findViewById(R.id.er_name);
        er_phone = findViewById(R.id.er_phone);
        er_email = findViewById(R.id.er_email);
        er_address = findViewById(R.id.er_address);
        er_password = findViewById(R.id.er_password);

        im_name = findViewById(R.id.im_name);
        im_phone = findViewById(R.id.im_phone);
        im_email = findViewById(R.id.im_email);
        im_address = findViewById(R.id.im_address);
        im_password = findViewById(R.id.im_password);


        signupBtn = findViewById(R.id.sign_btn);


        ed_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    er_name.setText("Name should contain at-least 4 word");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 4) {

                }
            }
        });

        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 11) {
                    er_phone.setText("Please enter correct Phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ed_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == "") {
                    er_email.setText("enter Email");
                } else if (!s.toString().contains("@")) {
                    er_email.setText("Please input Proper EMail");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ed_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final String REG = "^(?=.*\\d)(?=\\S+$)(?=.*[@#$%^&+=])(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
        final Pattern PATTERN = Pattern.compile(REG);

        ed_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!PATTERN.matcher(s).matches()) {
                    er_password.setText("Password (must contain one upper character, one lower character and a number. Max length 15 and min length 8)\n");
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ed_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(mainIntent);
                //saveData();
            }
        });
    }

    private void saveData() {
        //clearing all data before fetching new set of data
        loadingDialog.show();
        database.userDao().reset(userDataList);

        UserData data = new UserData();
        //Set data to the data
        data.setName(name);
        data.setPhone_no(phone);
        data.setEmail(email);
        data.setAddress(address);
        data.setPassword(password);
        //Insert data in database
        database.userDao().insert(data);

        userDataList.addAll(database.userDao().getAll());
        loadingDialog.dismiss();

    }
}