package loucass.kfet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import loucass.kfet.R;
import loucass.kfet.data.UserData;
import loucass.kfet.fragments.components.AccountEditAdapter;
import loucass.kfet.services.ConnectService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AccountEditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditor extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private boolean isAdmin = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private FloatingActionButton mFabView;
    private BroadcastReceiver mAccountsBc;
    private AccountEditAdapter adapter;
    private boolean editDialog;
    private boolean delDialog;
    private boolean addDialog;

    public AccountEditor() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountEditor.
     */
    public static AccountEditor newInstance()
    {
        return new AccountEditor();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mAccountsBc = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null)
                    return;
                ArrayList<UserData> users = (ArrayList<UserData>) intent.getSerializableExtra("USERS");

                adapter = new AccountEditAdapter(getContext(), users);
                mListView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new IntentFilter(ConnectService.USERS_BROADCAST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_account_editor, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mListView = view.findViewById(R.id.users);
        mFabView = view.findViewById(R.id.add_account);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFabView.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListView != null && mListView.getAdapter() == null && ConnectService.isConnected())
        {
            ConnectService.getUsers(getContext());
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mAccountsBc);
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
    public void onClick(View v)
    {
        if(v.equals(mFabView) && !addDialog)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View viewDialog = inflater.inflate(R.layout.account_add_dialog, null);

            final TextView login = viewDialog.findViewById(R.id.login_field);
            final TextView pass = viewDialog.findViewById(R.id.password_field);
            final CheckBox admin = viewDialog.findViewById(R.id.admin);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Add User");
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
                        ConnectService.addUser(getContext(), login.getText().toString(), pass.getText().toString(), admin.isChecked());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            addDialog = true;
        }
    }

    @Override
    public void onRefresh()
    {
        if(ConnectService.isConnected())
        {
            ConnectService.getUsers(getContext());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(!editDialog) {
            final UserData data = adapter.userDatas.get(position);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View viewDialog = inflater.inflate(R.layout.account_add_dialog, null);

            final TextView login = viewDialog.findViewById(R.id.login_field);
            final TextView pass = viewDialog.findViewById(R.id.password_field);
            final CheckBox admin = viewDialog.findViewById(R.id.admin);
            login.setVisibility(View.GONE);
            pass.setVisibility(View.GONE);

            admin.setChecked(data.isAdmin());

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Edit User");
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

            alertDialog.setPositiveButton("Enregister", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editDialog = false;
                    if (ConnectService.isConnected())
                    {
                        ConnectService.editUser(getContext(), data.getId(), admin.isChecked());
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
            final UserData data = adapter.userDatas.get(position);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Delete User");
            alertDialog.setMessage("Voulez vous vraiment supprimer cet utilisateur ?");
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
                        ConnectService.delUser(getContext(), data.getId());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            delDialog = true;
        }
        return true;
    }
}
