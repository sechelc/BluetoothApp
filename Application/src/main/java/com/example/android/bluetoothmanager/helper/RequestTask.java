package com.example.android.bluetoothmanager.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.bluetoothmanager.database.LogsDAO;
import com.example.android.bluetoothmanager.model.Entry;
import com.example.android.bluetoothmanager.model.LogEntryProtocol;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sechelc on 01.02.2015.
 */
public class RequestTask extends AsyncTask<Entry, Void, Long> {
    public AsyncResponse delegate=null;

    public RequestTask(AsyncResponse delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    protected Long doInBackground(Entry... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            HttpPost httpPost = new HttpPost("http://ibbmanager-xteg.rhcloud.com/addLog");
            httpPost.setHeader("Content-Type","application/x-protobuf");
            Entry entry = uri[0];
            LogEntryProtocol.LogEntry build = Utils.build(entry);
            httpPost.setEntity(new ByteArrayEntity(build.toByteArray()));
            response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
                return entry.getId();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                return null;
            }
        } catch (IOException e) {
            Log.e("httpclient","o puscat", e);
            return null;
        }

    }

    @Override
    protected void onPostExecute(Long result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }


}
