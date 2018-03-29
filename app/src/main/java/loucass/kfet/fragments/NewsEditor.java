package loucass.kfet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import loucass.kfet.R;
import loucass.kfet.data.NewsData;
import loucass.kfet.fragments.components.NewsEditAdapter;
import loucass.kfet.services.ConnectService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewsEditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsEditor extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, View.OnClickListener {
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver mNewsBc;
    private NewsEditAdapter adapter;
    private FloatingActionButton mFabView;

    private boolean editDialog = false;
    private boolean delDialog = false;
    private boolean addDialog = false;

    public NewsEditor() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Callendar.
     */
    public static NewsEditor newInstance() {
        return new NewsEditor();
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

                adapter = new NewsEditAdapter(getContext(), news);
                mListView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new IntentFilter(ConnectService.NEWS_BROADCAST));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListView != null && mListView.getAdapter() == null && ConnectService.isConnected())
        {
            ConnectService.getNews(getContext());
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_news_editor, container, false);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mListView = v.findViewById(R.id.news);
        mFabView = v.findViewById(R.id.add_news);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFabView.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mNewsBc);
    }

    @Override
    public void onDetach() {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(!editDialog) {
            final NewsData data = adapter.newsDatas.get(position);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View viewDialog = inflater.inflate(R.layout.news_add_dialog, null);

            final TextView title = viewDialog.findViewById(R.id.title);
            final TextView content = viewDialog.findViewById(R.id.content);

            title.setText(data.getTitle());
            content.setText(data.getContent());

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Edit News");
            alertDialog.setView(viewDialog);
            alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editDialog = false;
                }
            });

            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    editDialog = false;
                }
            });

            alertDialog.setPositiveButton("Enregister", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editDialog = false;
                    if (ConnectService.isConnected()) {
                        ConnectService.editNews(getContext(), data.getId(), title.getText().toString(), content.getText().toString());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            editDialog = true;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(!delDialog)
        {
            final NewsData data = adapter.newsDatas.get(position);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Delete News");
            alertDialog.setMessage("Voulez vous vraiment supprimer cette news ?");
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
                    {
                        ConnectService.delNews(getContext(), data.getId());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            delDialog = true;
        }

        return true;
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(mFabView) && !addDialog)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View viewDialog = inflater.inflate(R.layout.news_add_dialog, null);

            final TextView title = viewDialog.findViewById(R.id.title);
            final TextView content = viewDialog.findViewById(R.id.content);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Add News");
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

            alertDialog.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    addDialog = false;
                    if(ConnectService.isConnected())
                    {
                        ConnectService.addNews(getContext(), title.getText().toString(), content.getText().toString());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            addDialog = true;
        }
    }
}

