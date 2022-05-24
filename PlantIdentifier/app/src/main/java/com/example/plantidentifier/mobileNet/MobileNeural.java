package com.example.plantidentifier.mobileNet;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.plantidentifier.ml.MobileNetV2TFLitev2;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.common.ops.QuantizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MobileNeural {

    private static final String [] labels = {"daisy", "dandelion", "rose", "sunflower", "tulip"};

    private static int getIndexOfLargest(float[] array)
    {
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }

        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ){
                largest = i;
            }
        }
        return largest; // position of the first largest found
    }


    private static TensorBuffer preproccessing(Context context, Uri mImgUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mImgUri);
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new NormalizeOp(0f,255f)).build();
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);
        tensorImage = imageProcessor.process(tensorImage);

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(tensorImage.getBuffer()); //ByteBuffer.wrap(barr));// put pic in here
        return inputFeature0;
    }

    private static String proccess(MobileNetV2TFLitev2 model, TensorBuffer inputFeature0){
        // Runs model inference and gets result.
        MobileNetV2TFLitev2.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        float[] arr = outputFeature0.getFloatArray();

        // Releases model resources if no longer used.
        model.close();

        return labels[getIndexOfLargest(arr)];
    }

    public static String offlineModel(Context context, Uri mImgUri){
        String res = "";
        try {
            TensorBuffer inputFeature0 = preproccessing(context, mImgUri);
            MobileNetV2TFLitev2 model = MobileNetV2TFLitev2.newInstance(context);
            res = proccess(model, inputFeature0);
        } catch (IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            return res;
        }
//        Toast.makeText(context, res, Toast.LENGTH_LONG).show();
        return res;
    }




}
