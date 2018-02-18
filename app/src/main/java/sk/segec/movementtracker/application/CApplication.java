package sk.segec.movementtracker.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CApplication extends Application
{
    @Override
    public void onCreate ()
    {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
