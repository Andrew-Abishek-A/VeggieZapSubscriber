package com.example.veggiezapsubscriber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pb = findViewById(R.id.progressbarRegister);
        pb.setVisibility(View.INVISIBLE);

        final ImageView im = findViewById(R.id.correct);
        im.setVisibility(View.INVISIBLE);

        final ImageView im1 = findViewById(R.id.incorrect);
        im1.setVisibility(View.INVISIBLE);

        TextView PasswordConfirm = findViewById(R.id.registerPasswordConfirm);
        PasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView Password = findViewById(R.id.registerPassword);
                String password = Password.getText().toString();
                if(s.toString().equals(password)){
                    im.setVisibility(View.VISIBLE);
                    im1.setVisibility(View.INVISIBLE);
                }
                else{
                    im.setVisibility(View.INVISIBLE);
                    im1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        hideNavigationBar();

    }

    private void Register() {
        pb.setVisibility(View.VISIBLE);
        TextView Email = findViewById(R.id.registerEmail);
        String email = Email.getText().toString();
        TextView Password = findViewById(R.id.registerPassword);
        String password = Password.getText().toString();
        TextView PasswordConfirm = findViewById(R.id.registerPasswordConfirm);
        String passwordConfirm = PasswordConfirm.getText().toString();
        if(email == ""){
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            Email.requestFocus();
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if(!password.equals(passwordConfirm)){
            Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            Password.requestFocus();
            pb.setVisibility(View.VISIBLE);
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(RegisterActivity.this, MainPageActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Failed, Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void hideNavigationBar() {

        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );

    }
}
