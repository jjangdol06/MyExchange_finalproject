package com.cookandroid.myexchange;

import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    Spinner countrylist1, countrylist2;

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
        String[] CountryList = {
                "대한민국", "남아프리카 공화국", "네팔", "노르웨이", "뉴질랜드", "대만", "덴마크", "러시아", "마카오",
                "말레이시아", "멕시코", "몽골", "미국", "바레인", "방글라데시", "베트남", "브라질", "브루나이", "사우디아라비아",
                "스웨덴", "스위스", "싱가포르", "아랍에미리트", "영국", "오만", "요르단", "유럽연합", "이스라엘","이집트", "인도",
                "인도네시아", "일본", "중국", "체코", "칠레", "카자흐스탄", "카타르", "캐나다", "쿠웨이트", "태국", "터키", "파키스탄",
                "폴란드", "필리핀", "헝가리", "호주", "홍콩"};
        countrylist1 = (Spinner)findViewById(R.id.country1);
        countrylist2 = (Spinner)findViewById(R.id.country2);
        ArrayAdapter<String> cadapter = new ArrayAdapter<String>(this, R.layout.country_dropdown, CountryList);
        countrylist1.setAdapter(cadapter);
        countrylist1.setSelection(0);
        countrylist2.setAdapter(cadapter);

    } //onCreate

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