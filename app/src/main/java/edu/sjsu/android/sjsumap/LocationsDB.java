package edu.sjsu.android.sjsumap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class LocationsDB extends SQLiteOpenHelper {
    public static final String ID_COLUMN = "_id";
    public static final String LATITUDE_COLUMN = "Latitude";
    public static final String LONGITUDE_COLUMN = "Longitude";
    public static final String ZOOM_COLUMN = "Zoom";

    public static final String DATABASE_TABLE = "Locations";
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (%s integer primary key autoincrement, %s DOUBLE, %s DOUBLE, %s INTEGER);",
            DATABASE_TABLE, ID_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN, ZOOM_COLUMN);
    private Context context;

    public LocationsDB(Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addMarker(double lat, double lng, int zoom){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LATITUDE_COLUMN, lat);
        values.put(LONGITUDE_COLUMN, lng);
        values.put(ZOOM_COLUMN, zoom);
        if(database.insert(DATABASE_TABLE, null, values) == -1){
            Toast.makeText(context, "Did not add marker", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Marker is added to the map", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + DATABASE_TABLE);
        Toast.makeText(context, "All markers have been removed", Toast.LENGTH_SHORT).show();
    }

    public Cursor getAllMarkers(){
        String query = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor c = null;
        if(database != null){
            c = database.rawQuery(query, null);
        }
        return c;
    }
}
