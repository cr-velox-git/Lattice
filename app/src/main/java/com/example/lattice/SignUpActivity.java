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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;

import org.intellij.lang.annotations.RegExp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText ed_name, ed_phone, ed_email, ed_address, ed_password, ed_confirm_password;
    private TextView er_name, er_phone, er_email, er_address, er_password,login;
    private ImageView im_name, im_phone, im_email, im_address, im_password;
    private Button signupBtn;


    private Dialog loadingDialog;
    List<UserData> userDataList = new ArrayList<>();
    RoomDB database;
    private String TAG = "SignUpActivity:-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize database
        database = RoomDB.getInstance(this);

        //........................ loading dialog start ......................//
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corner);
        Objects.requireNonNull(loadingDialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //........................ loading dialog start ......................//

        login = findViewById(R.id.login);

        login.setOnClickListener(v->{
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        });

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

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    er_name.setVisibility(View.VISIBLE);
                    er_name.setText("Name should contain at-least 4 word");
                } else if (s.length() > 4) {
                    er_name.setVisibility(View.GONE);
                    im_name.setImageResource(R.drawable.id_1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 4) {
                    er_name.setVisibility(View.GONE);
                    im_name.setImageResource(R.drawable.id_1);
                }
            }
        });

        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 9) {
                    er_phone.setVisibility(View.VISIBLE);
                    er_phone.setText("Please enter correct Phone number");
                } else if (s.length() == 10) {
                    er_phone.setVisibility(View.GONE);
                    im_phone.setImageResource(R.drawable.phone_1);
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
                    er_email.setVisibility(View.VISIBLE);
                    er_email.setText("enter Email");
                } else if (!s.toString().contains("@")) {
                    er_email.setVisibility(View.VISIBLE);
                    er_email.setText("Please input Proper EMail");
                } else {
                    er_email.setVisibility(View.GONE);
                    im_email.setImageResource(R.drawable.email_1);
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 9) {
                    er_address.setVisibility(View.VISIBLE);
                    er_address.setText("Please enter appropriate address");
                } else if (s.length() == 10) {
                    er_address.setVisibility(View.GONE);
                    im_address.setImageResource(R.drawable.address_1);
                }
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

                if (!PATTERN.matcher(s.toString()).matches()) {
                    er_password.setVisibility(View.VISIBLE);
                    er_password.setText("Password (must contain one upper character, one lower character and a number. Max length 15 and min length 8)\n");
                } else {
                    er_password.setVisibility(View.GONE);
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (ed_password.getText().toString().equals(s.toString())) {
                    er_password.setVisibility(View.GONE);
                    im_password.setImageResource(R.drawable.key_1);

                } else {

                    er_password.setVisibility(View.VISIBLE);
                    er_password.setText("Password does not match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signupBtn.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        //clearing all data before fetching new set of data
        loadingDialog.show();


        if (!ed_name.getText().toString().equals("")) {
            if (!ed_phone.getText().toString().equals("")) {
                if (!ed_email.getText().toString().equals("")) {
                    if (!ed_address.getText().toString().equals("")) {
                        if (!ed_password.getText().toString().equals("")) {
                            if (ed_confirm_password.getText().toString().equals(ed_password.getText().toString())) {

                                database.userDao().reset(userDataList);

                                UserData data = new UserData();
                                //Set data to the data
                                data.setName(ed_name.getText().toString());
                                data.setPhone_no(ed_phone.getText().toString());
                                data.setEmail(ed_email.getText().toString());
                                data.setAddress(ed_address.getText().toString());
                                data.setPassword(ed_password.getText().toString());
                                //Insert data in database
                                database.userDao().insert(data);

                                userDataList.addAll(database.userDao().getAll());

                                Log.d(TAG,"Data uploaded to Json file");
                                Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                            } else {
                                Toast.makeText(this, "password did not match", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                } else {
                    Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Enter PhoneNumber", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        } else {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }
}