package edu.sjsu.android.sjsumap;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    private GoogleMap map;
    private ArrayList<Marker> markers;
    private float zoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markers = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10);
        map.animateCamera(update);//by default map loads in the city view
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                markers.add(marker);
                ContentValues values = new ContentValues();
                values.put(LocationsDB.LATITUDE_COLUMN, latLng.latitude);
                values.put(LocationsDB.LONGITUDE_COLUMN, latLng.longitude);
                values.put(LocationsDB.ZOOM_COLUMN, zoom);
                LocationInsertTask task = new LocationInsertTask();
                task.doInBackground(values);
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                for(Marker m : markers){
                    m.remove();
                }
                LocationDeleteTask task = new LocationDeleteTask();
                task.doInBackground(null);
            }
        });
    }

    public void onClickCS(View v){
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        zoom = 18;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, zoom);
        map.animateCamera(update);
    }

    public void onClickUniv(View v){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        zoom = 14;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, zoom);
        map.animateCamera(update);
    }

    public void onClickCity(View v){
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        zoom = 10;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, zoom);
        map.animateCamera(update);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> c = null;
        //Uri to the content provider LocationsContentProvider
        //Fetches all the rows from locations table
        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        int localZoom = 10;
        if(c != null) {
            do {
                localZoom = c.getInt(3);
                LatLng latLng = new LatLng(c.getDouble(1),c.getDouble(2));
                Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                markers.add(marker);
            } while(c.moveToNext());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, localZoom);
            map.animateCamera(update);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            LocationsDB db = new LocationsDB(MainActivity.this);
            db.addMarker(contentValues[0].getAsDouble(LocationsDB.LATITUDE_COLUMN),
                    contentValues[0].getAsDouble(LocationsDB.LONGITUDE_COLUMN),
                    contentValues[0].getAsInteger(LocationsDB.ZOOM_COLUMN));
            return null;
        }
    }
    private class LocationDeleteTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues){
            LocationsDB db = new LocationsDB(MainActivity.this);
            db.deleteAll();
            return null;
        }
    }
}