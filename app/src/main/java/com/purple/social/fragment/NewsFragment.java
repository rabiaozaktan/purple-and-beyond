package com.purple.social.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.purple.social.R;
import com.purple.social.adapter.NewsListAdapter;
import com.purple.social.api.models.ApiResponse;
import com.purple.social.data.Dao;
import com.purple.social.model.News;
import com.purple.social.net.Utility;
import com.purple.social.promise.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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
    @BindView(R.id.lv_news)
    ListView lv_news;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srl_main;
    @BindView(R.id.at_topic)
    AutoCompleteTextView at_topic;
    @BindView(R.id.at_lang)
    AutoCompleteTextView at_lang;

    String country = "tr";
    String tag = "general";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mContext = view.getContext();
        ButterKnife.bind(this, view);

        String lang = Utility.getInstance().readJsonFile(mContext, R.raw.lang);
        String topic = Utility.getInstance().readJsonFile(mContext, R.raw.topic);

        List<Map<String, Object>> langList = new Gson().fromJson(
                lang, new TypeToken<List<HashMap<String, Object>>>() {
                }.getType()
        );
        List<Map<String, Object>> topicList = new Gson().fromJson(
                topic, new TypeToken<List<HashMap<String, Object>>>() {
                }.getType()
        );

        List<String> langs = new ArrayList<>();
        for (Map<String, Object> map : langList) {

            String key = (String) map.get("key");
            String text = (String) map.get("text");
            langs.add(text);
        }

        List<String> topics = new ArrayList<>();
        for (Map<String, Object> map : topicList) {

            String key = (String) map.get("key");
            String text = (String) map.get("text");
            topics.add(text);
        }

        at_lang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                for (int i = 0; i < langList.size(); i++) {
                    String s = langs.get(i);
                    if (s.equals(selection)) {
                        country = (String) langList.get(i).get("key");
                        getUpdate();
                        break;
                    }
                }
            }
        });

        at_topic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                for (int i = 0; i < topicList.size(); i++) {
                    String s = topics.get(i);
                    if (s.equals(selection)) {
                        tag = (String) topicList.get(i).get("key");
                        getUpdate();
                        break;
                    }
                }
            }
        });

        ArrayAdapter<String> adapterLang = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, langs);
        at_lang.setAdapter(adapterLang);
        at_lang.setText(langs.get(0), false);

        ArrayAdapter<String> adapterTopic = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, topics);
        at_topic.setAdapter(adapterTopic);
        at_topic.setText(topics.get(0), false);

        srl_main.setOnRefreshListener(this);
        srl_main.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        srl_main.post(new Runnable() {
            @Override
            public void run() {
                getUpdate();
            }
        });

        return view;
    }

    private void getUpdate() {
        Utility.getInstance().log(country);
        Utility.getInstance().log(tag);
        srl_main.setRefreshing(true);
        Call<ApiResponse<List<News>>> call = Dao.getInstance(mContext).getApi().getNews("application/json", Utility.getInstance().getString(mContext, R.string.api_key), country, tag);
        Promise<List<News>> promise = new Promise<List<News>>().promise(call);

        promise.onSuccess(new Promise.MyConsumer<ApiResponse<List<News>>>() {
            @Override
            public void accept(ApiResponse<List<News>> value) {
                srl_main.setRefreshing(false);

                NewsListAdapter hikmetListAdapter = new NewsListAdapter(getActivity(), value.getResult() == null ? new ArrayList<>() : value.getResult());
                lv_news.setAdapter(hikmetListAdapter);
            }
        }).call(mContext);
    }

    @Override
    public void onRefresh() {
        getUpdate();
    }
}