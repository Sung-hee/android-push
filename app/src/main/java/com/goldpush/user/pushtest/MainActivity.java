package com.goldpush.user.pushtest;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout = null;
    private TextView mTextViewResult;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("noticeMsg");

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        mQueue = Volley.newRequestQueue(this);

        jsonParse();
    }
    private void scrollBottom(TextView mTextViewResult) {
        int lineTop =  mTextViewResult.getLayout().getLineTop(mTextViewResult.getLineCount()) ;
        int scrollY = lineTop - mTextViewResult.getHeight();
        if (scrollY > 0) {
            mTextViewResult.scrollTo(0, scrollY);
        } else {
            mTextViewResult.scrollTo(0, 0);
        }
    }
    private void jsonParse(){
        String url = "http://61.72.187.6/phps/readingRecode";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("push");

                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject push = jsonArray.getJSONObject(i);

                                String reading = push.getString("reading");

                                mTextViewResult.append(reading + "\n\n");
                                scrollBottom(mTextViewResult);
                                refreshLayout.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();;
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonParse();
            }
        });
        mQueue.add(request);
    }
}
