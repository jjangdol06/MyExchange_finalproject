package com.cookandroid.myexchange;

import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SecondActivity extends TabActivity implements OnMapReadyCallback { //TabActivity를 상속받는다.

    GoogleMap gMap;
    MapFragment mapFrag;
    GroundOverlayOptions videoMark;
    String tag = "country_alert";
    EditText search_country;
    Button search_country_button;
    TextView single_result;

    ListView listview;
    ListViewAdapter adapter;
    String strText;

    Spinner countrylist1, countrylist2;
    TextView iso1, iso2;
    EditText money1, money2;
    Button cal;

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    String tag2="SQLite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        TabHost tabHost = getTabHost();

        //TabSpec은 LinearLayout의 갯수만큼 필요하다.
        TabHost.TabSpec tabSmap = tabHost.newTabSpec("지도").setIndicator("지도");
        tabSmap.setContent(R.id.map);
        tabHost.addTab(tabSmap);
        //구글맵
        mapFrag=(MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); //mapAsync: 서버를 가져올 때 사용 (UI 가져오면서 뒤에서 MAP을 준비함)

        TabHost.TabSpec tabSexchangerate = tabHost.newTabSpec("환율").setIndicator("환율");
        tabSexchangerate.setContent(R.id.exchange);
        tabHost.addTab(tabSexchangerate);

        //리스트뷰(커뮤니티)
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
        registerForContextMenu(listview);


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                CountryItem item=(CountryItem)parent.getItemAtPosition(position);
                strText=item.getName();

                final String countryName;

                return false;

            }
        });


        TabHost.TabSpec tabSalert = tabHost.newTabSpec("여행 경보").setIndicator("여행 경보");
        tabSalert.setContent(R.id.Alert);
        tabHost.addTab(tabSalert);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.

       //여행경보_tabSalert
        search_country=(EditText)findViewById(R.id.search_country); //치안 정보가 궁금한 나라 입력
        search_country_button=(Button)findViewById(R.id.search_country_button); // 입력 버튼
        single_result=(TextView)findViewById(R.id.alert_result); //결과를 나타내는 textview

        //입력버튼에 리스너를 단다.
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

                Cursor cursor;
                String[] strC;
                Integer[] intR;
                String temp;
                int c=0;
                int num1, num2;
                sqlDB=myHelper.getReadableDatabase();
                cursor = sqlDB.rawQuery("SELECT currency, rate FROM exchangeTBL WHERE country='"+countrylist1.getSelectedItem().toString()+"' OR country='"
                        +countrylist2.getSelectedItem().toString()+"';", null);
                strC=new String[cursor.getCount()];
                intR=new Integer[cursor.getCount()];
                while (cursor.moveToNext()){
                    strC[c]=cursor.getString(0);
                    intR[c]=cursor.getInt(1);
                    c++;
                }
                if(!countrylist1.getSelectedItem().toString().equals(strC[0])){
                    iso1.setText(strC[1]);
                    iso2.setText(strC[0]);
                }else {
                    iso1.setText(strC[0]);
                    iso2.setText(strC[1]);
                }
                num1 = Integer.parseInt(money1.getText().toString());
                num2 = ((int) (num1 / intR[1] * intR[0]));
                money2.setText(Integer.toString(num2));

                cursor.close();
                sqlDB.close();
            }
        });

    } //onCreate

    //listview

    //컨텍스트 메뉴 달기
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mInflater = getMenuInflater();
        if(strText.equals("미국||USA")) {
            mInflater.inflate(R.menu.commu_list, menu);
        }
        else if(strText.equals("유럽||EUROP")){
            mInflater.inflate(R.menu.commu_list_eu, menu);
        }
        else if(strText.equals("베트남||VIETNAM")){
            mInflater.inflate(R.menu.commu_list_viet, menu);
        }
        else if(strText.equals("아시아||ASIA")){
            mInflater.inflate(R.menu.commu_list_asia, menu);
        }
    }

    //메뉴 선택시 상태변화
    public boolean onContextItemSelected(MenuItem item){
        Uri uri;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.commu1: //미여디
                uri= Uri.parse("https://cafe.naver.com/nyctourdesign");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu2:
                uri= Uri.parse("https://cafe.naver.com/drivetravel");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu3:
                uri= Uri.parse("https://cafe.naver.com/gototheusa");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu4: //유랑
                uri= Uri.parse("https://cafe.naver.com/firenze");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu5:
                uri= Uri.parse("https://cafe.naver.com/momsolleh");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu6:
                uri= Uri.parse("https://cafe.naver.com/moomge");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu7: //베트남
                uri= Uri.parse("https://cafe.naver.com/minecraftpe");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu8:
                uri= Uri.parse("https://cafe.naver.com/mindy7857");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu9:
                uri= Uri.parse("https://cafe.naver.com/xxdkdk");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu10: //아시아
                uri= Uri.parse("https://cafe.naver.com/yabamcafe2");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu11:
                uri= Uri.parse("https://cafe.naver.com/jpnstory");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.commu12:
                uri= Uri.parse("https://cafe.naver.com/chtour");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
        }
        return false;
    }

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

    //118~238 tabSalert에 관련된 api 호출
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
                boolean num=false, deal=false;
                Double mid,mid2,fin;
                String m2;
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
                        if (tag_name.equals("CUR_NM"))
                            num = true;
                        if (tag_name.equals("DEAL_BAS_R"))
                            deal = true;
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
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.651683,127.016171), 15)); //xml에서 초기값을 설정한 것과 동일한 효과
        gMap.getUiSettings().setZoomControlsEnabled(true);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) { //클릭하는 위도, 경도 값을 전달받는다.
                videoMark = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)).position(latLng,100f,100f);
                gMap.addGroundOverlay(videoMark);
                View dialogView=(View)View.inflate(SecondActivity.this, R.layout.map_dialog, null);
                AlertDialog.Builder dlg=new AlertDialog.Builder(SecondActivity.this);
                dlg.setTitle("나라 정보");
                dlg.setIcon(R.drawable.ic_beach_access_black_24dp);
                dlg.setView(dialogView); //이미 앞에 view가 있는데 또 view를 show 하게 된다.
                /*
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvName.setText(dlgEdtName.getText().toString());
                        tvEmail.setText(dlgEdtEmail.getText().toString());
                    }
                });*/
                dlg.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();//dialog를 좀더 안전하게 종료 dialoginterface는 메소드에서 전닫받은 변수
                    }
                });
                dlg.show(); //두번 눌렀을 때 오류가 난다. removeview를 통해서 다시 view를 만들어야된다.
            }
        });
    }
}