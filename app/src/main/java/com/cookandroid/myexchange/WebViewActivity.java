package com.cookandroid.myexchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends Activity {

    WebView webView;
    TextView commu1, commu2, commu3;
    ListViewAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        Intent intent = getIntent();
        String countryName = intent.getStringExtra("countryName");
        final String[] usa={"미여디", "드래블", "미준모"};
        final String[] europ={"유랑", "체크인유럽", "여기유럽"};
        final String[] vietnam={"푸꾸옥", "베나자", "아이러브 베트남"};
        final String[] asia={"고고아시아", "네일동", "투차"};
        listView=(ListView)findViewById(R.id.listViewMENU);
        adapter=new ListViewAdapter();


        if(countryName.equals("usa")){

            int icons[]={R.drawable.usa1, R.drawable.usa2, R.drawable.usa3};
            adapter = new ListViewAdapter();

            for(int i =0;i<3;i++){
                adapter.addItem(this.getResources().getDrawable(icons[i]),usa[i]);
            }


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                Uri uri;
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            uri= Uri.parse("https://cafe.naver.com/nyctourdesign");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 1:
                            uri= Uri.parse("https://cafe.naver.com/drivetravel");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 2:
                            uri= Uri.parse("https://cafe.naver.com/gototheusa");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                    }
                }
            });

        }
        else if(countryName.equals("europ")){

            int icons[]={R.drawable.europe1, R.drawable.europe2, R.drawable.europe3};
            adapter = new ListViewAdapter();

            for(int i =0;i<3;i++){
                adapter.addItem(this.getResources().getDrawable(icons[i]),europ[i]);
            }


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                Uri uri;
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            uri= Uri.parse("https://cafe.naver.com/firenze");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 1:
                            uri= Uri.parse("https://cafe.naver.com/momsolleh");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 2:
                            uri= Uri.parse("https://cafe.naver.com/moomge");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                    }
                }
            });

        }
        else if(countryName.equals("vietnam")){

            int icons[]={R.drawable.vietnam1, R.drawable.vietnam2, R.drawable.vietnam3};
            adapter = new ListViewAdapter();

            for(int i =0;i<3;i++){
                adapter.addItem(this.getResources().getDrawable(icons[i]),vietnam[i]);
            }


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                Uri uri;
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            uri= Uri.parse("https://cafe.naver.com/minecraftpe");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 1:
                            uri= Uri.parse("https://cafe.naver.com/mindy7857");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 2:
                            uri= Uri.parse("https://cafe.naver.com/xxdkdk");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                    }
                }
            });

        }
        else if(countryName.equals("asia")){

            int icons[]={R.drawable.asia1, R.drawable.asia2, R.drawable.asia3};
            adapter = new ListViewAdapter();

            for(int i =0;i<3;i++){
                adapter.addItem(this.getResources().getDrawable(icons[i]),asia[i]);
            }


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                Uri uri;
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            uri= Uri.parse("https://cafe.naver.com/yabamcafe2");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 1:
                            uri= Uri.parse("https://cafe.naver.com/jpnstory");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                        case 2:
                            uri= Uri.parse("https://cafe.naver.com/chtour");
                            intent = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            break;
                    }
                }
            });

        }


    }
/*
    class CookWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<CountryItem> listViewItemList = new ArrayList<CountryItem>() ;

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.textViewX) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            CountryItem listViewItem = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            iconImageView.setImageDrawable(listViewItem.getIcon());
            titleTextView.setText(listViewItem.getName());

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(Drawable icon, String title) {
            CountryItem item = new CountryItem();

            item.setIcon(icon);
            item.setName(title);

            listViewItemList.add(item);
        }
    }

    */

}