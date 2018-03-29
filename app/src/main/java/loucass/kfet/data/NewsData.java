package loucass.kfet.data;

import java.io.Serializable;

/**
 * Created by loucass003 on 11/29/17.
 */

public class NewsData implements Serializable
{
    private final String id;
    private final String title;
    private final String content;
    private final String date;

    public NewsData(String id, String title, String content, String date)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() { return title; }

    public String getDate() { return date; }
}
