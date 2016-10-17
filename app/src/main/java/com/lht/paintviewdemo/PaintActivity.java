package com.lht.paintviewdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lht.paintview.ImageUtil;
import com.lht.paintview.PaintView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {

    final static int WIDTH_WRITE = 2, WIDTH_PAINT = 40;
    final static int COLOR_RED = 0xffff4141, COLOR_BLUE = 0xff41c6ff;

    PaintView mPaintView;

    ImageButton mBtnColor, mBtnStroke, mBtnUndo;
    boolean bRedOrBlue = true, bWriteOrPaint = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mPaintView = (PaintView)findViewById(R.id.view_paint);
        mPaintView.setColor(COLOR_RED);
        mPaintView.setStrokeWidth(WIDTH_WRITE);

        byte[] bitmapByte = getIntent().getByteArrayExtra("bitmap");
        if (bitmapByte != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            mPaintView.setBitmap(bitmap);
        }

        mBtnColor = (ImageButton)findViewById(R.id.btn_color);
        mBtnColor.setOnClickListener(this);
        mBtnStroke = (ImageButton)findViewById(R.id.btn_stroke);
        mBtnStroke.setOnClickListener(this);
        mBtnUndo = (ImageButton)findViewById(R.id.btn_undo);
        mBtnUndo.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_color:
                colorChanged();
                break;
            case R.id.btn_stroke:
                strokeChanged();
                break;
            case R.id.btn_undo:
                mPaintView.undo();
                break;
        }
    }

    private void colorChanged() {
        bRedOrBlue = !bRedOrBlue;
        if (bRedOrBlue) {
            mPaintView.setColor(COLOR_RED);
            mBtnColor.setImageResource(R.drawable.ic_red);
        }
        else {
            mPaintView.setColor(COLOR_BLUE);
            mBtnColor.setImageResource(R.drawable.ic_blue);
        }
    }

    private void strokeChanged() {
        bWriteOrPaint = !bWriteOrPaint;
        if (bWriteOrPaint) {
            mPaintView.setStrokeWidth(WIDTH_WRITE);
            mBtnStroke.setImageResource(R.drawable.ic_write);
        }
        else {
            mPaintView.setStrokeWidth(WIDTH_PAINT);
            mBtnStroke.setImageResource(R.drawable.ic_paint);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareSingleImage(mPaintView.getBitmap());
                break;
        }
        return true;
    }

    final static String SHARE_IMAGE_DIR = "image";
    final static String SHARE_IMAGE_NAME = "share";
    final static String SHARE_IMAGE_EXTENSION = ".jpg";
    public void shareSingleImage(Bitmap bitmap) {

        String imageDir = Environment.getExternalStorageDirectory() + File.separator +
                getResources().getString(R.string.app_name) + File.separator +
                SHARE_IMAGE_DIR + File.separator;
        File bitmapDir = new File(imageDir);

        if (!bitmapDir.exists()) {
            bitmapDir.mkdirs();
        }

        File bitmapFile = new File (imageDir + SHARE_IMAGE_NAME + SHARE_IMAGE_EXTENSION);
        ImageUtil.saveBitmap(bitmap, bitmapFile.getAbsolutePath(), false);

        Uri imageUri = Uri.fromFile(bitmapFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(
                Intent.createChooser(shareIntent, getResources().getString(R.string.title_share)));

    }
}
