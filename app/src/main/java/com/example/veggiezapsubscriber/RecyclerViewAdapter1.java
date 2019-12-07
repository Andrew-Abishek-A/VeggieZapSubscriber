package com.example.veggiezapsubscriber;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder> {

    private String TAG = "RecyclerViewAdapter";

    ConstraintLayout layout;
    ArrayList<String> items;
    ArrayList<String> qty;
    ArrayList<String> price;
    ArrayList<String> date;
    LinkedHashSet<String> itemsSet = new LinkedHashSet<>();
    ArrayList<String> qtySet = new ArrayList<>();
    ArrayList<String> priceSet = new ArrayList<>();
    ArrayList<String> dateSet = new ArrayList<>();
    double Total=0;
    int count = 0;
    Context mContext;
    TextView NumberinCart, TotalView;

    public RecyclerViewAdapter1(Context mContext, ArrayList<String> items, ArrayList<String> qty,
                                ArrayList<String> date, ArrayList<String> price,
                                ConstraintLayout layout, TextView NumberinCart,
                                TextView Total){
        this.mContext = mContext;
        this.items = items;
        this.qty = qty;
        this.date = date;
        this.price = price;
        this.layout = layout;
        this.NumberinCart = NumberinCart;
        TotalView = Total;
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
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(items.get(position), price.get(position),position);
            }
        });
    }

    private void addToCart(final String item, final String price, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_window,null);

        TextView popItem = customView.findViewById(R.id.popItem);
        popItem.setText("Item: " + item);
        TextView popPrice = customView.findViewById(R.id.popPrice);
        popPrice.setText("Price: " + price);
        final EditText popAmount = customView.findViewById(R.id.popAmount);

        Button popOk = customView.findViewById(R.id.popOk);

        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        popupWindow.setFocusable(true);
        popupWindow.update();

        popOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = popAmount.getText().toString();
                if(amount.equalsIgnoreCase("")){
                    Toast.makeText(mContext, "Enter Quantity", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }

                boolean flag = itemsSet.add(item);
                if(flag){
                    count++;
                    qtySet.add(amount);
                    Total += Integer.parseInt(amount) * Integer.parseInt(price);
                    priceSet.add(price);
                    TotalView.setText("Total: " + Total);
                    NumberinCart.setText("No. In Cart: " + count);
                    DateFormat dateFormat = DateFormat.getDateTimeInstance();
                    dateSet.add(dateFormat.format(new Date()));
                    Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
                else{
                    Toast.makeText(mContext, "Cart Contains This Item", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
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
