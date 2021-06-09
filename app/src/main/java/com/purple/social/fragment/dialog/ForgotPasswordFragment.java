package com.purple.social.fragment.dialog;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.purple.social.R;
import com.purple.social.data.Dao;
import com.purple.social.net.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends DialogFragment {


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        final Context mContext = view.getContext();
        final EditText et_email = view.findViewById(R.id.et_email);
        final Button btn_login = view.findViewById(R.id.btn_login);
        final LinearLayout ll_loading = view.findViewById(R.id.ll_loading);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.getInstance().checkConnection(mContext)) {

                    String email = et_email.getText().toString();

                    if (!email.equals("")) {
                        btn_login.setVisibility(View.GONE);
                        ll_loading.setVisibility(View.VISIBLE);
                        Dao.getInstance(mContext).getmAuth().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.reset_success), Toast.LENGTH_SHORT).show();
                                btn_login.setVisibility(View.VISIBLE);
                                ll_loading.setVisibility(View.GONE);
                                ForgotPasswordFragment.this.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.reset_fail), Toast.LENGTH_SHORT).show();
                                btn_login.setVisibility(View.VISIBLE);
                                ll_loading.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.empty_email), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.net_fall), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

}
