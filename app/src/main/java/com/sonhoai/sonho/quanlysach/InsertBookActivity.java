package com.sonhoai.sonho.quanlysach;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertBookActivity extends AppCompatActivity {

    SQLiteDatabase database=null;
    List<InfoData> listBook=null;
    List<InfoData>listAuthor=null;
    InfoData authorData=null;
    MySimpleArrayAdapter adapter=null;
    int day,month,year;
    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);
        Spinner pinner=(Spinner) findViewById(R.id.spinner1);
        listAuthor=new ArrayList<InfoData>();
        InfoData d1=new InfoData();
        d1.setField1("_");
        d1.setField2("Show All");
        d1.setField3("_");
        listAuthor.add(d1);
        database=openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        if(database!=null)
        {

            Cursor cursor=database.query("tblAuthors", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(cursor.isAfterLast()==false)
            {
                InfoData d=new InfoData();
                d.setField1(cursor.getInt(0));
                d.setField2(cursor.getString(1));
                d.setField3(cursor.getString(2));
                listAuthor.add(d);
                cursor.moveToNext();
            }
            cursor.close();
        }
        adapter=new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data,listAuthor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinner.setAdapter(adapter);
        pinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(arg2==0)
                {
                    authorData=null;
                    loadAllListBook();
                }
                else
                {
                    authorData=listAuthor.get(arg2);
                    loadListBookByAuthor(authorData.getField1().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                authorData=null;
            }
        });

        setCurrentDateOnView();
        Button bChangeDate=(Button) findViewById(R.id.buttonDate);
        bChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(113);
            }
        });
        Button btnInsertBook =(Button) findViewById(R.id.buttonInsertBook);
        btnInsertBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(authorData==null)
                {
                    Toast.makeText(InsertBookActivity.this, "Please choose an author to insert", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText txtTitle=(EditText) findViewById(R.id.editTextTitle);
                ContentValues values=new ContentValues();
                values.put("title", txtTitle.getText().toString());
                Calendar c=Calendar.getInstance();
                c.set(year, month, day);
                SimpleDateFormat dfmt=new SimpleDateFormat("dd-MM-yyyy");
                values.put("dateadded",dfmt.format(c.getTime()));
                values.put("authorid", authorData.getField1().toString());
                long bId=database.insert("tblBooks", null, values);
                if(bId>0)
                {
                    Toast.makeText(InsertBookActivity.this, "Insert Book OK", Toast.LENGTH_LONG).show();
                    loadListBookByAuthor(authorData.getField1().toString());
                }
                else
                {
                    Toast.makeText(InsertBookActivity.this, "Insert Book Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void loadAllListBook()
    {
        Cursor cur=database.query("tblBooks", null, null, null, null, null, null);
        cur.moveToFirst();
        listBook=new ArrayList<InfoData>();
        while(cur.isAfterLast()==false)
        {
            InfoData d=new InfoData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();
        adapter=new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }
    public void loadListBookByAuthor(String authorid)
    {
        Cursor cur=database.query("tblBooks", null, "authorid=?", new String[]{authorid}, null, null, null);
        cur.moveToFirst();
        listBook=new ArrayList<InfoData>();
        while(cur.isAfterLast()==false)
        {
            InfoData d=new InfoData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();
        adapter=new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==113)
        {
            return new DatePickerDialog(this, dateChange, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener dateChange= new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year1, int monthOfYear,
                              int dayOfMonth) {
            year=year1;
            month=monthOfYear;
            day=dayOfMonth;
            EditText eDate=(EditText) findViewById(R.id.editTextDate);
            eDate.setText(day+"-"+(month+1)+"-"+year);
        }
    };
    public void setCurrentDateOnView()
    {
        EditText eDate=(EditText) findViewById(R.id.editTextDate);
        Calendar cal=Calendar.getInstance();
        day=cal.get(Calendar.DAY_OF_MONTH);
        month=cal.get(Calendar.MONTH);
        year=cal.get(Calendar.YEAR);
        eDate.setText(day+"-"+(month+1)+"-"+year);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_insert_book, menu);
        return true;
    }
}
