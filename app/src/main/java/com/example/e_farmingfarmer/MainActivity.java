package com.example.e_farmingfarmer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText name, email, pass;
    TextView textView;


    public void Login (View vIew){

        if (name.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
            Toast.makeText(MainActivity.this,"Please provide thr details to login",Toast.LENGTH_SHORT).show();

        }else {
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("farmerDetails");
            query1.whereEqualTo("Full_name", name.getText().toString());

            query1.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> user, ParseException e) {
                    if (e == null) {


                        ParseUser.logInInBackground(name.getText().toString(), pass.getText().toString(), new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Toast.makeText(MainActivity.this, "yoo", Toast.LENGTH_SHORT).show();
                                    Intent intent =  new Intent(getApplicationContext(),dashboardFarmer.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "SORRRYYYY", Toast.LENGTH_SHORT).show();
                                    // Signup failed. Look at the ParseException to see what happened.
                                }
                            }
                        });

                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        textView = findViewById(R.id.tv);


          ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.getUsername() != null) {
            Intent intent =  new Intent(getApplicationContext(),dashboardFarmer.class);
            startActivity(intent);
        } else {
            // show the signup or login screen
        }

    }

    public void onClickReg (View view){
        try{
            Intent intent = new Intent(MainActivity.this,Register.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to Exit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOutInBackground();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
