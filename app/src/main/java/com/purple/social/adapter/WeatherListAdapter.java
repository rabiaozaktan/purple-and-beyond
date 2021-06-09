package com.purple.social.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.purple.social.R;
import com.purple.social.model.Weather;
import com.purple.social.net.Utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class WeatherListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Activity context;
    private List<Weather> myList;
    private String urlSting;

    public WeatherListAdapter(Activity context, List<Weather> myList) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myList = myList;
        this.context = context;
    }

    public List<Weather> getMyList() {
        return myList;
    }

    private void updateResults(List<Weather> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View lineView = layoutInflater.inflate(R.layout.list_view_weather, null);

        ProgressBar pb_1 = lineView.findViewById(R.id.pb_1);
        SVGImageView svg_iv_ico = lineView.findViewById(R.id.svg_iv_ico);
        TextView tv_date = lineView.findViewById(R.id.tv_date);
        TextView tv_description = lineView.findViewById(R.id.tv_description);
        TextView tv_degree = lineView.findViewById(R.id.tv_degree);
        TextView tv_min = lineView.findViewById(R.id.tv_min);
        TextView tv_max = lineView.findViewById(R.id.tv_max);
        TextView tv_night = lineView.findViewById(R.id.tv_night);

        tv_date.setText(myList.get(position).getDate() + " " + myList.get(position).getDay());
        tv_description.setText(myList.get(position).getDescription());
        tv_degree.setText(myList.get(position).getDegree());
        tv_min.setText(myList.get(position).getMin());
        tv_max.setText(myList.get(position).getMax());
        tv_night.setText(myList.get(position).getNight());

        SvgSave svgSave = new SvgSave(context, myList.get(position), svg_iv_ico,pb_1);
        svgSave.execute("");

        return lineView;
    }

    class SvgSave extends AsyncTask<String, Integer, File> {
        private Context context;
        private String uzanti;
        private Weather weather;
        private SVGImageView svg_iv_ico;
        private ProgressBar pb_1;

        public SvgSave(Context context, Weather weather, SVGImageView svg_iv_ico,ProgressBar pb_1) {
            this.context = context;
            this.weather = weather;
            this.svg_iv_ico = svg_iv_ico;
            this.pb_1 = pb_1;
            pb_1.setVisibility(View.VISIBLE);
            svg_iv_ico.setVisibility(View.GONE);
        }

        @Override
        protected File doInBackground(String... params) {
            File dowlandFile = null;
            String fileName = "";
            try {
                File f = new File(context.getExternalCacheDir(), "icons/");
                //Eğer dosya yoksa oluştur
                if (!f.exists()) {
                    f.mkdirs();
                }

                if (weather.getIcon() != null && !weather.getIcon().equals("")) {

                    int i = weather.getIcon().lastIndexOf(".");
                    int l = weather.getIcon().lastIndexOf("/");
                    fileName = weather.getIcon().substring(l + 1, i);
                    uzanti = weather.getIcon().substring(i, weather.getIcon().length());

                    dowlandFile = new File(context.getExternalCacheDir(),
                            "icons/" + fileName + uzanti);

                    if (!dowlandFile.exists()) {
                        int count;
                        Utility.getInstance().log(weather);
                        URL url = new URL(weather.getIcon());

                        URLConnection conection = url.openConnection();
                        conection.connect();

                        int lenghtOfFile = conection.getContentLength();
                        InputStream input = new BufferedInputStream(url.openStream(), 8192);

                        FileOutputStream outStream;
                        outStream = new FileOutputStream(dowlandFile);

                        byte[] data = new byte[4096];
                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;

                            publishProgress((int) ((total * 100) / lenghtOfFile));

                            outStream.write(data, 0, count);
                        }
                        outStream.flush();
                        outStream.close();
                        input.close();
                    } else {
                        publishProgress(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                dowlandFile = new File(context.getExternalCacheDir(),
                        "icons/" + fileName + uzanti);

                if (dowlandFile.exists()) {
                    boolean delete = dowlandFile.delete();
                    Log.d("Silindi", "" + delete);
                }
            }
            return dowlandFile;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final File dowlandFile) {
            super.onPostExecute(dowlandFile);
            if (dowlandFile != null && dowlandFile.exists()) {
                pb_1.setVisibility(View.GONE);
                svg_iv_ico.setVisibility(View.VISIBLE);
                svg_iv_ico.setImageURI(Uri.fromFile(dowlandFile));
            }
        }
    }
}
