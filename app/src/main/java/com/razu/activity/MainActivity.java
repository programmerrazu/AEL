package com.razu.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.razu.Apps;
import com.razu.R;
import com.razu.helper.OnItemClickListener;
import com.razu.helper.PreferencesManager;
import com.razu.helper.RestaurantAdapter;
import com.razu.helper.RestaurantInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float MAP_ZOOM = 14.0f;
    private SupportMapFragment fragmentMaps;
    private GoogleMap maps;
    private Boolean locationPermissionGranted;
    private View btnLocation;
    private FloatingActionButton fabBtnLocation;
    private CardView cardViewSearchContainer;
    private TextView tvCLoc;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    public static final String EXTRA_REVEAL_X = "EXTRA_REVEAL_X";
    public static final String EXTRA_REVEAL_Y = "EXTRA_REVEAL_Y";
    private Boolean doubleBackPressed = false;
    private PreferencesManager session;
    private RecyclerView rvRestaurants;
    private SwipeRefreshLayout pullToRefresh;
    private RestaurantAdapter restaurantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new PreferencesManager(this);
        if (savedInstanceState == null) {
            revealAnimation();
        }
        setUIComponent();
    }

    private void revealAnimation() {
        final View mainLayout = findViewById(R.id.main_drawer_layout);
        final Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && intent.hasExtra(EXTRA_REVEAL_X) && intent.hasExtra(EXTRA_REVEAL_Y)) {
            mainLayout.setVisibility(View.INVISIBLE);
            ViewTreeObserver viewTreeObserver = mainLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealAnimator(mainLayout);
                        mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void revealAnimator(View rootLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator animator = ViewAnimationUtils.createCircularReveal(rootLayout, rootLayout.getMeasuredWidth() / 2, rootLayout.getMeasuredHeight() / 2, 50, finalRadius);
            animator.setDuration(800);
            animator.setInterpolator(new AccelerateInterpolator());
            rootLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            finish();
        }
    }

    private void setUIComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restaurantsAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });

        ((CircleImageView) findViewById(R.id.civ_photo)).setImageResource(R.drawable.app_logo);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getRestaurant();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onHome(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void onOrder(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Apps.redirect(MainActivity.this, OrderActivity.class);
    }

    public void onPayments(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Apps.redirect(MainActivity.this, PaymentsActivity.class);
    }

    public void onAbout(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Apps.redirect(MainActivity.this, AboutActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackPressed) {
                finish();
                // moveTaskToBack(true);
            } else {
                doubleBackPressed = true;
                Apps.snackBarMsg(getString(R.string.back_press), drawerLayout, this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed = false;
                    }
                }, 3000);
            }
        }
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

        RestaurantInfo restaurantInfo2 = new RestaurantInfo();
        restaurantInfo2.setImage("https://naosusu.com/wp-content/uploads/2018/07/Popular-and-best-restaurants-in-Warri.jpg");
        restaurantInfo2.setIcon("https://naosusu.com/wp-content/uploads/2018/07/Popular-and-best-restaurants-in-Warri.jpg");
        restaurantInfo2.setName("Jal Mukut");
        restaurantInfo2.setSlogan("Italian");
        restaurantInfo2.setRating(3.5f);
        restaurantInfo2.setNumberOfRating(25);
        restaurantInfo2.setMinimumOrder(100f);
        restaurantInfo2.setStatus(false);

        RestaurantInfo restaurantInfo3 = new RestaurantInfo();
        restaurantInfo3.setImage("http://www.makenewzealandhome.com/wp-content/uploads/2018/07/Restaurants2.jpg");
        restaurantInfo3.setIcon("http://www.makenewzealandhome.com/wp-content/uploads/2018/07/Restaurants2.jpg");
        restaurantInfo3.setName("The Brownie Coterie");
        restaurantInfo3.setSlogan("Indian");
        restaurantInfo3.setRating(5.0f);
        restaurantInfo3.setNumberOfRating(30);
        restaurantInfo3.setMinimumOrder(150f);
        restaurantInfo3.setStatus(true);

        final List<RestaurantInfo> restaurantInfoList = new ArrayList<>();
        restaurantInfoList.add(restaurantInfo);
        restaurantInfoList.add(restaurantInfo2);
        restaurantInfoList.add(restaurantInfo3);

        restaurantsAdapter = new RestaurantAdapter(this, restaurantInfoList, new OnItemClickListener() {
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
//                RestaurantActivity.this.startActivity(intent);
//                RestaurantActivity.this.overridePendingTransition(0, 0);
//                RestaurantActivity.this.finish();
            }
        });
        LinearLayoutManager restaurantManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRestaurants = (RecyclerView) findViewById(R.id.recycler_view_restaurants);
        rvRestaurants.setLayoutManager(restaurantManager);
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setNestedScrollingEnabled(false);
        rvRestaurants.setAdapter(restaurantsAdapter);
    }


    /**********************************************/

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        maps = googleMap;
//        maps.setTrafficEnabled(false);
//        maps.setIndoorEnabled(false);
//        maps.setBuildingsEnabled(false);
//        maps.getUiSettings().setZoomControlsEnabled(false);
//        getLocationPermission();
//        getLocations();
//    }
//    private void getLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    private void getLocations() {
//        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//                    maps.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, MAP_ZOOM));
//                    getAddress(location.getLatitude(), location.getLongitude());
//                    maps.setMyLocationEnabled(true);
//                    customiseBtnLocation();
//                }
//            }
//        });
//    }

//    private void customiseBtnLocation() {
//        if (maps == null) {
//            return;
//        }
//        try {
//            if (locationPermissionGranted) {
//                btnLocation = ((View) fragmentMaps.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//                if (btnLocation != null) {
//                    btnLocation.setVisibility(View.GONE);
//                }
//                fabBtnLocation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (maps != null) {
//                            if (btnLocation != null) {
//                                btnLocation.callOnClick();
//                            }
//                        }
//                    }
//                });
//            } else {
//                getLocationPermission();
//            }
//        } catch (SecurityException e) {
//
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
//                if (grantResults.length > 0) {
//                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (locationAccepted) {
//                        locationPermissionGranted = true;
//                        getLocations();
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
//                                showMessageOKCancel(
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                                                }
//                                            }
//                                        });
//                            }
//                        }
//                    }
//                }
//        }
//    }

//    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(MainActivity.this)
//                .setMessage("You need to allow location access permission")
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }

//    public void getAddress(double lat, double lng) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
//            Address obj = addresses.get(0);
//            tvCLoc.setText(obj.getAddressLine(0));
//
//            /* String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare(); */
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}