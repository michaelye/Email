package com.michael.email.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.michael.email.R;
import com.michael.email.dialog.DialogResultListener;
import com.michael.email.dialog.ListItemDialogFragment;
import com.michael.email.util.Consts;
import com.michael.email.util.EmailBus;
import com.michael.email.util.EmailFormatter;
import com.michael.email.util.ImageUtils;
import com.michael.email.util.SharedPreferenceUtils;
import com.michael.email.util.Toaster;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 用来设置和修改用户的邮箱
 * <p/>
 * Created by michael on 16/6/2.
 */
public class UserInfoSettingActivity extends AppCompatActivity
{

    private RelativeLayout rlUserName;
    private RelativeLayout rlPassword;

    private EditText etUserName;
    private EditText etPassword;

    private ImageView ivAvatar;

    private ImageView ivClearName;
    private ImageView ivClearPassword;

    private Button btnOk;

    private boolean isModify;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_setting);
        AVATAR_PATH = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
        isModify = getIntent().getBooleanExtra("isModify", false);
        iniComponent();
        iniViews();
    }

    private void iniComponent()
    {
        rlUserName = (RelativeLayout) findViewById(R.id.rlUserName);
        rlPassword = (RelativeLayout) findViewById(R.id.rlPassword);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        ivClearName = (ImageView) findViewById(R.id.ivClearName);
        ivClearPassword = (ImageView) findViewById(R.id.ivClearPassword);
        btnOk = (Button) findViewById(R.id.btnOk);

        etUserName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().trim().equalsIgnoreCase(""))
                {
                    ivClearName.setVisibility(View.INVISIBLE);
                } else
                {
                    ivClearName.setVisibility(View.VISIBLE);
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().trim().equalsIgnoreCase(""))
                {
                    ivClearPassword.setVisibility(View.INVISIBLE);
                } else
                {
                    ivClearPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        ivAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO 选择头像
                showPickAvatarDialog();
            }
        });

        ivClearName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                etUserName.setText("");
            }
        });

        ivClearPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                etPassword.setText("");
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveAvatar();
                String email = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (email != null && !email.isEmpty() && EmailFormatter.isEmailFormat(email)
                        && password != null && !password.isEmpty() && is163())
                {
                    saveEmail();
                    savePassword();
                    notifyUserInfoChange();
                    if(!isModify)
                    {
                        Intent intent = new Intent(UserInfoSettingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else
                {
                    if (email == null || email.isEmpty())
                    {
                        Toaster.show(getResources().getString(R.string.toast_email_empty));
                        rlUserName.startAnimation(AnimationUtils.loadAnimation(UserInfoSettingActivity.this, R.anim.shake));
                    } else if(!EmailFormatter.isEmailFormat(email))
                    {
                        Toaster.show(getResources().getString(R.string.toast_email_format_invalid));
                        rlUserName.startAnimation(AnimationUtils.loadAnimation(UserInfoSettingActivity.this, R.anim.shake));
                    } else if(!is163())
                    {
                        Toaster.show(getResources().getString(R.string.toast_email_not_163));
                        rlUserName.startAnimation(AnimationUtils.loadAnimation(UserInfoSettingActivity.this, R.anim.shake));
                    } else if(password == null || password.isEmpty())
                    {
                        Toaster.show(getResources().getString(R.string.toast_email_password_not_empty));
                        rlPassword.startAnimation(AnimationUtils.loadAnimation(UserInfoSettingActivity.this, R.anim.shake));
                    }
                }
            }
        });
    }

    /**
     * 是否是163邮箱
     * */
    private boolean is163()
    {
        if(etUserName.getText().toString().endsWith("@163.com"))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 初始化
     */
    private void iniViews()
    {
        if (isModify)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_text_modify_account));
        } else
        {
            getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_text_set_account));
        }

        String userEmail = SharedPreferenceUtils.getString(this, Consts.USER_EMAIL, "");
        if (userEmail != null)
        {
            etUserName.setText(userEmail);
            etUserName.setSelection(userEmail.length());
        }

        String password = SharedPreferenceUtils.getString(this, Consts.PASSWORD, "");
        if (password != null)
        {
            etPassword.setText(password);
            etPassword.setSelection(password.length());
        }

        displayAvatar();
    }

    /**
     * 保存用户的邮箱
     */
    private void saveEmail()
    {
        SharedPreferenceUtils.putString(this, Consts.USER_EMAIL, etUserName.getText().toString());
    }

    /**
     * 保存用户的密码
     * */
    private void savePassword()
    {
        SharedPreferenceUtils.putString(this, Consts.PASSWORD, etPassword.getText().toString());
    }

    /**
     * 保存头像
     * */
    private void saveAvatar()
    {
        if(bitmapToBeSaved != null)
        {
            ImageUtils.convertBitmapToFile(AVATAR_PATH + Consts.AVATAR_NAME, bitmapToBeSaved);
        }
    }

    /**
     * 通知刷新
     * */
    private void notifyUserInfoChange()
    {
        EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_USER_INFO));
    }

    /**
     * 打开选择头像的对话框
     */
    private void showPickAvatarDialog()
    {
        ListItemDialogFragment listItemDialogFragment = ListItemDialogFragment.show(this,
                R.string.dialog_choose_avatar, R.array.choose_avatar_id, R.array.choose_avatar, -1);
        listItemDialogFragment.setDialogResultListener(new DialogResultListener()
        {
            @Override
            public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
            {
                if (resultCode == R.id.dialog_choose_avatar_from_gallery)
                {
                    openGallery();
                } else if (resultCode == R.id.dialog_choose_avatar_from_camera)
                {
                    openCamera();
                }
            }
        });
    }

    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private void openGallery()
    {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, null);
        intentGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intentGallery, PHOTOZOOM);
    }

    /**
     * 打开相机，需要检查一下权限
     */
    private void openCamera()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else
        {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(AVATAR_PATH, tempFileName)));
            startActivityForResult(intentCamera, PHOTOHRAPH);
        }
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    /**
     * 用户授权权限的回调，否则会crash
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // 权限通过
                openCamera();
            } else
            {
                // 权限拒绝
                Toaster.show(getResources().getString(R.string.dialog_choose_avatar_camera_refuse), true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // 拍照
        if (requestCode == PHOTOHRAPH)
        {
            // 设置文件保存路径这里放在跟目录下
            File picture = new File(AVATAR_PATH + tempFileName);
            if(picture.exists())
            {
                startPhotoZoom(Uri.fromFile(picture));
            }
        }

        if (data == null)
            return;

        // 读取相册缩放图片
        if (requestCode == PHOTOZOOM)
        {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTORESOULT)
        {
            Bundle extras = data.getExtras();
            if (extras != null)
            {
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0-100)压缩文件
                photo = ImageUtils.getCroppedBitmap(photo);
                // imageView.setImageBitmap(photo);
                if (photo != null)
                {
                    bitmapToBeSaved = photo;
                    displayAvatar(photo);
                } else
                {
                    Toaster.show(getResources().getString(R.string.dialog_choose_avatar_save_avatar_fail));
                }
            }
        }
    }

    private Bitmap bitmapToBeSaved = null;

    /**
     * 创建文件夹
     * */
    private void createFolderIfNotExist()
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            //handle case of no SDCARD present
            Toaster.show(getResources().getString(R.string.dialog_choose_avatar_sdcard_not_found));
        } else
        {
            String dir = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name);
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
        }
    }

    /**
     * 头像路径
     */
    private String AVATAR_PATH;

    private String tempFileName = "temp.jpg";


    /**
     * 如果有头像，就显示头像
     */
    private void displayAvatar()
    {
        createFolderIfNotExist();
        File imageFile = new File(AVATAR_PATH + Consts.AVATAR_NAME);
        if (imageFile.exists())
        {
            Bitmap bitmap = ImageUtils.getCroppedBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            displayAvatar(bitmap);
        }
    }

    /**
     * 显示图片，但是没有保存
     * */
    private void displayAvatar(Bitmap bitmap)
    {
        if(ivAvatar != null)
        {
            ivAvatar.setImageBitmap(bitmap);
        }
    }

    public void startPhotoZoom(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }
}
