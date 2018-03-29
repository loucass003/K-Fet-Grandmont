package loucass.kfet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import loucass.kfet.R;
import loucass.kfet.data.NewsData;
import loucass.kfet.fragments.components.NewsViewAdapter;
import loucass.kfet.services.ConnectService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link News#newInstance} factory method to
 * create an instance of this fragment.
 */
public class News extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver mNewsBc;
    private BroadcastReceiver mConnectBc;



    public News() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment News.
     */
    public static News newInstance()
    {
        return new News();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getActivity().registerReceiver(mNewsBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null)
                    return;
                ArrayList<NewsData> news = (ArrayList<NewsData>) intent.getSerializableExtra("NEWS");

                NewsViewAdapter adapter = new NewsViewAdapter(getContext(), news);
                mListView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
                System.out.println(news);
            }
        }, new IntentFilter(ConnectService.NEWS_BROADCAST));


        getActivity().registerReceiver(mConnectBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectService.getNews(getContext());
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }, new IntentFilter(ConnectService.CONNECTED_BROADCAST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mListView = view.findViewById(R.id.news);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mListView != null && mListView.getAdapter() == null && ConnectService.isConnected())
        {
            ConnectService.getNews(getContext());
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getContext().unregisterReceiver(mConnectBc);
        getContext().unregisterReceiver(mNewsBc);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onRefresh()
    {
        if(ConnectService.isConnected())
        {
            ConnectService.getNews(getContext());
        }
    }
}
