package com.bustamante.tipcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;





public class Database extends SQLiteOpenHelper
{

    private static final int    DATABASE_VERSION    = 2;
    private static final String COLUMN_TIP_PERCENT  = "tip_percent";
    private static final String COLUMN_ID           = "_id";
    private static final String DATABASE_NAME       = "Bills.db";
    private static final String TABLE_TIPS          = "bills";
    private static final String COLUMN_BILL_DATE    = "bill_date";
    private static final String COLUMN_BILL_AMOUNT  = "bill_amount";



    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =
                "CREATE TABLE " + TABLE_TIPS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_BILL_DATE + " INTEGER, " + COLUMN_BILL_AMOUNT + " REAL, " + COLUMN_TIP_PERCENT + " REAL" + ");";
        sqLiteDatabase.execSQL(query);
    }

    public Tip last ( ) {
        SQLiteDatabase dataSQL = getReadableDatabase();
         Cursor cursor = dataSQL.rawQuery("SELECT * FROM " + TABLE_TIPS +
                " WHERE (" + COLUMN_ID + " = (SELECT MAX(" + COLUMN_ID + ") " +
                "FROM " + TABLE_TIPS + "))", null);
        if (cursor.getCount() <= 0)
          {
            return null;
        }
        cursor.moveToFirst();
        Tip tip = cursorToTip(cursor);
          dataSQL.close();
        return tip;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int newVersion, int oldVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPS);
        onCreate(sqLiteDatabase);
    }

    public float average ( ) {
        SQLiteDatabase dataSQL = getReadableDatabase();
          Cursor cursor = dataSQL.query(TABLE_TIPS, new String[] {"AVG(" + COLUMN_TIP_PERCENT + ")"}, null, null, null, null, null);
        cursor.moveToFirst();
        float average = cursor.getFloat(0);
          dataSQL.close();
        return average;
    }

    public void add (Tip tip) {

        SQLiteDatabase dataSQL = getWritableDatabase();
        String sqlCode = "INSERT INTO " + TABLE_TIPS + "(" + COLUMN_BILL_DATE + ", " + COLUMN_BILL_AMOUNT + ", " + COLUMN_TIP_PERCENT + ") VALUES (" + tip.getDateMillis() + ", " + tip.getBillAmount() + ", " + tip.getTipPercent() + ")";
        dataSQL.execSQL(sqlCode);
        dataSQL.close();
    }

    public List<Tip> getTips ( ) {
        List<Tip> list = new ArrayList<Tip>( );
        SQLiteDatabase dataSQL = getReadableDatabase();
        Cursor cursor = dataSQL.rawQuery("SELECT * FROM " + TABLE_TIPS + " WHERE 1", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Tip tip = cursorToTip(cursor);
            list.add(tip);
            cursor.moveToNext();
        }

        dataSQL.close();
        return list;
    }

    private Tip cursorToTip(Cursor c) {

        Tip tip = new Tip();
        tip.setId(c.getInt(0));
        tip.setDateMillis(c.getInt(1));
        tip.setBillAmount(c.getFloat(2));
        tip.setTipPercent(c.getFloat(3));
        return tip;
    }
}
