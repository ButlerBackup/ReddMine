package dev.blacksheep.reddmine.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import dev.blacksheep.reddmine.R;
import dev.blacksheep.reddmine.adapters.HelpAdapters;

public class HelpFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_help, container, false);
		String [] strings = getActivity().getResources().getStringArray(R.array.help);
		ArrayList<String> data = new ArrayList<String>(Arrays.asList(strings));
		for (String d : data) {
			Log.e("LOL", d);
		}
		HelpAdapters adapter = new HelpAdapters(getActivity(), data);
		ListView lvHelp = (ListView) rootView.findViewById(R.id.lvHelp);
		lvHelp.setAdapter(adapter);
		//((ListView) rootView.findViewById(R.id.lvHelp)).setAdapter(adapter);
		return rootView;
	}
}
