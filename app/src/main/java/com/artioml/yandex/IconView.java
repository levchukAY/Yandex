package com.artioml.yandex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IconView extends RelativeLayout {

    TextView titleTextView;
    ImageView iconImageView;

    public IconView(Context context) {
        super(context);
        init();
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.desktop_item, this);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
    }
}

/*public class IconView extends View {

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.desktop_item, null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}*/
