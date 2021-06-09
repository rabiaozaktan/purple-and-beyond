package com.purple.social.data;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.purple.social.api.retrofit.Api;
import com.purple.social.api.retrofit.ApiClient;
import com.purple.social.model.User;


public class Dao {

    private static Dao uniqueInstance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mFirestore;
    private final FirebaseDatabase mDatabase;
    private final StorageReference storageReference;
    private final Api api;
    public static User currentUser;

    public static Dao getInstance(Context context) {

        if (uniqueInstance == null) {
            uniqueInstance = new Dao(context);
        }
        return uniqueInstance;
    }

    private Dao(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        api = ApiClient.getClient(context).create(Api.class);
        currentUser = null;
    }

    public String getUserId() {
        FirebaseUser firebaseUser = this.getmAuth().getCurrentUser();
        if (firebaseUser != null) {
            if (!firebaseUser.getUid().equals("")) {
                return firebaseUser.getUid();
            }
        }
        return null;
    }

    public Api getApi() {
        return api;
    }

    public CollectionReference getUser() {
        return getmFirestore().collection("Users");
    }

    public CollectionReference getMessages() {
        return getmFirestore().collection("Messages");
    }

    //------------------------------

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseFirestore getmFirestore() {
        return mFirestore;
    }

    public FirebaseDatabase getmDatabase() {
        return mDatabase;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }
}









