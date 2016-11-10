package com.lht.paintviewdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lht.paintview.PaintView;
import com.lht.paintview.pojo.DrawShape;
import com.lht.paintviewdemo.util.ImageUtil;
import com.lht.paintviewdemo.util.KeyboardUtil;

import java.util.ArrayList;

public class PaintActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher, PaintView.OnDrawListener {

    final static int WIDTH_WRITE = 2, WIDTH_PAINT = 40;
    final static int COLOR_RED = 0xffff4141, COLOR_BLUE = 0xff41c6ff;

    PaintView mPaintView;

    View mLayoutAction, mLayoutText;
    ImageButton mBtnColor, mBtnStroke, mBtnText, mBtnUndo;
    boolean bRedOrBlue = true, bWriteOrPaint = true;

    EditText mEtText;
    ImageButton mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mPaintView = (PaintView)findViewById(R.id.view_paint);
        mPaintView.setColor(COLOR_RED);
        mPaintView.setStrokeWidth(WIDTH_WRITE);
        mPaintView.setOnDrawListener(this);

        Uri uri = getIntent().getParcelableExtra("bitmap_uri");
        Bitmap bitmap = ImageUtil.getBitmapByUri(this, uri);
        if (bitmap != null) {
            mPaintView.setBitmap(bitmap);
        }

        mLayoutAction = findViewById(R.id.layout_action);

        mBtnColor = (ImageButton)findViewById(R.id.btn_color);
        mBtnColor.setOnClickListener(this);
        mBtnStroke = (ImageButton)findViewById(R.id.btn_stroke);
        mBtnStroke.setOnClickListener(this);
        mBtnText = (ImageButton)findViewById(R.id.btn_text);
        mBtnText.setOnClickListener(this);
        mBtnUndo = (ImageButton)findViewById(R.id.btn_undo);
        mBtnUndo.setImageAlpha(0x77);
        mBtnUndo.setOnClickListener(this);

        mLayoutText = findViewById(R.id.layout_text);

        mEtText = (EditText)findViewById(R.id.et_text);
        mEtText.addTextChangedListener(this);
        mBtnSubmit = (ImageButton)findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
    }

    public static void start(Context context, Bitmap bitmap) {
        Intent intent = new Intent();
        intent.setClass(context, PaintActivity.class);
        intent.putExtra("bitmap_uri", ImageUtil.saveShareImage(context, bitmap));
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
            case R.id.btn_text:
                mPaintView.startText();
                mEtText.setText("");
                mLayoutText.setVisibility(View.VISIBLE);
                mLayoutAction.setVisibility(View.GONE);
                break;
            case R.id.btn_undo:
                mPaintView.undo();
                break;
            case R.id.btn_submit:
                mPaintView.endText();
                KeyboardUtil.hidekeyboard(mEtText);
                mLayoutAction.setVisibility(View.VISIBLE);
                mLayoutText.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPaintView.changeText(s.toString());
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
                shareSingleImage(
                        ImageUtil.saveShareImage(this, mPaintView.getBitmap()));
                break;
        }
        return true;
    }

    private void shareSingleImage(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(
                Intent.createChooser(shareIntent, getResources().getString(R.string.title_share)));

    }

    @Override
    public void afterDraw(ArrayList<DrawShape> mDrawShapes) {
        if (mDrawShapes.size() == 0) {
            mBtnUndo.setImageAlpha(0x77);
            mBtnUndo.setEnabled(false);
        }
        else {
            mBtnUndo.setImageAlpha(0xFF);
            mBtnUndo.setEnabled(true);
        }
    }
}
