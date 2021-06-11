package com.tactical.sanidigsmobile;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.request.DirectionTask;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.message_actions.PNMessageAction;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.tactical.sanidigsmobile.util.Constants;
import com.tactical.sanidigsmobile.util.JsonUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import customfonts.Button_SF_Pro_Display_Medium;
import customfonts.EditText__SF_Pro_Display_Regular;
import customfonts.MyTextView_Roboto_Regular;

import static java.lang.Boolean.TRUE;

public class SanidigsHome extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    public static PubNub pubnub; // Pubnub instance
    private Marker driverMarker; // Marker to display provider's location

    //fused locatio provider
    //get mobile current location
    FusedLocationProviderClient myFusedLocation ;
    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.

    ImageView car1,car2,car3,car4,plumb, contruction, vht, reportit, dest_trig, loca_trig;
    String originText, destinationText;

    LatLng originlatlng, destinationlatlng;

    EditText__SF_Pro_Display_Regular destinate, located;

    //the textview
    MyTextView_Roboto_Regular profileTextView, gndTextView, offersTextView,notificationsTextView, hnsTextView, logoutTextView, settingsTextView;

    Button_SF_Pro_Display_Medium confirmButton;

    //alert dialog
    AlertDialog servicesAlert;
    AlertDialog.Builder alertDialogBuilder;
    String dialogMessage = "Please choose a service";
    //flags for service before submit
    boolean isServiceValid;

    private double radius = 2000;


    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";



    private GoogleMap mMap;

    //the toolbar

    private ActionBarDrawerToggle actionBarDrawerToggle;
    //NavigationView navigationView;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    NavigationView navigationView;

    //toolbar


    //autocomplete
    private  static int AUTOCOMPLETE_REQUEST_CODE = 1; // for destination button
    private static  int AUTOCOMPLETE_REQUEST_CODE_L = 2; // for location

    //the places
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG); //all the fields of interest should be in here
    //placesclient
    PlacesClient placesClient;

    //permissions
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final int INITIAL_REQUEST=1337;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //current location of mobile
        //get mobile current location
        myFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        //the location request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests



        car1=findViewById(R.id.car1);
        car2=findViewById(R.id.car2);
        car3=findViewById(R.id.car3);
        car4=findViewById(R.id.car4);
//        car5=findViewById(R.id.car5);
        plumb = findViewById(R.id.plumb);
        contruction = findViewById(R.id.construction);
        vht = findViewById(R.id.vht);
        reportit = findViewById(R.id.reportit);
        confirmButton = findViewById(R.id.confirmbutton);

        //alert dialog buider
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(dialogMessage);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //choose a service toast
                Toast.makeText(SanidigsHome.this, "Make a choice", Toast.LENGTH_SHORT).show();
            }
        });
        servicesAlert = alertDialogBuilder.create();


        //the navigation menu
        profileTextView = findViewById(R.id.profiling);
        profileTextView.setOnClickListener(this);
        gndTextView = findViewById(R.id.gndhistory);
        gndTextView.setOnClickListener(this);
        offersTextView = findViewById(R.id.offers);
        offersTextView.setOnClickListener(this);
        notificationsTextView = findViewById(R.id.notificationssan);
        notificationsTextView.setOnClickListener(this);
        hnsTextView = findViewById(R.id.helpnsupport);
        hnsTextView.setOnClickListener(this);
        logoutTextView = findViewById(R.id.logout);
        logoutTextView.setOnClickListener(this);
        settingsTextView = findViewById(R.id.settingssan);
        settingsTextView.setOnClickListener(this);



        car1.setOnClickListener(this);
        car2.setOnClickListener(this);
        car3.setOnClickListener(this);
        car4.setOnClickListener(this);
//        car5.setOnClickListener(this);
        plumb.setOnClickListener(this);
        contruction.setOnClickListener(this);
        vht.setOnClickListener(this);
        vht.setOnClickListener(this);
        reportit.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


        //autocomplete

        //code, initialized, let's seee
        Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.UK);


        //autocomplete trigger buttons
        dest_trig = findViewById(R.id.dest_trig);
        loca_trig = findViewById(R.id.loca_trig);
        dest_trig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setCountry("UG").build(getApplicationContext());//this getapplicationcontext from then Vs this
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

        loca_trig.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setCountry("UG").build(getApplicationContext());//this getapplicationcontext from then Vs this
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_L);

            }
        });

        //movement fiels
        destinate = findViewById(R.id.txtmall);
        located = findViewById(R.id.txthome);



        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        
        
        //check app permissions
        checkPermission();
        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

        //navigation view
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        
      //the toolbar
        setToolbar();



        
    }

    //initialize pubnub
    private void initPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(true);
        pnConfiguration.setUuid("SANMOB0001"); //setting the uuid for the sanidigs mobile user
        pubnub = new PubNub(pnConfiguration);
    }

    private void setToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setTitle("");

        toolbar.findViewById(R.id.navigation_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);

                    Log.e("Click", "keryu");

                } else {
                    drawer.openDrawer(navigationView);

                    Log.e("abc","abc");

                }
            }
        });
    }

    private void checkPermission() {

        Log.v("location", "inside checkpermission()");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    3857);//code for fine location, otherwise PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,3857, library thing
        }

    }

    //on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){

            if(resultCode == RESULT_OK){

                Place place = Autocomplete.getPlaceFromIntent(data);


                Log.d("Place", ": "+ place.getName() + ", "+ place.getId()+ " "+ place.getLatLng());

                //get the latlng, add it, then drop the marker

                //desitnation
                destinationText = place.getName();

                //destionation coordinates
                destinationlatlng = place.getLatLng();

                //set the edittext to the name given
                destinate.setText(place.getName());


                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Destination"));

                //draw the direction lines
                //test if origin + destination fields have been captured
                if( destinationText != null && originText != null){

                    Log.d("location Fields", "filled");

                    //route


                    GoogleDirection.withServerKey("AIzaSyBywdOSXXwVBLdxCsItuII96fp9OHW3kko").from(originlatlng).to(destinationlatlng).transportMode(TransportMode.DRIVING)
                            .unit(Unit.METRIC).execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(@Nullable Direction direction) {

                                    Log.d(" drw", direction.getStatus());
                                    Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);

                                    ArrayList<LatLng> directionPositionLit = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),directionPositionLit,5, Color.RED);
                                    mMap.addPolyline(polylineOptions);

                                    Log.d(" Distance",leg.getDistance().getText());
                                }

                                @Override
                                public void onDirectionFailure(@NonNull Throwable t) {

                                    Log.d("Not drw", "whyer");

                                }
                            });


                    //route


                } else {

                    Log.d("location Fields", " not filled");
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("Place error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

                Log.d("Place", "cancelled");
            }


            return;
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_L) {

            //as there is need for the different edittext field
            if (resultCode == RESULT_OK){

                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.d("Place", ": "+ place.getName() + ", "+ place.getId());

                //origintext
                originText = place.getName();

                originlatlng = place.getLatLng();


                //set the edittext to the name given
                located.setText(place.getName());

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Origin"));

                //show destination and orgin

                if( destinationText != null && originText != null){

                    Log.d("location Fields", "filled");

                    //route


                    GoogleDirection.withServerKey("AIzaSyBywdOSXXwVBLdxCsItuII96fp9OHW3kko").from(originlatlng).to(destinationlatlng).transportMode(TransportMode.DRIVING)
                            .unit(Unit.METRIC).execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(@Nullable Direction direction) {

                                    Log.d(" drw", direction.getStatus());
                                    Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);

                                    ArrayList<LatLng> directionPositionLit = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),directionPositionLit,5, Color.RED);
                                    mMap.addPolyline(polylineOptions);

                                    Log.d(" Distance",leg.getDistance().getText());
                                }

                                @Override
                                public void onDirectionFailure(@NonNull Throwable t) {

                                    Log.d("Not drw", "whyer");

                                }
                            });


                    //route
                } else {

                    Log.d("location Fields", " not filled");
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("Place error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

                Log.d("Place", "cancelled");
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {



//      googleMap.setMyLocationEnabled(true);

        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        LatLng origin = new LatLng(0.3596229726043695, 32.58488451023283);
        LatLng destination = new LatLng(0.3606860294519166, 32.585671454411376);

        LatLng latLng = new LatLng(0.3601145722187156, 32.58415475415318);



        //draw route
 //       DrawRouteMaps.getInstance(this).draw(origin, destination, mMap);
//        DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.currentlocation, "Pickup");
//        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.droplocation, "Destination");

        //adding line to map
       // Polyline polyline = googleMap.addPolyline(new PolylineOptions().clickable(true).add(origin, latLng));

        //exorcist direction lib

        GoogleDirection.withServerKey("AIzaSyBywdOSXXwVBLdxCsItuII96fp9OHW3kko").from(origin).to(latLng).transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(@Nullable Direction direction) {

                        Log.d(" drw", direction.getStatus());
                        Route route = direction.getRouteList().get(0);
                        Leg leg = route.getLegList().get(0);

                        ArrayList<LatLng> directionPositionLit = leg.getDirectionPoint();
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),directionPositionLit,5, Color.RED);
                        googleMap.addPolyline(polylineOptions);


                    }

                    @Override
                    public void onDirectionFailure(@NonNull Throwable t) {

                        Log.d("Not drw", "whyer");

                    }
                });

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));




        double lat = 37.7830989;
        double lng = -122.3993836;


//        googleMap.addCircle(new CircleOptions()
//                .center(new LatLng(lat, lng))
//                .radius(radius)
//                .strokeColor(Color.BLUE)
//                .strokeWidth(0f)
//                .fillColor(Color.BLACK)
//
//                .fillColor(Color.parseColor("#26006ef1")));
//
//        CircleOptions circle=new CircleOptions();
//        circle.center(googleMap).fillColor(Color.LTGRAY).radius(1000);
//        googleMap.addCircle(circle);


        // create marker
        MarkerOptions marker = new MarkerOptions().position(latLng).title("Set Pickup Point");
//        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));
        // adding marker
        googleMap.addMarker(marker);


        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                latLng).zoom(14).build();

        //may remove this animatecamera
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //our code downward
        //adding mylocation button, check permission before adding the button

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            //according to the UI design we have, this buttion maybe covered.

        }

//pubnub listener and subscribe


        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {

                //network status flag for connection, handling disconnects, check t4ticket bit as well

            }

            @Override
            public void message(@NotNull PubNub pubnub, @NotNull PNMessageResult pnMessageResult) {
                //pnMessageResult.getPublisher();//for the publisher, see what comes


                //the location GPS lat/lng from the provider
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Map<String, String> newLocation = JsonUtil.fromJson(pnMessageResult.getMessage().toString(), LinkedHashMap.class);
                            updateUI(newLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult pnPresenceEventResult) {

            }

            @Override
            public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {

            }

            @Override
            public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {



            }

            @Override
            public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {

            }

            @Override
            public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {

            }

            @Override
            public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {

                //the message action result for uuid versus the publisher, to see
                //probably assign values then use them accordingly after
                PNMessageAction pnMessageAction = pnMessageActionResult.getMessageAction();
                String uuidIdentifier = pnMessageAction.getUuid(); //for the message
                //pnMessageAction.getValue();//could be the same message

            }

            @Override
            public void file(@NotNull PubNub pubnub, @NotNull PNFileEventResult pnFileEventResult) {

            }
        });
        //the values from the listener assigned, then manipulated there after

        //subscribe to the channel, this also where we could add another channely maybe for chat?
        pubnub.subscribe(). channels(Arrays.asList(Constants.PUBNUB_CHANNEL_NAME)).execute();


    //finding the current place
//        placesClient = Places.createClient(this);//place client instance
//
//        FindCurrentPlaceRequest requestCurrentPlace = FindCurrentPlaceRequest.newInstance(fields);
//
//        //handle the response
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//
//            Task<FindCurrentPlaceResponse> placeResponse  = placesClient.findCurrentPlace(requestCurrentPlace);
//            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
//
//                    if (task.isSuccessful()) {
//
//                        FindCurrentPlaceResponse response = task.getResult();
//                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//
//                            Log.d("Place likehood", placeLikelihood.getPlace().getName() + " " + placeLikelihood.getLikelihood());
//
//                        }
//
//                    } else {
//
//                        Exception exception = task.getException();
//                        if (exception instanceof ApiException) {
//
//                            ApiException apiException = (ApiException) exception;
//                            Log.d("Placenot ", String.valueOf(apiException.getStatusCode()));
//
//                        }
//
//                    }
//
//                }
//            });
//
//
//        } else {
//
//            checkPermission();
//        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.car1:
                car1.setImageResource(R.drawable.reservedd);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
//                car5.setImageResource(R.drawable.car3);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);


                //bool for service
                isServiceValid = true;

                break;

            case R.id.car2:
                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.reservedd);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
//                car5.setImageResource(R.drawable.car3);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;
            case R.id.car3:
                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.reservedd);
                car4.setImageResource(R.drawable.cleaning);
//                car5.setImageResource(R.drawable.car3);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;
            case R.id.car4:
                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.reservedd);
//                car5.setImageResource(R.drawable.car5);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;
            case R.id.plumb:
                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
                plumb.setImageResource(R.drawable.reservedd);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;

            case R.id.construction:

                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.reservedd);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;


            case R.id.vht:

                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.reservedd);
                reportit.setImageResource(R.drawable.reportsite);

                //bool for service
                isServiceValid = true;

                break;

            case R.id.reportit:

                car1.setImageResource(R.drawable.garbage);
                car2.setImageResource(R.drawable.waste);
                car3.setImageResource(R.drawable.fumigation);
                car4.setImageResource(R.drawable.cleaning);
                plumb.setImageResource(R.drawable.plumber);
                contruction.setImageResource(R.drawable.construction);
                vht.setImageResource(R.drawable.vht);
                reportit.setImageResource(R.drawable.reservedd);

                //bool for service
                isServiceValid = true;

                break;

            case R.id.profiling:
                Log.d("Profileing", "clicked");
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.gndhistory:

                //history actvity
                Intent intentHis = new Intent(this, Ride_History.class);
                startActivity(intentHis);

                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.offers:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.notificationssan:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.helpnsupport:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.logout:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.settingssan:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.confirmbutton:

                if(isServiceValid){

                    dialogMessage = "Oops, this supplier has not been approved yet, please try again later...";

                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage(dialogMessage);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //choose a service toast
                            Toast.makeText(SanidigsHome.this, "Stay tuned!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    servicesAlert = alertDialogBuilder.create();
                    servicesAlert.show();


                } else {

                    //show the dialog
                    servicesAlert.show();

                }



                break;
        }
    }

    //update the UI with the new location
    private void updateUI(Map<String, String> newLoc) {
        LatLng newLocation = new LatLng(Double.valueOf(newLoc.get("lat")), Double.valueOf(newLoc.get("lng")));
        if (driverMarker != null) {
            animateCar(newLocation);
            boolean contains = mMap.getProjection()
                    .getVisibleRegion()
                    .latLngBounds
                    .contains(newLocation);
            if (!contains) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            }
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    newLocation, 15.5f));
            driverMarker = mMap.addMarker(new MarkerOptions().position(newLocation).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.driving)));
        }
        getRouteToMarker(newLocation);

    }

    private void getRouteToMarker(LatLng newLocation) {

        //get mobile current location
        myFusedLocation.requestLocationUpdates(locationRequest, new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location mylocation = locationResult.getLastLocation();

                //mobile location in lat/lng
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                //drawing direction
                GoogleDirection.withServerKey(Constants.GOOGLE_API_KEY).from(newLocation).to(myLatLng).
                        transportMode(TransportMode.DRIVING).execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(@Nullable Direction direction) {

                        Log.d(" enroutedrw", direction.getStatus());
                        Route route = direction.getRouteList().get(0);
                        Leg leg = route.getLegList().get(0);

                        ArrayList<LatLng> directionPositionLit = leg.getDirectionPoint();
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),directionPositionLit,5, Color.RED);

                        //polylines just added
                       // mMap.addPolyline(polylineOptions);

                        //betterway to add the lines
                        Polyline polyline = mMap.addPolyline(polylineOptions);

                        //to remove polylines
                        //polyline.remove();


                    }

                    @Override
                    public void onDirectionFailure(@NonNull Throwable t) {

                        Log.d("Not enrout drw", "whyer");

                    }
                });

            }

        }, Looper.myLooper());

    }

    private void animateCar(LatLng newLocation) {

        //animate with the current location of the provider enroute

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = animation.getAnimatedFraction();
                    //LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);, no need for this
                    driverMarker.setPosition(newLocation);
                } catch (Exception ex) {
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(Gravity.LEFT); //OPEN Nav Drawer!
        }else {
            finish();// closes the menu activity
        }
    }
}
