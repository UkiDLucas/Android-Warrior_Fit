package com.cyberwalkabout.cyberfit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.R;

/**
 * @author Maria Dzyokh
 */
public class TabView extends FrameLayout {

    private TextView title;
    private ImageView icon;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tab_view, this);

        title = (TextView) findViewById(R.id.tab_title);
        icon = (ImageView) findViewById(R.id.tab_icon);

        TypedArray customAttributes = context.obtainStyledAttributes(attrs, R.styleable.TabView);

        int titleResId = customAttributes.getResourceId(R.styleable.TabView_tab_title, 0);
        if (titleResId > 0) {
            title.setText(context.getString(titleResId));
        }
        int iconResId = customAttributes.getResourceId(R.styleable.TabView_tab_icon, 0);
        if (iconResId > 0) {
            icon.setImageResource(iconResId);
        }
        int backgroundResId = customAttributes.getResourceId(R.styleable.TabView_tab_background, 0);
        if (backgroundResId > 0) {
            getRootView().setBackgroundResource(backgroundResId);
        }
    }

    public String getTitle() {
        return title.getText().toString();
    }

}
