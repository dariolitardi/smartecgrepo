package com.dario.smartecg;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_userprofile, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.data);
        textView.setText("Your name: " + UserSession.getUserName() + "\n" + "\n" +
                "Gender: " + UserSession.getGender() + "\n" + "\n" +
                "Age: " + UserSession.getAge()
        );
        return rootView;
    }
}
