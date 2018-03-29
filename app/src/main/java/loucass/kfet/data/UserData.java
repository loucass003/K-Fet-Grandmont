package loucass.kfet.data;

import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by loucass003 on 11/30/17.
 */

public class UserData implements Serializable
{
    private final String id;
    private final String login;
    private final String pass;
    private final String socket;
    private final boolean isAdmin;

    public UserData(String id, String login, String pass, String socket, boolean isAdmin)
    {
        this.id = id;
        this.login = login;
        this.pass = pass;
        this.socket = socket;
        this.isAdmin = isAdmin;
    }

    public String getId()
    {
        return id;
    }

    public String getLogin()
    {
        return login;
    }

    public String getPass()
    {
        return pass;
    }

    public String getSocket()
    {
        return socket;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void putData(SharedPreferences.Editor editor)
    {
        editor.putString("login", login);
        editor.putString("pass", pass);
    }
}
