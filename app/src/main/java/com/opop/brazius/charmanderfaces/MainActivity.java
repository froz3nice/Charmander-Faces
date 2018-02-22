package com.opop.brazius.charmanderfaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewSwitcher;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FloatingActionMenu menuRed;
    private FloatingActionButton programFab1;
    private FloatingActionButton programFab2;
    private FloatingActionButton programFab3;
    private ViewPager viewPager;

    private Context context;

    public Integer[] mImages = new Integer[] {
            R.drawable.a,
            R.drawable.b,
            R.drawable.ba,
            R.drawable.bb,
            R.drawable.c,
            R.drawable.dd,
            R.drawable.e,
            R.drawable.ee,
            R.drawable.f,
            R.drawable.g,
            R.drawable.gg,
            R.drawable.h,
            R.drawable.hh,
            R.drawable.i,
            R.drawable.j,
            R.drawable.jj,
            R.drawable.l,
            R.drawable.ma,
            R.drawable.n,
            R.drawable.o,
            R.drawable.p,
            R.drawable.pi,
            R.drawable.s,
            R.drawable.w,
            R.drawable.ww,
            R.drawable.x,
            R.drawable.xx,
            R.drawable.y,
            R.drawable.z
    };
    public Integer[] mSounds = new Integer[] {
            R.raw.one,
            R.raw.du,
            R.raw.tres,
            R.raw.chetyri,
            R.raw.fun,
            R.raw.sesi,
            R.raw.sem,
            R.raw.ocho,
    };
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        MobileAds.initialize(this, getString(R.string.app_id));

        viewPager = findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                Random rnd = new Random();
                int index = rnd.nextInt(7);
                final MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, mSounds[index]);
                mPlayer.start();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mPlayer.release();
                    }
                }, 2000);

            }
        });
        menuRed =findViewById(R.id.menu_red);
        programFab1 = new FloatingActionButton(context);
        programFab2 = new FloatingActionButton(context);
        setUpFab(android.R.drawable.ic_menu_gallery,"set as wallpaper",programFab1);
        setUpFab(android.R.drawable.btn_star,"send as sticker",programFab2);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            programFab3 = new FloatingActionButton(context);
            setUpFab(android.R.drawable.ic_lock_idle_lock,"set as lock screen wallpaper",programFab3);
            setLockScreenWpListener();
        }
        menuRed.setClosedOnTouchOutside(true);
        setShareListener();
        setWallpaperListener();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.bringToFront();
    }

    private void setLockScreenWpListener() {
        programFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLockScrWpConfirmationDialog().show();
            }
        });
    }

    private void setWallpaperListener() {
        programFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWpConfirmationDialog().show();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @SuppressLint("NewApi")
    private AlertDialog getLockScrWpConfirmationDialog(){

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to set this image as a lock screen wallpaper? (/) (°,,°) (/)");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            WallpaperManager.getInstance(MainActivity.this).setResource(mImages[viewPager.getCurrentItem()],WallpaperManager.FLAG_LOCK);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder1.create();
    }

    private AlertDialog getWpConfirmationDialog(){

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to set this image as a wallpaper? ( ͡° ͜ʖ ͡°)");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       setAsWallpaper();
                       dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder1.create();
    }

    private void setAsWallpaper() {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        try {
            myWallpaperManager.setResource(mImages[viewPager.getCurrentItem()]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setShareListener() {
        programFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    }else{
                        shareImage();
                    }
                }else {
                    shareImage();
                }
            }
        });
    }

    private void shareImage() {
        Drawable mDrawable = ContextCompat.getDrawable(context,mImages[viewPager.getCurrentItem()]);
        Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        String name = getResources().getResourceEntryName(mImages[viewPager.getCurrentItem()])+".png";
        saveImage(mBitmap,name);

        String path = Environment.getExternalStorageDirectory().toString()+"/Charmander_images/"+name;
        File file = new File(path);
        Uri uri = Uri.fromFile(file);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }


    private void saveImage(Bitmap finalBitmap,String name) {
        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/Charmander_images/"+name);
        if(!myFile.exists()){
            File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Charmander_images");
            myDir.mkdirs();
            File file = new File (myDir+"/"+name);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    try {
                        Drawable mDrawable = ContextCompat.getDrawable(context,mImages[viewPager.getCurrentItem()]);
                        Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
                        String name = getResources().getResourceEntryName(mImages[viewPager.getCurrentItem()])+".png";
                        saveImage(mBitmap,name);

                        String path = Environment.getExternalStorageDirectory().toString()+"/Charmander_images/"+name;
                        File file = new File(path);
                        Uri uri = Uri.fromFile(file);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/png");
                        startActivity(Intent.createChooser(shareIntent, "Share Image"));
                    }catch (NullPointerException ex){
                        ex.printStackTrace();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setUpFab(int res,String text,FloatingActionButton programFab1){
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText(text);
        programFab1.setImageResource(res);
        menuRed.addMenuButton(programFab1);
    }


    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container,final int position) {
            final Context context = MainActivity.this;
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.getAdjustViewBounds();
            imageView.setImageResource(mImages[position]);
            (container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((ImageView) object);
        }

    }
}
