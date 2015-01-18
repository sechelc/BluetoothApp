package com.example.android.bluetoothmanager;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.android.bluetoothchat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class StatusReadingsActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_readings);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status_readings, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            String[] countries = new String[] {
            "India",
            "Pakistan",
            "Sri Lanka",
            "China",
            "Bangladesh",
            "Nepal",
            "Afghanistan",
            "North Korea",
            "South Korea",
            "Japan"
           };
            String[] currency = new String[]{
                    "Indian Rupee",
                    "Pakistani Rupee",
                    "Sri Lankan Rupee",
                    "Renminbi",
                    "Bangladeshi Taka",
                    "Nepalese Rupee",
                    "Afghani",
                    "North Korean Won",
                    "South Korean Won",
                    "Japanese Yen"
            };
            View rootView = inflater.inflate(R.layout.fragment_status_readings, container, false);
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            for(int i=0;i<10;i++){
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("keys", "Country : " + countries[i]);
                hm.put("values","Currency : " + currency[i]);
                aList.add(hm);
            }

            // Keys used in Hashmap
            String[] from = { "keys","values"};
            int[] to = { R.id.keys,R.id.values};
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), aList, R.layout.readings_list_layout, from, to);


            ListView viewById = (ListView) rootView.findViewById(R.id.list_view_readings);
            viewById.setAdapter(adapter);
            return rootView;
        }
    }
}
