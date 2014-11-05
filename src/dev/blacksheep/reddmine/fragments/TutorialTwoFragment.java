package dev.blacksheep.reddmine.fragments;

import info.hoang8f.widget.FButton;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
import com.securepreferences.SecurePreferences;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dev.blacksheep.reddmine.Consts;
import dev.blacksheep.reddmine.MainActivity;
import dev.blacksheep.reddmine.R;
import dev.blacksheep.reddmine.Utils;
import dev.blacksheep.reddmine.classes.MCrypt;

public class TutorialTwoFragment extends Fragment {
	FButton bTipMe, bPlayNow;
	tipMe mTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial_two, container, false);

		bPlayNow = (FButton) view.findViewById(R.id.bPlayNow);
		bPlayNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SecurePreferences sp = new SecurePreferences(getActivity());
				sp.edit().putString("tutorial", "1").commit();
				startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			}
		});
		bTipMe = (FButton) view.findViewById(R.id.bTipMe);
		bTipMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTask = null;
				mTask = new tipMe();
				mTask.execute();
			}
		});
		return view;
	}

	private class tipMe extends AsyncTask<Void, Void, Void> {

		boolean success = false;
		String message = "";
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setIndeterminate(true);
			pd.setTitle("Please wait..");
			pd.setMessage("Tipping you 100 coins..");
			pd.setCancelable(false);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
				String unique = new Utils(getActivity()).getUnique();
				String origString = unique + "|" + currentTime;
				MCrypt mcrypt = new MCrypt(currentTime);
				String encrypted = Utils.base64String(MCrypt.bytesToHex(mcrypt.encrypt(origString)));
				String encryptedId = MCrypt.bytesToHex(mcrypt.encrypt(unique));
				
				Future<String> res = Ion.with(getActivity()).load(Consts.WEBSITE_MAIN + "?action=tip&x=" + encrypted + "&t=" + currentTime + "&id=" + encryptedId).asString();
				Log.e("TEXT", Consts.WEBSITE_MAIN + "?action=tip&x=" + encrypted + "&t=" + currentTime + "&id=" + encryptedId);
				String result = res.get();
				Log.e("RESULT", result);
				JSONObject jObject = new JSONObject(result);
				if (jObject.getString("success").equals("1")) {
					success = true;
				} else {
					message = jObject.getString("message");
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (pd != null && pd.isShowing()) {
						pd.cancel();
					}
					if (!isCancelled()) {
						if (success) {
							Crouton.makeText(getActivity(), "You're tipped 100 ReddCoins! Enjoy :)", Style.INFO).show();
							bTipMe.setEnabled(false);
						} else {
							Crouton.makeText(getActivity(), message, Style.ALERT).show();
						}
					} else {
						Crouton.makeText(getActivity(), "Cancelled", Style.ALERT).show();
					}
				}
			});
		}
	}

	@Override
	public void onPause() {
		if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
			mTask.cancel(true);
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
			mTask.cancel(true);
		}
		super.onDestroy();
	}
}