package com.yimeng.babymom.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.AccountInterface;
import com.yimeng.babymom.task.UpdateTask;
import com.yimeng.babymom.task.UploadImgTask;
import com.yimeng.babymom.utils.BitmapUtils;
import com.yimeng.babymom.utils.PickImageUtils;

import java.util.HashMap;

/**
 * 账号资料页面
 */

public class AccountActivity extends BaseActivity implements AccountInterface {
    private ImageView iv;
    private EditText et_phone;
    private EditText et_name;
    private TextView tv_edit;
    private TextView tv_score;
    private static final int STATE_EDIT = 0;
    private static final int STATE_READ = 1;
    private static final int REQUEST_GALLERY_FOR_AVATAR = 101;
    private int mState = STATE_READ;
    private HashMap<String, Object> mAvatarParams = new HashMap<>();
    private String mAvatarString;
    private String mAvatarUrl;
    private AsyncTask<Object, Object, String> mUploadImgTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initView() {
        iv = (ImageView) findViewById(R.id.iv);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_score = (TextView) findViewById(R.id.tv_score);
        setEnable(false);
    }

    private void setEnable(boolean enable) {
        iv.setEnabled(enable);
        et_phone.setEnabled(enable);
        et_name.setEnabled(enable);
    }

    @Override
    protected void setListener() {
        iv.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        et_phone.setText("132248556");
        et_name.setText("范冰冰");
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.tv_edit:
                switch (mState) {
                    case STATE_EDIT:
                        showToast("修改完成");
                        tv_edit.setText(getString(R.string.edit));
                        setEnable(false);
                        break;
                    case STATE_READ:
                        setEnable(true);
                        tv_edit.setText(getString(R.string.finish));
                        et_phone.requestFocus();
                        et_phone.setSelection(et_phone.getText().toString().length());
                        break;
                }
                mState = 1 - mState;
                break;
            case R.id.iv:
                PickImageUtils.getGalleryImage(this, REQUEST_GALLERY_FOR_AVATAR);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_GALLERY_FOR_AVATAR:
                Bitmap bitmap = PickImageUtils.getBitmapFromIntent(data, mActivity);
                if (null == bitmap)
                    return;
//                iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, iv.getMeasuredWidth(), iv.getMeasuredHeight()));
                mAvatarString = BitmapUtils.compressBitmap2Base64String(bitmap, 1024 * 200);
                bitmap.recycle();
                if (null == mAvatarString)
                    showToast("出错了，请重新选择图片");
                else
                    uploadImg();
                break;
        }
    }

    @Override
    public void uploadImg() {
        if (mUploadImgTask != null)
            mUploadImgTask.cancel(true);
        mAvatarParams.clear();
        mAvatarParams.put(UploadImgTask.FILE_NAME, "1.jpg");
        mAvatarParams.put(UploadImgTask.DEL_FILE_PATH, mAvatarUrl);
        mAvatarParams.put(UploadImgTask.IMAGE, mAvatarString);
        mUploadImgTask = new UploadImgTask(this, iv).execute(UpdateTask.METHOD, mAvatarParams);
    }

    @Override
    public void onSuccess(String imgUrl) {
        mAvatarUrl = imgUrl;
        System.out.println(imgUrl);
    }

    @Override
    public void onError() {
        showToast("出错了，请重新选择图片");
    }

    @Override
    protected void onDestroy() {
        if (mUploadImgTask != null)
            mUploadImgTask.cancel(true);
        super.onDestroy();
    }
}
