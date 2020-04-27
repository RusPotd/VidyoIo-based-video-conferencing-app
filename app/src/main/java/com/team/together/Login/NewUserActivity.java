package com.team.together.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team.together.Home.HomeActivity;
import com.team.together.Models.FilePaths;
import com.team.together.Models.UserDetailsList;
import com.team.together.Models.UserList;
import com.team.together.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUserActivity extends AppCompatActivity {
    private Context mContext = NewUserActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference userRef;
    private String userID;
    private static int RESULT_LOAD_IMAGE = 1;


    private CircleImageView mProfile_img;
    private EditText mUsername;
    private ImageView mCamera;
    private Button mSaveProfile;
    private Uri mSelectedImage;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        setupUI(findViewById(R.id.new_user_activity));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        userRef = mFirebaseFirestore.collection("users");


        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }




        mProfile_img = (CircleImageView) findViewById(R.id.profile_image);
        mUsername = (EditText) findViewById(R.id.username);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        mUsername.setFilters(new InputFilter[] { filter });
        mCamera = (ImageView) findViewById(R.id.camera);
        mSaveProfile = (Button) findViewById(R.id.btn_save_profile);
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.custom);



        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String mUName = mUsername.getText().toString();

                if (mSelectedImage == null){

                    if (mUName.isEmpty()) {

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Enter Username", Snackbar.LENGTH_LONG);
                        snackbar.show();


                    }else {




                            mDialog.show();


                            myRef.child(mContext.getString(R.string.dbname_users_list))
                                    .child(userID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            UserList userList = dataSnapshot.getValue(UserList.class);

                                            String mMobileNo = userList.getPhone_number();

                                            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


                                            UserDetailsList userDetailsList = new UserDetailsList(user_id,getTimestamp(),mUName,"",mMobileNo,"");



                                            myRef.child(mContext.getString(R.string.dbname_new_users_list))
                                                    .child(user_id)
                                                    .setValue(userDetailsList);

                                            userRef.document(user_id).set(userDetailsList);



                                            Intent intent = new Intent(NewUserActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });


                    }

                }else {

                    if (mUName.isEmpty()) {

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Enter Username", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }else {

                        mDialog.show();


                            myRef.child(mContext.getString(R.string.dbname_users_list))
                                    .child(userID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            FilePaths filePaths = new FilePaths();

                                            UserList userList = dataSnapshot.getValue(UserList.class);

                                            final String mMobileNo = userList.getPhone_number();

                                            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            final StorageReference storageReference = mStorageReference
                                                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");


                                            try {
                                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedImage);
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                byte[] byteArray = stream.toByteArray();
                                                bitmap.recycle();

                                                UploadTask uploadTask = null;
                                                uploadTask = storageReference.putBytes(byteArray);

                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String fileUrl = uri.toString();


                                                                mDialog.dismiss();
                                                                UserDetailsList userDetailsList = new UserDetailsList(user_id,getTimestamp(),mUName,fileUrl,mMobileNo,"");


                                                                myRef.child(mContext.getString(R.string.dbname_new_users_list))
                                                                        .child(user_id)
                                                                        .setValue(userDetailsList);

                                                                userRef.document(user_id).set(userDetailsList);

                                                                //userRef.document("users").collection(user_id).add(userDetailsList);
                                                                //mFirebaseFirestore.collection("users").document(user_id).set(userDetailsList);

                                                                Intent intent = new Intent(NewUserActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



                                                            }
                                                        });


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        mDialog.dismiss();
                                                        Toast.makeText(mContext, "failed try again ", Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                            } catch (IOException e) {

                                                e.printStackTrace();

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                    }

                }
            }
        });


    }

    private String getTimestamp(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            mSelectedImage = data.getData();

            Glide.with(NewUserActivity.this)
                    .load(mSelectedImage)
                    .centerCrop()
                    .into(mProfile_img);

        }
    }



    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        }
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(NewUserActivity.this);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
