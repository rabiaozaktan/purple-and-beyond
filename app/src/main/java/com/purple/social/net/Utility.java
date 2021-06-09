package com.purple.social.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private static Utility uniqueInstance;
    public Gson gson;

    public static Utility getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Utility();
        }
        return uniqueInstance;
    }

    private Utility() {
        gson = new Gson();
    }

    @SuppressLint("MissingPermission")
    public boolean checkConnection(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected();
    }

    public String getString(Context context, int value) {
        return context.getResources().getString(value);
    }

    public String[] getArray(Context context, int value) {
        return context.getResources().getStringArray(value);
    }

    public Drawable getImage(Context context, int value) {
        return context.getResources().getDrawable(value);
    }

    public int getColor(Context context, int value) {
        return context.getResources().getColor(value);
    }

    public void log(String text) {
        Log.e("APP_STRING", text);
    }

    public void log(Object o) {
        Gson gson = new Gson();
        Log.e("APP_JSON", gson.toJson(o));
    }

    public String json(Object o) {
        return gson.toJson(o);
    }

    public int booleanToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public int dpToPx(int dp, Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public boolean intToBoolean(int i) {
        return i == 1;
    }


    public String getTwoSteps(int value) {
        String s = String.valueOf(value);

        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    public void getAnimate(View view, Techniques techniques, int duration, int repeat) {
        YoYo.with(techniques)
                .duration(duration)
                .repeat(repeat)
                .playOn(view);
    }


    public String readJsonFile(Context context, int drawableId) {

        try {

            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(drawableId)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }

            return jsonBuilder.toString();

        } catch (FileNotFoundException e) {
            Log.e("jsonFile", "file not found");
        } catch (IOException e) {
            Log.e("jsonFile", "ioerror");
        }

        return null;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public Date stringToDate(String date) {
        if (date == null)
            return null;
        try {
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return df1.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("SimpleDateFormat")
    public String stringToString(String sDate) {
        if (sDate == null)
            return "";
        try {
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            Date date = df1.parse(sDate);
            if (date != null) {
                return df2.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context);
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        result = address.getAdminArea().toLowerCase();
                    }
                } catch (Exception e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    Bundle bundle = new Bundle();
                    bundle.putString("city", result);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

}
