package com.purple.social.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.purple.social.R;
import com.purple.social.data.Dao;
import com.purple.social.model.User;
import com.purple.social.net.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private Context mContext;
    private Uri selectedImageUri = null;
    private String selectedImageString;
    private boolean isHide = true;
    private boolean isMale;
    // UI
    private CircleImageView civ_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;

        final LinearLayout ll_loading = findViewById(R.id.ll_loading);
        civ_user_image = findViewById(R.id.civ_user_image);
        final Button btn_register = findViewById(R.id.btn_register);
        final EditText et_email = findViewById(R.id.et_email);
        final EditText et_password = findViewById(R.id.et_password);
        final EditText et_password_again = findViewById(R.id.et_password_again);
        final AutoCompleteTextView filled_exposed_dropdown = findViewById(R.id.filled_exposed_dropdown);

        String[] sexList = Utility.getInstance().getArray(mContext, R.array.sex_list);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, sexList);

        filled_exposed_dropdown.setAdapter(adapter);

        filled_exposed_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isMale = getSexValue(position);

            }
        });

        final EditText et_name = findViewById(R.id.et_name);
        final EditText et_surname = findViewById(R.id.et_surname);
        final EditText et_birth_date = findViewById(R.id.et_birth_date);

        final TextInputLayout tl_password_again = findViewById(R.id.tl_password_again);
        final TextInputLayout tl_password = findViewById(R.id.tl_password);

        et_password.setTransformationMethod(new PasswordTransformationMethod()); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.
        et_password_again.setTransformationMethod(new PasswordTransformationMethod()); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.

        tl_password_again.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide) {
                    isHide = false;
                    et_password.setTransformationMethod(null); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.
                    et_password_again.setTransformationMethod(null);
                } else {
                    isHide = true;
                    et_password.setTransformationMethod(new PasswordTransformationMethod()); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.
                    et_password_again.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        tl_password.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide) {
                    isHide = false;
                    et_password.setTransformationMethod(null); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.
                    et_password_again.setTransformationMethod(null);
                } else {
                    isHide = true;
                    et_password.setTransformationMethod(new PasswordTransformationMethod()); // Edittext'in inputype özelliği password olunca fontlar çalışmıyor.
                    et_password_again.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        setDateTextFormat(et_birth_date);

        civ_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(RegisterActivity.this);
            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.getInstance().checkConnection(mContext)) {

                    final String email = et_email.getText().toString();
                    String password = et_password.getText().toString();
                    String password_again = et_password_again.getText().toString();
                    final String name = et_name.getText().toString();
                    final String surname = et_surname.getText().toString();
                    final String birth_date = et_birth_date.getText().toString();

                    String isEmpty = getEmpty(email, password, password_again, name, surname, birth_date);

                    if (isEmpty == null) {
                        if (password.equals(password_again)) {
                            btn_register.setVisibility(View.GONE);
                            ll_loading.setVisibility(View.VISIBLE);

                            Dao.getInstance(mContext).getmAuth().createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    final String key = authResult.getUser().getUid();

                                    if (selectedImageUri != null) {
                                        final StorageReference userImage = Dao.getInstance(mContext).getStorageReference()
                                                .child("User").child(key + ".jpg");

                                        userImage.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                userImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        selectedImageString = uri.toString();
                                                        saveNewUser(email, name, surname, birth_date, selectedImageString, key, btn_register, ll_loading, isMale);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                btn_register.setVisibility(View.VISIBLE);
                                                ll_loading.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {
                                        saveNewUser(email, name, surname, birth_date, selectedImageString, key, btn_register, ll_loading, isMale);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("OnFailure", e.getMessage());


                                    if (e.getMessage() != null) {
                                        if (e.getMessage().equals("The email address is already in use by another account.")) {
                                            Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_already_use), Toast.LENGTH_SHORT).show();
                                        } else if (e.getMessage().equals("The email address is badly formatted.")) {
                                            Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_bad_format), Toast.LENGTH_SHORT).show();
                                        } else if (e.getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]")) {
                                            Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.email_is_invalid), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    btn_register.setVisibility(View.VISIBLE);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.pass_fail), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, isEmpty, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.net_fall), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private boolean getSexValue(int selectedItemPosition) {
        Log.e("TAG", "getSexValue: " + selectedItemPosition);
        return selectedItemPosition == 0;
    }

    private String getEmpty(String email, String password, String password_again, String name, String surname, String birth_date) {

        if (email.equals("") || !email.contains("@")) {
            getAnimate(findViewById(R.id.tl_email));
            return Utility.getInstance().getString(mContext, R.string.empty_email);
        } else if (password.equals("")) {
            getAnimate(findViewById(R.id.tl_password));
            return Utility.getInstance().getString(mContext, R.string.empty_password);
        } else if (password_again.equals("")) {
            getAnimate(findViewById(R.id.tl_password_again));
            return Utility.getInstance().getString(mContext, R.string.empty_password);
        } else if (name.equals("")) {
            getAnimate(findViewById(R.id.tl_name));
            return Utility.getInstance().getString(mContext, R.string.empty_name);
        } else if (surname.equals("")) {
            getAnimate(findViewById(R.id.tl_surname));
            return Utility.getInstance().getString(mContext, R.string.empty_surname);
        } else if (birth_date.equals("")) {
            getAnimate(findViewById(R.id.tl_birth_date));
            return Utility.getInstance().getString(mContext, R.string.empty_birthdate);
        }

        return null;
    }

    private void getAnimate(final View view) {
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .repeat(0)
                    .playOn(view);
    }

    private void saveNewUser(String email, String name, String surname, String birth_date, String selectedImageString, final String key, final Button btn_register, final LinearLayout ll_loading, boolean isMale) {

        String name_surname = name.toLowerCase() + " " + surname.toLowerCase();

        User newUser = new User();
        newUser.setUser_id(key);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setBirth_date(birth_date);
        newUser.setUser_image_url(selectedImageString);
        newUser.setMale(isMale);
        newUser.setName_surname(name_surname);

        Dao.getInstance(mContext).getUser().document(key).set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(mContext, Utility.getInstance().getString(mContext, R.string.register_success), Toast.LENGTH_SHORT).show();

                        btn_register.setVisibility(View.VISIBLE);
                        ll_loading.setVisibility(View.GONE);

                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                btn_register.setVisibility(View.VISIBLE);
                ll_loading.setVisibility(View.GONE);
            }
        });

    }

    private void setDateTextFormat(final EditText et_birth_date) {
        TextWatcher textWatcher = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ggaayyyy";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    et_birth_date.setText(current);
                    et_birth_date.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        et_birth_date.addTextChangedListener(textWatcher);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectedImageUri = result.getUri();
                civ_user_image.setImageURI(selectedImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                selectedImageUri = null;
            }
        }
    }
}