package net.yazeed44.imagepicker.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.yazeed44.imagepicker.library.R;
import net.yazeed44.imagepicker.model.AlbumEntry;
import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.CameraSupport;
import net.yazeed44.imagepicker.util.Events;
import net.yazeed44.imagepicker.util.Picker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class PickerActivity extends AppCompatActivity {


    public static final int NO_LIMIT = -1;

    public static final String KEY_ACTION_BAR_TITLE = "actionBarKey";
    public static final String KEY_SHOULD_SHOW_ACTIONBAR_UP = "shouldShowUpKey";
    public static final String CAPTURED_IMAGES_ALBUM_NAME = "captured_images";
    public static final String CAPTURED_IMAGES_DIR = Environment.getExternalStoragePublicDirectory(CAPTURED_IMAGES_ALBUM_NAME).getAbsolutePath();
    private static final int REQUEST_PORTRAIT_RFC = 1337;
    private static final int REQUEST_PORTRAIT_FFC = REQUEST_PORTRAIT_RFC + 1;
    public static final int REQUEST_IMAGE_CAPTURE = 99;
    public static ArrayList<ImageEntry> sCheckedImages = new ArrayList<>();

    private boolean mShouldShowUp = false;

    private com.melnykov.fab.FloatingActionButton mDoneFab;
    private Picker mPickOptions;
    //For ViewPager
    private ImageEntry mCurrentlyDisplayedImage;
    private AlbumEntry mSelectedAlbum;
    private MenuItem mSelectAllMenuItem;
    private MenuItem mDeselectAllMenuItem;

    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvImageCount;

    //TODO Add animation
    //TODO Fix bugs with changing orientation
    //TODO Add support for gif
    //TODO Use robust method for capturing photos
    //TODO Add support for picking videos
    //TODO When photo is captured a new album created for it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPickOptions = (EventBus.getDefault().getStickyEvent(Events.OnPublishPickOptionsEvent.class)).options;
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        tvImageCount = (TextView) findViewById(R.id.tv_count);
        tvImageCount.setText("0");
        addToolbarToLayout();
        initActionbar(savedInstanceState);
        setupAlbums(savedInstanceState);
        initFab();
        updateFab();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ACTION_BAR_TITLE, getSupportActionBar().getTitle().toString());
        outState.putBoolean(KEY_SHOULD_SHOW_ACTIONBAR_UP, mShouldShowUp);
    }

    private void initTheme() {
        setTheme(mPickOptions.themeResId);
     /*   mToolbar = new Toolbar(new ContextThemeWrapper(mPickOptions.context, Util.getToolbarThemeResId(this)));
        mToolbar.setPopupTheme(mPickOptions.popupThemeResId);*/
    }

    private void addToolbarToLayout() {
       /* final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        final AppBarLayout.LayoutParams toolbarParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.getActionBarHeight(this));
        toolbarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        appBarLayout.addView(mToolbar, toolbarParams);*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);



    }


    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    private void initActionbar(final Bundle savedInstanceState) {


        if (savedInstanceState == null) {
            mShouldShowUp = mPickOptions.backBtnInMainActivity;
            getSupportActionBar().setDisplayHomeAsUpEnabled(mPickOptions.backBtnInMainActivity);
            getSupportActionBar().setTitle(R.string.albums_title);
        } else {
            mShouldShowUp = savedInstanceState.getBoolean(KEY_SHOULD_SHOW_ACTIONBAR_UP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(mShouldShowUp && mPickOptions.backBtnInMainActivity);
            getSupportActionBar().setTitle(savedInstanceState.getString(KEY_ACTION_BAR_TITLE));


        }


    }

    public void initFab() {
        Drawable doneIcon = ContextCompat.getDrawable(this, R.drawable.ic_action_done_white);
        doneIcon = DrawableCompat.wrap(doneIcon);
        DrawableCompat.setTint(doneIcon, mPickOptions.doneFabIconTintColor);

        mDoneFab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fab_done);
        mDoneFab.setImageDrawable(doneIcon);
        mDoneFab.setColorNormal(mPickOptions.fabBackgroundColor);
        mDoneFab.setColorPressed(mPickOptions.fabBackgroundColorWhenPressed);

        EventBus.getDefault().postSticky(new Events.OnAttachFabEvent(mDoneFab));


    }

    public void setupAlbums(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new AlbumsFragment(), AlbumsFragment.TAG)
                        .commit();


            }
        }
    }


    public void updateFab() {

        if (mPickOptions.pickMode == Picker.PickMode.SINGLE_IMAGE) {
            mDoneFab.setVisibility(View.GONE);
            mDoneFab.hide();
            return;
        }


        if (sCheckedImages.size() == 0) {
            mDoneFab.setVisibility(View.GONE);

        } else if (sCheckedImages.size() == mPickOptions.limit) {

            //Might change FAB appearance on other version
            mDoneFab.setVisibility(View.VISIBLE);
            mDoneFab.show();
            mDoneFab.bringToFront();

        } else {
            mDoneFab.setVisibility(View.VISIBLE);
            mDoneFab.show();
            mDoneFab.bringToFront();


        }

    }

    public void onClickDone(View view) {

        if (mPickOptions.pickMode == Picker.PickMode.SINGLE_IMAGE) {

            sCheckedImages.add(mCurrentlyDisplayedImage);
            mCurrentlyDisplayedImage.isPicked = true;
        } else {
            //No need to modify sCheckedImages for Multiple images mode
        }

        super.finish();

        //New object because sCheckedImages will get cleared
        mPickOptions.pickListener.onPickedSuccessfully(new ArrayList<>(sCheckedImages));
        sCheckedImages.clear();
        EventBus.getDefault().removeAllStickyEvents();

    }

    public void onCancel() {
        mPickOptions.pickListener.onCancel();
        sCheckedImages.clear();
        EventBus.getDefault().removeAllStickyEvents();


    }

    public void startCamera(View view) {


        if (!CameraSupport.isEnabled()) {
            return;
        }

        if (sCheckedImages != null && sCheckedImages.size() >= mPickOptions.limit) {
            Snackbar.make(coordinatorLayout, "You cannot select more than "+ mPickOptions.limit  +" images", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }


        if (!mPickOptions.videosEnabled) {
            capturePhoto();
            return;
        }


        new AlertDialog.Builder(this).setTitle(R.string.dialog_choose_camera_title)
                .setItems(new String[]{getResources().getString(R.string.dialog_choose_camera_item_0), getResources().getString(R.string.dialog_choose_camera_item_1)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //   capturePhoto();
                        } else {
                            //captureVideo();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    public void capturePhoto() {


        dispatchTakePictureIntent();

     /*   final File captureImageFile = createTemporaryFileForCapturing(".png");
        CameraSupport.startPhotoCaptureActivity(this, captureImageFile, REQUEST_PORTRAIT_FFC);*/
    }

    private File createTemporaryFileForCapturing(final String extension) {
        final File captureTempFile = new File(CAPTURED_IMAGES_DIR + "/tmp" + System.currentTimeMillis() + extension);
        try {
            captureTempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("capture", e.getMessage());
        }

        return captureTempFile;
    }

    public void captureVideo() {
        final File captureVideoFile = createTemporaryFileForCapturing(".mp4");
        CameraSupport.startVideoCaptureActivity(this,
                captureVideoFile, mPickOptions.videoLengthLimit, REQUEST_PORTRAIT_FFC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
            Toast.makeText(this, "Succcess", Toast.LENGTH_SHORT).show();
//            galleryAddPic();
            refreshMediaScanner(mCurrentPhotoPath);

           /* Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);*/
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_PORTRAIT_FFC) {
            //For capturing image from camera
            galleryAddPic();
            refreshMediaScanner(data.getData().getPath());


        } else {
            Log.i("onActivityResult", "User canceled the camera activity");
        }
    }

    private void refreshMediaScanner(final String imagePath) {
        MediaScannerConnection.scanFile(this,
                new String[]{imagePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(final String path, final Uri uri) {


                        PickerActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ImageEntry imageEntry;
                                try {
                                    imageEntry = new ImageEntry.Builder(path).imageId(Integer.parseInt(uri.getLastPathSegment())).dateAdded(SystemClock.currentThreadTimeMillis()).build();
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    imageEntry = null;
                                }
                                if (imageEntry != null)
                                    EventBus.getDefault().postSticky(new Events.OnPickImageEvent(imageEntry));


                                reloadAlbums();
                            }
                        });


                        Log.d("onActivityResult", "New image should appear in camera folder");
                    }
                });
    }

    private void reloadAlbums() {
        if (isImagesThumbnailShown()) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            getSupportFragmentManager().popBackStackImmediate(ImagesThumbnailFragment.TAG, 0);
            getSupportFragmentManager().popBackStackImmediate();
        }

        EventBus.getDefault().post(new Events.OnReloadAlbumsEvent());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


     /*   if (mPickOptions.shouldShowCaptureMenuItem) {
            initCaptureMenuItem(menu);
        }

        getMenuInflater().inflate(R.menu.menu_select_all, menu);
        getMenuInflater().inflate(R.menu.menu_deselect_all, menu);


        mSelectAllMenuItem = menu.findItem(R.id.action_select_all);
        mDeselectAllMenuItem = menu.findItem(R.id.action_deselect_all);

        if (shouldShowDeselectAll()) {
            showDeselectAll();
        } else {
            hideDeselectAll();
        }

        if (shouldShowSelectAll()) {
            showSelectAll();
        } else {
            hideSelectAll();
        }*/


        return true;
    }

    private boolean shouldShowSelectAll() {
        final Fragment imageThumbnailFragment = getSupportFragmentManager().findFragmentByTag(ImagesThumbnailFragment.TAG);
        return imageThumbnailFragment != null && imageThumbnailFragment.isVisible() && !mPickOptions.pickMode.equals(Picker.PickMode.SINGLE_IMAGE);
    }

    private void initCaptureMenuItem(final Menu menu) {
        if (CameraSupport.isEnabled()) {
            getMenuInflater().inflate(R.menu.menu_take_photo, menu);
            Drawable captureIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_action_camera_white);
            captureIconDrawable = DrawableCompat.wrap(captureIconDrawable);

            DrawableCompat.setTint(captureIconDrawable, mPickOptions.captureItemIconTintColor);

            menu.findItem(R.id.action_take_photo).setIcon(captureIconDrawable);
        }
    }

    private void hideDeselectAll() {
//        mDeselectAllMenuItem.setVisible(false);

    }

    private void showDeselectAll() {

       /* if (mPickOptions.pickMode == Picker.PickMode.SINGLE_IMAGE) {
            return;
        }

        mDeselectAllMenuItem.setVisible(true);*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

/*

        final int itemId = item.getItemId();


        if (itemId == R.id.action_take_photo) {
            startCamera();

        } else if (itemId == R.id.action_select_all) {
            selectAllImages();

        } else if (itemId == R.id.action_deselect_all) {
            deselectAllImages();
        }
*/

        return super.onOptionsItemSelected(item);
    }

    private void deselectAllImages() {

        for (final ImageEntry imageEntry : mSelectedAlbum.imageList) {
            imageEntry.isPicked = false;
            sCheckedImages.remove(imageEntry);
        }

        EventBus.getDefault().post(new Events.OnUpdateImagesThumbnailEvent());

        hideDeselectAll();
        updateFab();

    }

    private void selectAllImages() {

        if (mSelectedAlbum == null) {
            mSelectedAlbum = EventBus.getDefault().getStickyEvent(Events.OnClickAlbumEvent.class).albumEntry;
        }

        if (sCheckedImages.size() < mPickOptions.limit || mPickOptions.limit == NO_LIMIT) {

            for (final ImageEntry imageEntry : mSelectedAlbum.imageList) {

                if (mPickOptions.limit != NO_LIMIT && sCheckedImages.size() + 1 > mPickOptions.limit) {
                    //Hit the limit
                    Toast.makeText(this, R.string.you_cant_check_more_images, Toast.LENGTH_SHORT).show();
                    break;
                }


                if (!imageEntry.isPicked) {
                    //To avoid repeated images
                    sCheckedImages.add(imageEntry);
                    imageEntry.isPicked = true;
                }
            }
        }
        EventBus.getDefault().post(new Events.OnUpdateImagesThumbnailEvent());
        updateFab();

        if (shouldShowDeselectAll()) {
            showDeselectAll();
        }


    }


    @Override
    public void onBackPressed() {

        if (isImagesThumbnailShown()) {
            //Return to albums fragment
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setTitle(R.string.albums_title);
            mShouldShowUp = mPickOptions.backBtnInMainActivity;
            getSupportActionBar().setDisplayHomeAsUpEnabled(mShouldShowUp);
            hideSelectAll();
            hideDeselectAll();

        } else if (isImagesPagerShown()) {
            //Returns to images thumbnail fragment

            if (mSelectedAlbum == null) {
                mSelectedAlbum = EventBus.getDefault().getStickyEvent(Events.OnClickAlbumEvent.class).albumEntry;
            }
            mDoneFab.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack(ImagesThumbnailFragment.TAG, 0);
            getSupportActionBar().setTitle(mSelectedAlbum.name);
            getSupportActionBar().show();
            showSelectAll();

        } else {
            //User on album fragment
            super.onBackPressed();
            onCancel();
        }
    }

    private boolean isImagesThumbnailShown() {
        return isFragmentShown(ImagesThumbnailFragment.TAG);
    }

    private boolean isImagesPagerShown() {
        return isFragmentShown(ImagesPagerFragment.TAG);
    }


    private boolean isFragmentShown(final String tag) {

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

        return fragment != null && fragment.isVisible();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }

    private void hideSelectAll() {
//        mSelectAllMenuItem.setVisible(false);

    }

    private void showSelectAll() {
      /*  if (mPickOptions.pickMode == Picker.PickMode.SINGLE_IMAGE) {
            //Keep it hidden
            return;
        }
        mSelectAllMenuItem.setVisible(true);*/

    }

    private void handleMultipleModeAddition(final ImageEntry imageEntry) {

        if (mPickOptions.pickMode != Picker.PickMode.MULTIPLE_IMAGES) {
            return;
        }

        if (sCheckedImages.size() < mPickOptions.limit || mPickOptions.limit == NO_LIMIT) {
            imageEntry.isPicked = true;
            sCheckedImages.add(imageEntry);
        } else {
            Snackbar.make(coordinatorLayout, "You cannot select more than "+ mPickOptions.limit  +" images", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            Toast.makeText(this, R.string.you_cant_check_more_images, Toast.LENGTH_SHORT).show();
            Log.i("onPickImage", "You can't check more images");
        }

    }

    private boolean shouldShowDeselectAll() {

        if (mSelectedAlbum == null) {
            return false;
        }

        boolean isAllImagesSelected = true;
        for (final ImageEntry albumChildImage : mSelectedAlbum.imageList) {

            if (!sCheckedImages.contains(albumChildImage)) {
                isAllImagesSelected = false;
                break;
            }
        }

        final Fragment imageThumbnailFragment = getSupportFragmentManager().findFragmentByTag(ImagesThumbnailFragment.TAG);

        return isAllImagesSelected && imageThumbnailFragment != null && imageThumbnailFragment.isVisible();
    }

    private void handleToolbarVisibility(final boolean show) {

        final AppBarLayout appBarLayout = (AppBarLayout) mToolbar.getParent();
        final CoordinatorLayout rootLayout = (CoordinatorLayout) appBarLayout.getParent();

        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (show) {
            //Show appBar
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(rootLayout, appBarLayout, null, 0, 1, new int[2]);

        } else {
            //Hide appBar
            behavior.onNestedFling(rootLayout, appBarLayout, null, 0, 10000, true);
        }

    }


    public void onEvent(final Events.OnClickAlbumEvent albumEvent) {
        mSelectedAlbum = albumEvent.albumEntry;


        final ImagesThumbnailFragment imagesThumbnailFragment;


        if (getSupportFragmentManager().findFragmentByTag(ImagesThumbnailFragment.TAG) != null) {
            imagesThumbnailFragment = (ImagesThumbnailFragment) getSupportFragmentManager().findFragmentByTag(ImagesThumbnailFragment.TAG);
        } else {
            imagesThumbnailFragment = new ImagesThumbnailFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, imagesThumbnailFragment, ImagesThumbnailFragment.TAG)
                .addToBackStack(ImagesThumbnailFragment.TAG)
                .commit();

        getSupportActionBar().setTitle(albumEvent.albumEntry.name);
        mShouldShowUp = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(mShouldShowUp);
        showSelectAll();

        if (shouldShowDeselectAll()) {
            showDeselectAll();
        } else {
            hideDeselectAll();
        }


    }


    public void onEvent(final Events.OnPickImageEvent pickImageEvent) {
        if (mPickOptions.videosEnabled && mPickOptions.videoLengthLimit > 0 && pickImageEvent.imageEntry.isVideo) {
            // Check to see if the selected video is too long in length
            final MediaPlayer mp = MediaPlayer.create(this, Uri.parse(pickImageEvent.imageEntry.path));
            final int duration = mp.getDuration();
            mp.release();
            if (duration > (mPickOptions.videoLengthLimit)) {
                Toast.makeText(this, getResources().getString(R.string.video_too_long).replace("$", String.valueOf(mPickOptions.videoLengthLimit / 1000)), Toast.LENGTH_SHORT).show();
                return; // Don't allow selection
            }
        }

        if (mPickOptions.pickMode == Picker.PickMode.MULTIPLE_IMAGES) {
            handleMultipleModeAddition(pickImageEvent.imageEntry);


        } else if (mPickOptions.pickMode == Picker.PickMode.SINGLE_IMAGE) {
            //Single image pick mode


            final ImagesPagerFragment pagerFragment;

            if (getSupportFragmentManager().findFragmentByTag(ImagesPagerFragment.TAG) != null) {

                pagerFragment = (ImagesPagerFragment) getSupportFragmentManager().findFragmentByTag(ImagesPagerFragment.TAG);
            } else {
                pagerFragment = new ImagesPagerFragment();
            }


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, pagerFragment, ImagesPagerFragment.TAG)
                    .addToBackStack(ImagesPagerFragment.TAG)
                    .commit();


        }


        updateFab();
        updateCount();

    }

    private void updateCount() {
        tvImageCount.setText(sCheckedImages != null ? sCheckedImages.size() + "" : "0");
    }


    public void onEvent(final Events.OnUnpickImageEvent unpickImageEvent) {
        sCheckedImages.remove(unpickImageEvent.imageEntry);
        unpickImageEvent.imageEntry.isPicked = false;

        updateCount();
        updateFab();
        hideDeselectAll();
    }

    public void onEvent(final Events.OnChangingDisplayedImageEvent newImageEvent) {
        mCurrentlyDisplayedImage = newImageEvent.currentImage;

    }

    public void onEvent(final Events.OnShowingToolbarEvent showingToolbarEvent) {
        handleToolbarVisibility(true);
    }


    public void onEvent(final Events.OnHidingToolbarEvent hidingToolbarEvent) {
        handleToolbarVisibility(false);
    }


    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = createDirectory();


//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private File createDirectory() {
        File directory1 = Environment.getExternalStorageDirectory();
        String appName = "Jugnoo";
        String appDirectory = directory1.getAbsolutePath() + File.separator + appName;
        File fileAppDirectory = new File(appDirectory);
        if (!fileAppDirectory.exists()) {
            fileAppDirectory.mkdir();
        }

        String appTypeDirectory = fileAppDirectory.getAbsolutePath() + File.separator  + Environment.DIRECTORY_PICTURES;
        File finalDirectory = new File(appTypeDirectory);
        if (!finalDirectory.exists()) {
            finalDirectory.mkdir();
        }

        if (finalDirectory == null) {
            throw new RuntimeException("Couldn\'t initialize External Storage Path");
        } else {
            return finalDirectory;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(this, "Camera is not accessible", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }


    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}
