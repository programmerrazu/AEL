package com.razu.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.razu.Apps;
import com.razu.R;
import com.razu.helper.OnItemClickListener;
import com.razu.helper.OrderAdapter;
import com.razu.helper.RestaurantInfo;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView rvOrder;
    private SwipeRefreshLayout pullToRefresh;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setUIComponent();
    }

    private void setUIComponent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Apps.redirect(OrderActivity.this, MainActivity.class);
            }
        });

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });

        getRestaurant();
    }

    private void getRestaurant() {
        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.setImage("https://media.timeout.com/images/102600575/image.jpg");
        restaurantInfo.setIcon("https://media.timeout.com/images/102600575/image.jpg");
        restaurantInfo.setName("SubHub");
        restaurantInfo.setSlogan("Casual Food for kids");
        restaurantInfo.setRating(4.5f);
        restaurantInfo.setNumberOfRating(20);
        restaurantInfo.setMinimumOrder(50f);
        restaurantInfo.setStatus(true);
        restaurantInfo.setQnt(2);
        restaurantInfo.setPrice(100);

        RestaurantInfo restaurantInfo2 = new RestaurantInfo();
        restaurantInfo2.setImage("https://naosusu.com/wp-content/uploads/2018/07/Popular-and-best-restaurants-in-Warri.jpg");
        restaurantInfo2.setIcon("https://naosusu.com/wp-content/uploads/2018/07/Popular-and-best-restaurants-in-Warri.jpg");
        restaurantInfo2.setName("Jal Mukut");
        restaurantInfo2.setSlogan("Italian");
        restaurantInfo2.setRating(3.5f);
        restaurantInfo2.setNumberOfRating(25);
        restaurantInfo2.setMinimumOrder(100f);
        restaurantInfo2.setStatus(false);
        restaurantInfo2.setQnt(3);
        restaurantInfo2.setPrice(150);

        RestaurantInfo restaurantInfo3 = new RestaurantInfo();
        restaurantInfo3.setImage("http://www.makenewzealandhome.com/wp-content/uploads/2018/07/Restaurants2.jpg");
        restaurantInfo3.setIcon("http://www.makenewzealandhome.com/wp-content/uploads/2018/07/Restaurants2.jpg");
        restaurantInfo3.setName("The Brownie Coterie");
        restaurantInfo3.setSlogan("Indian");
        restaurantInfo3.setRating(5.0f);
        restaurantInfo3.setNumberOfRating(30);
        restaurantInfo3.setMinimumOrder(150f);
        restaurantInfo3.setStatus(true);
        restaurantInfo3.setQnt(4);
        restaurantInfo3.setPrice(50);

        final List<RestaurantInfo> restaurantInfoList = new ArrayList<>();
        restaurantInfoList.add(restaurantInfo);
        restaurantInfoList.add(restaurantInfo2);
        restaurantInfoList.add(restaurantInfo3);

        orderAdapter = new OrderAdapter(this, restaurantInfoList, new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = new Intent(RestaurantActivity.this, RestaurantDetails.class);
//
//                intent.putExtra("title", restaurantInfoList.get(position).getName());
//                intent.putExtra("image", restaurantInfoList.get(position).getImage());
//                intent.putExtra("slogan", restaurantInfoList.get(position).getSlogan());
//                intent.putExtra("rating", restaurantInfoList.get(position).getRating()
//                        + " " + "(" + restaurantInfoList.get(position).getNumberOfRating() + ")");
//                intent.putExtra("minimum_order", restaurantInfoList.get(position).getMinimumOrder() + " minimum");
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//               finish();
            }
        });
        LinearLayoutManager restaurantManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvOrder = (RecyclerView) findViewById(R.id.recycler_view_order);
        rvOrder.setLayoutManager(restaurantManager);
        rvOrder.setHasFixedSize(true);
        rvOrder.setNestedScrollingEnabled(false);
        rvOrder.setAdapter(orderAdapter);
    }

    @Override
    public void onBackPressed() {
        Apps.redirect(OrderActivity.this, MainActivity.class);
    }
}