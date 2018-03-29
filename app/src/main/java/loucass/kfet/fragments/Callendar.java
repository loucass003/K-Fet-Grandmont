package loucass.kfet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import loucass.kfet.MainActivity;
import loucass.kfet.R;
import loucass.kfet.data.DatesData;
import loucass.kfet.data.NewsData;
import loucass.kfet.fragments.components.NewsViewAdapter;
import loucass.kfet.services.ConnectService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Callendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Callendar extends Fragment implements MonthLoader.MonthChangeListener, WeekView.EmptyViewClickListener, WeekView.EventLongPressListener {

    private WeekView mWeekView;
    private BroadcastReceiver mCalendarBc;
    private BroadcastReceiver mConnectBc;
    private ArrayList<DatesData> dates = new ArrayList<>();


    public boolean addDialog = false;
    public boolean delDialog = false;

    public Callendar() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Callendar.
     */
    public static Callendar newInstance() {
        return new Callendar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getActivity().registerReceiver(mCalendarBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null)
                    return;
                dates = (ArrayList<DatesData>) intent.getSerializableExtra("DATES");
                mWeekView.goToToday();
            }
        }, new IntentFilter(ConnectService.CALENDAR_BROADCAST));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(ConnectService.isConnected())
        {
            ConnectService.getCalendar(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_callendar, container, false);
        mWeekView = view.findViewById(R.id.weekView);
        mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
        mWeekView.setShowDistinctPastFutureColor(true);
        mWeekView.setShowDistinctWeekendColor(true);
        mWeekView.setEventCornerRadius(5);
        mWeekView.goToHour(8);
        mWeekView.goToToday();
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEmptyViewClickListener(this);
        mWeekView.setEventLongPressListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mCalendarBc);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth)
    {
        List<WeekViewEvent> events = new ArrayList<>();
        for(DatesData d : dates)
        {
            Calendar startTime = Calendar.getInstance();
            startTime.setTimeInMillis(d.getStart());
            startTime.set(Calendar.MONTH, newMonth - 1);
            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(d.getEnd());
            endTime.set(Calendar.MONTH, newMonth - 1);
            WeekViewEvent event = new WeekViewEvent(d.hashCode(), "Ouvert", startTime, endTime);
            event.setColor(Color.argb(128, 0, 128, 0));
            events.add(event);
        }
        return events;
    }

    @Override
    public void onEmptyViewClicked(final Calendar time)
    {
        if(MainActivity.isLoggedIn && !addDialog)
        {
            time.set(Calendar.MINUTE, 0);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View viewDialog = inflater.inflate(R.layout.calendar_add_dialog, null);

            final NumberPicker numberPicker = viewDialog.findViewById(R.id.numberPicker);
            numberPicker.setMaxValue(10);
            numberPicker.setMinValue(1);
            numberPicker.setWrapSelectorWheel(false);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Ouvert pendant X heures");
            alertDialog.setView(viewDialog);
            alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addDialog = false;
                }
            });

            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    addDialog = false;
                }
            });
            alertDialog.setPositiveButton("Ajouter", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    addDialog = false;
                    if(ConnectService.isConnected())
                    {
                        Calendar endC = (Calendar) time.clone();
                        endC.set(Calendar.MINUTE, 0);
                        endC.add(Calendar.HOUR_OF_DAY, numberPicker.getValue());

                        if(time.get(Calendar.HOUR_OF_DAY) < 8) {
                            time.set(Calendar.HOUR_OF_DAY, 8);
                            endC.set(Calendar.HOUR_OF_DAY, 8);
                        }

                        if(endC.get(Calendar.HOUR_OF_DAY) > 18)
                            endC.set(Calendar.HOUR_OF_DAY, 18);

                        if(time.get(Calendar.HOUR_OF_DAY) < endC.get(Calendar.HOUR_OF_DAY))
                            ConnectService.addCalendarDate(getContext(), time.getTime().getTime(), endC.getTime().getTime());
                        else
                        {
                            Intent intent = new Intent(ConnectService.TOAST_BROADCAST);
                            intent.putExtra("MESSAGE", "Impossible de placer une ouverture ici (8h - 18h max)");
                            getContext().sendBroadcast(intent);
                        }
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            addDialog = true;
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect)
    {
        if(!MainActivity.isLoggedIn || delDialog)
            return;
        for(final DatesData d : dates)
        {
            if(d.hashCode() == event.getId())
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Delete Date");
                alertDialog.setMessage("Voulez vous vraiment supprimer cette date ?");
                alertDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delDialog = false;
                    }
                });

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        delDialog = false;
                    }
                });
                alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        delDialog = false;
                        if(ConnectService.isConnected())
                            ConnectService.delCalendarDate(getContext(), d.getId());
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
                delDialog = true;
                break;
            }
        }
    }
}
