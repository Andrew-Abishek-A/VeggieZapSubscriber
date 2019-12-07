package com.example.veggiezapsubscriber;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String TAG = "RecyclerViewAdapter";

    private String Tag = "com.example.veggiezapsubscriber.RecyclerViewAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<String> items;
    ArrayList<String> qty;
    ArrayList<String> price;
    ArrayList<String> date;
    Context mContext;
    double Total=0;
    ConstraintLayout layout;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> items, ArrayList<String> qty,
                               ArrayList<String> date, ArrayList<String> price,
                               ConstraintLayout layout){
        this.mContext = mContext;
        this.items = items;
        this.qty = qty;
        this.date = date;
        this.price = price;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: The View Binder Has Been Called");
        holder.item.setText(items.get(position));
        holder.price.setText(price.get(position));
        holder.date.setText(date.get(position));
        holder.qty.setText(qty.get(position));
        Glide.with(mContext)
                .asBitmap()
                .load("https://www.vegsoc.org/wp-content/uploads/2019/03/vegetable-box-750x580.jpg")
                .into(holder.image);
        Total += Integer.parseInt(qty.get(position)) * Integer.parseInt(price.get(position));

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromCart(position);
            }
        });
    }

    private void removeFromCart(int position) {
        final String item = items.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_window1,null);

        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        Button popYes = customView.findViewById(R.id.popYes);
        popYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users")
                        .document(mAuth.getCurrentUser().getEmail())
                        .collection("cart")
                        .document(item)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                MainPageActivity mainPageActivity = MainPageActivity.getInstance();
                                mainPageActivity.onStart();
                                popupWindow.dismiss();
                                //mContext.startActivity(new Intent(mContext, MainPageActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: This should'nt have happened");
                            }
                        });
            }
        });

        Button popCan = customView.findViewById(R.id.popCan);
        popCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView item;
        TextView qty;
        TextView price;
        TextView date;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            item = itemView.findViewById(R.id.item);
            qty = itemView.findViewById(R.id.qty);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
