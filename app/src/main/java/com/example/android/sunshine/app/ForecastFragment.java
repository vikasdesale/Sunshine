package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A Forecast fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER = 0;
    private ForecastAdapter mForecastAdapter;

    public ForecastFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView.
         mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

}