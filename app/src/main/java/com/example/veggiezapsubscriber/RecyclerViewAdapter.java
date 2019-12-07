package com.example.veggiezapsubscriber;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    public RecyclerViewAdapter(Context mContext, ArrayList<String> items, ArrayList<String> qty, ArrayList<String> date, ArrayList<String> price){
        this.mContext = mContext;
        this.items = items;
        this.qty = qty;
        this.date = date;
        this.price = price;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                removeFromCart();
            }
        });
    }

    private void removeFromCart() {

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
