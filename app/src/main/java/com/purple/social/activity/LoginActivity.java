package com.purple.social.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.purple.social.R;
import com.purple.social.data.Dao;
import com.purple.social.fragment.dialog.ForgotPasswordFragment;
import com.purple.social.net.Utility;

public class LoginActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;

        final LinearLayout ll_loading = findViewById(R.id.ll_loading);
        final Button btn_login = findViewById(R.id.btn_login);
        final EditText et_email = findViewById(R.id.et_email);
        final EditText et_password = findViewById(R.id.et_password);
        TextView tv_forgot_password = findViewById(R.id.tv_forgot_password);
        TextView tv_register = findViewById(R.id.tv_register);

        et_password.setTransformationMethod(new PasswordTransformationMethod()); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("ForgotPasswordFragment");

                ForgotPasswordFragment fragment = new ForgotPasswordFragment();
                if (prev != null) {
                    transaction.remove(prev);
                }
                transaction.addToBackStack(null);
                fragment.show(transaction, "ForgotPasswordFragment");
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.getInstance().checkConnection(mContext)) {
                    String email = et_email.getText().toString();
                    String password = et_password.getText().toString();

                    String isEmpty = getEmpty(email, password);
                    if (isEmpty == null) {
                        btn_login.setVisibility(View.GONE);
                        ll_loading.setVisibility(View.VISIBLE);

                        Dao.getInstance(mContext).getmAuth().signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                //Login giriş işlemleri yapılacak

                                btn_login.setVisibility(View.VISIBLE);
                                ll_loading.setVisibility(View.GONE);

                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("OnFailure", e.getMessage());

                                if (e.getMessage() != null) {
                                    if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_no_user), Toast.LENGTH_SHORT).show();
                                    } else if (e.getMessage().equals("The email address is badly formatted.")) {
                                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_bad_format), Toast.LENGTH_SHORT).show();
                                    } else if (e.getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]")) {
                                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_is_invalid), Toast.LENGTH_LONG).show();
                                    } else if (e.getMessage().equals("The password is invalid or the user does not have a password.")) {
                                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email__no_user_password), Toast.LENGTH_LONG).show();
                                    }
                                }
                                btn_login.setVisibility(View.VISIBLE);
                                ll_loading.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        Toast.makeText(mContext, isEmpty, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.net_fall), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getEmpty(String email, String password) {

        if (email.equals("")) {
            getAnimate(findViewById(R.id.tl_email));
            return Utility.getInstance().getString(mContext, R.string.empty_email);

        } else if (password.equals("")) {
            getAnimate(findViewById(R.id.tl_password));
            return Utility.getInstance().getString(mContext, R.string.empty_password);
        }
        return null;
    }

    private void getAnimate(final View view) {
            YoYo.with(Techniques. Tada)
                    .duration(1000)
                    .repeat(0)
                    .playOn(view);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
        builder
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(Utility.getInstance().getString(mContext, R.string.app_name))
                .setMessage(Utility.getInstance().getString(mContext, R.string.dilalog_exit));

        builder.setCancelable(false)
                .setPositiveButton(Utility.getInstance().getString(mContext, R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
                .setNegativeButton(Utility.getInstance().getString(mContext, R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();

    }
}