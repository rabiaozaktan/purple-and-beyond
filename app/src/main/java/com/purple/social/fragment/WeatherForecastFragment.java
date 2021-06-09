package com.purple.social.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.purple.social.R;
import com.purple.social.adapter.WeatherListAdapter;
import com.purple.social.api.models.ApiResponse;
import com.purple.social.data.Dao;
import com.purple.social.model.Weather;
import com.purple.social.net.MyLocation;
import com.purple.social.net.Utility;
import com.purple.social.promise.Promise;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String city = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherForecastFragment newInstance(String param1, String param2) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Context mContext;
    @BindView(R.id.lv_weather)
    ListView lv_weather;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srl_main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        mContext = view.getContext();
        ButterKnife.bind(this, view);

        srl_main.setOnRefreshListener(this);
        srl_main.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        srl_main.post(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        });

        return view;
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    private void startLocation() {
        srl_main.setRefreshing(true);

        MyLocation myLocation = new MyLocation();

        myLocation.getLocation(mContext, new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        Bundle bundle = msg.getData();
                        WeatherForecastFragment.city = bundle.getString("city");
                        String city = bundle.getString("city");

                        Log.e("TAG", city != null ? city : "");
                        if (city != null) {
                            Call<ApiResponse<List<Weather>>> call = Dao.getInstance(mContext).getApi().getWeather("application/json",
                                    Utility.getInstance().getString(mContext, R.string.api_key), "en", city);

                            Promise<List<Weather>> promise = new Promise<List<Weather>>().promise(call);
                            promise.onSuccess(new Promise.MyConsumer<ApiResponse<List<Weather>>>() {
                                @Override
                                public void accept(ApiResponse<List<Weather>> value) {
                                    srl_main.setRefreshing(false);
                                    Utility.getInstance().log(value);
                                    WeatherListAdapter hikmetListAdapter = new WeatherListAdapter(getActivity(), value.getResult());
                                    lv_weather.setAdapter(hikmetListAdapter);
                                }
                            }).onFail(value -> {
                                srl_main.setRefreshing(false);
                                Log.e("TAG", value.getLocalizedMessage());
                            }).call(mContext);
                        }
                        return false;
                    }
                });

                Utility.getAddressFromLocation(location.getLatitude(), location.getLongitude(), mContext, handler);
                myLocation.cancelLM();
            }
        });
    }

    @Override
    public void onRefresh() {
        getLocation();
    }
}