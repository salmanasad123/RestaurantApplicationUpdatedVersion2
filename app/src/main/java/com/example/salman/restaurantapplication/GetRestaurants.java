package com.example.salman.restaurantapplication;

import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;

public class GetRestaurants extends AppCompatActivity {


    private static final String TAG = "MTAG";
    List<Restaurant> restaurants;
    Gson gson;
    android.support.v7.widget.Toolbar toolbar;
    EditText editText;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RestaurantAdapter restaurantAdapter;

    Button customerAccountinfo;
    Button orderHistory;
    SharedPreferences sharedPreferences;
    Integer CustomerIDfromSharedPreferences;


    // ACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_restaurants);
        sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        CustomerIDfromSharedPreferences = sharedPreferences.getInt("customerID", 0);

        EventBus.getDefault().postSticky(new AccountIDEvent(CustomerIDfromSharedPreferences));

        /**
         * RecyclerView
         **/
        recyclerView = findViewById(R.id.showRestaurants);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        ImageView imageView = findViewById(R.id.mainImage);
        editText = findViewById(R.id.SearchEditText);
        customerAccountinfo = findViewById(R.id.AccountInfo);
        orderHistory = findViewById(R.id.btnOrderDetails);
        toolbar = findViewById(R.id.getRestaurantToolbar);


        // dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        // recyclerView.addItemDecoration(dividerItemDecoration);


        customerAccountinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetRestaurants.this, CustomerAccountActivity.class);
                startActivity(intent);
            }
        });
        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetRestaurants.this, Main2Activity.class);
                startActivity(intent);
            }
        });


        /**
         * RETROFIT INSTANCE
         */

        Retrofit retrofit = RetrofitClient.getClient();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<List<Restaurant>> restaurantList = apiInterface.getRestaurants();
        restaurantList.enqueue(new Callback<List<Restaurant>>() {

            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {

                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                restaurants = response.body();


                /*
                Collections.sort(restaurants, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant restaurant, Restaurant t1) {
                        return restaurant.getRating() > t1.getRating() ? -1 : (restaurant.getRating() < t1.getRating()) ? 1 : 0;
                    }
                });

                */
              //  restaurantAdapter = new RestaurantAdapter(GetRestaurants.this, restaurants);
                //recyclerView.setAdapter(restaurantAdapter);

                /**
                 * Progress Bar will become invisible when data is loaded, its like
                 * a loading screen
                 */
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


    }

    public void filter(String text) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getRestaurantName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }
        restaurantAdapter.filterList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}

