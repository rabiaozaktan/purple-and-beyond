package com.purple.social.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.purple.social.R;
import com.purple.social.adapter.MainViewPagerFragmentAdapter;
import com.purple.social.data.Dao;
import com.purple.social.fragment.WeatherForecastFragment;
import com.purple.social.model.User;
import com.purple.social.net.Utility;
import com.tompee.funtablayout.FunTabLayout;
import com.tompee.funtablayout.SimpleTabAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.vp_main)
    ViewPager vp_main;
    @BindView(R.id.tablayout)
    FunTabLayout tablayout;
    @BindView(R.id.iv_exit)
    ImageView iv_exit;
    @BindView(R.id.iv_message)
    ImageView iv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        login();

        iv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dao.getInstance(mContext).getmAuth().signOut();
                Dao.currentUser = null;

                FirebaseMessaging.getInstance().setAutoInitEnabled(false);

                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
            }
        });

        iv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (WeatherForecastFragment.city != null) {
                    Intent intent = new Intent(mContext, MessagesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Lütfen konumun bulunmasını bekleyiniz.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void login() {

        FirebaseUser currentUser = Dao.getInstance(mContext).getmAuth().getCurrentUser();

        if (currentUser == null) {
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        } else {

            Dao.getInstance(mContext).getUser().document(Dao.getInstance(mContext).getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.user_entry_success), Toast.LENGTH_LONG).show();
                    Dao.currentUser = documentSnapshot.toObject(User.class);

                    MainViewPagerFragmentAdapter viewFragStatePagerAdapter = new MainViewPagerFragmentAdapter(getSupportFragmentManager());
                    vp_main.setAdapter(viewFragStatePagerAdapter);

                    tablayout.setTabVisibleCount(2);
                    SimpleTabAdapter.Builder builder = new SimpleTabAdapter.Builder(mContext).
                            setViewPager(vp_main).
                            setTabTextAppearance(R.style.SimpleTabText).
                            setTabBackgroundResId(R.drawable.ripple).
                            setTabIndicatorColor(Utility.getInstance().getColor(mContext, R.color.colorAccent)).
                            setTabSelectedTextColor(Utility.getInstance().getColor(mContext, R.color.colorAccent)).
                            setTabPadding(8, 8, 8, 8).
                            setTabIndicatorHeight(5);
                    tablayout.setUpWithAdapter(builder.build());

                    getToken();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Dao.currentUser = null;
                    Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.bir_hata_olustu), Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            });
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);

                Map<String, Object> map = new HashMap<>();
                map.put("token", token);

                Dao.getInstance(mContext).getUser().document(Dao.getInstance(mContext).getUserId()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("TAG_INFO", "Token kaydı başarılı.");
                    }
                });

            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("PUBLIC")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("TAG_INFO", "Genel konuşma başlığına abone olundu.");
                        }
                    }
                });
    }
}