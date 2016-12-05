package com.example.susong.testmvp.business.login.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.util.SqliteOpenHelper;
import com.example.susong.testmvp.database.Repository;
import com.example.susong.testmvp.database.Specification;
import com.example.susong.testmvp.entity.po.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<User> {
    @Override
    public void insert(User item) {
        SqliteOpenHelper openHelper = new SqliteOpenHelper(A.instance.getApplicationContext());
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getUsername());
        values.put("pwd", item.getPwd());
        database.insert("user", null, values);
        database.close();
    }

    @Override
    public void update(User item) {
    }

    @Override
    public void remove(User item) {
    }

    @Override
    public void remove(Specification specification) {
    }

    @Override
    public List<User> query(Specification specification) {
        SqliteOpenHelper openHelper = new SqliteOpenHelper(A.instance.getApplicationContext());
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(specification.toSqlQuery(), null, null);
        List<User> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            User user = new User();
            user.setUsername(name);
            user.setPwd(pwd);
            users.add(user);
        }
        cursor.close();
        return users;
    }
}
