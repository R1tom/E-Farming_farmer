package com.example.e_farmingfarmer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class dashboardFarmer extends AppCompatActivity {


    TextView textView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String pinAddress, Full_name, phoneNumber, state, price;
    ListView requestListView;
    ArrayList<String> requests = new ArrayList<String>();
    ArrayList<String> usernames = new ArrayList<String>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> order = new ArrayList<>();
    ArrayList<String> amount = new ArrayList<>();
    ArrayList<String> stateA = new ArrayList<>();
    ArrayList<String> pin = new ArrayList<>();
    ArrayList<Integer> randoms = new ArrayList<>();


    ArrayAdapter arrayAdapter;

    public void updateList(View view) {

        if (Full_name != null) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

            query.whereEqualTo("pin", pinAddress);// accepted
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {

                        Log.i("YOOOOOOOOOOOOOOOOOOOOOOOOOO","YOOOOOOOOOOO");
                        requests.clear();

                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {


                                usernames.add(object.getString("username"));
                                phones.add(object.getString("phone"));
                                order.add(object.getString("order"));
                                amount.add(object.getString("amount"));
                                stateA.add(object.getString("State"));
                                pin.add(object.getString("pin"));
                                randoms.add(object.getInt("random"));
                                //object.put("order_done","DONE");

                                Log.i("Hellllo",object.getString("random"));

                                requests.add(object.getString("username") + "\n" + "ordered " +object.getString("order")+ " of " + object.getString("amount")+" KG"+"\n" + "PIN "
                                + object.getString("pin") + " Phone number : "+object.getString("phone"));




                            }


                        } else {

                            Log.i("Noactiverequestsnearby", "o");
                            requests.add("No order available nearby");

                        }


                        arrayAdapter.notifyDataSetChanged();

                    }

                }
            });


        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_farmer);

        textView = findViewById(R.id.textViewE);
        requestListView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);

        requests.clear();

        requests.add("Getting nearby orders...");

        requestListView.setAdapter(arrayAdapter);



        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("farmerDetails");
        query1.whereEqualTo("Full_name",ParseUser.getCurrentUser().getUsername());

        query1.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> user, ParseException e) {
                if (e == null) {

                    Log.d("police", "Retrieved " + user.size() + " scores");

                    for(ParseObject users : user){
                        Log.d("police", "Retrieved " + users.getString("Full_name") );
                        Log.d("police", "Retrieved " + users.getString("Phone_number") );
                        Log.d("police", "Retrieved " + users.getString("Pin") );
                        Full_name = users.getString("Full_name");
                        phoneNumber = users.getString("Phone_number");
                        pinAddress = users.getString("Pin");
                        state = users.getString("State");
                        price = users.getString("price");

                        textView.setText("Welcome "+ users.getString("Full_name")+ "\n This is your own Dashboard \n" +
                                "Here you can see all the orders from your area "+ "\n" +"Click on any order to continue");

                        update();


                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        }); // has to change

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final ParseQuery<ParseObject> ok = ParseQuery.getQuery("Request");

                ok.whereEqualTo("pin", pin.get(i));
                ok.whereEqualTo("order",order.get(i));
                ok.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                for (ParseObject object : objects) {

                                    object.deleteInBackground();

                                }
                            }

                        }

                    }
                });

                new AlertDialog.Builder(dashboardFarmer.this)
                        .setTitle("Are you sure want to call "+ usernames.get(i))
                        .setMessage("You will be in direct contact with the buyer")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                if (!phones.get(i).isEmpty()) {
                                    String dial = "tel:" + phones.get(i);
                                    if (ActivityCompat.checkSelfPermission(dashboardFarmer.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        requestPermission();
                                        return;
                                    }

                                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));


                                }else {
                                    Toast.makeText(dashboardFarmer.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });



    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to Logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOutInBackground();
                        finish();
                        Intent intent =  new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(dashboardFarmer.this, new String[]
                {

                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);

    }

    public void update(){
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

        query.whereEqualTo("pin", pinAddress);// accepted
        query.whereDoesNotExist("orderDoneBy");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    Log.i("YOOOOOOOOOOOOOOOOOOOOOOOOOO","YOOOOOOOOOOO");
                    requests.clear();

                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {


                            usernames.add(object.getString("username"));
                            phones.add(object.getString("phone"));
                            order.add(object.getString("order"));
                            amount.add(object.getString("amount"));
                            stateA.add(object.getString("State"));
                            pin.add(object.getString("pin"));
                            //object.put("order_done","DONE");

                            requests.add(object.getString("username") + "\n" + "ordered " +object.getString("order")+ " of " + object.getString("amount")+" KG"+"\n" + "PIN "
                                    + object.getString("pin") + " Phone number : "+object.getString("phone"));




                        }


                    } else {

                        Log.i("Noactiverequestsnearby", "o");
                        requests.add("No order available nearby");

                    }


                    arrayAdapter.notifyDataSetChanged();

                }

            }
        });


    }

}

