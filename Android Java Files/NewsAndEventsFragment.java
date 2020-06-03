package ca.mohawk.fallis;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
//NewsAndEventsFragment loads up and displays the XML for the Mohawk College News and Events Page.
public class NewsAndEventsFragment extends Fragment {


    public NewsAndEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_and_events, container, false);
    }

}
