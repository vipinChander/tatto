package com.example.tattomobile.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class PoppinBoldTextView extends AppCompatTextView {

    public PoppinBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PoppinBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PoppinBoldTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        setTypeface(tf ,0);
    }
}

