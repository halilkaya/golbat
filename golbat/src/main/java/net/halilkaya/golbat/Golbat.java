package net.halilkaya.golbat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Golbat is an Android library that helps on working with camera,
 * gallery and output and its encoded types.
 * Copyright (C) 2016  Halil Kaya

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Golbat {

    /**
     * Please do not forget to require WRITE_EXTERNAL_STORAGE
     * and READ_EXTERNAL_STORAGE permissions in your Manifest
     * file to be able to use Golbat!
     */

    /**
     * You can track logs and errors on this tag.
     */
    private static final String TAG = "Golbat";

    /**
     * Variables that will be collected from Builder class.
     */
    private static ContentResolver contentResolver;
    private static String directoryName;
    private static int cameraRequestCode = 9420;
    private static int galleryRequestCode = 9421;
    private static int imageQuality = 100;
    private static ImageType imageType = ImageType.JPEG;
    private static int maxSizeInPixel = 500;

    /**
     * Initializer method called by Builder class to
     * pass the arguments to use in Golbat class.
     * @param gContentResolver ContentResolver
     * @param gDirectoryName String
     * @param gCameraRequestCode int
     * @param gGalleryRequestCode int
     * @param gImageQuality int
     * @param gImageType ImageType
     * @param gMaxSizeInPixel int
     */
    private static void initialize(ContentResolver gContentResolver,
                                   String gDirectoryName,
                                   int gCameraRequestCode,
                                   int gGalleryRequestCode,
                                   int gImageQuality,
                                   ImageType gImageType,
                                   int gMaxSizeInPixel) {
        contentResolver = gContentResolver;
        directoryName = gDirectoryName;
        cameraRequestCode = gCameraRequestCode;
        galleryRequestCode = gGalleryRequestCode;
        imageQuality = gImageQuality;
        imageType = gImageType;
        maxSizeInPixel = gMaxSizeInPixel;
    }

    /**
     * Getter method to use ContentResolver inside.
     * @return ContentResolver
     */
    public static ContentResolver getContentResolver() {
        if (contentResolver == null)
            throw new NullPointerException("ContentResolver not set!");
        return contentResolver;
    }

    /**
     * Getter method to use directory name inside.
     * @return String
     */
    public static String getDirectoryName() {
        if (directoryName == null)
            throw new NullPointerException("Directory name not set!");
        return directoryName;
    }

    /**
     * Getter method to use request code for camera inside.
     * @return int
     */
    public static int getCameraRequestCode() {
        return cameraRequestCode;
    }

    /**
     * Getter method to use request code for gallery inside.
     * @return int
     */
    public static int getGalleryRequestCode() {
        return galleryRequestCode;
    }

    /**
     * Getter method to use image quality value inside.
     * @return int
     */
    public static int getImageQuality() {
        return imageQuality;
    }

    /**
     * Getter method to use image type (see ImageType) inside.
     * @return ImageType
     */
    public static ImageType getImageType() {
        return imageType;
    }

    /**
     * Getter method to use maximum size (in pixel) of the image
     * if it's resized.
     * @return int
     */
    public static int getMaxSizeInPixel() {
        return maxSizeInPixel;
    }

    /**
     * Opens gallery intent to select a photo.
     * @param activity Activity
     */
    public static void openGallery(Activity activity) {
        if (activity == null) {
            throw new RuntimeException("Activity not set!");
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(
                galleryIntent,
                getGalleryRequestCode()
        );
    }

    /**
     * Opens the camera intent and returns Uri of the captured image.
     * Please be careful about NullPointerException!
     * @param activity Activity
     * @return Uri
     */
    public static Uri openCamera(Activity activity) {
        if (activity == null) {
            throw new RuntimeException("Activity not set!");
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri fileUri;
        fileUri = getOutputMediaFileUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        activity.startActivityForResult(
                cameraIntent,
                getCameraRequestCode()
        );

        return fileUri;
    }

    /**
     * Gets Uri of an image that stored as file.
     * @return Uri
     */
    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Private method to return stored image as file.
     * @return File
     */
    private static File getMediaStorageDir() {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                getDirectoryName()
        );
    }

    /**
     * Creates an image file in the directory that described in Builder class
     * and returns its file as File.
     * @return File
     */
    private static File getOutputMediaFile() {
        File mediaStorageDir = getMediaStorageDir();

        Log.d(TAG, "Media storage directory path: " + mediaStorageDir.getPath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Failed to create " + getDirectoryName() + " directory!");
                return null;
            }
        }

        // TODO: File name will be changed and will be optional!
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());

        File mediaFile = new File(
                mediaStorageDir,
                timeStamp + (getImageType() == ImageType.JPEG ? ".jpg" : ".png")
        );

        return mediaFile;
    }

    /**
     * Returns the image that selected from gallery as Bitmap.
     * @param fileUri Uri
     * @return Bitmap
     */
    public static Bitmap getSelectedImageAsBitmap(Uri fileUri) {
        if (getContentResolver() == null) {
            throw new RuntimeException("ContentResolver not set!");
        }

        if (fileUri == null) {
            throw new NullPointerException("File uri not set!");
        }

        try {
            return MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    fileUri
            );
        } catch (IOException e) {
            Log.e(TAG, "Failed to get bitmap of selected image from gallery!");
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Returns the image that captured from camera as Bitmap.
     * @param fileUri Uri
     * @return Bitmap
     */
    public static Bitmap getCapturedImageAsBitmap(Uri fileUri) {
        if (fileUri == null) {
            throw new NullPointerException("File uri not set!");
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        return BitmapFactory.decodeFile(
                fileUri.getPath(),
                options
        );
    }

    /**
     * Generates Base64 encoded version of an image in Bitmap.
     * @param image Bitmap
     * @param resize boolean
     * @return String
     */
    public static String getBase64(Bitmap image, boolean resize) {
        if (image == null) {
            throw new NullPointerException("Bitmap not set!");
        }

        image = resize ? resizeImage(image) : image;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(
                getImageType() == ImageType.JPEG ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG,
                getImageQuality(),
                outputStream
        );

        return Base64.encodeToString(
                outputStream.toByteArray(),
                Base64.DEFAULT
        );
    }

    /**
     * Resizes the image in range if its one of the sides is higher
     * than maxSizeInPixel (default 500px).
     * @param image Bitmap
     * @return Bitmap
     */
    public static Bitmap resizeImage(Bitmap image) {
        if (image == null) {
            throw new NullPointerException("Bitmap not set!");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if (width > height && width > getMaxSizeInPixel())
            return resizeImageByWidth(image);

        if (height > width && height > getMaxSizeInPixel())
            return resizeImageByHeight(image);

        return image;
    }

    /**
     * Resizes the image by only its width.
     * @param image Bitmap
     * @return Bitmap
     */
    public static Bitmap resizeImageByWidth(Bitmap image) {
        if (image == null) {
            throw new NullPointerException("Bitmap not set!");
        }

        int newHeight = (image.getHeight() * getMaxSizeInPixel()) / image.getWidth();
        return Bitmap.createScaledBitmap(image, getMaxSizeInPixel(), newHeight, true);
    }

    /**
     * Resizes the image by only its height.
     * @param image Bitmap
     * @return Bitmap
     */
    public static Bitmap resizeImageByHeight(Bitmap image) {
        if (image == null) {
            throw new NullPointerException("Bitmap not set!");
        }

        int newWidth = (image.getWidth() * getMaxSizeInPixel()) / image.getHeight();
        return Bitmap.createScaledBitmap(image, newWidth, getMaxSizeInPixel(), true);
    }

    /**
     * Resizes the image in fixed size.
     * @param image Bitmap
     * @param width int
     * @param height int
     * @return Bitmap
     */
    public static Bitmap resizeImageInFixedSize(Bitmap image, int width, int height) {
        if (image == null) {
            throw new NullPointerException("Bitmap not set!");
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Deletes a photo that given by Uri and returns its
     * result as boolean.
     * @param fileUri Uri
     * @return boolean
     */
    public static boolean deletePhoto(Uri fileUri) {
        try {
            File mediaStorageDir = getMediaStorageDir();

            if (!mediaStorageDir.exists())
                return false;

            File photoToDelete = new File(fileUri.getPath());
            return photoToDelete.delete();
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete image!");
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Deletes the latest photo that placed in the determined
     * directory and returns its result as boolean.
     * @return boolean
     */
    public static boolean deleteLatestPhoto() {
        try {
            File mediaStorageDir = getMediaStorageDir();

            if (!mediaStorageDir.exists())
                return false;

            File[] images = mediaStorageDir.listFiles();
            File latestImage = images[0];

            for (File image : images) {
                if (image.lastModified() > latestImage.lastModified())
                    latestImage = image;
            }

            return latestImage.delete();
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete latest image!");
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Puts the image into an ImageView according to capture type (see CaptureType).
     * @param captureType CaptureType
     * @param imageView ImageView
     * @param fileUri Uri
     */
    public static void showImage(CaptureType captureType,
                                 ImageView imageView,
                                 Uri fileUri) {
        if (imageView == null) {
            throw new RuntimeException("ImageView not set!");
        }

        if (fileUri == null) {
            throw new NullPointerException("File uri not set!");
        }

        try {
            imageView.setImageBitmap(
                    captureType == CaptureType.CAPTURED_FROM_CAMERA ?
                            getCapturedImageAsBitmap(fileUri) :
                            getSelectedImageAsBitmap(fileUri)
            );
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to generate image bitmap!");
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * Enum values to determine if the image captured from camera or
     * selected from gallery.
     */
    public enum CaptureType {
        CAPTURED_FROM_CAMERA, SELECTED_FROM_GALLERY
    }

    /**
     * Enum values to determine if the image is stored and used in
     * JPEG or PNG.
     */
    public enum ImageType {
        JPEG, PNG
    }

    /**
     * Builder class to begin to use Golbat.
     */
    public static class Builder {

        private ContentResolver contentResolver;
        private String directoryName;
        private int cameraRequestCode = 9420;
        private int galleryRequestCode = 9421;
        private int imageQuality = 100;
        private ImageType imageType = ImageType.JPEG;
        private int maxSizeInPixel = 500;

        /**
         * Setter method for ContentResolver
         * Required if getSelectedImageAsBitmap() method will be used.
         * @param contentResolver ContentResolver
         * @return Builder
         */
        public Builder setContentResolver(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
            return this;
        }

        /**
         * Setter method for directory name
         * Required to use Golbat.
         * @param directoryName String
         * @return Builder
         */
        public Builder setDirectoryName(String directoryName) {
            this.directoryName = directoryName;
            return this;
        }

        /**
         * Setter method for request code for camera.
         * Optional. Default: 9420
         * @param cameraRequestCode int
         * @return Builder
         */
        public Builder setCameraRequestCode(int cameraRequestCode) {
            this.cameraRequestCode = cameraRequestCode;
            return this;
        }

        /**
         * Setter method for request code for gallery.
         * Optional. Default: 9421
         * @param galleryRequestCode int
         * @return Builder
         */
        public Builder setGalleryRequestCode(int galleryRequestCode) {
            this.galleryRequestCode = galleryRequestCode;
            return this;
        }

        /**
         * Setter method for image quality value.
         * Optional. Default: 100
         * @param imageQuality int
         * @return Builder
         */
        public Builder setImageQuality(int imageQuality) {
            this.imageQuality = imageQuality;
            return this;
        }

        /**
         * Setter method for image type (see ImageType).
         * Optional. Default: JPEG
         * @param imageType ImageType
         * @return Builder
         */
        public Builder setImageType(ImageType imageType) {
            this.imageType = imageType;
            return this;
        }

        /**
         * Setter method for maximum size (in pixel) of the image.
         * Optional. Default: 500
         * @param maxSizeInPixel int
         * @return Builder
         */
        public Builder setImageMaxSize(int maxSizeInPixel) {
            this.maxSizeInPixel = maxSizeInPixel;
            return this;
        }

        /**
         * Initializes Golbat!
         */
        public void build() {
            if (directoryName == null) {
                throw new RuntimeException("Directory name not set!");
            }

            Golbat.initialize(
                    contentResolver,
                    directoryName,
                    cameraRequestCode,
                    galleryRequestCode,
                    imageQuality,
                    imageType,
                    maxSizeInPixel
            );
        }

    }

}
