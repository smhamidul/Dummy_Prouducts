package com.hamidul.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartList extends AppCompatActivity {
    GridView textView;
    public static ArrayList<ModelClass> arrayList=new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_list);

        textView = findViewById(R.id.textView);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();

        CartAdapter cartAdapter = new CartAdapter();
        textView.setAdapter(cartAdapter);

    }

    private class CartAdapter extends BaseAdapter{
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.add_cart_item,parent,false);

            ImageView cartImage = view.findViewById(R.id.cartImage);
            TextView cartTitle = view.findViewById(R.id.cartTitle);
            TextView cartPrice = view.findViewById(R.id.cartPrice);
            Button delete = view.findViewById(R.id.delete);

            String id = arrayList.get(position).id;
            String url = arrayList.get(position).url;
            String title = arrayList.get(position).title;
            String price = arrayList.get(position).price;

            cartTitle.setText(title);
            cartPrice.setText("TK : "+price);

            Picasso.get()
                    .load(url)
                    .into(cartImage);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(position);
                    editor.putInt(id,0);
                    Gson gson = new Gson();
                    String json = gson.toJson(arrayList);
                    editor.putString("list",json);
                    editor.apply();
                    CartAdapter cartAdapter = new CartAdapter();
                    textView.setAdapter(cartAdapter);
                }
            });

            return view;
        }
    }


    private void loadData (){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list",null);
        Type type = new TypeToken<ArrayList<ModelClass>>(){
        }.getType();
        arrayList = gson.fromJson(json,type);
        if (arrayList==null){
            arrayList=new ArrayList<>();
        }
    }

}