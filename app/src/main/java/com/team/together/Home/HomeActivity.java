package com.team.together.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.team.together.MainActivity;
import com.team.together.People.PeopleListFragment;
import com.team.together.Profile.ProfileFragment;
import com.team.together.R;
import com.team.together.vidyo.main_vid;


public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private BottomNavigationView mBottomNavigationView;
    private ImageView mDots;

    private HomeFragment mHomeFragment =  new HomeFragment(); //1
    private PeopleListFragment mPeopleListFragment = new PeopleListFragment();//2
    private ProfileFragment mProfileFragment = new ProfileFragment();//3
    private FragmentManager fm = getSupportFragmentManager();


    private Fragment mActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView2);
        mDots = (ImageView) findViewById(R.id.dots);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mActive = mHomeFragment;
                        fm.beginTransaction().add(R.id.main_container, mProfileFragment, "3").hide(mProfileFragment).commit();
                        fm.beginTransaction().add(R.id.main_container, mPeopleListFragment, "2").hide(mPeopleListFragment).commit();
                        fm.beginTransaction().add(R.id.main_container,mHomeFragment, "1").commit();


                        mBottomNavigationView.setOnNavigationItemSelectedListener(
                                new BottomNavigationView.OnNavigationItemSelectedListener() {
                                    @Override
                                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.action_home:
                                                /*Intent intent = new Intent(HomeActivity.this, main_vid.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();*/
                                                item.setChecked(true);
                                                mDots.setVisibility(View.GONE);
                                                fm.beginTransaction().hide(mActive).show(mHomeFragment).commit();
                                                mActive = mHomeFragment;
                                                break;


                                            case R.id.action_peoples:
                                                item.setChecked(true);
                                                mDots.setVisibility(View.GONE);
                                                fm.beginTransaction().hide(mActive).show(mPeopleListFragment).commit();
                                                mActive = mPeopleListFragment;
                                                break;


                                            case R.id.action_profile:
                                                item.setChecked(true);
                                                mDots.setVisibility(View.VISIBLE);
                                                fm.beginTransaction().hide(mActive).show(mProfileFragment).commit();
                                                mActive = mProfileFragment;
                                                break;
                                        }
                                        return false;
                                    }
                                });




                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();



        mDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, mDots);
                popup.getMenuInflater().inflate(R.menu.poupup_profile, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {


                            case R.id.sign_out:

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);

                                mBuilder.setTitle("Sign out!");

                                mBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {


                                        mAuth.signOut();
                                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        dialog.dismiss();
                                    }
                                });

                                mBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog mAlert = mBuilder.create();
                                mAlert.show();


                                break;


                        }

                        return true;
                    }
                });

                popup.show();
            }
        });


    }





    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Contact Permission");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }



    @Override
    public void onBackPressed() {
        if (mActive != mHomeFragment) {
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
            /*Intent intent = new Intent(HomeActivity.this, main_vid.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();*/
            mDots.setVisibility(View.GONE);
            fm.beginTransaction().hide(mActive).show(mHomeFragment).commit();
            mActive = mHomeFragment;

        }else {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            super.onBackPressed();
        }
    }


}
