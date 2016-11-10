package com.lht.paintviewdemo.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by lht on 16/11/10.
 */

public class KeyboardUtil {
    public static void hidekeyboard(View view) {
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
}
