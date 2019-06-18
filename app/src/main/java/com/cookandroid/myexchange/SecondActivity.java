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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    //ListView listview;
    String tag = "country_alert";
    EditText search_country;
    Button search_country_button;
    TextView single_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        TabHost.TabSpec tabSalert = tabHost.newTabSpec("여행 경보").setIndicator("여행 경보");
        tabSalert.setContent(R.id.Alert);
        tabHost.addTab(tabSalert);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.

        //구글맵
        mapFrag=(MapFragment)getFragmentManager().findFragmentById(R.id.map);
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
    } //onCreate
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