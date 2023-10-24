package com.hamidul.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    GridView gridView;
    HashMap<String ,String> hashMap;
    ArrayList<HashMap<String ,String>> arrayList = new ArrayList<>();
    ImageView cart;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        gridView = findViewById(R.id.gridView);
        cart = findViewById(R.id.cart);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CartList.class));
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        String url = "https://dummyjson.com/products";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONArray jsonArray = response.getJSONArray("products");

                    for (int x = 0; x<jsonArray.length(); x++){
                        JSONObject jsonObject = jsonArray.getJSONObject(x);

                        String id = jsonObject.getString("id");
                        String title = jsonObject.getString("title");
                        String price = jsonObject.getString("price");
                        String url = jsonObject.getString("thumbnail");

                        hashMap = new HashMap<>();
                        hashMap.put("id",id);
                        hashMap.put("title",title);
                        hashMap.put("price",price);
                        hashMap.put("url",url);
                        arrayList.add(hashMap);

                    }// for loop end

                    MyAdapter myAdapter = new MyAdapter();
                    gridView.setAdapter(myAdapter);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);



    }

    private class MyAdapter extends BaseAdapter{
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
            View myView = layoutInflater.inflate(R.layout.item,parent,false);

            LinearLayout addCart = myView.findViewById(R.id.addCart);
            ImageView imageView = myView.findViewById(R.id.imageView);
            TextView tvTitle = myView.findViewById(R.id.tvTitle);
            TextView tvPrice = myView.findViewById(R.id.tvPrice);

            HashMap<String,String> hashMap1 = arrayList.get(position);

            int i = position+1;
            String id = hashMap1.get("id");
            String title = hashMap1.get("title");
            String price = hashMap1.get("price");
            String url = hashMap1.get("url");

            tvTitle.setText(title);
            tvPrice.setText("TK : "+price);

            Picasso.get()
                    .load(url)
                    .into(imageView);

            addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = sharedPreferences.getInt(id,0);
                    if (i==q){
                        toastMessage("Al Ready Added");
                    }else {
                        toastMessage("Add to Card");
                        saveData(id,title,price,url);
                        editor.putInt(id,i);
                        editor.apply();
                    }
                }
            });

            return myView;
        }
    }

    private void saveData (String id, String title, String price, String url){
        Gson gson = new Gson();
        CartList.arrayList.add(new ModelClass(id,title,price,url));
        String json = gson.toJson(CartList.arrayList);
        editor.putString("list",json);
        editor.apply();
    }

    private void toastMessage(String message){
        if (toast!=null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
        toast.show();
    }


}