package ca.mohawk.fallis;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;


/**
 * A simple {@link Fragment} subclass.
 */
//SchoolInformationFragment is misnamed. It should be called SchoolMappingFragment. This fragment displays a picture of the layout of mohawk college and displays a view flipper that has current locations around Mohawk College.
public class SchoolInformationFragment extends Fragment {
    private ViewFlipper viewFlipper;
    private View myView;

    public SchoolInformationFragment() {
        // Required empty public constructor
    }


	//On Create Method assigns the OnClickListeners on the next and previous button. Displays the XML for School Mapping.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_school_information, container, false);
		
		//Get the ID of the view flipper.
		viewFlipper = myView.findViewById(R.id.viewFlipper);

		//Get the ID for the previous button.
        Button previousButton = (Button)myView.findViewById(R.id.previousButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				//Change the InAnimation and OutAnimation to animations that we create. Then show previous item.
                viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
                viewFlipper.showPrevious();
            }
        });

		//Get the ID for the next button.
        Button nextButton = (Button)myView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				//Change the InAnimation and OutAnimation to animations that we create. Then show previous item.
                viewFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
                viewFlipper.showNext();
            }
        });
		
        return myView;
    }

}
