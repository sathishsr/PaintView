package com.lht.paintviewdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lht.paintview.PaintView;

import java.io.ByteArrayOutputStream;

public class PaintActivity extends AppCompatActivity {

    PaintView mPaintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mPaintView = (PaintView)findViewById(R.id.view_paint);

        byte[] bitmapByte = getIntent().getByteArrayExtra("bitmap");
        if (bitmapByte != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            mPaintView.setBitmap(bitmap);
        }
    }

    public static void start(Context context, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);//压缩位图
        byte[] bitmapByte = baos.toByteArray();//创建分配字节数组

        Intent intent = new Intent();
        intent.setClass(context, PaintActivity.class);
        intent.putExtra("bitmap", bitmapByte);
        context.startActivity(intent);
    }
}
