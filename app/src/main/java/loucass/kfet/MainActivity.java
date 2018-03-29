package loucass.kfet;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import loucass.kfet.data.DatesData;
import loucass.kfet.data.UserData;
import loucass.kfet.fragments.AccountEditor;
import loucass.kfet.fragments.Callendar;
import loucass.kfet.fragments.Login;
import loucass.kfet.fragments.News;
import loucass.kfet.fragments.NewsEditor;
import loucass.kfet.services.ConnectService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;
    private Handler mHandler;
    private BroadcastReceiver mToastBc;
    private BroadcastReceiver mLoggedBc;

    public static boolean isLoggedIn = false;
    private NavigationView navigationView;
    private UserData mCurrentUser;
    private BroadcastReceiver mConnectBc;
    private BroadcastReceiver mStatusBc;

    private DatesData mLastStatusDate;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED
            }, 0);
        }

        Intent intent = new Intent(this, ConnectService.class);
        startService(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, News.newInstance());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("News");

        registerReceiver(mToastBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null)
                    return;
                Toast.makeText(context, intent.getExtras().getString("MESSAGE"), Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(ConnectService.TOAST_BROADCAST));


        registerReceiver(mLoggedBc = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getExtras() == null)
                    return;
                mCurrentUser = (UserData)intent.getSerializableExtra("LOGGED");
                isLoggedIn = true;
                changeNavComponents(true);
                navigationView.getMenu().findItem(R.id.nav_news).setChecked(true);
                changeView(News.newInstance(), navigationView.getMenu().findItem(R.id.nav_news).getTitle().toString());
                drawer.closeDrawer(GravityCompat.START);
            }
        }, new IntentFilter(ConnectService.LOGGED_BROADCAST));

        registerReceiver(mConnectBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectService.getStatus(getApplicationContext());
            }
        }, new IntentFilter(ConnectService.CONNECTED_BROADCAST));

        registerReceiver(mStatusBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView status = drawer.findViewById(R.id.status);
                String s = "Fermé";
                if (intent.hasExtra("STATUS"))
                {
                    DatesData date = (DatesData) intent.getSerializableExtra("STATUS");
                    if(date != null)
                    {
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(date.getStart());
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(date.getEnd());
                        s = String.format(Locale.FRANCE, "Ouvert de %dh à %dh", startTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.HOUR_OF_DAY));
                    }
                }
                status.setText(s);
            }
        }, new IntentFilter(ConnectService.STATUS_BROADCAST));

        drawer.addDrawerListener(toggle);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(ConnectService.isConnected())
        {
            ConnectService.getStatus(getApplicationContext());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        changeNavComponents(isLoggedIn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mToastBc);
        unregisterReceiver(mLoggedBc);
        unregisterReceiver(mConnectBc);
        unregisterReceiver(mStatusBc);

        Intent broadcastIntent = new Intent("loucass.kfet.services.StatusService");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item)
    {
        final Fragment f;
        String forcedName = null;
        switch (item.getItemId())
        {
            case R.id.nav_news:
                f = News.newInstance();
                break;
            case R.id.nav_edit_news:
                f = NewsEditor.newInstance();
                break;
            case R.id.nav_calendar:
                f = Callendar.newInstance();
                break;
            case R.id.nav_login:
                f = Login.newInstance();
                break;
            case R.id.nav_edit_account:
                f = AccountEditor.newInstance();
                break;
            case R.id.nav_logout:
                f = News.newInstance();
                if(ConnectService.isConnected())
                    ConnectService.doLogout(this);
                isLoggedIn = false;
                changeNavComponents(false);
                forcedName = navigationView.getMenu().findItem(R.id.nav_news).getTitle().toString();
                break;
            default:
                f = News.newInstance();
        }
        changeView(f, forcedName != null ? forcedName : item.getTitle().toString());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeView(final Fragment f, final String title)
    {
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, f);
                fragmentTransaction.commitAllowingStateLoss();
                getSupportActionBar().setTitle(title);
            }
        };
        mHandler.post(mPendingRunnable);
    }

    public void changeNavComponents(boolean isLoggedIn)
    {
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(!isLoggedIn);
        navigationView.getMenu().findItem(R.id.nav_edit_news).setVisible(isLoggedIn);
        navigationView.getMenu().findItem(R.id.nav_edit_account).setVisible(isLoggedIn && mCurrentUser.isAdmin());
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(isLoggedIn);

        TextView login = drawer.findViewById(R.id.user_login);
        login.setVisibility(isLoggedIn ? View.VISIBLE : View.INVISIBLE);
        login.setText(isLoggedIn ? mCurrentUser.getLogin() : "");
    }
}
