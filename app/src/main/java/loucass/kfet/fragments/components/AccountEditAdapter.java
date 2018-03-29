package loucass.kfet.fragments.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import loucass.kfet.R;
import loucass.kfet.data.NewsData;
import loucass.kfet.data.UserData;

/**
 * Created by loucass003 on 11/29/17.
 */

public class AccountEditAdapter extends ArrayAdapter<UserData>
{
    public List<UserData> userDatas;

    public AccountEditAdapter(Context context, List<UserData> user)
    {
        super(context, 0, user);
        userDatas = user;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.account_edit_element, parent, false);
        TextView login = rowView.findViewById(R.id.login);
        TextView admin = rowView.findViewById(R.id.admin);
        UserData data = userDatas.get(position);

        if(data != null)
        {
            login.setText(data.getLogin());
            admin.setText(data.isAdmin() ? "ADMIN" : "NORMAL");
        }

        return rowView;
    }
}