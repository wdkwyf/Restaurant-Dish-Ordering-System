package com.example.wuyufei.restaurant;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyufei.restaurant.beans.TableInfo;
import com.example.wuyufei.restaurant.countdown.CountdownDemo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    private Gson gson;
    private Context context;
    private SharedPreferences pref;
    private RequestQueue mRequestQueue;
    private SharedPreferences.Editor editor;
    private static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.choose_you_like);
        context = this;
        gson = new GsonBuilder().
                excludeFieldsWithoutExposeAnnotation().
                setPrettyPrinting().
                serializeNulls().
                create();
        mRequestQueue = Volley.newRequestQueue(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.set_table:
                        new MaterialDialog.Builder(context)
                                .title(R.string.set_table)
                                .content(R.string.set_table_input_content1)
                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                .positiveText(R.string.next)
                                .input(null, "0", new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                        final String table_id = input.toString();
                                        new MaterialDialog.Builder(context)
                                                .title(R.string.set_table)
                                                .content(R.string.set_table_input_content2)
                                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                                .input(null, "1", new MaterialDialog.InputCallback() {
                                                    @Override
                                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                                        int table_nums = Integer.parseInt(input.toString());
                                                        editor.putString("table_id", table_id);
                                                        editor.putInt("customer_nums", table_nums);
                                                        editor.commit();
                                                        TableInfo tableInfo = new TableInfo(table_id, table_nums);
                                                        String json = gson.toJson(tableInfo);
                                                        Log.d(TAG, "onInput: " + json);
                                                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                                                entity.URL_TABLE_SET, getJsonObject(json), new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) throws JSONException {
                                                                Log.d(TAG, "onResponse: response ok set table");
                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                VolleyLog.e("Error: ", error.getMessage());

                                                            }
                                                        });
                                                        mRequestQueue.add(request);
                                                    }
                                                })
                                                .show();

                                    }
                                })
                                .show();
                        break;
                    case R.id.call_finish:
                        String table_id = pref.getString("table_id", "000");
                        String json = "{\"tableId\":\"" + table_id + "\"}";
                        Log.d(TAG, "onOptionsItemSelected: call finish" + json);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                entity.URL_WAITER_RESPONSE, getJsonObject(json), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) throws JSONException {
                                Log.d(TAG, "onResponse: response ok");
                                new MaterialDialog.Builder(context).title(R.string.call_response_finish)
                                        .positiveText(R.string.confirm)
                                        .show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());

                            }
                        });
                        mRequestQueue.add(request);
                        break;
                }
                return false;

            }
        });
        setSupportActionBar(toolbar);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this).
                add(R.string.tab_meat, TabFragment.class)
                .add(R.string.tab_veg, TabFragment.class)
                .add(R.string.tab_staple, TabFragment.class)
                .add(R.string.tab_drinks, TabFragment.class).create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


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
        switch (id) {
            case R.id.action_show_time:
                String waitingTime = pref.getString("waiting_time", "0");
                Log.d(TAG, "onOptionsItemSelected: " + waitingTime);
                Intent intent = new Intent(MainActivity.this, CountdownDemo.class);
                startActivity(intent);
                break;
            case R.id.action_call_waiter:
                String table_id = pref.getString("table_id", "000");
                String json = "{\"tableId\":\"" + table_id + "\"}";

                Log.d(TAG, "onOptionsItemSelected: " + json);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        entity.URL_TABLE_SET, getJsonObject(json), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        Log.d(TAG, "onResponse: response ok");
                        //get response
                        new MaterialDialog.Builder(context).title(R.string.call_response)
                                .positiveText(R.string.confirm)
                                .show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());

                    }
                });
                mRequestQueue.add(request);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    JSONObject getJsonObject(String json) {
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;

    }

    void sendTableId(String url) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.wuyufei.restaurant/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.wuyufei.restaurant/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
