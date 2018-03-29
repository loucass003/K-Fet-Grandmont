package loucass.kfet.services;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import loucass.kfet.data.DatesData;
import loucass.kfet.data.NewsData;
import loucass.kfet.data.UserData;

public class ConnectService extends Service
{
//    public static final String SERVER_URL = "http://192.168.1.23:3000";
    public static final String SERVER_URL = "https://lgkfet.herokuapp.com";

    public static final String CONNECTED_BROADCAST = "loucass.kfet.services.bc.CONNECTED";

    public static final String ACTION_STATUS = "loucass.kfet.services.action.STATUS",
                                STATUS_BROADCAST = "loucass.kfet.services.bc.STATUS";

    public static final String ACTION_LOGIN = "loucass.kfet.services.action.LOGIN",
            ACTION_LOGIN_ADD = "loucass.kfet.services.action.LOGIN_ADD",
            ACTION_LOGIN_DEL = "loucass.kfet.services.action.LOGIN_DEL",
            ACTION_LOGIN_EDIT = "loucass.kfet.services.action.LOGIN_EDIT",
            ACTION_USERS = "loucass.kfet.services.action.USERS",
            ACTION_LOGOUT = "loucass.kfet.services.action.LOGOUT",
            ACTION_LOGIN_ID = "loucass.kfet.services.param.ID",
            ACTION_LOGIN_USER = "loucass.kfet.services.param.USER",
            ACTION_LOGIN_PASS = "loucass.kfet.services.param.PASS",
            ACTION_LOGIN_ADMIN = "loucass.kfet.services.param.ADMIN",
            USERS_BROADCAST = "loucass.kfet.services.bc.USERS",
            LOGGED_BROADCAST = "loucass.kfet.services.bc.LOGGED";

    public static final String ACTION_NEWS = "loucass.kfet.services.action.NEWS",
            ACTION_ADD_NEWS = "loucass.kfet.services.action.ADD_NEWS",
            ACTION_DEL_NEWS = "loucass.kfet.services.action.DEL_NEWS",
            ACTION_EDIT_NEWS = "loucass.kfet.services.action.EDIT_NEWS",
            ACTION_NEWS_ID = "loucass.kfet.services.param.NEWS_ID",
            ACTION_NEWS_TITLE = "loucass.kfet.services.param.NEWS_TITLE",
            ACTION_NEWS_CONTENT = "loucass.kfet.services.param.NEWS_CONTENT",
            NEWS_BROADCAST = "loucass.kfet.services.bc.NEWS";


    public static final String ACTION_CALENDAR = "loucass.kfet.services.action.CALENDAR",
            ACTION_CALENDAR_ADD_DATE = "loucass.kfet.services.action.CALENDAR_ADD_DATE",
            ACTION_CALENDAR_DEL_DATE = "loucass.kfet.services.action.CALENDAR_DEL_DATE",
            ACTION_CALENDAR_ID = "loucass.kfet.services.param.ID",
            ACTION_CALENDAR_START = "loucass.kfet.services.param.CALENDAR_START",
            ACTION_CALENDAR_END = "loucass.kfet.services.param.CALENDAR_END",
            CALENDAR_BROADCAST = "loucass.kfet.services.bc.CALENDAR";

    public static final String TOAST_BROADCAST = "loucass.kfet.services.bc.TOAST";

    private static Socket socket;
    private String mLastPassword;

    private SharedPreferences mUserdata;

    public ConnectService() {}

    public static void doLogin(Context context, String user, String password) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(ACTION_LOGIN_USER, user);
        intent.putExtra(ACTION_LOGIN_PASS, password);
        context.startService(intent);
    }

    public static void doLogout(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_LOGOUT);
        context.startService(intent);
    }

    public static void getNews(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_NEWS);
        context.startService(intent);
    }

    public static void getUsers(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_USERS);
        context.startService(intent);
    }

    public static void getStatus(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_STATUS);
        context.startService(intent);
    }

    public static void getCalendar(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_CALENDAR);
        context.startService(intent);
    }

    public static void addNews(Context context, String title, String content) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_ADD_NEWS);
        intent.putExtra(ACTION_NEWS_TITLE, title);
        intent.putExtra(ACTION_NEWS_CONTENT, content);
        context.startService(intent);
    }

    public static void addCalendarDate(Context context, long start, long end) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_CALENDAR_ADD_DATE);
        intent.putExtra(ACTION_CALENDAR_START, start);
        intent.putExtra(ACTION_CALENDAR_END, end);
        context.startService(intent);
    }

    public static void editNews(Context context, String id, String title, String content) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_EDIT_NEWS);
        intent.putExtra(ACTION_NEWS_ID, id);
        intent.putExtra(ACTION_NEWS_TITLE, title);
        intent.putExtra(ACTION_NEWS_CONTENT, content);
        context.startService(intent);
    }

    public static void delNews(Context context, String id) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_DEL_NEWS);
        intent.putExtra(ACTION_NEWS_ID, id);
        context.startService(intent);
    }

    public static void delUser(Context context, String id) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_LOGIN_DEL);
        intent.putExtra(ACTION_LOGIN_ID, id);
        context.startService(intent);
    }

    public static void editUser(Context context, String id, boolean isAdmin) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_LOGIN_EDIT);
        intent.putExtra(ACTION_LOGIN_ID, id);
        intent.putExtra(ACTION_LOGIN_ADMIN, isAdmin);
        context.startService(intent);
    }

    public static void delCalendarDate(Context context, String id) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_CALENDAR_DEL_DATE);
        intent.putExtra(ACTION_CALENDAR_ID, id);
        context.startService(intent);
    }

    public static void addUser(Context context, String user, String password, boolean isAdmin) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.setAction(ACTION_LOGIN_ADD);
        intent.putExtra(ACTION_LOGIN_USER, user);
        intent.putExtra(ACTION_LOGIN_PASS, password);
        intent.putExtra(ACTION_LOGIN_ADMIN, isAdmin);
        context.startService(intent);
    }

    public static boolean isConnected() {
        return socket != null && socket.connected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUserdata = getSharedPreferences("USERDATA", 0);

        try {
            IO.Options opt = new IO.Options();

            socket = IO.socket(SERVER_URL, opt);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Intent intent = new Intent(CONNECTED_BROADCAST);
                    sendBroadcast(intent);
                    if (mUserdata.contains("login") && mUserdata.contains("pass")) {
                        socket.emit("login", mUserdata.getString("login", "rest"), mUserdata.getString("pass", "in peace"));
                    }
                }
            });

            socket.on("status", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    DatesData date = null;
                    if (obj != null && obj.has("start"))
                    {
                        try {
                            date = new DatesData(
                                    obj.getString("id"),
                                    obj.getLong("start"),
                                    obj.getLong("end")
                            );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(STATUS_BROADCAST);
                    intent.putExtra("STATUS", date);
                    sendBroadcast(intent);
                }
            });

            socket.on("news", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        ArrayList<NewsData> news = new ArrayList<>();
                        JSONArray array = (JSONArray) args[0];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            news.add(new NewsData(
                                    obj.getString("id"),
                                    obj.getString("title"),
                                    obj.getString("content"),
                                    obj.getString("date")
                            ));
                        }

                        Intent intent = new Intent(NEWS_BROADCAST);
                        intent.putExtra("NEWS", news);
                        sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on("calendar", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        ArrayList<DatesData> dates = new ArrayList<>();
                        JSONArray array = (JSONArray) args[0];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            dates.add(new DatesData(
                                    obj.getString("id"),
                                    obj.getLong("start"),
                                    obj.getLong("end")
                            ));
                        }

                        Intent intent = new Intent(CALENDAR_BROADCAST);
                        intent.putExtra("DATES", dates);
                        sendBroadcast(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on("users", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        ArrayList<UserData> users = new ArrayList<>();
                        JSONArray array = (JSONArray) args[0];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            users.add(new UserData(
                                    obj.getString("id"),
                                    obj.getString("login"),
                                    null,
                                    null,
                                    obj.getBoolean("isAdmin")
                            ));
                        }

                        Intent intent = new Intent(USERS_BROADCAST);
                        intent.putExtra("USERS", users);
                        sendBroadcast(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on("login", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        if (obj.has("error")) {
                            String message = obj.getString("error");
                            if (mUserdata.contains("login") && mUserdata.contains("pass")) {
                                SharedPreferences.Editor editor = mUserdata.edit();
                                editor.clear();
                                editor.apply();
                                message = "Unable to re-use saved account!";
                            }

                            Intent intent = new Intent(TOAST_BROADCAST);
                            intent.putExtra("MESSAGE", message);
                            sendBroadcast(intent);
                        } else {
                            UserData userData = new UserData(
                                    obj.getString("id"),
                                    obj.getString("login"),
                                    mLastPassword,
                                    obj.getString("socket"),
                                    obj.getBoolean("isAdmin")
                            );
                            Intent intent = new Intent(LOGGED_BROADCAST);
                            intent.putExtra("LOGGED", userData);
                            sendBroadcast(intent);

                            SharedPreferences.Editor editor = mUserdata.edit();
                            userData.putData(editor);
                            editor.apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_LOGIN: {
                    final String user = intent.getStringExtra(ACTION_LOGIN_USER).trim();
                    mLastPassword = intent.getStringExtra(ACTION_LOGIN_PASS).trim();
                    socket.emit("login", user, mLastPassword);
                    break;
                }
                case ACTION_LOGIN_ADD: {
                    final String user = intent.getStringExtra(ACTION_LOGIN_USER).trim();
                    final String password = intent.getStringExtra(ACTION_LOGIN_PASS).trim();
                    final boolean isAdmin = intent.getBooleanExtra(ACTION_LOGIN_ADMIN, false);
                    socket.emit("add_user", user, password, isAdmin);
                    break;
                }
                case ACTION_LOGIN_DEL: {
                    final String id = intent.getStringExtra(ACTION_LOGIN_ID);
                    socket.emit("del_user", id);
                    break;
                }
                case ACTION_LOGIN_EDIT: {
                    final String id = intent.getStringExtra(ACTION_LOGIN_ID);
                    final boolean isAdmin = intent.getBooleanExtra(ACTION_LOGIN_ADMIN, false);
                    socket.emit("edit_user", id, isAdmin);
                    break;
                }
                case ACTION_USERS: {
                    socket.emit("users");
                    break;
                }
                case ACTION_NEWS: {
                    socket.emit("news");
                    break;
                }
                case ACTION_ADD_NEWS: {
                    final String title = intent.getStringExtra(ACTION_NEWS_TITLE).trim();
                    final String content = intent.getStringExtra(ACTION_NEWS_CONTENT).trim();
                    socket.emit("add_news", title, content);
                    break;
                }
                case ACTION_EDIT_NEWS: {
                    final String id = intent.getStringExtra(ACTION_NEWS_ID);
                    final String title = intent.getStringExtra(ACTION_NEWS_TITLE).trim();
                    final String content = intent.getStringExtra(ACTION_NEWS_CONTENT).trim();
                    socket.emit("edit_news", id, title, content);
                    break;
                }
                case ACTION_DEL_NEWS: {
                    final String id = intent.getStringExtra(ACTION_NEWS_ID);
                    socket.emit("del_news", id);
                    break;
                }
                case ACTION_CALENDAR_ADD_DATE: {
                    final long start = intent.getLongExtra(ACTION_CALENDAR_START, -1L);
                    final long end = intent.getLongExtra(ACTION_CALENDAR_END, -1L);
                    socket.emit("add_calendar_date", start, end);
                    break;
                }
                case ACTION_CALENDAR_DEL_DATE: {
                    final String id = intent.getStringExtra(ACTION_CALENDAR_ID);
                    socket.emit("del_calendar_date", id);
                    break;
                }
                case ACTION_CALENDAR: {
                    socket.emit("calendar");
                    break;
                }
                case ACTION_STATUS:
                {
                    socket.emit("status");
                    break;
                }
                case ACTION_LOGOUT: {
                    socket.emit("logout");
                    break;
                }
            }
        }
        return START_STICKY;
    }

    public class MyBinder extends Binder {
        public ConnectService getService() {
            return ConnectService.this;
        }
    }
}