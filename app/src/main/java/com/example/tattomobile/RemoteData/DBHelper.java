package com.example.tattomobile.RemoteData;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tattomobile.model.BannerModel;
import com.example.tattomobile.model.ServiceModel;
import com.example.tattomobile.model.Slot;
import com.example.tattomobile.model.TimesDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table pageviewurl " +
                        "(id integer primary key, name text)"
        );
        db.execSQL(
                "create table ServiceListTable " +
                        "(id integer primary key, service_id text,title_name text,img_url text)"
        );
        db.execSQL(
                "create table TimeListTable " +
                        "(id integer primary key, slot_time text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS ServiceListTable");
        db.execSQL("DROP TABLE IF EXISTS pageviewurl");
        db.execSQL("DROP TABLE IF EXISTS TimeListTable");
        db.execSQL(
                "create table ServiceListTable " +
                        "(id integer primary key, service_id text,title_name text,img_url text)"
        );

        db.execSQL(
                "create table pageviewurl " +
                        "(id integer primary key, name text)"
        );
        db.execSQL(
                "create table TimeListTable " +
                        "(id integer , slot_time text)"
        );
    }

    public boolean insertPagerView(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("id", id);
        db.insert("pageviewurl", null, contentValues);
        return true;
    }

    @SuppressLint("Range")
    public List<BannerModel> getAllCotacts() {
        List<BannerModel> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + "pageviewurl";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(selectQuery, null)) {

            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    try {
                        BannerModel locationServiceInfo = new BannerModel(
                                cursor.getString(1));

                        list.add(locationServiceInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insertServiceList(int id, String service_id,String Title,String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("service_id", service_id);
        contentValues.put("title_name", Title);
        contentValues.put("img_url", imageUrl);
        db.insert("ServiceListTable", null, contentValues);
        return true;
    }
    public boolean insertTimeList(int id, String slot_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("slot_time", slot_time);

        db.insert("TimeListTable", null, contentValues);
        return true;
    }
    @SuppressLint("Range")
    public List<ServiceModel> getAlServiceData() {
        List<ServiceModel> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + "ServiceListTable";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    try {
                        ServiceModel locationServiceInfo = new ServiceModel(
                                Integer.parseInt(cursor.getString(1)),
                                cursor.getString(2),
                                cursor.getString(3));

                        list.add(locationServiceInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Slot> getAllTime() {
        List<Slot> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + "TimeListTable";
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    try {
                        Slot locationServiceInfo = new Slot(cursor.getString(1),cursor.getString(2));
                        list.add(locationServiceInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
