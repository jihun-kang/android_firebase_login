package com.a2big.firebase.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;


import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    // UI references.
    private EditText mEmail;
    private EditText mPasswd;
    private View mProgressView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean mAllowNavigation = true;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        mEmail = (EditText) findViewById(R.id.email);
        mPasswd = (EditText) findViewById(R.id.passwd);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();

        Button btnRegisterButton = (Button) findViewById(R.id.email_sign_up_button);
        if (btnRegisterButton != null) {
            btnRegisterButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptRegister();
                }
            });
        }

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TEST", "onAuthStateChanged:signed_in:" + user.getUid());

                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            // email sent
                                            // after email is sent just logout the user and finish this activity
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            // email not sent, so display message and restart the activity or do whatever you wish to do

                                            //restart this activity
                                            overridePendingTransition(0, 0);
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());

                                        }
                                    }
                                });
                        Toast.makeText(getApplicationContext(), R.string.msg_email_address_not_verified, Toast.LENGTH_LONG).show();
                    }


                } else {
                    // User is signed out
                    Log.d("TEST", "onAuthStateChanged:signed_out");
                }

            }
        };
        // [END auth_state_listener]

    }

    private boolean checkEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean checkPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        /*
        ^                 # start-of-string
        (?=.*[0-9])       # 숫자는 적어도 한번 사용
        (?=.*[a-z])       # 소문자 적어도 한번 사용
        (?=.*[A-Z])       # 대문자 적어도 한번 사용
        (?=.*[@#$%^&+=])  # 해당 특수문자 사용 가능
        (?=\\S+$)         # 공백은 사용 불가능
        .{6,}             # 최소한 문자열 6글자 이상 조합
         $
         */
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    private void attemptRegister() {
        String email = mEmail.getText().toString();
        String password = mPasswd.getText().toString();

        if( checkEmail(email) == false){
           // getString(R.string.error_invalid_email)
            Toast.makeText(mContext, R.string.error_invalid_email,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        if( checkPassword(password) == false ){
            Toast.makeText(mContext, R.string.error_invalid_password,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TEST", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                        showProgress(false);
                    }
                });
        // [END create_user_with_email]

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}

