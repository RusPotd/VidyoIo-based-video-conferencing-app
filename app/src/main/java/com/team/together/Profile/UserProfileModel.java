package com.team.together.Profile;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team.together.Models.UserDetailsList;

public class UserProfileModel extends ViewModel {

    private static final DatabaseReference DATABASE_REFERENCE =
            FirebaseDatabase.getInstance().getReference("/users_details_list" ).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(DATABASE_REFERENCE);

    private final LiveData<UserDetailsList> listLiveData =
            Transformations.map(liveData, new Deserializer());

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }




    private class Deserializer implements Function<DataSnapshot, UserDetailsList> {
        @Override
        public UserDetailsList apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(UserDetailsList.class);
        }
    }

    @NonNull
    public LiveData<UserDetailsList> getLiveData() {
        return listLiveData;
    }

}
