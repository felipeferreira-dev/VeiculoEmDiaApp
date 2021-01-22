package br.com.dev.felipeferreira.veiculoemdia.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import br.com.dev.felipeferreira.veiculoemdia.model.Registros;

public class SqlHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "veiculo_em_dia.db";
    private final static int DB_VERSION = 1;
    private static SqlHelper INSTANCE;

    //Padrão SINGLETON = Um único objeto existente em toda a execução do aplicativo
    public static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new SqlHelper(context);
        return INSTANCE;
    }


    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //CREATE
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE veiculo (id INTEGER PRIMARY KEY, type TEXT, name VARCHAR(20), placa VARCHAR(7))");
    }

    //UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //SELECT lista completa
    public List<Registros> getRegistros(String type) {
        List<Registros> registros = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM veiculo WHERE type = ?", new String[]{type});

        try {

            if(cursor.moveToFirst()){
                do {

                   Registros registrosItem = new Registros();

                   registrosItem.id = cursor.getInt(cursor.getColumnIndex("id"));
                   registrosItem.type = cursor.getString(cursor.getColumnIndex("type"));
                   registrosItem.name = cursor.getString(cursor.getColumnIndex("name"));
                   registrosItem.placa = cursor.getString(cursor.getColumnIndex("placa"));

                   registros.add(registrosItem);

                } while (cursor.moveToNext());

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.e("SQLSelect", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registros;
    }

    //Select item específico
    List<Registros> getRegistroUnico(int id, String type) {
        List<Registros> registros = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM veiculo WHERE type = ? and id = ?", new String[]{type, String.valueOf(id)});

        try {

            if(cursor.moveToFirst()){
                do {

                    Registros registrosItem = new Registros();

                    registrosItem.id = cursor.getInt(cursor.getColumnIndex("id"));
                    registrosItem.type = cursor.getString(cursor.getColumnIndex("type"));
                    registrosItem.name = cursor.getString(cursor.getColumnIndex("name"));
                    registrosItem.placa = cursor.getString(cursor.getColumnIndex("placa"));

                    registros.add(registrosItem);

                } while (cursor.moveToNext());

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.e("SQLSelect", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registros;
    }

    //INSERT
    public long addItem(String type, String name, String placa) {
        SQLiteDatabase db = getWritableDatabase();

        long veiculoId = 0;

        try {
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put("type", type);
            contentValues.put("name", name);
            contentValues.put("placa", placa);

            veiculoId = db.insertOrThrow("veiculo", null, contentValues);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLInsert", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return veiculoId;
    }

    //Delete
    public long removeItem(int id, String type) {
        SQLiteDatabase db = getWritableDatabase();
        long removeId = 0;

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("type", type);

            removeId = db.delete("veiculo", "id = ? and type = ?", new String[]{String.valueOf(id), type});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLRemove", e.getMessage(), e);
        } finally {
            if(db.isOpen())
                db.endTransaction();
        }
        return removeId;
    }

    //Update
    public long updateItem(int id, String type, String name, String placa) {
        SQLiteDatabase db = getWritableDatabase();
        long updateId = 0;

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("type", type);
            values.put("name", name);
            values.put("placa", placa);

            updateId = db.update("veiculo", values, "id = ? and type = ?",
                    new String[]{String.valueOf(id), type});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLUpdate", e.getMessage(), e);
        } finally {
            if(db.isOpen())
                db.endTransaction();
        }

        return updateId;
    }

    public Cursor displayLastCustomer(int id, String type) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM veiculo WHERE type = ? and id = ?", new String[]{type, String.valueOf(id)});

        if (mCursor != null) {
            mCursor.moveToLast();
        }
        return mCursor;
    }
}
