package dev.blacksheep.reddmine.fragments;

import dev.blacksheep.reddmine.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialOneFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial_one, container, false);
		TextView tvTutorialOne = (TextView) view.findViewById(R.id.tvTutorialOne);
		tvTutorialOne.setText(Html.fromHtml(getActivity().getResources().getString(R.string.tutorial_one_text)));
		return view;
	}
}