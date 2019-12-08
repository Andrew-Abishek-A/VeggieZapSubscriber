package com.example.veggiezapsubscriber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    static MainPageActivity instance;

    private String TAG = "MainPageActivity";
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> qty = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    RecyclerViewAdapter adapter;
    double total=0;
    final ArrayList<Integer> Total = new ArrayList<>();
    ConstraintLayout MainLayout;

    public static MainPageActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        MainLayout = findViewById(R.id.MainLayout);
        instance = this;
        hideNavigationBar();

        Button userDetails = findViewById(R.id.detailsButton);
        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, UserDetails.class));
            }
        });

        Button Order = findViewById(R.id.retailerButton);
        Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, OrderActivity.class));
            }
        });

        FloatingActionButton buy = findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, PayActivity.class));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        hideNavigationBar();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        UpdateUI(currentUser);
        try {
            items.removeAll(items);
            qty.removeAll(qty);
            price.removeAll(price);
            date.removeAll(date);
            Total.removeAll(Total);
            total = 0;
        }
        catch (Exception e){
            Log.d(TAG, "onStart: There is nothing i can do");
        }
    }

    public void UpdateUI(final FirebaseUser user){
        try{
            db.collection("users")
                    .document(user.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                Map<String, Object> data = documentSnapshot.getData();
                                TextView Welcome = findViewById(R.id.welcome);
                                Welcome.setText("Welcome " + data.get("name"));
                                TextView Retailer = findViewById(R.id.retailer);
                                Retailer.setText("Subscribed to: " + data.get("retailer"));
                                TextView Address = findViewById(R.id.textView);
                                Address.setText("Address:\n" + data.get("address"));
                                db.collection("users")
                                        .document(user.getEmail())
                                        .collection("cart")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                try {
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        items.add(document.get("name").toString());
                                                        qty.add(document.get("quantity").toString());
                                                        date.add(document.get("datetime").toString());
                                                        price.add(document.get("price").toString());
                                                        Total.add(Integer.parseInt(document.get("quantity").toString())
                                                                * Integer.parseInt(document.get("price").toString()));
                                                    }
                                                    initRecyclerView();
                                                    updateTotal();
                                                }
                                                catch(Exception e){
                                                    Log.d(TAG, "onSuccess: There is somthing wrong");
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: There is nothing here");
                                            }
                                        });
                            }
                            catch(Exception E){
                                Log.d(TAG, "onSuccess: There is nothing to display");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: This Failed :)");
                        }
                    });
        }
        catch (Exception e){
            Log.d(TAG, "UpdateUI: This Failed :)");
        }
    }

    private void updateTotal() {
        for (int i = 0; i < Total.size(); i++) {
            total += Total.get(i);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", String.valueOf(total));
        data.put("items", String.valueOf(Total.size()));
        data.put("datetime", DateFormat.getDateTimeInstance().format(new Date()));
        db.collection("users")
                .document(mAuth.getCurrentUser().getEmail())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: YAYAYAY");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Sad Life");
                    }
                });
    }

    public void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Started recycler view");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, items, qty, date, price, MainLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
