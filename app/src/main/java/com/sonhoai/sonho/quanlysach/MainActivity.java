package com.sonhoai.sonho.quanlysach;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnInsertAuthor=null;
    Button btnShowAuthorList=null;
    Button btnInsertBook=null;
    public static final int OPEN_AUTHOR_DIALOG=1;
    SQLiteDatabase database=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnInsertAuthor=(Button) findViewById(R.id.btnInsertAuthor);
        btnInsertAuthor.setOnClickListener(new MyEvent());
        btnShowAuthorList=(Button) findViewById(R.id.buttonShowAuthorList);
        btnShowAuthorList.setOnClickListener(new MyEvent());
        btnInsertBook=(Button) findViewById(R.id.buttonInsertBook);
        btnInsertBook.setOnClickListener(new MyEvent());
        getDatabase();
    }
    public boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    @SuppressLint("WrongConstant")
    public SQLiteDatabase getDatabase()
    {
        try
        {
            database=openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            if(database!=null)
            {
                if(isTableExists(database,"tblAuthors"))
                    return database;
                database.setLocale(Locale.getDefault());
                database.setVersion(1);
                String sqlAuthor="create table tblAuthors ("
                        +"id integer primary key autoincrement,"
                        +"firstname text, "
                        +"lastname text)";
                database.execSQL(sqlAuthor);
                String sqlBook="create table tblBooks ("
                        +"id integer primary key autoincrement,"
                        +"title text, "
                        +"dateadded date,"
                        +"authorid integer not null constraint authorid references tblAuthors(id) on delete cascade)";
                database.execSQL(sqlBook);
                String sqlTrigger="create trigger fk_insert_book before insert on tblBooks "
                        +" for each row "
                        +" begin "
                        +" 	select raise(rollback,'them du lieu tren bang tblBooks bi sai') "
                        +" 	where (select id from tblAuthors where id=new.authorid) is null ;"
                        +" end;";
                database.execSQL(sqlTrigger);
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return database;
    }
    public void showInsertAuthorDialog()
    {
        Intent intent=new Intent(MainActivity.this, CreateAuthorActivity.class);
        startActivityForResult(intent, OPEN_AUTHOR_DIALOG);
    }
    public void showAuthorList1()
    {
        Intent intent=new Intent(MainActivity.this, ShowListAuthorActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Bundle bundle= data.getBundleExtra("DATA_AUTHOR");
            String firstname=bundle.getString("firstname");
            String lastname=bundle.getString("lastname");
            ContentValues content=new ContentValues();
            content.put("firstname", firstname);
            content.put("lastname", lastname);
            if(database!=null)
            {
                long authorid=database.insert("tblAuthors", null, content);
                if(authorid==-1)
                {
                    Toast.makeText(MainActivity.this,authorid+" - "+ firstname +" - "+lastname +" ==> insert error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, authorid+" - "+firstname +" - "+lastname +" ==>insert OK!", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    private class MyEvent implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btnInsertAuthor)
            {
                showInsertAuthorDialog();
            }
            else if(v.getId()==R.id.buttonShowAuthorList)
            {
                showAuthorList1();
            }

            else if(v.getId()==R.id.buttonInsertBook)
            {
                Intent intent=new Intent(MainActivity.this, InsertBookActivity.class);
                startActivity(intent);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_simple_database_main, menu);
        return true;
    }
}
