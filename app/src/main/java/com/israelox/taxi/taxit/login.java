package com.israelox.taxi.taxit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

public class login extends AppCompatActivity implements PermissionCallback, ErrorCallback {
    private static final String TAG = "DemoActivity";
    private static final int REQUEST_PERMISSIONS = 20;
    private Button reqButton;

    private Button driver;
    private Button passenger;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    SharedPreferences sp;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            String state= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("STATEDRIVE", "none");

            if(state.equals("Driver")) {
                startActivity(new Intent(getApplicationContext(), DriverMapActivity.class));
                finish();
            }
            else
                if (state.equals("Passenger"))
                {
                    startActivity(new Intent(getApplicationContext(), CustomerMapActivity.class));
                    finish();

                }

                else
                {
                    return;
                }
        }

        reqPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();


        driver=(Button)findViewById(R.id.driver);
        passenger=(Button)findViewById(R.id.passenger);

        startService(new Intent(login.this, onAppKilled.class));
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getApplicationContext(), LoginSignUpDriver.class));
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );


            }
        });

        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginSignUpPassenger.class));
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );

            }
        });


    }
    private void reqPermission() {
        new AskPermission.Builder(this).setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.INTERNET
                , Manifest.permission.READ_EXTERNAL_STORAGE
        ,Manifest.permission.ACCESS_FINE_LOCATION)
                .setCallback(this)
                .setErrorCallback(this)
                .request(REQUEST_PERMISSIONS);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }
}