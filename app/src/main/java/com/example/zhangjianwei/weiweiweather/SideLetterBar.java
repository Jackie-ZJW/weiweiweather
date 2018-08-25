package com.example.zhangjianwei.weiweiweather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideLetterBar extends View {

    private static final String[] INITIAL = {"定位", "热门", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private Paint paint = new Paint();
    private OnLetterChangedListener onLetterChangedListener;
    private TextView overlay;

    public SideLetterBar(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        int height = getHeight();
        int width = getWidth();

        int singleHeight = height / INITIAL.length;

        for (int i = 0; i < INITIAL.length; i++) {
            paint.setTextSize(getResources().getDimension(R.dimen.text_size_small));
            paint.setColor(getResources().getColor(R.color.colorAccent));
            paint.setAntiAlias(true);
            float xPos = width / 2 - paint.measureText(INITIAL[i]) / 2;
            float yPos = singleHeight * (i + 1);
            canvas.drawText(INITIAL[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int c = (int) (event.getY() / getHeight() * INITIAL.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (onLetterChangedListener != null) {
                    if (c >= 0 && c < INITIAL.length) {
                        invalidate();
                        if (overlay != null) {
                            overlay.setVisibility(VISIBLE);
                            overlay.setText(INITIAL[c]);
                            onLetterChangedListener.onLetterChanged(INITIAL[c]);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (overlay != null) {
                    overlay.setVisibility(GONE);
                }
                break;
            default:
                break;
        }

        return true;
    }

    public void setOverlay(TextView overlay) {
        this.overlay = overlay;
    }

    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface OnLetterChangedListener {
        void onLetterChanged(String letter);
    }
}
