package jkmdroid.toptier;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class UserActivity extends AppCompatActivity{
    final int REGISTER = 1, LOGIN = 2;
    private ProgressDialog progressDialog;
    Tip tip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        tip = new Tip();
        init();
    }

    private void init(){
        SharedPreferences loginPreferences = getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);

        if (loginPreferences.getString(Preferences.Login.USERNAME,"").length() > 3){
            if (loginPreferences.getBoolean(Preferences.Login.LOGGED, false)){
                startActivity(new Intent(UserActivity.this, MainActivity.class));
                finish();
            }else {
                login();
            }
        }else {
            register();
        }

    }

    private void register(){
        setContentView(R.layout.register);
        //redirect to the login page of the use
        //has an account
        ((TextView)findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText)findViewById(R.id.password)).getText().toString();
                String confirm = ((EditText)findViewById(R.id.confirm)).getText().toString();
                String email = ((EditText)findViewById(R.id.email)).getText().toString();
                String username = ((EditText)findViewById(R.id.username)).getText().toString();

                password = password.trim();
                confirm = confirm.trim();
                email = email.trim();
                username = username.trim();

                String error = "";
                if (password.length() < 5)
                    error += "\nPassword too short";
                if (!password.equals(confirm))
                    error +="\nPassword do not match";
                if (!email.contains("@") || !email.contains(".") || email.indexOf('@') > email.lastIndexOf('.'))
                    error +="\nInvalid email";
                if (username.length() < 5)
                    error = "\nToo short username";

                if (error.trim().length() > 5)
                    ((TextView)findViewById(R.id.error)).setText(error);
                else if (!MyHelper.isOnline(UserActivity.this)){
                    Snackbar.make(findViewById(R.id.layout), "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
                else {
                    String data = "";
                    try {
//                        data += URLEncoder.encode("phonenumber", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&";
                        data += URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&";
                        data += URLEncoder.encode("register_user", "UTF-8") + "=" + URLEncoder.encode("register", "UTF-8") + "&";
                        data += URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&";
                        data += URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                        progressDialog = new ProgressDialog(UserActivity.this,R.style.progressDialogColor);
                        progressDialog.setMessage("Registering...Please wait");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(true);
                        if (progressDialog != null)
                            progressDialog.show();
                        sendOnline(REGISTER,"https://toptier.mblog.co.ke/users/register_user.php", data);

                        tip.setUsername(username);
                        tip.setEmail(email);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void login(){
        setContentView(R.layout.login);
        //redirect to the register page if the user lacks
        //an account
        (findViewById(R.id.register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String user = ((EditText)findViewById(R.id.user)).getText().toString();
                String password = ((EditText)findViewById(R.id.password)).getText().toString();

                user = user.trim();
                password = password.trim();

                String error = "";
                if (user.length() < 5)
                    error += "\nUsername/email invalid";
                if (password.length() < 5)
                    error += "\nPassword invalid";

                if (error.trim().length() > 5)
                    ((TextView)findViewById(R.id.error)).setText(error);
                else if (!MyHelper.isOnline(UserActivity.this)){
                    Snackbar.make(findViewById(R.id.layout), "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
                else {
                    String data = "";
                    try {
                        data += URLEncoder.encode("email_username", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&";
                        data += URLEncoder.encode("login_user", "UTF-8") + "=" + URLEncoder.encode("login", "UTF-8") + "&";
                        data += URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                        progressDialog = new ProgressDialog(UserActivity.this,R.style.progressDialogColor);
                        progressDialog.setMessage("Authenticating...Please wait");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(true);
                        if (progressDialog != null)
                            progressDialog.show();
                        sendOnline(LOGIN, "https://toptier.mblog.co.ke/users/login_user.php", data);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void sendOnline(final int item, final String link, final String data){
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                switch (item){
                    case REGISTER:
                        if (((String)msg.obj).equalsIgnoreCase("registered successfully")){
                            SharedPreferences preferences = getSharedPreferences(Preferences.Register.NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString(Preferences.Register.EMAIL, tip.getEmail());
                            editor.putString(Preferences.Register.USERNAME, tip.getUsername());
                            editor.putBoolean(Preferences.Register.REGISTERED, true);
                            editor.apply();

                            AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                            builder.setTitle("Registration is Successful")
                                    .setMessage("You have been registered successfully")
                                    .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            login();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                            builder.setMessage(((String)msg.obj))
                                    .setTitle("Error Occurred")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                        break;
                    case LOGIN:
                        //check if the response is in json format
                        if(((String)msg.obj).startsWith("{") && (((String) msg.obj).endsWith("}"))){
                            JSONObject object = null;
                            try {
                                object = new JSONObject((String)msg.obj);
                                //if the status code is 200==>login is successful
                                if(object.getInt("status_code") == 200){
                                    SharedPreferences preferences = getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Preferences.Login.EMAIL, object.getString("email"));
                                    editor.putString(Preferences.Login.USERNAME, object.getString("username"));
                                    editor.putInt(Preferences.Login.STATUS, object.getInt("user_status"));
                                    editor.putBoolean(Preferences.Login.LOGGED, true);
                                    editor.apply();

                                   Toast.makeText(UserActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(UserActivity.this, MainActivity.class));
                                    finish();
                                    //login attempt is unsuccessful
                                }else if(object.getInt("status_code") == 201){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                                    builder.setMessage(object.getString("message"))
                                            .setTitle("Error Occurred")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", null)
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                            builder.setMessage(((String)msg.obj))
                                    .setTitle("Error Occurred")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                        break;
                }
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    String response = MyHelper.connectOnline(link, data);
                    Message message = new Message();
                    message.arg1 = 1;
                    message.obj = response;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setCancelable(false)
                .setPositiveButton("No", null)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setMessage("Do you really want to exit")
                .show();
    }
}