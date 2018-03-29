package loucass.kfet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import loucass.kfet.R;
import loucass.kfet.services.ConnectService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment implements View.OnClickListener {
    private TextView mLoginField;
    private TextView mPasswordField;
    private Button mSubmit;

    public Login() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Login.
     */
    public static Login newInstance() {
        return new Login();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mLoginField = view.findViewById(R.id.login_field);
        mPasswordField = view.findViewById(R.id.password_field);
        mSubmit = view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);

        return view;
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
    public void onClick(View v)
    {
        if(v.equals(mSubmit))
        {
            if(mLoginField.getText().length() > 0 && mPasswordField.getText().length() > 0) {
                ConnectService.doLogin(
                        getContext(),
                        mLoginField.getText().toString(),
                        mPasswordField.getText().toString()
                );
            }
            else
            {
                Intent intent = new Intent(ConnectService.TOAST_BROADCAST);
                intent.putExtra("MESSAGE", "Un des champs est vide");
                getContext().sendBroadcast(intent);
            }
        }
    }
}
