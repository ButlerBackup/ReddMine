package dev.blacksheep.reddmine;

import com.crashlytics.android.Crashlytics;
import info.hoang8f.widget.FButton;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
import com.securepreferences.SecurePreferences;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class WelcomeActivity extends ActionBarActivity {
	FButton bGetWallet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		SecurePreferences sp = new SecurePreferences(WelcomeActivity.this);
		if (!sp.getString("unique", "").equals("") && !sp.getString("wallet", "").equals("")) {
			startActivity(new Intent(WelcomeActivity.this, TutorialActivity.class));
			finish();
		}
		setContentView(R.layout.fragment_welcome);

		bGetWallet = (FButton) findViewById(R.id.bGetWallet);
		bGetWallet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new generateWallet().execute();
			}
		});
	}

	private class generateWallet extends AsyncTask<Void, Void, Void> {
		boolean success = false;
		String message = "";
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(WelcomeActivity.this);
			pd.setIndeterminate(true);
			pd.setTitle("Please wait..");
			pd.setMessage("Getting your personal wallet..");
			pd.setCancelable(false);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				SecurePreferences sp = new SecurePreferences(WelcomeActivity.this);
				String unique = new Utils(WelcomeActivity.this).getUnique();
				String hashUnique = Utils.md5(unique);
				String hashedUnique = hashUnique.substring(0, 20);
				Future<String> res = Ion.with(WelcomeActivity.this).load(Consts.WEBSITE_MAIN + "?action=generateWallet&id=" + hashedUnique).asString();
				//Log.e("TEXT", Consts.WEBSITE_MAIN + "?action=generateWallet&id=" + hashedUnique);
				String result = res.get();
				JSONObject jObject = new JSONObject(result);
				if (jObject.getString("success").equals("1")) {
					sp.edit().putString("unique", hashedUnique).commit();
					sp.edit().putString("wallet", jObject.getString("message")).commit();
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
			WelcomeActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (pd != null && pd.isShowing()) {
						pd.cancel();
					}
					if (success) {
						startActivity(new Intent(WelcomeActivity.this, TutorialActivity.class));
						finish();
					} else {
						Crouton.makeText(WelcomeActivity.this, message, Style.ALERT).show();
					}
				}
			});
		}
	}
}
