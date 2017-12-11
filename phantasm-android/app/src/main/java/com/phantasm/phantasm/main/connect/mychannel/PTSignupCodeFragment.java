package com.phantasm.phantasm.main.connect.mychannel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpit.android.ui.common.CircleImageView;
import com.gpit.android.util.Utils;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.PTPreference;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.PTMainActivity;
import com.phantasm.phantasm.main.api.vma.PTVMAPostSignInAPI;
import com.phantasm.phantasm.main.connect.PTConnectFragment;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTSignupCodeFragment extends PTBaseFragment {

    private PTMainActivity mMainActivity;
    private PTConnectFragment mConnectFragment;
    private PTVMAPostSignInAPI mLoginApi;

    private CircleImageView mImgPhoto;
    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;

    private Uri mPhotoUri;
    private String mPhotoFilePath;

    public static PTSignupCodeFragment newInstance() {
        return new PTSignupCodeFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_signup_code;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mLoginApi = new PTVMAPostSignInAPI(getContext());
    }

    @Override
    protected void initUI() {
        mMainActivity = (PTMainActivity) getContext();
        initActionBar();

        mImgPhoto = (CircleImageView) findViewById(R.id.img_photo);
        mImgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPhoto();
            }
        });
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        Button btnContinue = (Button) findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContinue();
            }
        });

        updateUI();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PTConst.CHOOSE_PHOTO_FROM_GALLERY:
                if(data != null) {
                    mPhotoUri = data.getData();
                    if(mPhotoUri != null) {
                        beginCrop(mPhotoUri);
                    } else {
                        Toast.makeText(mMainActivity, "Unable to choose image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PTConst.TAKE_PHOTO_FROM_CAMERA:
                if(mPhotoUri != null) {
                    beginCrop(mPhotoUri);
                }
                break;
            case Crop.REQUEST_CROP:
                if(data != null) {
                    handleCrop(resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void initActionBar() {
        setHasOptionsMenu(true);
    }

    public void setParentFragment(PTConnectFragment fragment) {
        this.mConnectFragment = fragment;
    }

    private void updateUI() {
//        if (mSearchView == null) return;

//        updateLayout(false);
    }

    private void onClickPhoto() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(mMainActivity);
        dlg.setTitle("Choose your photo");
        String[] items = {"Choose from Gallery", "Take Photo from Camera"};
        dlg.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromGallery();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        }).show();
    }

    private void onClickContinue() {
        if(mPhotoFilePath == null || mPhotoFilePath.isEmpty()) {
            Toast.makeText(mMainActivity, "Please choose your photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        String strName = mEditName.getText().toString();
        if(strName.isEmpty()) {
            Toast.makeText(mMainActivity, "Please enter the Name.", Toast.LENGTH_SHORT).show();
            mEditName.setFocusableInTouchMode(true);
            return;
        }

        String strEmail = mEditEmail.getText().toString();
        if(strEmail.isEmpty()) {
            Toast.makeText(mMainActivity, "Please enter the Email Address.", Toast.LENGTH_SHORT).show();
            mEditEmail.setFocusableInTouchMode(true);
            return;
        } else if(!Utils.checkEmailFormat(strEmail)) {
            Toast.makeText(mMainActivity, "Please enter the Email Address correctly.", Toast.LENGTH_SHORT).show();
            mEditEmail.setFocusableInTouchMode(true);
            mEditEmail.setSelected(true);
            return;
        }

        String strPwd = mEditPassword.getText().toString();
        if(strPwd.isEmpty()) {
            Toast.makeText(mMainActivity, "Please enter the Password.", Toast.LENGTH_SHORT).show();
            mEditPassword.setFocusableInTouchMode(true);
        }

        String jsonArguments = String.format(Locale.getDefault(), "[\"%s\",\"$s\",\"$s\"]", strEmail, strPwd, strName);
        mLoginApi.setInfo(PTConst.ACTION_SIGNUP, jsonArguments);
        sendRequestSignup(new OnCommonAPICompleteListener<PTWebAPI>(getActivity()) {
            @Override
            public void onCompleted(PTWebAPI webapi) {
                try {
                    JSONObject jsonObj = webapi.getResponseData();

                    // Getting JSON Array node
                    JSONObject contents = jsonObj.getJSONObject(PTConst.TAG_DATA);
                    JSONObject user = contents.getJSONObject(PTConst.TAG_USER);

                    PTPreference ptPreference = PTPreference.getInstance(getContext());
                    ptPreference.putIsSignin(true);
                    ptPreference.putUserId(user.getInt("id"));
                    ptPreference.putUserName(user.getString("username"));
                    ptPreference.putEmail(user.getString("email"));

                    mConnectFragment.gotoNextFragment(PTConnectFragment.FRAGMENT_TAG_SIGNIN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(PTWebAPI webapi) {
                super.onFailed(webapi);
                try {
                    JSONObject jsonObj = webapi.getResponseData();

                    // Getting JSON Array node
                    JSONObject contents = jsonObj.getJSONObject(PTConst.TAG_DATA);
                    Utils.showAlertDialog(getActivity(), contents.getString(PTConst.TAG_ERROR), true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendRequestSignup(OnAPICompletedListener<PTWebAPI> listener) {
        mLoginApi.setRequestMethod(false);
        mLoginApi.showProgress(true, "Signing up...", false);
        mLoginApi.exec(listener);
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PTConst.CHOOSE_PHOTO_FROM_GALLERY);
    }

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));
        if(file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPhotoUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, PTConst.TAKE_PHOTO_FROM_CAMERA);
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), (Calendar.getInstance().getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent intent) {
        if(resultCode == mMainActivity.RESULT_OK) {
            Uri uri = Crop.getOutput(intent);
            mPhotoFilePath = Utils.getImagePath(mMainActivity, uri);
            mImgPhoto.setImageURI(uri);
            mImgPhoto.setRotation(90);
        } else {
            Toast.makeText(mMainActivity, Crop.getError(intent).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
