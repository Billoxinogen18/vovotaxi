package com.israelox.taxi.taxit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class RealLogConsumer extends AppCompatActivity {
    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mLogin_btn;
    private Button regist_button;
    private ProgressDialog mLoginProgress;

    private boolean isMainLobbyStart=false;

    private String user_id;


    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;
    private EditText mEditText;
    private String display_name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_cons);

        mAuth = FirebaseAuth.getInstance();





        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginEmail=(TextInputLayout) findViewById(R.id.register_email);
        mLoginPassword=(TextInputLayout)findViewById(R.id.reg_password);
        mLogin_btn=(Button)findViewById(R.id.login_button);
        regist_button=(Button)findViewById(R.id.logina);
        
        regist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginSignUpDriver.class));
            }
        });

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Checking in.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);

                }

                else
                {

                    mLoginEmail.setHint("Please Enter Email");

                }

            }
        });







//        user_id = FirebaseAuth.getInstance().getUid();
//        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);






//            if(AccessToken.getCurrentAccessToken()!=null)
//            {
//
//                loginButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        isLogged();
//                    }
//                });
//            }







    }



    private void loginUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("STATEDRIVE", "Passenger").apply();




                            Intent mainIntent = new Intent(RealLogConsumer.this, DriverMapActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();


                        }
                    });




                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(RealLogConsumer.this, "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });


    }















}
