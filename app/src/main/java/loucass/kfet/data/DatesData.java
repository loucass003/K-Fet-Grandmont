package loucass.kfet.data;

import java.io.Serializable;

/**
 * Created by loucass003 on 12/2/17.
 */

public class DatesData implements Serializable
{
    private final String id;
    private final long start;
    private final long end;

    public long eventid;

    public DatesData(String id, long start, long end)
    {
        this.id = id;
        this.start = start;
        this.end = end;
        this.eventid = hashCode();
    }

    public String getId()
    {
        return id;
    }

    public long getEnd()
    {
        return end;
    }

    public long getStart()
    {
        return start;
    }

    public void setEventid(long eventid) {
        this.eventid = eventid;
    }

    public long getEventid() {
        return eventid;
    }

    public boolean equals(DatesData obj) {
        return obj.getId().equals(id);
    }
}
