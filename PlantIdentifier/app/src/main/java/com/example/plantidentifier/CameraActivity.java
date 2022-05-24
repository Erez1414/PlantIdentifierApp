package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantidentifier.NetworkUtils.CameraConnection;
import com.example.plantidentifier.dataUtils.FlowerInfoPage;
import com.example.plantidentifier.ml.MobileNetV2TFLitev2;
import com.example.plantidentifier.mobileNet.MobileNeural;

import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CameraConnection>, LocationListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int REQUEST_SELECT_FROM_GALLERY = 2;

    private static final int CAMERA_LOADER_ID = 3;

    private static final String TAG = "CameraActivityDebug";

    private String mRelativeLocation;

    private Button mBtnTakePhoto;

    private Button mBtnChooseFromGallery;

    private Button mBtnUploadPhoto;

    private ImageView mIvPhotoPreview;

    private TextView mTvCameraInstructions;

    private JSONObject mCurData;

    private Uri mImgUri;

    private boolean firstTime;

    private Bitmap imageBitmap;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean locationPermission;
    private double[] myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mRelativeLocation =  Environment.DIRECTORY_DCIM + File.separator + getResources()
                .getString(R.string.app_name);
        mBtnChooseFromGallery = (Button) findViewById(R.id.btn_choose_from_gallery);
        mBtnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        mBtnUploadPhoto = (Button) findViewById(R.id.btn_upload_photo);
        mIvPhotoPreview = (ImageView) findViewById(R.id.iv_photo_preview);
        mTvCameraInstructions = (TextView) findViewById(R.id.tv_camera_instructions);

        mBtnUploadPhoto.setVisibility(View.INVISIBLE);
        firstTime = true;

        //check for location permission and if there is, add location listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        locationPermission = (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (locationPermission) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        myLocation = new double[] {-33.8523341, 151.2106085}; // default place in (Sydney, Australia)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    public void onAbout(MenuItem menuItem){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onContactUs(MenuItem menuItem){
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void onClickTakePhoto(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onClickUploadPhoto(View v){
        if (mImgUri == null){
            Toast.makeText(this, getString(R.string.err_no_photo_selected), Toast.LENGTH_LONG).show();
            return;
        }
        LoaderManager.LoaderCallbacks<CameraConnection> callback = CameraActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(CAMERA_LOADER_ID, bundleForLoader, callback);
    }

    private void showIdentifyButton(){
        mBtnUploadPhoto.setVisibility(View.VISIBLE);
        mTvCameraInstructions.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = getResizedBitmap(imageBitmap,700);
            mIvPhotoPreview.setImageBitmap(imageBitmap);//getResizedBitmap(imageBitmap, 700)); //was 700
            showIdentifyButton();
            try {
                saveImage(imageBitmap);
            } catch (IOException e){
                Toast.makeText(this, getString(R.string.err_gallery_save), Toast.LENGTH_LONG).show();
            }
        }

        else if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_FROM_GALLERY) {
            // Get the url of the image from data
            Uri selectedImageUri = data.getData();
            mImgUri = selectedImageUri;
            Log.d(TAG, selectedImageUri.getPath() + "chooos from galle !!!!!!!");
            if (null != selectedImageUri) {
                // update the preview image in the layout
//                Bitmap imageBitmap;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    imageBitmap = getResizedBitmap(imageBitmap, 700);
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.err_gallery_access), Toast.LENGTH_LONG).show();
                    return;
                }
                mIvPhotoPreview.setImageBitmap(imageBitmap); //getResizedBitmap(imageBitmap, 700));
                showIdentifyButton();
            }
        }
    }

    public void onClickChooseFromGallery(View v) {
        // create an instance of the
        // intent of the type image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_SELECT_FROM_GALLERY);
    }

    private void saveImage(Bitmap bitmap) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "JPEG_" + timeStamp + "_";
        OutputStream fos;
        File dir = new File(mRelativeLocation);
        if (!dir.exists()){
            dir.mkdirs();
        }
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, mRelativeLocation);
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
        Log.d(TAG, imageUri.getPath() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mImgUri = imageUri;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        Objects.requireNonNull(fos).close();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @NonNull
    @Override
    public Loader<CameraConnection> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<CameraConnection>(this) {

            CameraConnection cameraConnection = null;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "@@@@@@@@@@@@@@@2 in onStartLoading");
                cameraConnection = new CameraConnection(mImgUri, this.getContext());
                forceLoad();
            }

            @Override
            public CameraConnection loadInBackground() {
                cameraConnection.connect();
                while(!cameraConnection.isFinished()){}
                return cameraConnection;
            }

            public void deliverResult(CameraConnection data) {
                cameraConnection = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<CameraConnection> loader, CameraConnection data) {
        Log.d(TAG, "onLoadFinished!!!!!!!!!!!!!!!! "+data.getRes() + " !!!!!!!!!!!!!!!!!!!");
        if (null == data.getRes() || !data.getSuccess()) {
//            Toast.makeText(this, getString(R.string.err_server), Toast.LENGTH_LONG).show();
            Log.d(TAG, "onLoadFinished!!!!!!!!!!!!!!!! "+getString(R.string.err_server) + " !!!!!!!!!!!!!!!!!!!");
//
            String flower = MobileNeural.offlineModel(this, mImgUri);

            if(flower.matches("")){
                Toast.makeText(this, "didnt work", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onLoadFinished!!!!!!! flower didnt work !!!!!!!!! ");
            }
            Toast.makeText(this, "Flower is " + flower + "\nfor more info please connect to internet",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "onLoadFinished!!!!!!! flower work !!!!!!!!! "+flower + " !!!!!!!!!!!!!!!!!!!");
            onBackPressed();
            return;
        } else {
            mCurData = data.getRes();
//            onLoaderReset(loader);
            if(mCurData.length() == 0){
                if(firstTime) {
                    firstTime = false;
                    Toast.makeText(this, getString(R.string.err_no_flower), Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    firstTime = true;
                    onLoaderReset(loader);
                    return;
                }
            }
//            onLoaderReset(loader);
            Intent intent = new Intent(this, FlowerInfoPage.class);
            intent.putExtra("INFO", mCurData.toString());
            imageBitmap = getResizedBitmap(imageBitmap, 300);
            intent.putExtra("img", imageBitmap);
            intent.putExtra("location", myLocation);
            Log.d(TAG, "cur location: " + Arrays.toString(myLocation));
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<CameraConnection> loader) {
        loader.startLoading();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocation[0] = location.getLatitude();
        myLocation[1] = location.getLongitude();
    }
}