package com.monri.android.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageProcessingUtil {
    private static final int EOF = -1;
    private static final int WRITE_OFFSET = 0;
    private static final int BUFFER_SIZE = 1024;
    private static final int SCALED_HEIGHT = 1024;
    private static final int SCALED_WIDTH = 1024;
    private static final int ROTATION_DEGREES = 90;

    public static String imageToBase64String(final File file) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final FileInputStream fileInputStream = new FileInputStream(file);
        final byte[] buffer = new byte[BUFFER_SIZE];

        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != EOF) {
            outputStream.write(buffer, WRITE_OFFSET, bytesRead);
        }
        fileInputStream.close();

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
    }

    public static String imageToBase64StringWithCompression(final File file, final int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bitmap = Bitmap.createScaledBitmap(bitmap, SCALED_WIDTH, SCALED_HEIGHT, true);
        bitmap = rotateBitmap(bitmap, ROTATION_DEGREES);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
    }

    public static Bitmap rotateBitmap(final Bitmap bitmap, final float degrees) {
        if (degrees == 0) return bitmap;

        final Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );
    }

    public static Bitmap base64ToImage(final String base64Image) {
        final byte[] decodedBytes = Base64.decode(base64Image, Base64.NO_WRAP);

        return BitmapFactory.decodeByteArray(decodedBytes, WRITE_OFFSET, decodedBytes.length);
    }
}
