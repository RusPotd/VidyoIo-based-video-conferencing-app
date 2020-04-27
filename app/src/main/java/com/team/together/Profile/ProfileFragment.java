package com.team.together.Profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team.together.Models.UserDetailsList;
import com.team.together.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    private String userID;
    private Uri mSelectedImage;


    private CircleImageView mProfilePhoto;
    private TextView mUsername, mAbout;

    private ImageView mUsernameEdit, mAboutEdit, mNewProfileImg;

    private UserProfileModel mUserProfileModel;
    private MediatorLiveData<UserDetailsList> mNewUserListMediatorLiveData;
    private LiveData<DataSnapshot> mDataSnapshotLiveData;


    public ProfileFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_img_frag);
        mUsername = (TextView) view.findViewById(R.id.username_);
        mAbout = (TextView) view.findViewById(R.id.about_);
        mNewProfileImg = (ImageView) view.findViewById(R.id.camera);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserProfileModel = ViewModelProviders.of(getActivity()).get(UserProfileModel.class);
        mDataSnapshotLiveData = mUserProfileModel.getDataSnapshotLiveData();
        mNewUserListMediatorLiveData = new MediatorLiveData<>();


        mDataSnapshotLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserDetailsList newUserList = dataSnapshot.getValue(UserDetailsList.class);

                    String url = newUserList.getProfile_img();


                    mUsername.setText(newUserList.getUsername());
                    mAbout.setText(newUserList.getAbout());


                    Glide.with(getActivity()).load(url).centerCrop().placeholder(R.mipmap.user).into(mProfilePhoto);


                } else {
                    mNewUserListMediatorLiveData.setValue(null);
                }

            }
        });
    }
}
