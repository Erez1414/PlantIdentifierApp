package com.example.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantidentifier.NetworkUtils.SearchConnection;
import com.example.plantidentifier.dataUtils.FlowerInfoPage;

import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SearchConnection> {

    private EditText mEt;

    private Button mBtn;
    private static final String TAG = "SearchConnectionDebug";
    private TextView mTv;
    private JSONObject mCurData;
    private static final int SEARCH_LOADER_ID = 1;
    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEt = (EditText) findViewById(R.id.et_search_activity);

        mBtn = (Button) findViewById(R.id.btn_search_activity);

        mTv = (TextView) findViewById(R.id.tv_result_activity_search);

        firstTime = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    public void onAbout(MenuItem menuItem){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onContactUs(MenuItem menuItem){
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void onClickSearch(View v) {
        mCurData = null;
        if(mEt.getText().toString().matches("")){
            Toast.makeText(this, R.string.err_name, Toast.LENGTH_LONG).show();
            return;
        }
        LoaderManager.LoaderCallbacks<SearchConnection> callback = SearchActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(SEARCH_LOADER_ID, bundleForLoader, callback);
    }


    @Override
    public Loader<SearchConnection> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<SearchConnection>(this) {

            SearchConnection searchConnection = null;

            @Override
            protected void onStartLoading() {
                String name = mEt.getText().toString();
                searchConnection = new SearchConnection(name);
                forceLoad();
            }

            @Override
            public SearchConnection loadInBackground() {
                searchConnection.connect();
                while(!searchConnection.isFinished()){}
                return searchConnection;
            }

            public void deliverResult(SearchConnection data) {
                searchConnection = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<SearchConnection> loader, SearchConnection data) {
        Log.d(TAG, "onLoadFinished!!!!!!!!!!!!!!!! "+data.getRes() + " !!!!!!!!!!!!!!!!!!!");
        if (null == data || !data.getSuccess()) {
            Toast.makeText(this, getString(R.string.err_server), Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        } else {
            mCurData = data.getRes();
            if(mCurData.length() == 0){
                if(firstTime) {
                    Toast.makeText(this, getString(R.string.err_no_flower), Toast.LENGTH_LONG).show();
                    firstTime = false;
                    return;
                }
                else {
                    firstTime = true;
                    onLoaderReset(loader);
                    return;
                }
            }
            Intent intent = new Intent(this, FlowerInfoPage.class);
            intent.putExtra("INFO", mCurData.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<SearchConnection> loader) {
        loader.startLoading();
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }
}