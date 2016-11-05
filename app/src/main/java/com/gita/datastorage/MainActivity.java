package com.gita.datastorage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.edittext)
    EditText editText;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.photo_location_tv)
    TextView photoLocationTV;

    private String lastPath;

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int WRITE_STORAGE_PERMISSION_REQUEST = 2;

    private static final int CAMERA_REQUEST = 3;

    private static final String EDIT_TEXT_KEY = "EDIT_TEXT_KEY";
    private static final String IMAGE_PATH = "IMAGE_PATH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        editText.setText(Settings.getString(EDIT_TEXT_KEY));
        loadImage();

        editText.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Settings.saveString(EDIT_TEXT_KEY, editable.toString());
    }

    @TargetApi(23)
    private void requestCameraPermission() {
        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        List<String> permissions = new ArrayList<>();

        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), CAMERA_PERMISSION_REQUEST);
        } else {
            requestStoragePermission();
        }
    }

    @TargetApi(23)
    private void requestStoragePermission() {
        int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> permissions = new ArrayList<>();

        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), WRITE_STORAGE_PERMISSION_REQUEST);
        } else {
            openCamera();
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    Toast.makeText(this, "No camera this time", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case WRITE_STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "No storage permission this time", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File f = getExternalFile();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        cameraIntent.putExtra("path", f.getAbsolutePath());
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private File getExternalFile() {
        File f = new File(Environment.getExternalStorageDirectory() + "/GiTa/");
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File(f.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        lastPath = file.getAbsolutePath();

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File f = new File(lastPath);
            try {
                f = fixImage(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Settings.saveString(IMAGE_PATH, lastPath);
            makeVisibleInGallery(f);
            loadImage();
        } else if (requestCode == 5 && resultCode == RESULT_OK) {
            recreate();
        }

    }

    private void makeVisibleInGallery(File out) {
        if (Tools.atLeastKitKat()) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(out);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    private void loadImage() {
        String path = Settings.getString(IMAGE_PATH);
        photoLocationTV.setText(String.format(getString(R.string.last_photo_location), path));
        Glide.with(this).load(path).into(imageView);
    }

    private File fixImage(File imageFile) throws Exception {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        bitmap = fixExif(imageFile.getAbsolutePath(), bitmap, -1);

        File file = saveBitmap(imageFile, bitmap);
        bitmap.recycle();
        return file;
    }

    private File saveBitmap(File imageFile, Bitmap bmp) throws Exception {

        File file = new File(imageFile.getAbsolutePath());
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fOut = new FileOutputStream(imageFile);
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
        fOut.flush();
        fOut.close();
        return file;
    }

    private Bitmap fixExif(String path, Bitmap bmp, int orientation) throws Exception {
        ExifInterface exif = new ExifInterface(path);
        int rotate = 0;

        if (orientation < 0) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } else {
            rotate = orientation;
        }

        if (rotate > 1) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            return bmp;
        }

        return bmp;
    }

    public void onTakePhotoClick(View view) {
        if (Tools.atLeastMarshmallow()) {
            requestCameraPermission();
        } else {
            openCamera();
        }
    }

    public void onSettingsClick(View view) {
        startActivityForResult(new Intent(this, SettingsActivity.class), 5);
    }
}
