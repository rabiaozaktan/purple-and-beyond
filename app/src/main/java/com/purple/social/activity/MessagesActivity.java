package com.purple.social.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.purple.social.R;
import com.purple.social.adapter.MesagesRecyclerViewAdapter;
import com.purple.social.data.Dao;
import com.purple.social.fragment.WeatherForecastFragment;
import com.purple.social.model.Message;
import com.purple.social.net.Constants;
import com.purple.social.net.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessagesActivity extends AppCompatActivity {

    Context mContext;
    String city;
    int chat = 0;
    private List<Message> messageList;

    RecyclerView rv_search;
    GridLayoutManager gridLayoutManager;
    MesagesRecyclerViewAdapter mesagesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        mContext = this;
        city = WeatherForecastFragment.city;

        ImageButton btn_send_message = findViewById(R.id.btn_send_message);
        TextInputEditText et_message = findViewById(R.id.et_message);
        ProgressBar iv_main_progress = findViewById(R.id.iv_main_progress);
        rv_search = findViewById(R.id.rv_search);
        AutoCompleteTextView at_chat = findViewById(R.id.at_chat);

        String[] chat_list = Utility.getInstance().getArray(mContext, R.array.chat_list);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, chat_list);
        at_chat.setAdapter(adapter);

        messageList = new ArrayList<>();

        int limit = Constants.LIMIT;

        gridLayoutManager = new GridLayoutManager(mContext, 1);
        mesagesRecyclerViewAdapter = new MesagesRecyclerViewAdapter(mContext, messageList, getSupportFragmentManager());
        rv_search.setAdapter(mesagesRecyclerViewAdapter);
        rv_search.setLayoutManager(gridLayoutManager);

        at_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chat = position;
                getMessage();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            at_chat.setText("All", false);
        } else {
            at_chat.setText("All");
        }

        getMessage();

        btn_send_message.setEnabled(false);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(Constants.TAG_INFO, s + " " + start + " " + before + " " + count);
                if (s.length() > 0) {
                    btn_send_message.setEnabled(true);
                } else {
                    btn_send_message.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(Constants.TAG_INFO, s.toString() + " -- ");
            }
        });

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String key = Dao.getInstance(mContext).getmDatabase().getReference("Messages").push().getKey();

                Message message = new Message();
                message.setMessage(Objects.requireNonNull(et_message.getText()).toString());
                message.setSendUser(Dao.currentUser.getUser_id());
                message.setLocation(city);
                message.setId(key);
                et_message.setText("");

                Dao.getInstance(mContext).getMessages().document(key).set(message);
            }
        });
    }

    private void getMessage() {
        Query query;
        if (chat == 0) {
            query = Dao.getInstance(mContext).getMessages();
        } else {
            query = Dao.getInstance(mContext).getMessages().whereEqualTo("location", city);
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                messageList.clear();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    Message message = documentSnapshot.toObject(Message.class);
                    if (message.getSendUser().equals(Dao.currentUser.getUser_id())) {
                        message.setTypeView(1);
                    } else {
                        message.setTypeView(2);
                    }
                    messageList.add(message);
                }
                mesagesRecyclerViewAdapter.notifyDataSetChanged();
                rv_search.scrollToPosition(mesagesRecyclerViewAdapter.getItemCount() - 1);
            }
        });
    }
}