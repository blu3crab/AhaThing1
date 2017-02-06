package com.adaptivehandyapps.ahathing.ahautils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.CalendarView;

/**
 * Created by mat on 4/7/2016.
 */

public class CalendarViewScrollable extends CalendarView {

    public CalendarViewScrollable(Context context) {
        super(context);
    }

    public CalendarViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarViewScrollable(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}