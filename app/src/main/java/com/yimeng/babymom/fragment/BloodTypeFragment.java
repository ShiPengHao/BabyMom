package com.yimeng.babymom.fragment;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.MeasureActivity;
import com.yimeng.babymom.utils.BloodTypeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 测算血型的fragment
 */

public class BloodTypeFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, MeasureActivity.MeasureSubmitListener {

    private TextView tv;
    private RadioGroup groupDaddy;
    private RadioGroup groupMom;
    private int mDaddyCheckedId;
    private int mMomcheckedId;

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_blood_type;
    }

    @Override
    protected void initView(View view) {
        tv = (TextView) view.findViewById(R.id.tv);
        groupDaddy = (RadioGroup) view.findViewById(R.id.rg_blood_type_daddy);
        groupMom = (RadioGroup) view.findViewById(R.id.rg_blood_type_mom);
        setButtonId();
    }

    /**
     * 为RadioGroup设置id
     */
    private void setButtonId() {
        for (int i = 0; i < 4; i++) {
            groupDaddy.getChildAt(i).setId(BloodTypeUtils.BLOOD_TYPES[i]);
            groupMom.getChildAt(i).setId(BloodTypeUtils.BLOOD_TYPES[i]);
        }
        groupDaddy.check(BloodTypeUtils.BLOOD_TYPES[0]);
        groupMom.check(BloodTypeUtils.BLOOD_TYPES[0]);
    }

    @Override
    protected void setListener() {
        groupDaddy.setOnCheckedChangeListener(this);
        groupMom.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
    }

    private void getTypeResult() {
        HashMap<Integer, String> resultMap = BloodTypeUtils.calculateBloodTypeChance(
                mDaddyCheckedId, mMomcheckedId);
        StringBuilder sb = new StringBuilder("宝宝的血型概率\n");
        for (Map.Entry<Integer, String> entry : resultMap.entrySet()) {
            sb.append(BloodTypeUtils.BLOOD_TYPE_STRINGS[entry.getKey()]).append(":").append(entry.getValue()).append("\n");
        }
        tv.setText(sb.toString());
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_blood_type_daddy:
                if (checkedId != mDaddyCheckedId) {
                    mDaddyCheckedId = checkedId;
                    tv.setText("");
                }
                break;
            case R.id.rg_blood_type_mom:
                if (checkedId != mMomcheckedId) {
                    mMomcheckedId = checkedId;
                    tv.setText("");
                }
                break;
        }
    }

    @Override
    public void onSubmit() {
        getTypeResult();
    }
}
