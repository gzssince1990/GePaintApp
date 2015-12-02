package com.example.ge.gepaintapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Ge on 2015/4/11.
 */
public class ColorPickerDialog extends Dialog {

    public interface OnColorChangedListener {
        void colorChanged(String key, int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor, mDefaultColor;
    private String mKey;

    private static class ColorPickerView extends View {
        private Paint mPaint;
        private float mCurrentHue = 0;
        private int mCurrentX = 0, mCurrentY = 0;
        private int mCurrentColor, mDefaultColor;
        private final int[] mHueBarColors = new int[258];
        private int[] mMainColors = new int[65536];
        private OnColorChangedListener mListener;

        private final int DEFAULT_SIZE_X = 256;
        private final int DEFAULT_SIZE_Y = 326;
        private final int ZOOM_VALUE = 3;
        private final int CUSTOM_SIZE_X = DEFAULT_SIZE_X*ZOOM_VALUE;
        private final int CUSTOM_SIZE_Y = DEFAULT_SIZE_Y*ZOOM_VALUE;
        private final int DEFAULT_MARGIN = 10;
        private final int CUSTOM_MARGIN = DEFAULT_MARGIN*ZOOM_VALUE;

        ColorPickerView(Context c, OnColorChangedListener l, int color,
                        int defaultColor) {
            super(c);
            mListener = l;
            mDefaultColor = defaultColor;

            // Get the current hue from the current color and update the main
            // color field
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            mCurrentHue = hsv[0];
            updateMainColors();

            mCurrentColor = color;

            // Initialize the colors of the hue slider bar
            int index = 0;
            for (float i = 0; i < 256; i += 256 / 42) // Red (#f00) to pink
            // (#f0f)
            {
                mHueBarColors[index] = Color.rgb(255, 0, (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) // Pink (#f0f) to blue
            // (#00f)
            {
                mHueBarColors[index] = Color.rgb(255 - (int) i, 0, 255);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) // Blue (#00f) to light
            // blue (#0ff)
            {
                mHueBarColors[index] = Color.rgb(0, (int) i, 255);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) // Light blue (#0ff) to
            // green (#0f0)
            {
                mHueBarColors[index] = Color.rgb(0, 255, 255 - (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) // Green (#0f0) to yellow
            // (#ff0)
            {
                mHueBarColors[index] = Color.rgb((int) i, 255, 0);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) // Yellow (#ff0) to red
            // (#f00)
            {
                mHueBarColors[index] = Color.rgb(255, 255 - (int) i, 0);
                index++;
            }

            // Initializes the Paint that will draw the View
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(12);
        }

        // Get the current selected color from the hue bar
        private int getCurrentMainColor() {
            int translatedHue = 255 - (int) (mCurrentHue * 255 / 360);
            int index = 0;
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb(255, 0, (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb(255 - (int) i, 0, 255);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb(0, (int) i, 255);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb(0, 255, 255 - (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb((int) i, 255, 0);
                index++;
            }
            for (float i = 0; i < 256; i += 256 / 42) {
                if (index == translatedHue)
                    return Color.rgb(255, 255 - (int) i, 0);
                index++;
            }
            return Color.RED;
        }

        // Update the main field colors depending on the current selected hue
        private void updateMainColors() {
            int mainColor = getCurrentMainColor();
            int index = 0;
            int[] topColors = new int[256];
            for (int y = 0; y < 256; y++) {
                for (int x = 0; x < 256; x++) {
                    if (y == 0) {
                        mMainColors[index] = Color.rgb(
                                255 - (255 - Color.red(mainColor)) * x / 255,
                                255 - (255 - Color.green(mainColor)) * x / 255,
                                255 - (255 - Color.blue(mainColor)) * x / 255);
                        topColors[x] = mMainColors[index];
                    } else
                        mMainColors[index] = Color.rgb(
                                (255 - y) * Color.red(topColors[x]) / 255,
                                (255 - y) * Color.green(topColors[x]) / 255,
                                (255 - y) * Color.blue(topColors[x]) / 255);
                    index++;
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int translatedHue = 255 - (int) (mCurrentHue * 255 / 360);
            // Display all the colors of the hue bar with lines


            for (int x = 0; x < 256; x++) {
                // If this is not the current selected hue, display the actual
                // color
                if (translatedHue != x) {
                    mPaint.setColor(mHueBarColors[x]);
                    mPaint.setStrokeWidth(1);
                } else // else display a slightly larger black line
                {
                    mPaint.setColor(Color.BLACK);
                    mPaint.setStrokeWidth(3);
                }

                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(
                        x * ZOOM_VALUE + CUSTOM_MARGIN, 0,
                        (x + 1) * ZOOM_VALUE + CUSTOM_MARGIN, 40 * ZOOM_VALUE, mPaint);
            }



            // Display the main field colors using LinearGradient
            for (int x = 0; x < 256; x++) {
                int[] colors = new int[2];
                colors[0] = mMainColors[x];
                colors[1] = Color.BLACK;
                Shader shader = new LinearGradient(0, 40*ZOOM_VALUE+CUSTOM_MARGIN,
                        0, 306*ZOOM_VALUE,
                        colors, null, Shader.TileMode.REPEAT);
                mPaint.setShader(shader);
                //canvas.drawLine(x*ZOOM_VALUE + 10, 40*ZOOM_VALUE+10,
                //        x*ZOOM_VALUE + 10, 306*ZOOM_VALUE, mPaint);
                canvas.drawRect(x * ZOOM_VALUE + CUSTOM_MARGIN, 40 * ZOOM_VALUE + CUSTOM_MARGIN,
                        (x + 1) * ZOOM_VALUE + CUSTOM_MARGIN, 306 * ZOOM_VALUE, mPaint);
            }
            mPaint.setShader(null);

            // Display the circle around the currently selected color in the
            // main field
            if (mCurrentX != 0 && mCurrentY != 0) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.BLACK);
                canvas.drawCircle(mCurrentX, mCurrentY, 10, mPaint);
            }

            // Draw a 'button' with the currently selected color
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCurrentColor);
            canvas.drawRect(CUSTOM_MARGIN, 306*ZOOM_VALUE+CUSTOM_MARGIN,
                    128*ZOOM_VALUE+CUSTOM_MARGIN, (306+40)*ZOOM_VALUE + CUSTOM_MARGIN, mPaint);

            // Set the text color according to the brightness of the color
            if (Color.red(mCurrentColor) + Color.green(mCurrentColor)
                    + Color.blue(mCurrentColor) < 384)
                mPaint.setColor(Color.WHITE);
            else
                mPaint.setColor(Color.BLACK);
            canvas.drawText(
                    getResources()
                            .getString(R.string.settings_bg_color_confirm), 64*ZOOM_VALUE+CUSTOM_MARGIN,
                    (306+20)*ZOOM_VALUE+CUSTOM_MARGIN, mPaint);

            // Draw a 'button' with the default color
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mDefaultColor);
            canvas.drawRect(128*ZOOM_VALUE+CUSTOM_MARGIN, 306*ZOOM_VALUE+CUSTOM_MARGIN,
                    256*ZOOM_VALUE+CUSTOM_MARGIN, (306+40)*ZOOM_VALUE+CUSTOM_MARGIN, mPaint);

            // Set the text color according to the brightness of the color
            if (Color.red(mDefaultColor) + Color.green(mDefaultColor)
                    + Color.blue(mDefaultColor) < 384)
                mPaint.setColor(Color.WHITE);
            else
                mPaint.setColor(Color.BLACK);
            canvas.drawText(
                    getResources().getString(
                            R.string.settings_default_color_confirm), 192*ZOOM_VALUE+CUSTOM_MARGIN,
                    (306+20)*ZOOM_VALUE+CUSTOM_MARGIN, mPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //setMeasuredDimension(276, 366);
            setMeasuredDimension(CUSTOM_SIZE_X+2*CUSTOM_MARGIN,CUSTOM_SIZE_Y+4*CUSTOM_MARGIN);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN)
                return true;
            float x = event.getX();
            float y = event.getY();

            // If the touch event is located in the hue bar
            if (x > CUSTOM_MARGIN && x < 266*ZOOM_VALUE && y > 0 && y < 40*ZOOM_VALUE) {
                // Update the main field colors
                mCurrentHue = (255 - x/ZOOM_VALUE) * 360 / 255;
                updateMainColors();

                // Update the current selected color
                int transX = (mCurrentX - CUSTOM_MARGIN)/ZOOM_VALUE;
                int transY = (mCurrentY - 50*ZOOM_VALUE-CUSTOM_MARGIN)/ZOOM_VALUE;
                int index = 256 * (transY - 1) + transX;
                if (index > 0 && index < mMainColors.length)
                    mCurrentColor = mMainColors[256 * (transY - 1) + transX];

                // Force the redraw of the dialog
                invalidate();
            }

            // If the touch event is located in the main field
            if (x > CUSTOM_MARGIN && x < 266*ZOOM_VALUE
                    && y > 40*ZOOM_VALUE+CUSTOM_MARGIN && y < 306*ZOOM_VALUE) {
                mCurrentX = (int) x;
                mCurrentY = (int) y;
                int transX = (mCurrentX - CUSTOM_MARGIN)/ZOOM_VALUE;
                int transY = (mCurrentY - 50*ZOOM_VALUE-CUSTOM_MARGIN)/ZOOM_VALUE;
                int index = 256 * (transY - 1) + transX;
                if (index > 0 && index < mMainColors.length) {
                    // Update the current color
                    mCurrentColor = mMainColors[index];
                    // Force the redraw of the dialog
                    invalidate();
                }
            }

            // If the touch event is located in the left button, notify the
            // listener with the current color
            if (x > CUSTOM_MARGIN && x < 128*ZOOM_VALUE+CUSTOM_MARGIN
                    && y > 306*ZOOM_VALUE+CUSTOM_MARGIN
                    && y < (306+40)*ZOOM_VALUE+CUSTOM_MARGIN)
                mListener.colorChanged("", mCurrentColor);

            // If the touch event is located in the right button, notify the
            // listener with the default color
            if (x > 128*ZOOM_VALUE+CUSTOM_MARGIN && x < 256*ZOOM_VALUE+CUSTOM_MARGIN
                    && y > 306*ZOOM_VALUE+ CUSTOM_MARGIN
                    && y < (306+40)*ZOOM_VALUE+CUSTOM_MARGIN)
                mListener.colorChanged("", mDefaultColor);

            return true;
        }
    }

    public ColorPickerDialog(Context context, OnColorChangedListener listener,
                             String key, int initialColor, int defaultColor) {
        super(context);

        mListener = listener;
        mKey = key;
        mInitialColor = initialColor;
        mDefaultColor = defaultColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(String key, int color) {
                mListener.colorChanged(mKey, color);
                dismiss();
            }
        };

        setContentView(new ColorPickerView(getContext(), l, mInitialColor,
                mDefaultColor));
        setTitle(R.string.settings_bg_color_dialog);

    }
}