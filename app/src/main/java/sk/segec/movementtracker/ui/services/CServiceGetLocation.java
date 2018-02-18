package sk.segec.movementtracker.ui.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import io.realm.Realm;
import sk.segec.movementtracker.ui.db.objects.CLocation;
import sk.segec.movementtracker.ui.enums.ELoggingType;
import sk.segec.movementtracker.utils.CConstant;

/**
 * Created by Michal on 18. 2. 2018.
 */
public class CServiceGetLocation extends Service
{
    public static final String TAG = CServiceGetLocation.class.getSimpleName();
    private static final float METERS_TRESHOLD = 100;
    private Realm mRealm;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    public CServiceGetLocation ()
    {
        super();
    }

    public static void startService (Activity activity, ELoggingType loggingType, int loggingInterval)
    {
        Intent serviceIntent = new Intent(activity, CServiceGetLocation.class);
        if (loggingType.equals(ELoggingType.MINUTES))
        {
            serviceIntent.putExtra(CConstant.KEY_LOG_INTERVAL, loggingInterval * 60 * 1000);
        }
        else
        {
            serviceIntent.putExtra(CConstant.KEY_LOG_INTERVAL, loggingInterval * 1000);
        }
        activity.startService(serviceIntent);
    }

    @Override
    public void onCreate ()
    {
        super.onCreate();
        mRealm = Realm.getDefaultInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Nullable
    @Override
    public IBinder onBind (Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            createLocationRequest(intent);

            mLocationCallback = new LocationCallback()
            {
                @Override
                public void onLocationResult (LocationResult locationResult)
                {
                    if (locationResult != null && locationResult.getLastLocation() != null)
                    {
                        super.onLocationResult(locationResult);
                        final Location location = locationResult.getLastLocation();
                        try
                        { // I could use try-with-resources here
                            mRealm = Realm.getDefaultInstance();
                            mRealm.executeTransaction(new Realm.Transaction()
                            {
                                @Override
                                public void execute (Realm realm)
                                {
                                    //Distance between this and last stored object is significant, so we want to store it
                                    CLocation object = new CLocation();
                                    //Find the biggest id in db for locations
                                    Number maxId = realm.where(CLocation.class).max("id");

                                    boolean saveToDb = false;

                                    //We do not have any locations stored in db
                                    if (maxId == null)
                                    {
                                        saveToDb = true;
                                    }
                                    //Get last location, so we can calculate distance between points
                                    else
                                    {
                                        CLocation lastLocation = realm.where(CLocation.class).equalTo("id", maxId.longValue()).findFirst();
                                        if (lastLocation != null)
                                        {
                                            float[] results = new float[1];
                                            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), location.getLatitude(), location.getLongitude(), results);
                                            //Distance between points is bigger than 100 meters, so save new location
                                            if (results[0] > METERS_TRESHOLD)
                                            {
                                                saveToDb = true;
                                            }
                                        }
                                        //Getting last location failed, save actual location
                                        else
                                        {
                                            saveToDb = true;
                                        }

                                    }
                                    if (saveToDb)
                                    {
                                        if (maxId == null)
                                        {
                                            object.setId(0L);
                                        }
                                        else
                                        {
                                            object.setId(maxId.longValue() + 1);
                                        }
                                        object.setLongitude(location.getLongitude());
                                        object.setLatitude(location.getLatitude());
                                        object.setAccuracy(location.getAccuracy());
                                        object.setSource(location.getProvider());
                                        object.setTime(location.getTime());
                                        realm.insertOrUpdate(object);
                                    }
                                }
                            });
                        }
                        finally
                        {
                            if (mRealm != null)
                            {
                                mRealm.close();
                            }
                        }
                    }
                }
            };

            if (mFusedLocationClient != null && mLocationRequest != null)
            {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected void createLocationRequest (Intent intent)
    {
        if (intent != null)
        {
            //Set default logging interval to 1 minute
            int interval = intent.getIntExtra(CConstant.KEY_LOG_INTERVAL, 60000);
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(interval);
            mLocationRequest.setFastestInterval(interval);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
