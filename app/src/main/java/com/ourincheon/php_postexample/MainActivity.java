package com.ourincheon.php_postexample;

import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
    public class MainActivity extends TabActivity {
        // 전역변수를 선언한다
        TabHost mTabHost = null;
        String myId, myPWord, myTitle, mySubject, myResult;
        HttpPostData httpPostData = new HttpPostData();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mTabHost = getTabHost();          // Tab 만들기
            mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator("서버로 전송").setContent(R.id.page01));
            mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator("서버에서 받음").setContent(R.id.page02));
            findViewById(R.id.button_submit).setOnClickListener(buttonClick);
        }

        //------------------------------
        //    button Click
        //------------------------------
        Button.OnClickListener buttonClick = new Button.OnClickListener() {
            public void onClick(View v) {
                // 사용자가 입력한 내용을 전역변수에 저장한다
                myId = ((EditText)(findViewById(R.id.edit_Id))).getText().toString();
                myPWord = ((EditText)(findViewById(R.id.edit_pword))).getText().toString();
                myTitle = ((EditText)(findViewById(R.id.edit_title))).getText().toString();
                mySubject = ((EditText)(findViewById(R.id.edit_subject))).getText().toString();
                   // 서버와 자료 주고받기
                httpPostData.execute();
            }
        };

        //------------------------------
        //   Http Post로 주고 받기
        //------------------------------
        public class HttpPostData extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL("http://mobuyapp.dothome.co.kr/test.html");       // URL 설정
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------
                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    http.setRequestMethod("POST");         // 전송 방식은 POST

                    // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("id").append("=").append(myId).append("&");                 // php 변수에 값 대입
                    buffer.append("pword").append("=").append(myPWord).append("&");   // php 변수 앞에 '$' 붙이지 않는다
                    buffer.append("title").append("=").append(myTitle).append("&");           // 변수 구분은 '&' 사용
                    buffer.append("subject").append("=").append(mySubject);

                    OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();
                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    myResult = builder.toString();                       // 전송결과를 전역 변수에 저장

                    Toast.makeText(MainActivity.this, "전송 후 결과 받음", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                                    // try
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ((TextView)(findViewById(R.id.text_result))).setText(myResult);
            }
        } // HttpPostData


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
