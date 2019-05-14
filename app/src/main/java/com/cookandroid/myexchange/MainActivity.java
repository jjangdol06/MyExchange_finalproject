package com.cookandroid.myexchange;

import android.app.TabActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

public class MainActivity extends TabActivity { //TabActivity를 상속받는다.

    //Button btn1, btnGo, btnP;
    //EditText edtUrl;
    //WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        //TabSpec은 LinearLayout의 갯수만큼 필요하다.
        TabHost.TabSpec tabSsong = tabHost.newTabSpec("지도").setIndicator("지도");
        tabSsong.setContent(R.id.map);
        tabHost.addTab(tabSsong);

        TabHost.TabSpec tabSartist = tabHost.newTabSpec("환율").setIndicator("환율");
        tabSartist.setContent(R.id.exchange);
        tabHost.addTab(tabSartist);

        TabHost.TabSpec tabSalbum = tabHost.newTabSpec("커뮤니티").setIndicator("커뮤니티");
        tabSalbum.setContent(R.id.webcafe);
        tabHost.addTab(tabSalbum);

        TabHost.TabSpec tabSweb = tabHost.newTabSpec("마이페이지").setIndicator("마이페이지");
        tabSweb.setContent(R.id.myPage);
        tabHost.addTab(tabSweb);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.
    } //onCreate
}