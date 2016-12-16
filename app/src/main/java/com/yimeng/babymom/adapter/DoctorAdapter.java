package com.yimeng.babymom.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.BaseActivity;
import com.yimeng.babymom.bean.DoctorBean;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 医生头像适配器
 */
public class DoctorAdapter extends BaseAdapter {
    private ArrayList<DoctorBean> data;
    private final Context context = MyApp.getAppContext();
    private HashMap<String, Object> values = new HashMap<>();

    public DoctorAdapter(ArrayList<DoctorBean> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.item_doctor);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DoctorBean doctorBean = data.get(position);
        requestCommentScore(doctorBean.doctor_id, holder.rating_bar);
        String doctorTitle = context.getString(R.string.general_doctor);
        switch (doctorBean.doctor_title) {
            case 0:
                doctorTitle = context.getString(R.string.village_doctor);
                break;
            case 1:
                doctorTitle = context.getString(R.string.general_doctor);
                break;
            case 2:
                doctorTitle = context.getString(R.string.chief_doctor);
                break;
            case 3:
                doctorTitle = context.getString(R.string.vice_director_doctor);
                break;
            case 4:
                doctorTitle = context.getString(R.string.director_doctor);
                break;
        }
        holder.item_name.setText(String.format("%s(%s)", doctorBean.doctor_name, doctorTitle));
        holder.item_limit.setText(String.format("%s:%s", context.getString(R.string.appointment_limit)
                , doctorBean.doctor_UCL == -1 ? "不限" : doctorBean.doctor_UCL));
        holder.item_introduce.setText(String.format("%s:%s", context.getString(R.string.doctor_introduce)
                , doctorBean.remark == null ? context.getString(R.string.empty_content) : doctorBean.remark));
        Picasso.with(MyApp.getAppContext())
                .load(MyConstant.NAMESPACE + doctorBean.doctor_avatar)
                .resize(DensityUtil.dip2px(64), DensityUtil.dip2px(64))
                .into(holder.item_icon);
        return convertView;
    }

    /**
     * 根据医生id获取这个医生的满意度平均分
     */
    private void requestCommentScore(final int id, final RatingBar ratingBar) {
        HashMap<String,Object> values = new HashMap<>();
        values.clear();
        values.put("doctor_id", id);
        ratingBar.setTag(id);
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    if ((int) ratingBar.getTag() == id) {
                        ratingBar.setRating(new JSONObject(s).optInt("AVG"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("get_doctor_AVG", values);
    }

    private static class ViewHolder {
        public View rootView;
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_limit;
        public TextView item_introduce;
        public RatingBar rating_bar;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.item_icon = (ImageView) rootView.findViewById(R.id.item_icon);
            this.item_name = (TextView) rootView.findViewById(R.id.item_name);
            this.item_limit = (TextView) rootView.findViewById(R.id.item_limit);
            this.item_introduce = (TextView) rootView.findViewById(R.id.item_introduce);
            this.rating_bar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        }

    }
}
