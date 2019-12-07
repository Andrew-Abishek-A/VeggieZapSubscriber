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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private String TAG = "OrderActivity";
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ConstraintLayout layout;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> qty = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    TextView NumberinCart, Total;
    RecyclerViewAdapter1 adapter;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        layout = findViewById(R.id.orderLayout);
        NumberinCart = findViewById(R.id.cart);
        Total = findViewById(R.id.total);
        pb = findViewById(R.id.progressbarOrder);
        pb.setVisibility(View.INVISIBLE);

        getProducts();

        hideNavigationBar();

        FloatingActionButton UploadOrder = findViewById(R.id.uploadOrder);
        UploadOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderUpload();
            }
        });
    }

    private void OrderUpload() {
        pb.setVisibility(View.VISIBLE);
        ArrayList<String> itemsSet = new ArrayList<>(adapter.itemsSet);
        ArrayList<String> qtySet = adapter.qtySet;
        ArrayList<String> priceSet = adapter.priceSet;
        ArrayList<String> dateSet = adapter.dateSet;
        for (int i = 0; i < itemsSet.size(); i++) {
//            System.out.println(itemsSet.get(i));
//            System.out.println(qtySet.get(i));
//            System.out.println(priceSet.get(i));
//            System.out.println(dateSet.get(i));
            Map<String, String> data = new HashMap<>();
            data.put("name", itemsSet.get(i));
            data.put("quantity", qtySet.get(i));
            data.put("price", priceSet.get(i));
            data.put("date", dateSet.get(i));
            //data.put("total", String.valueOf(Total));
            db.collection("users")
                    .document(mAuth.getCurrentUser().getEmail())
                    .collection("cart")
                    .document(itemsSet.get(i))
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(OrderActivity.this, "Upload Succesful", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(OrderActivity.this, MainPageActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            pb.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    private void getProducts() {
        db.collection("zapper")
                .document("test@test.com")
                .collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            items.add(document.get("name").toString());
                            price.add(document.get("price").toString());
                            qty.add("");
                            date.add("");
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: This Failed :)" + e.toString());
                    }
                });
    }

    @Override
    public void onStart(){
        super.onStart();
        hideNavigationBar();
    }

    public void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Started recycler view");
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCart);
        adapter = new RecyclerViewAdapter1(this, items, qty, date, price, layout,
                NumberinCart, Total);
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
