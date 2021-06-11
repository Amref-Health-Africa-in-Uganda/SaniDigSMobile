package com.tactical.sanidigsmobile;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.RidehistoryAdapter;
import model.RidehistoryModel;

public class Ride_History extends AppCompatActivity {

    private RidehistoryAdapter ridehistoryAdapter;
    private RecyclerView recyclerview;
    private ArrayList<RidehistoryModel> ridehistoryModelArrayList;
    private Toolbar toolbar;


    Integer i1[]={R.drawable.pin_black,R.drawable.pin_black,R.drawable.pin_black,R.drawable.pin_black,R.drawable.pin_black};
    Integer i2[]={R.drawable.rect_dotted,R.drawable.rect_dotted,R.drawable.rect_dotted,R.drawable.rect_dotted,R.drawable.rect_dotted};
    Integer i3[]={R.drawable.navigatiob_blue,R.drawable.navigatiob_blue,R.drawable.navigatiob_blue,R.drawable.navigatiob_blue,R.drawable.navigatiob_blue};
    String txtmall[]={"Kisalosalo Gulpers","Liquid soap Delivery","Garbage collection","Crafts Delivery","Water Kit Delivery"};
    String txthome[]={"Home","Home","Home","Home","Home"};
    String txtdate[]={"01 May 2019","02 May 2019","04 May 2018","07 May 2018","09 May 2018"};
    String txtprice[]={"UGX 10,000","UGX 20,000","UGX 15,000","UGX 10,000","UGX 25,000"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride__history);

        recyclerview=findViewById(R.id.recycler1);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Ride_History.this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        ridehistoryModelArrayList = new ArrayList<>();

        for (int i=0;i<i1.length;i++){

            RidehistoryModel listModel = new RidehistoryModel(i1[i],i2[i],i3[i],txtmall[i],txthome[i],txtdate[i],txtprice[i]);

            ridehistoryModelArrayList.add(listModel);

        }
        ridehistoryAdapter = new RidehistoryAdapter(Ride_History.this,ridehistoryModelArrayList);
        recyclerview.setAdapter(ridehistoryAdapter);

        //set toolbar
        setToolbar();

    }

    private void  setToolbar(){

        //the rating toolbar as identified in the layout xml
        toolbar = (Toolbar) findViewById(R.id.toolbar_ride_his);

        //set supportactionbar
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setTitle("");

        //the real action button of backward
        toolbar.findViewById(R.id.backward_history).setOnClickListener(new View.OnClickListener() {
                                                                          @Override
                                                                          public void onClick(View v) {

                                                                              finish();// closes the menu activity

                                                                          }
                                                                      }

        );
    }
}

