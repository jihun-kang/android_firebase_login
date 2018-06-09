package com.a2big.firebase.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;



import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;



public class ResetActivity extends AppCompatActivity {

    private EditText mEmailView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mEmailView = (EditText) findViewById(R.id.email);
        mProgressView = findViewById(R.id.login_progress);

        Button btnPasswordRecovery = (Button) findViewById(R.id.password_recovery_button);
            btnPasswordRecovery.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = mEmailView.getText().toString();

                    showProgress(true);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Log.d("ResetActivity", "Email sent 0000.");

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("ResetActivity", "Email sent 11111.");

                                    if (task.isSuccessful()) {
                                        Log.d("ResetActivity", "Email sent.");
                                        showProgress(false);
                                        finish();
                                    } else {
                                        //restart this activity
                                        overridePendingTransition(0, 0);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                    }
                                }
                            });
                }
            });
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

