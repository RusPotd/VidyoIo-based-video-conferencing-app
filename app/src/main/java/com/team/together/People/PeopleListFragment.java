package com.team.together.People;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team.together.Models.PeopleList;
import com.team.together.R;
import com.team.together.Utils.PeopleListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PeopleListFragment extends Fragment {

    private static final String TAG = "Peoples fragment";

    private ListView lstNames;
    private  FirebaseFirestore mFirebaseFirestore;

    private List<PeopleList> mUserList;
    private PeopleListAdapter mAdapter;


    public PeopleListFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peoplr_list, container, false);

        lstNames = (ListView) view.findViewById(R.id.lstNames);

        mAdapter = new PeopleListAdapter(getActivity(), R.layout.layout_peoples_listitem, getContactNames());
        lstNames.setAdapter(mAdapter);

//        FirebaseFirestore cloudstorage = FirebaseFirestore.getInstance();
//        CollectionReference citiesRef = mFirebaseFirestore.collection("users");
//
//        citiesRef.whereIn("phone_number", Arrays.asList(getContactNames()));
//
//
//        Toast.makeText(getActivity(), "match: "+citiesRef, Toast.LENGTH_SHORT).show();

        return view;
    }

    private List<PeopleList> getContactNames() {
        List<PeopleList> list = new ArrayList<>();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);


                    while (cursorInfo.moveToNext()) {

                        PeopleList peopleList = new PeopleList();

                        peopleList.setPhone_number(cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        list.add(peopleList);
                        String name = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //contacts.add(name);

                    }
                    cursorInfo.close();
                }
            }
            cursor.close();
        }

        return list;
    }

}
