package com.ly.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.ly.qr.utils.MakeQRCodeUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btMainCreate = findViewById(R.id.bt_main_create);
        final ImageView ivMainShow = findViewById(R.id.iv_main_show);
        try {
            Bitmap bitmap = MakeQRCodeUtil.makeQRImage(BitmapFactory.decodeResource(getResources(), R.mipmap.test), "https://github.com/OpenFlutter/Flutter-Notebook/blob/master/mecury_project/example/redux_demo/lib/under_screen.dart", dip2px(MainActivity.this, 200), dip2px(MainActivity.this, 200));
            ivMainShow.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        btMainCreate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Bitmap bitmap = MakeQRCodeUtil.makeQRImage(BitmapFactory.decodeResource(getResources(), R.mipmap.test), "https://www.baidu.com", dip2px(MainActivity.this, 200), dip2px(MainActivity.this, 200));
                            ivMainShow.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    /**
     * 25、描述：根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
