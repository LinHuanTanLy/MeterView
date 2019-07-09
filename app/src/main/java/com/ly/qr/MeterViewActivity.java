package com.ly.qr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ly.qr.meter.MeterView;

import java.util.ArrayList;
import java.util.List;

public class MeterViewActivity extends AppCompatActivity {
    private MeterView mMvControllerTarget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);
        mMvControllerTarget = findViewById(R.id.mv_controller_target);
//        final LinearLayout constraintLayout = findViewById(R.id.action_container);
//        final MeterView meterView = findViewById(R.id.tv_plan_meter);
//        constraintLayout.setScaleX(0.95f);
//        constraintLayout.setScaleY(0.95f);

//        meterView.setScaleX(constraintLayout.getScaleX());
//        meterView.setScaleY(constraintLayout.getScaleY());
//        meterView.setScaleY(1);
//        meterView.setScaleX(1);


//        constraintLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                meterView.setScaleX(constraintLayout.getScaleX());
//                meterView.setScaleY(constraintLayout.getScaleY());
//                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) meterView.getLayoutParams();
//                layoutParams.width= (int) (constraintLayout.getWidth()*0.55);
//                layoutParams.height= (int) (constraintLayout.getWidth()*0.55);
//                meterView.setLayoutParams(layoutParams);
//                return true;
//            }
//        });


        initController();
    }

    private void initController() {
        final CheckBox cbController1 = findViewById(R.id.cb_controller_1);
        final CheckBox cbController2 = findViewById(R.id.cb_controller_2);
        final CheckBox cbController3 = findViewById(R.id.cb_controller_3);
        final CheckBox cbController4 = findViewById(R.id.cb_controller_4);
        final CheckBox cbController5 = findViewById(R.id.cb_controller_5);
        final CheckBox cbController6 = findViewById(R.id.cb_controller_6);
        final CheckBox cbController7 = findViewById(R.id.cb_controller_7);
        final CheckBox cbController8 = findViewById(R.id.cb_controller_8);
        final EditText etControllerInputClock = findViewById(R.id.et_controller_clock);
        final EditText etControllerInputNext = findViewById(R.id.et_controller_next);
        findViewById(R.id.bt_controller_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> listForDataSource = new ArrayList<>();
                if (cbController1.isChecked()) {
                    listForDataSource.add(1);
                }
                if (cbController2.isChecked()) {
                    listForDataSource.add(2);
                }
                if (cbController3.isChecked()) {
                    listForDataSource.add(3);
                }
                if (cbController4.isChecked()) {
                    listForDataSource.add(4);
                }
                if (cbController5.isChecked()) {
                    listForDataSource.add(5);
                }
                if (cbController6.isChecked()) {
                    listForDataSource.add(6);
                }
                if (cbController7.isChecked()) {
                    listForDataSource.add(7);
                }
                if (cbController8.isChecked()) {
                    listForDataSource.add(8);
                }
                mMvControllerTarget.updatePunchList(listForDataSource);
            }
        });
        findViewById(R.id.bt_controller_clock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempStr = etControllerInputClock.getText().toString();
                if (!TextUtils.isEmpty(tempStr)) {
                    int target = Integer.valueOf(tempStr);
                    mMvControllerTarget.updateSign(target);
                }
            }
        });
        findViewById(R.id.bt_controller_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempStr = etControllerInputNext.getText().toString();
                if (!TextUtils.isEmpty(tempStr)) {
                    int next = Integer.valueOf(tempStr);
                    mMvControllerTarget.updateTargetIndex(next);
                }
            }
        });
    }

}
