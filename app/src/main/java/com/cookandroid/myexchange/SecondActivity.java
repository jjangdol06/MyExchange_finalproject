package com.cookandroid.myexchange;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ClipData;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.Inflater;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SecondActivity extends TabActivity implements OnMapReadyCallback { //TabActivity를 상속받는다.

    GoogleMap gMap;
    MapFragment mapFrag;
    GroundOverlayOptions videoMark;

    ListView listview;
    ListViewAdapter adapter;
    String tag = "country_alert";
    EditText search_country;
    Button search_country_button;
    TextView single_result;
    Spinner countrylist1, countrylist2;
    String strText;

    TextView iso1, iso2;
    EditText money1, money2;
    Button cal;

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    String tag2="SQLite";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        TabHost tabHost = getTabHost();

        //TabSpec은 LinearLayout의 갯수만큼 필요하다.
        TabHost.TabSpec tabSmap = tabHost.newTabSpec("지도").setIndicator("지도");
        tabSmap.setContent(R.id.map);
        tabHost.addTab(tabSmap);

        TabHost.TabSpec tabSexchangerate = tabHost.newTabSpec("환율").setIndicator("환율");
        tabSexchangerate.setContent(R.id.exchange);
        tabHost.addTab(tabSexchangerate);

        TabHost.TabSpec tabScommunity = tabHost.newTabSpec("커뮤니티").setIndicator("커뮤니티");
        tabScommunity.setContent(R.id.webcafe);
        tabHost.addTab(tabScommunity);
        listview = (ListView)findViewById(R.id.listview);
        String names[]={"미국||USA","유럽||EUROP","베트남||VIETNAM","아시아||ASIA" };
        int icons[]={R.drawable.usa, R.drawable.europe, R.drawable.vietnam, R.drawable.asia};
        adapter = new ListViewAdapter();

        for(int i =0;i<4;i++){
            adapter.addItem(this.getResources().getDrawable(icons[i]),names[i]);
        }


        listview.setAdapter(adapter);
        //registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CountryItem item=(CountryItem)parent.getItemAtPosition(position);
                strText=item.getName();
                final String countryName;
                //MenuInflater mInflater = getMenuInflater();
                if(strText.equals("미국||USA")) {
                    countryName="usa";
                    Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
                    intent.putExtra("countryName",countryName);
                    startActivity(intent);
                    //mInflater.inflate(R.menu.commu_list, menu);
                }
                else if(strText.equals("유럽||EUROP")){
                    countryName="europ";
                    Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
                    intent.putExtra("countryName",countryName);
                    startActivity(intent);
                }
                else if(strText.equals("베트남||VIETNAM")){
                    countryName="vietnam";
                    Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
                    intent.putExtra("countryName",countryName);
                    startActivity(intent);
                }
                else if(strText.equals("아시아||ASIA")){
                    countryName="asia";
                    Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
                    intent.putExtra("countryName",countryName);
                    startActivity(intent);
                }

            }


        });


        TabHost.TabSpec tabSalert = tabHost.newTabSpec("여행 경보").setIndicator("여행 경보");
        tabSalert.setContent(R.id.Alert);
        tabHost.addTab(tabSalert);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.

        //구글맵
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); //mapAsync: 서버를 가져올 때 사용 (UI 가져오면서 뒤에서 MAP을 준비함)

//listview=(ListView)findViewById(R.id.listView);
        search_country=(EditText)findViewById(R.id.search_country);
        search_country_button=(Button)findViewById(R.id.search_country_button);
        single_result=(TextView)findViewById(R.id.alert_result);

        search_country_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String srvUrl = " http://apis.data.go.kr/1262000/TravelWarningService/getTravelWarningList";
                String srvKey = "Nq%2Fww%2FKNMUR06%2Fp2Cd5qNy%2F%2F8ZYqHSMjyNlg%2Bre9%2FbHhdnk92m1Kc2%2B0LIWXxojEmFgJneihFcryruTf43cEfw%3D%3D";
                String strSrch = search_country.getText().toString();
                String strUrl = srvUrl + "?ServiceKey="+srvKey+"&countryName="+strSrch;

                new DownloadWebpageTask().execute(strUrl);
            }
        });



        //국가 선택에 대한 Spinner(드롭다운):자체 xml 파일 생성해줌(country_dropdown)
        String[] CountryList = {"아랍에미리트", "호주", "바레인","캐나다","스위스","중국"," 덴마크","유럽연합","영국","홍콩",
                "인도네시아","일본","한국","쿠웨이트","말레이시아","노르웨이","뉴질랜드","사우디아라비아","스웨덴","싱가포르","태국","미국"};
        countrylist1 = (Spinner)findViewById(R.id.country1);
        countrylist2 = (Spinner)findViewById(R.id.country2);
        ArrayAdapter<String> cadapter = new ArrayAdapter<String>(this, R.layout.country_dropdown, CountryList);
        countrylist1.setAdapter(cadapter);
        countrylist1.setSelection(12);
        countrylist2.setAdapter(cadapter);
        iso1=(TextView)findViewById(R.id.isocode1);
        iso2=(TextView)findViewById(R.id.isocode2);
        money1=(EditText)findViewById(R.id.money1);
        money2=(EditText)findViewById(R.id.money2);
        myHelper=new myDBHelper(this);
        cal=(Button)findViewById(R.id.calculatecurrency);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sql db 입력
                sqlDB=myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 1,2);
                sqlDB.close();

                Cursor cursor, cursor2;
                String str1="";
                String str2="";
                int intR1=0;
                int intR2=0;
                String temp;
                int num1, num2;
                sqlDB=myHelper.getReadableDatabase();
                cursor = sqlDB.rawQuery("SELECT currency, rate FROM exchangeTBL WHERE country='"+countrylist1.getSelectedItem().toString()+"';", null);
                cursor2 = sqlDB.rawQuery("SELECT currency, rate FROM exchangeTBL WHERE country='"+countrylist2.getSelectedItem().toString()+"';", null);

                while (cursor.moveToNext()){
                    str1=cursor.getString(0);
                    intR1=cursor.getInt(1);
                }
                while (cursor2.moveToNext()){
                    str2=cursor2.getString(0);
                    intR2=cursor2.getInt(1);
                }

                iso1.setText(str1);
                iso2.setText(str2);

                num1 = Integer.parseInt(money1.getText().toString());
                num2 = ((int) (num1 / intR2 * intR1));
                money2.setText(Integer.toString(num2));

                cursor.close();
                cursor2.close();
                sqlDB.close();
            }
        });



    } //onCreate

    //db를 위한 dbheloer
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "exchangeDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE exchangeTBL (currency CHAR(4) PRIMARY KEY, country CHAR(10), rate INTEGER);"); //SQL 명령어, gName이 기본키이므로 동일한 속성이 있으면 안된다. MySQL오류가 난다면 예외처리를 해주어야한다.
            db.execSQL("INSERT INTO exchangeTBL VALUES ('AED','아랍에미리트',322);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('AUD','호주',814);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('BHD','바레인','3,143');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('CAD','캐나다',885);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('CHF','스위스','1,184');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('CNH','중국',170);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('DKK','덴마크',177);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('EUR','유럽연합','1,326');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('GBP','영국','1,488');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('HKD','홍콩',151);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('JPY','일본','1,092');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('KRW','한국',1);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('KWD','쿠웨이트','3,899');");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('MYR','말레이시아',283);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('NOK','노르웨이',135);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('NZD','뉴질랜드',774);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('SAR','사우디아라비아',315);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('SGD','싱가포르',866);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('THB','태국',37);");
            db.execSQL("INSERT INTO exchangeTBL VALUES ('USD','미국',1185);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS exchangeTBL"); //기존에 있다면 지운다.
            onCreate(db); //지우고 생성한다.
        }
    }


    class CookWebViewClient extends WebViewClient{
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


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String)downloadUrl((String)urls[0]);
            } catch (IOException e) {
                return "==>다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            Log.d(tag, result);
            //tv.append(result + "\n");
            //tv.append("========== 파싱 결과 ==========\n");

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean a=false, aP=false, aN=false, c=false, cN=false, l=false, lP=false, lN=false;
                single_result.setText("");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if(eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("attention"))
                            a = true;
                        if (tag_name.equals("attentionPartial"))
                            aP= true;
                        if (tag_name.equals("attentionNote"))
                            aN = true;
                        if (tag_name.equals("control"))
                            c = true;
                        if (tag_name.equals("controlNote"))
                            cN = true;
                        if (tag_name.equals("limita"))
                            l = true;
                        if (tag_name.equals("limitaPartial"))
                            lP= true;
                        if (tag_name.equals("limitaNote"))
                            lN = true;
                    } else if(eventType == XmlPullParser.TEXT) {
                        if (a) {
                            //String content = xpp.getText();
                            single_result.setText(xpp.getText());
                            a = false;
                        }if (aP) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            aP = false;
                        }
                        if (aN) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            aN = false;
                        }
                        if (c) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            c = false;
                        }if (cN) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            cN = false;
                        }if (l) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            l = false;
                        }
                        if (l) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            lN = false;
                        }
                        if (lP) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            lP = false;
                        }
                    } else if(eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                } // while
            } catch (Exception e) {
                //tv.setText("\n"+e.getMessage());
            }
            if(single_result.getText().length() == 0){//빈값이 넘어올때의 처리
                single_result.setText("발령된 여행경보가 없습니다.");
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                Log.d(tag, "downloadUrl : "+  myurl);
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } catch(Exception e){
                return " ";
            }
            finally {
                conn.disconnect();
            }
        }
    }


    //구글맵
    @Override
    public void onMapReady(GoogleMap googleMap) { //MAP이 준비되면 Async가 자동으로 부름
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.651683,127.016171), 5)); //xml에서 초기값을 설정한 것과 동일한 효과
        gMap.getUiSettings().setZoomControlsEnabled(true);

        // marker 표시
        // market 의 위치, 타이틀, 짧은설명 추가 가능.
        MarkerOptions marker1 = new MarkerOptions();
        marker1 .position(new LatLng(37.923327, 105.244130))
                .title("국가명: 중국")
                .snippet("통화: 위안(CNH)");
        googleMap.addMarker(marker1).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker2 = new MarkerOptions();
        marker2 .position(new LatLng(37.651683,127.016171))
                .title("국가명: 한국")
                .snippet("통화: 원(KRW)");
        googleMap.addMarker(marker2).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker3 = new MarkerOptions();
        marker3 .position(new LatLng(41.047798, -109.535998))
                .title("국가명: 미국")
                .snippet("통화: 달러(USD)");
        googleMap.addMarker(marker3).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker4 = new MarkerOptions();
        marker4 .position(new LatLng(57.844322, -114.589504))
                .title("국가명: 캐나다")
                .snippet("통화: 달러(CAD)");
        googleMap.addMarker(marker4).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker5 = new MarkerOptions();
        marker5 .position(new LatLng(35.265910, 138.617868))
                .title("국가명: 일본")
                .snippet("통화: 엔(JPY)");
        googleMap.addMarker(marker5).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker6 = new MarkerOptions();
        marker6 .position(new LatLng(23.860027, 77.369613))
                .title("국가명: 인도")
                .snippet("통화: 루피(INR)");
        googleMap.addMarker(marker6).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker7 = new MarkerOptions();
        marker7 .position(new LatLng(22.295069,114.166866))
                .title("국가명: 홍콩")
                .snippet("통화: 달러(HKD)");
        googleMap.addMarker(marker7).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker8 = new MarkerOptions();
        marker8 .position(new LatLng(24.202164, 120.898560))
                .title("국가명: 대만")
                .snippet("통화: 달러(TWD)");
        googleMap.addMarker(marker8).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker9 = new MarkerOptions();
        marker9 .position(new LatLng(-6.100664, -52.402524))
                .title("국가명: 브라질")
                .snippet("통화: 레알(BRL)");
        googleMap.addMarker(marker9).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker10 = new MarkerOptions();
        marker10 .position(new LatLng(47.338787, 2.777564))
                .title("국가명: 유럽연합")
                .snippet("통화: 유로(EUR)");
        googleMap.addMarker(marker10).showInfoWindow(); // 마커추가,화면에출력

        MarkerOptions marker11 = new MarkerOptions();
        marker11 .position(new LatLng(51.697354, -1.102058))
                .title("국가명: 영국")
                .snippet("통화: 파운드(GBP)");
        googleMap.addMarker(marker11).showInfoWindow(); // 마커추가,화면에출력

    }


}