package com.example.masho.instagenew;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpPage extends AppCompatActivity {

    private EditText mEmail;
    private EditText mNames;
    private EditText mPassword;
    private Button mSignupBtn;
    private FirebaseAuth mAuths;
    private FirebaseUser user;
    private TextView signin;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);


        mEmail = (EditText) findViewById(R.id.email);
        mAuths = FirebaseAuth.getInstance();
        mNames = (EditText) findViewById(R.id.MembershipNo);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").push().child("Sign-up");
        mPassword = (EditText) findViewById(R.id.password);
        mSignupBtn = (Button) findViewById(R.id.email_sign_up_button);
        signin = (TextView) findViewById(R.id.Signintxt);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Name = mEmail.getText().toString();
                final String Email = mNames.getText().toString();
                final String Pass = mPassword.getText().toString();

                user = mAuths.getCurrentUser();

                if (TextUtils.isEmpty(Name) && TextUtils.isEmpty(Email) && TextUtils.isEmpty(Pass)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Insert all Fields", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                } else if (!Email.contains("@") || !Email.contains(".com")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Check your Email", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                } else if (Pass.length() != 6) {

                    Toast toast = Toast.makeText(getApplicationContext(), "Your Password length must be equal to six", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                } else {

                    mAuths.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registered Succesfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpPage.this, HomePage.class));
                                FirebaseUser user = mAuths.getCurrentUser();
                                if (user != null) {
                                    String uId = user.getUid();
                                    databaseReference.child("Email").setValue(Email);
                                    databaseReference.child("UId").setValue(uId);
                                    databaseReference.child("Name").setValue(Name);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "User Already Exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
