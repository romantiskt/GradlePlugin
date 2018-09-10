package com.rolan.gradleplugin;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;

/**
 * Created by wangyang on 2018/9/6.上午11:18
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"hello test",Toast.LENGTH_SHORT).show();
            }
        });
        test();
    }

    private void test() {
        boolean b = AndPermission.hasPermission(this, Manifest.permission.CAMERA);
        Log.d("wang","test is has permission:"+b);
    }

    public void getMsg(){


    }
}
