package com.example.rih.safiforstudent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    private static GoogleMap mMap;
    private EditText search,placename,description,datetime;
    private ImageView search_btn,placeImage;
    private Button addInteressingplace,cancel;
    private RelativeLayout addInteressingPlaceLayout;
    private RatingBar rating;
    private Spinner choice;
    private FirebaseDatabase database;
    private DatabaseReference databaseref;
    private static Thread t,tt;
    private NotificationManagerCompat notificationmanager;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static float  lastratimg;
    private RelativeLayout addRating;
    private Button save,cancell;
    private RatingBar newrating;
    private TextView labelname;
    private TextView labeldesc;
    private TextView labelrating;
    private static Bitmap photo;

    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        InitVariables();
        t = new Thread(rn);
        t.start();
        tt = new Thread(rnn);
        tt.start();

          search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = search.getText().toString();
                Geocoder geocoder = new Geocoder(Navigation.this);
                List<Address> list = new ArrayList<>();
                try
                {
                    list = geocoder.getFromLocationName(searchString,1);
                }catch (IOException e)
                {
                    Log.e("exception locate",e.getMessage());
                    Toast.makeText(getApplicationContext(),"Verify your connection",Toast.LENGTH_SHORT).show();
                }
                if (list.size()>0)
                {
                    Address address = list.get(0);
                    // Add a marker in Sydney and move the camera32.245229

                    LatLng searching = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(searching).title(searchString));
                    Toast.makeText(getApplicationContext(),address.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });


        placeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;
        LatLng safi = new LatLng(32.2930725,-9.2417123);
        mMap.addMarker(new MarkerOptions().position(safi).title("Safi"));
        float zoomLevel = 13.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(safi, zoomLevel));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                final String[] liste = title.split(" ");
                try
                {
                    lastratimg = Float.valueOf(liste[4]);
                }catch(Exception ex)
                {
                    try {
                        lastratimg = Integer.parseInt(liste[5]);
                    }catch(Exception exp)
                    {
                       // Toast.makeText(getApplicationContext(),"Verify your connexion",Toast.LENGTH_SHORT).show();
                    }
                }
                final LatLng position = marker.getPosition();
                FirebaseDatabase databas;
                final DatabaseReference databasereff;
                databas = FirebaseDatabase.getInstance();
                databasereff = databas.getReference();

                databasereff.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot child : dataSnapshot.getChildren()) {
                         for (DataSnapshot child_ : child.child("Places").getChildren()) {


                                 if(String.valueOf(child_.child("longtitude").getValue()).equals(String.valueOf(position.longitude))&& String.valueOf(child_.child("latitude").getValue()).equals(String.valueOf(position.latitude)))
                             {

                                 addRating.setVisibility(View.VISIBLE);
                                 final String[] path = String.valueOf(child_.getRef()).split("/");
                                // Toast.makeText(getApplicationContext(),String.valueOf(child_.getRef()),Toast.LENGTH_SHORT).show();
                                 try{

                                     labelname.setText("place's name: "+liste[1]);
                                     labeldesc.setText(liste[7]);
                                     labelrating.setText("rating: "+liste[5]);
                                 }
                                 catch (Exception exxx)
                                 {}





                                 FirebaseStorage storage = FirebaseStorage.getInstance();
                                 StorageReference storageReference = storage.getReferenceFromUrl("gs://safi-for-student.appspot.com").child(path[3]).child(path[5]).child("image.png");

                                 final ImageView photo = (ImageView)findViewById(R.id.imagrfromfirebase);
                                 final long ONE_MEGABYTE = 1024 * 1024;
                                 storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                     @Override
                                     public void onSuccess(byte[] bytes) {
                                         Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                         photo.setImageBitmap(bitmap);
                                     }
                                 });


                                 save.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         float new_rating = newrating.getRating();
                                         float last_rating = lastratimg;
                                         databaseref.child("Places").child(path[5]).child("rating").setValue(String.valueOf((new_rating+last_rating)/2));

                                         finish();
                                         Intent i = new Intent(Navigation.this,Navigation.class);
                                         startActivity(i);



                                     }
                                 });
                                 cancell.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         addRating.setVisibility(View.INVISIBLE);
                                     }
                                 });

                             }
                         }
                     }
                 }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //addRating.setVisibility(View.VISIBLE);
                 return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                addInteressingPlaceLayout.setVisibility(View.VISIBLE);
                String date = DateFormat.getDateTimeInstance().format(new Date());
                StringBuilder sb = new StringBuilder(date);
                sb.deleteCharAt(7);
                datetime.setText(sb);
                addInteressingplace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String dateafterreplace = datetime.getText().toString().replace(" ","-").replace(':','-');

                        databaseref.child("Places").child(dateafterreplace).child("latitude").setValue(latLng.latitude);
                        databaseref.child("Places").child(dateafterreplace).child("longtitude").setValue(latLng.longitude);
                        databaseref.child("Places").child(dateafterreplace).child("place name").setValue(placename.getText().toString());
                        databaseref.child("Places").child(dateafterreplace).child("categorie").setValue(choice.getSelectedItem().toString());
                        databaseref.child("Places").child(dateafterreplace).child("description").setValue(description.getText().toString());
                        databaseref.child("Places").child(dateafterreplace).child("rating").setValue(rating.getRating());
                        databaseref.child("isAdded").setValue("true");
                        storage = FirebaseStorage.getInstance();
                        storageReference = storage.getReference();
                        StorageReference ref = storageReference.child(Authentification.account.getGivenName()+"-"+Authentification.account.getFamilyName()).child(dateafterreplace).child("image.png");

                        try {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            photo.recycle();
                            ref.putBytes(byteArray);
                        }catch(Exception ex)
                        {
                            Toast.makeText(getApplicationContext(),"Please select a new photo",Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        Intent i = new Intent(Navigation.this,Navigation.class);
                        startActivity(i);
                        addInteressingPlaceLayout.setVisibility(View.INVISIBLE);
                    }
                });
               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       addInteressingPlaceLayout.setVisibility(View.INVISIBLE);
                   }
               });
            }
        });
    }
    public void InitVariables()
    {

        search = (EditText)findViewById(R.id.search);
        search_btn = (ImageView)findViewById(R.id.search_btn);
        addInteressingPlaceLayout = (RelativeLayout)findViewById(R.id.addInteressingPlace);
        addInteressingplace = (Button)findViewById(R.id.add);
        placename = (EditText)findViewById(R.id.placename);
        description = (EditText)findViewById(R.id.description);
        datetime = (EditText)findViewById(R.id.date);
        placeImage =(ImageView)findViewById(R.id.placeImage);
        rating = (RatingBar)findViewById(R.id.ratingBar);
        addInteressingPlaceLayout.setVisibility(View.INVISIBLE);
        database = FirebaseDatabase.getInstance();
        databaseref = database.getReference(Authentification.account.getGivenName()+"-"+Authentification.account.getFamilyName());
        cancel = (Button)findViewById(R.id.cancel);
        notificationmanager = NotificationManagerCompat.from(this);

        addRating = (RelativeLayout)findViewById(R.id.AddRating);
        save = (Button)findViewById(R.id.Save);
        cancell=(Button)findViewById(R.id.Cancel);
        newrating = (RatingBar)findViewById(R.id.SumratingBar);
        labelname = (TextView)findViewById(R.id.labelplacenm);
        labeldesc = (TextView)findViewById(R.id.labeldesc);
        labelrating = (TextView)findViewById(R.id.labelrat);
        //charging the spinner
        String[] arraySpinner = new String[] {
                "Select a categorie","Coffee", "Pharmacist", "Mini Market", "School", "Games room", "House for rent", "Hammam"
        };
        choice = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choice.setAdapter(adapter);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();



                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");

            String dateafterreplace = datetime.getText().toString().replace(" ","-").replace(':','-');
            File f = new File(Environment.getExternalStorageDirectory()+"/"+File.separator+Authentification.account.getGivenName()+"-"+Authentification.account.getFamilyName()+"/"+dateafterreplace);
            f.mkdir();
            File file = new File(Environment.getExternalStorageDirectory()+"/"+File.separator+Authentification.account.getGivenName()+"-"+Authentification.account.getFamilyName()+"/"+dateafterreplace+"/","image.png");
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            placeImage.setImageBitmap(photo);
        }
    }


Runnable rn = new Runnable() {
    @Override
    public void run() {
            FirebaseDatabase databas;
            DatabaseReference databasere;
            databas = FirebaseDatabase.getInstance();
            databasere = databas.getReference();

            databasere.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        for (DataSnapshot child_ : child.child("Places").getChildren())
                        {
                            try {

                                if (child_.child("categorie").getValue().toString().equals("Coffee")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.coffee);
                                    LatLng coffe = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(coffe).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("School")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.school);
                                    LatLng school = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(school).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("Pharmacist")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pharmacist);
                                    LatLng pharmacist = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(pharmacist).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + (String) String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("Mini Market")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.shop);
                                    LatLng market = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(market).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("Games room")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.games);
                                    LatLng gamesroom = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(gamesroom).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("House for rent")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.house);
                                    LatLng houseforrent = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(houseforrent).icon(icon).title((String) (child_.child("place name").getValue()) + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }

                                if (child_.child("categorie").getValue().toString().equals("Hammam")) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.hammam);
                                    LatLng hammam = new LatLng((Double) child_.child("latitude").getValue(), (Double) child_.child("longtitude").getValue());
                                    mMap.addMarker(new MarkerOptions().position(hammam).icon(icon).title((String) child_.child("place name").getValue() + " | Rating : " + String.valueOf(child_.child("rating").getValue()) + " | Description:" + String.valueOf(child_.child("description").getValue())));
                                }
                            }catch(Exception ex)
                            {

                            }

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    };




    public void sendNotif(String AccountName)
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notif = new NotificationCompat.Builder(this,App.CHANNEL).setSmallIcon(R.drawable.notificon)
                .setContentTitle(AccountName)
                .setContentText("A new place added")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(alarmSound)
                .setColor(Color.argb(235,255,255,255))
                .build();

        notificationmanager.notify(1,notif);
    }



    Runnable rnn = new Runnable() {

        @Override
        public void run() {
            FirebaseDatabase databas;
            final DatabaseReference databasere;
            databas = FirebaseDatabase.getInstance();
            databasere = databas.getReference();
            databasere.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   for(DataSnapshot child : dataSnapshot.getChildren())
                   {
                       try {
                           if (child.child("isAdded").getValue().equals("true")) {
                               if(child.getKey().equals(Authentification.account.getGivenName()+"-"+Authentification.account.getFamilyName()));
                               else
                                    sendNotif(child.getKey());
                               databaseref.child("isAdded").setValue("false");
                           }
                       }catch (Exception c)
                       {
                           //Toast.makeText(getApplicationContext(),"nnnn",Toast.LENGTH_SHORT).show();
                       }
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           }) ;
        }
    };





}
