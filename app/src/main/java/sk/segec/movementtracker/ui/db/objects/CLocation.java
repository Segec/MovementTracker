package sk.segec.movementtracker.ui.db.objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CLocation extends RealmObject
{
    @PrimaryKey
    private Long id;
    @Required
    private Double latitude;
    @Required
    private Double longitude;
    @Required
    private String source;
    @Required
    private Long time;
    @Required
    private Float accuracy;

    public String getSource ()
    {
        return source;
    }

    public void setSource (String source)
    {
        this.source = source;
    }

    public Long getTime ()
    {
        return time;
    }

    public void setTime (Long time)
    {
        this.time = time;
    }

    public Float getAccuracy ()
    {
        return accuracy;
    }

    public void setAccuracy (Float accuracy)
    {
        this.accuracy = accuracy;
    }

    public Double getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (Double longitude)
    {
        this.longitude = longitude;
    }

    public Long getId ()
    {
        return id;
    }

    public void setId (Long id)
    {
        this.id = id;
    }
}
