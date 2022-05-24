package com.example.plantidentifier.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static byte[]  fromBitmap(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @TypeConverter
    public static Bitmap toBitmap(byte[] byteArray){
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @TypeConverter
    public static String fromDoubleArray(double[] imgLocation){
        return String.valueOf(imgLocation[0]) + " " + String.valueOf(imgLocation[1]);
    }

    @TypeConverter
    public static double[] toDoubleArray(String imgLocation){
        String[] split = imgLocation.split(" ");
        return new double[] {Double.parseDouble(split[0]), Double.parseDouble(split[1])};
    }
}
