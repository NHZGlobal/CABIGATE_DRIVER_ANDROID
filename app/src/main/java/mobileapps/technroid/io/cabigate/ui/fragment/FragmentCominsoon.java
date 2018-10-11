package mobileapps.technroid.io.cabigate.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobileapps.technroid.io.cabigate.R;


public final class FragmentCominsoon extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_comming_soon, container,
				false);
		

		return rootView;
	}
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	
	
}
}
