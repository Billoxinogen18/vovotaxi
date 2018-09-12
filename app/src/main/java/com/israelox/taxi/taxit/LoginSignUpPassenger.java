package com.israelox.taxi.taxit;

import android.app.ProgressDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;




public class LoginSignUpPassenger extends AppCompatActivity {
    private DatabaseReference mDatabase;


    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout phonenumb;
    private Button mCreateBtn;
    private Button loginActiv;
    private Button lognActivity;


    private Toolbar mToolbar;



    //ProgressDialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;
private Button skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginconsumer);


        mRegProgress = new ProgressDialog(this);



        // Firebase Auth

        mAuth = FirebaseAuth.getInstance();


        // Android Fields
        lognActivity=(Button)findViewById(R.id.logina);

        mDisplayName = (TextInputLayout) findViewById(R.id.register_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.register_email);
        phonenumb = (TextInputLayout) findViewById(R.id.phonenumb);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.login_button);

lognActivity.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), RealLogConsumer.class));
    }
});
     

//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                startActivity(new Intent(getApplicationContext(), LoginSignUpPassenger.class));
//            }
//        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String phonenumber = phonenumb.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Creating Profile");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password, phonenumber);

                }
                else
                {

                    mDisplayName.setHint("Please Enter Your Username!");
                    mEmail.setHint("Input a Valid e-mail");
                    mPassword.setHint("Input a password");
                }



            }
        });


    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginSignUpPassenger.this, CustomerMapActivity.class));
            finish();
        }
    }
    // [END on_start_check_user]



    private void register_user(final String display_name, String email, String password, final String phonenumber) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "Vovo Ride");
                    userMap.put("image", "default");
                    userMap.put("points", "0");
                    userMap.put("phone", phonenumber);
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {



                            if(task.isSuccessful()){
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("STATEDRIVE", "Passenger").apply();

                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(LoginSignUpPassenger.this, CustomerMapActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });


                } else {

                    mRegProgress.hide();
                    Toast.makeText(LoginSignUpPassenger.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
