package me.myds.g2u.bookscanner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import me.myds.g2u.bookscanner.R;

public class PickPointActivity extends AppCompatActivity {

    private ImageView imgContent;
    private FloatingActionButton btnConfirm;
    private RadioGroup rbgroup;

    private int checked뀨 = -1;
    Map<Integer, Point> mapPoint = new HashMap<>();
    Map<Integer, ImageView> mapPin = new HashMap<>();
    Map<Integer, TextView> mapTxt = new HashMap<>();


    float scaleX;
    float scaleY;
    int orgW;
    int orgH;
    int actW;
    int actH;
    int actX;
    int actY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_point);

        imgContent = findViewById(R.id.imgContent);
        btnConfirm = findViewById(R.id.btnConfirm);
        rbgroup = findViewById(R.id.rbgroup);

        mapPoint.put(R.id.rbLT, new Point(100, 100));
        mapPoint.put(R.id.rbRT, new Point(300, 100));
        mapPoint.put(R.id.rbRB, new Point(300, 500));
        mapPoint.put(R.id.rbLB, new Point(100, 500));

        mapPin.put(R.id.rbLT, findViewById(R.id.ptLT));
        mapPin.put(R.id.rbRT, findViewById(R.id.ptRT));
        mapPin.put(R.id.rbRB, findViewById(R.id.ptRB));
        mapPin.put(R.id.rbLB, findViewById(R.id.ptLB));

        mapTxt.put(R.id.rbLT, findViewById(R.id.txtLT));
        mapTxt.put(R.id.rbRT, findViewById(R.id.txtRT));
        mapTxt.put(R.id.rbRB, findViewById(R.id.txtRB));
        mapTxt.put(R.id.rbLB, findViewById(R.id.txtLB));

        Uri imageURI = getIntent().getData();
        imgContent.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imgContent.getViewTreeObserver().removeOnPreDrawListener(this);

                float[] f = new float[9];
                imgContent.getImageMatrix().getValues(f);
                Drawable d = imgContent.getDrawable();

                scaleX = f[Matrix.MSCALE_X];
                scaleY = f[Matrix.MSCALE_Y];
                orgW = d.getIntrinsicWidth();
                orgH = d.getIntrinsicHeight();
                actW = Math.round(orgW * scaleX);
                actH = Math.round(orgH * scaleY);
                actX = Math.round(f[Matrix.MTRANS_X]);
                actY = Math.round(f[Matrix.MTRANS_Y]);

                for (int pinid: mapPin.keySet()) {
                    Point point = mapPoint.get(pinid);
                    mapPin.get(pinid).animate()
                            .x(actX + point.x).y(actY + point.y)
                            .setDuration(0).start();
                    mapTxt.get(pinid).setText(
                            "x: "+(int)(point.x/scaleX) +
                            "\ny: "+(int)(point.y/scaleY)
                    );
                }

                return true;
            }
        });
        imgContent.setImageURI(imageURI);

        rbgroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) ->{
            checked뀨 = checkedId;
        });

        imgContent.setOnTouchListener(onTouchListener);
    }

    Point touchStart = null;
    View.OnTouchListener onTouchListener = (View v, MotionEvent event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (checked뀨 == -1) break;
                touchStart = new Point((int)event.getRawX(), (int)event.getRawY());
            }; return true;
            case MotionEvent.ACTION_MOVE: {
                if (touchStart == null || checked뀨 == -1) break;
                Point diff = new Point(
                        (int)event.getRawX() - touchStart.x,
                        (int)event.getRawY() - touchStart.y);
                Point fromPoint = mapPoint.get(checked뀨);
                Point toPoint = new Point(fromPoint.x + diff.x, fromPoint.y + diff.y);
                mapPin.get(checked뀨).animate()
                        .x(actX + toPoint.x).y(actY + toPoint.y)
                        .setDuration(0).start();
                mapTxt.get(checked뀨).setText(
                        "x: "+(int)(toPoint.x/scaleX) +
                        "\ny: "+(int)(toPoint.y/scaleY)
                );
            }; return true;
            case MotionEvent.ACTION_UP: {
                if (touchStart == null || checked뀨 == -1) break;
                Point diff = new Point(
                        (int)event.getRawX() - touchStart.x,
                        (int)event.getRawY() - touchStart.y);
                Point curPoint = mapPoint.get(checked뀨);
                curPoint.set(curPoint.x + diff.x, curPoint.y + diff.y);
            }; return true;
        }
        return false;
    };
}
