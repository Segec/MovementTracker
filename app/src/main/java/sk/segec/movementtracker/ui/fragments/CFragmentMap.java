package sk.segec.movementtracker.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import sk.segec.movementtracker.R;
import sk.segec.movementtracker.ui.db.objects.CLocation;
import sk.segec.movementtracker.ui.enums.ELoggingType;
import sk.segec.movementtracker.ui.services.CServiceGetLocation;
import sk.segec.movementtracker.utils.CConstant;
import sk.segec.movementtracker.utils.CUtils;
import sk.segec.movementtracker.utils.CViewUtils;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CFragmentMap extends AFragmentDefault implements OnMapReadyCallback
{
    public static final String TAG = CFragmentMap.class.getSimpleName();
    private static final String TAG_MAP = "TAG_MAP";
    private Realm mRealm;
    private boolean mMaxPointsShow = false;

    private RealmResults<CLocation> mRealmLocations;
    private GoogleMap mGoogleMap;
    private Snackbar mSnackBar;
    private int mLineWidth;
    private int mLineColor;
    private ELoggingType mLoggingType;
    private SharedPreferences mSharedPrefs;
    private int mLoggingInterval;
    private boolean mFirstCameraMove = true;

    @Override
    int getFragmentLayout ()
    {
        return R.layout.fragment_map;
    }

    @BindView (R.id.fragment_map_layout)
    RelativeLayout mLayout;

    @BindView (R.id.fragment_map_floating_button_show_points)
    FloatingActionButton mButtonShowMaxPoints;

    @OnClick (R.id.fragment_map_button_start_tracking)
    public void startTracking ()
    {
        requestPermissionsAndStartTrackingService();
    }

    @OnClick (R.id.fragment_map_button_stop_tracking)
    public void stopTracking ()
    {
        stopTrackingService();
    }

    @OnClick (R.id.fragment_map_floating_button_show_points)
    public void showOrHidePoints ()
    {
        if (mMaxPointsShow)
        {
            mMaxPointsShow = false;
            mButtonShowMaxPoints.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_show_max_points));
        }
        else
        {
            mMaxPointsShow = true;
            mButtonShowMaxPoints.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_hide_max_points));
        }
        addMarkersToMap();
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mLineWidth = mSharedPrefs.getInt(CConstant.KEY_LINE_WIDTH, 1);
        mLineColor = mSharedPrefs.getInt(CConstant.KEY_LINE_COLOR, R.color.colorBlack);
        mLoggingType = ELoggingType.valueOf(mSharedPrefs.getString(CConstant.KEY_LOG_INTERVAL_TYPE, ELoggingType.MINUTES.name()));
        if (mLoggingType.equals(ELoggingType.MINUTES))
        {
            mLoggingInterval = mSharedPrefs.getInt(CConstant.KEY_LOG_INTERVAL, 1);
        }
        else
        {
            mLoggingInterval = mSharedPrefs.getInt(CConstant.KEY_LOG_INTERVAL, 59);
        }

        mRealm = Realm.getDefaultInstance();

        mCallBackActivity.setToolbarTitle(getString(R.string.toolbar_title_map));

        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);

        FragmentManager childFragmentManager = getChildFragmentManager();

        childFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_map_map, mapFragment, TAG_MAP)
                .commit();
        return view;
    }

    @Override
    public void onMapReady (GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
        mRealmLocations = mRealm.where(CLocation.class).findAll();

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick (Marker marker)
            {
                openBottomSheetDialog((Long) marker.getTag());
                return false;
            }
        });

        mRealmLocations.addChangeListener(new RealmChangeListener<RealmResults<CLocation>>()
        {
            @Override
            public void onChange (RealmResults<CLocation> locations)
            {
                addMarkersToMap();
            }
        });

        addMarkersToMap();
    }

    public synchronized void addMarkersToMap ()
    {
        if (mGoogleMap != null)
        {
            mGoogleMap.clear();

            PolylineOptions polylineOptions = new PolylineOptions();

            if (mRealmLocations != null && mRealmLocations.size() > 0)
            {
                for (int i = 0; i < mRealmLocations.size(); i++)
                {
                    CLocation location = mRealmLocations.get(i);
                    LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                    //We need to show all points
                    if (!mMaxPointsShow)
                    {
                        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(coordinates));
                        marker.setTag(location.getId());

                        //we want to drive lines between markers, so we wait, until be have second marker
                        polylineOptions.add(coordinates);
                        polylineOptions.width(mLineWidth);
                        polylineOptions.color(mLineColor);
                        //move camera to last logged position
                        if (mFirstCameraMove && (i == mRealmLocations.size() - 1))
                        {
                            mFirstCameraMove = false;
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18));
                        }
                        if (polylineOptions != null)
                        {
                            mGoogleMap.addPolyline(polylineOptions);
                        }
                    }
                    //We need to show max points - N,S,W,E
                    else
                    {
                        displayMinMaxPoints(i);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        mRealm.close();
    }

    public void openBottomSheetDialog (Long markerId)
    {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_location, null);
        TextView textCoordinates = (TextView) sheetView.findViewById(R.id.bottom_sheet_location_text_coordinates);
        TextView textSource = (TextView) sheetView.findViewById(R.id.bottom_sheet_location_text_source);
        TextView textTime = (TextView) sheetView.findViewById(R.id.bottom_sheet_location_text_time);
        TextView textAccuracy = (TextView) sheetView.findViewById(R.id.bottom_sheet_location_text_accuracy);

        CLocation location = mRealm.where(CLocation.class).equalTo("id", markerId).findFirst();

        if (location != null)
        {
            textCoordinates.setText(getResources().getString(R.string.fragment_map_text_coordinates, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
            textSource.setText(location.getSource());
            textAccuracy.setText(String.valueOf(location.getAccuracy()));
            textTime.setText(CUtils.getFullDateFromMilliseconds(location.getTime(), getContext()));
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
        }
    }

    private void requestPermissionsAndStartTrackingService ()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            boolean shouldShowRationale = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(CConstant.KEY_SHOW_LOCATION_PERMISSION, true);
            if (shouldShowRationale)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        CConstant.REQUEST_CODE_LOCATION);
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(CConstant.KEY_SHOW_LOCATION_PERMISSION, false).commit();
            }
            else
            {
                mSnackBar = CViewUtils.showSnackBarSettings(mLayout, getActivity(), getString(R.string.snackbar_settings_info));
                mSnackBar.show();
            }
        }
        else
        {
            startTrackingService();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case CConstant.REQUEST_CODE_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startTrackingService();
                }
            }
        }
    }

    public void displayMinMaxPoints (int position)
    {
        CLocation loc;
        //We have last positions
        if (position == mRealmLocations.size() - 1)
        {
            //Show westernmost point
            Number minLatitude = mRealm.where(CLocation.class).min("latitude");
            if (minLatitude != null)
            {
                loc = mRealm.where(CLocation.class).equalTo("latitude", minLatitude.doubleValue()).findFirst();
                if (loc != null)
                {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
                    marker.setTag(loc.getId());
                }
            }

            //Show easternmost point
            Number maxLatitude = mRealm.where(CLocation.class).max("latitude");
            if (maxLatitude != null)
            {
                loc = mRealm.where(CLocation.class).equalTo("latitude", minLatitude.doubleValue()).findFirst();
                if (loc != null)
                {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
                    marker.setTag(loc.getId());
                }
            }

            //Show northernmost point
            Number maxLongitude = mRealm.where(CLocation.class).max("longitude");
            if (maxLatitude != null)
            {
                loc = mRealm.where(CLocation.class).equalTo("longitude", maxLongitude.doubleValue()).findFirst();
                if (loc != null)
                {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
                    marker.setTag(loc.getId());
                }
            }

            //Show southernmost point
            Number minLongitude = mRealm.where(CLocation.class).max("longitude");
            if (minLongitude != null)
            {
                loc = mRealm.where(CLocation.class).equalTo("longitude", minLongitude.doubleValue()).findFirst();
                if (loc != null)
                {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
                    marker.setTag(loc.getId());
                }
            }
        }
    }

    public void startTrackingService ()
    {
        if (!CUtils.isServiceRunning(CServiceGetLocation.class, getContext()))
        {
            Toast.makeText(getContext(), getString(R.string.toast_tracking_running), Toast.LENGTH_SHORT).show();
            CServiceGetLocation.startService(getActivity(), mLoggingType, mLoggingInterval);
        }
        else
        {
            Toast.makeText(getContext(), getString(R.string.toast_tracking_already_running), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopTrackingService ()
    {
        if (CUtils.isServiceRunning(CServiceGetLocation.class, getContext()))
        {
            getActivity().stopService(new Intent(getActivity(), CServiceGetLocation.class));
            Toast.makeText(getContext(), getString(R.string.toast_tracking_stopped), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getContext(), getString(R.string.toast_tracking_not_running), Toast.LENGTH_SHORT).show();
        }
    }
}
