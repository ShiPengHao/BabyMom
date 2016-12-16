package com.yimeng.babymom.task;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.DepartmentActivity;

/**
 * 请求医生
 */

public class LoadDoctorTask extends BaseTask<DepartmentActivity> {

    public static final String METHOD = "Load_Doctor";
    public static final String DEPARTMENTS_ID = "departments_id";
    public static final String WEEK = "week";
    public static final int FLAG_DUTY = 1;
    public static final int FLAG_ALL = 2;

    private int flag;

    /**
     * @param activity
     * @param flag     加载科室或是加载值班医生的标志
     */
    public LoadDoctorTask(DepartmentActivity activity, int flag) {
        super(activity, null);
        this.flag = flag;
    }

    @Override
    public void parseResult(DepartmentActivity activity, String result) {
        switch (flag) {
            case FLAG_DUTY:
                activity.onDutyDoctors(result);
                break;
            case FLAG_ALL:
                activity.onAllDoctors(result);
                break;
        }
    }

    @Override
    protected void onError(DepartmentActivity activity) {
        activity.showToast(activity.getString(R.string.no_doctor));
    }
}
