package com.example.veggiezapsubscriber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        pb = findViewById(R.id.progressbar);
        pb.setVisibility(View.INVISIBLE);

        Button Login = findViewById(R.id.login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        final TextView register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        hideNavigationBar();
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        hideNavigationBar();
        if(currentuser != null) {
            startActivity(new Intent(this, MainPageActivity.class));
        }
    }

    public void login(){
        pb.setVisibility(View.VISIBLE);
        TextView username = findViewById(R.id.username);
        String Email = username.getText().toString();
        TextView password = findViewById(R.id.password);
        String pass = password.getText().toString();
        if(username.getText() == ""){
            Toast.makeText(this,"Enter valid username", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        mAuth.signInWithEmailAndPassword(Email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Signed In" ,Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(MainActivity.this, MainPageActivity.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Login Failed, Please Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void Register(){
        startActivity(new Intent(this, RegisterActivity.class));
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
