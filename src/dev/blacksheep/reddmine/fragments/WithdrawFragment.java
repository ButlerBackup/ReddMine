package dev.blacksheep.reddmine.fragments;

import info.hoang8f.widget.FButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dev.blacksheep.reddmine.Consts;
import dev.blacksheep.reddmine.R;
import dev.blacksheep.reddmine.Utils;

public class WithdrawFragment extends Fragment {
	TextView tvWalletAmount;
	DecimalFormat decimalFormat;
	FButton bWithdraw, bQr;
	EditText etAmountToWithdraw, etWithdrawAddress;

	SecurePreferences sp;
	getBalance mTask;
	double availableCoins = 0;

	public WithdrawFragment() {
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

	private void showBannedDialog() {
		String unique = new Utils(getActivity()).getUnique();
		String text = "You're banned from using this application due to malicious activities. To appeal, contact me <a href=\"https://www.reddit.com/message/compose/?to=shaunidiot&subject=ReddMine%20Ban%20Appeal&message=ID:"
				+ unique + " //do not remove\">here</a>";
		final AlertDialog d = new AlertDialog.Builder(getActivity()).setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}
		}).setIcon(R.drawable.icon).setMessage(Html.fromHtml(text)).setCancelable(false).show();
		d.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				getActivity().finish();
			}
		});
		d.show();
		((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_withdraw, container, false);
		sp = new SecurePreferences(getActivity());
		decimalFormat = new DecimalFormat("##.##");
		tvWalletAmount = (TextView) rootView.findViewById(R.id.tvWalletAmount);
		tvWalletAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTask = null;
				mTask = new getBalance();
				mTask.execute();
			}
		});
		etWithdrawAddress = (EditText) rootView.findViewById(R.id.etWithdrawAddress);
		etWithdrawAddress.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etAmountToWithdraw.getText().toString().length() > 0 && etWithdrawAddress.getText().toString().length() > 0) {
					if (Double.parseDouble(etAmountToWithdraw.getText().toString()) < availableCoins) {
						bWithdraw.setEnabled(true);
					} else {
						bWithdraw.setEnabled(false);
					}
				} else {
					bWithdraw.setEnabled(false);
				}
			}

		});
		etAmountToWithdraw = (EditText) rootView.findViewById(R.id.etAmountToWithdraw);
		etAmountToWithdraw.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etAmountToWithdraw.getText().toString().length() > 0 && etWithdrawAddress.getText().toString().length() > 0) {
					if (Double.parseDouble(etAmountToWithdraw.getText().toString()) < availableCoins) {
						bWithdraw.setEnabled(true);
					} else {
						bWithdraw.setEnabled(false);
					}
				} else {
					bWithdraw.setEnabled(false);
				}
			}

		});
		bWithdraw = (FButton) rootView.findViewById(R.id.bWithdraw);
		bWithdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etAmountToWithdraw.getText().toString().length() > 0 && etWithdrawAddress.getText().toString().length() > 0) {
					if (Utils.isNumeric(etAmountToWithdraw.getText().toString())) {
						new withdrawToWallet().execute(etAmountToWithdraw.getText().toString().trim());
					} else {
						Crouton.makeText(getActivity(), "Amount must be less/equal to remaining coins, and must be a whole number (no decimal place).", Style.INFO).show();
					}
				} else {
					Crouton.makeText(getActivity(), "Please input amount of coins to withdraw.", Style.INFO).show();
				}
			}
		});
		bQr = (FButton) rootView.findViewById(R.id.bQr);
		bQr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					Intent intent = new Intent("com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, 0);
				} catch (Exception e) {
					Toast.makeText(getActivity(), "ZXing QR Code scanner needed", Toast.LENGTH_LONG).show();
					Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android&hl=en");
					Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
					startActivity(marketIntent);
				}
			}
		});
		SecurePreferences sp = new SecurePreferences(getActivity());
		etWithdrawAddress.setText(sp.getString(Consts.PREFERENCE_WITHDRAW_WALLET, ""));
		mTask = null;
		mTask = new getBalance();
		mTask.execute();
		return rootView;
	}

	private class withdrawToWallet extends AsyncTask<String, Void, Void> {
		ProgressDialog pd;
		String res = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setMessage("Withdrawing..");
			pd.setTitle("Please hold..");
			pd.setCancelable(false);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(String... arg0) {
			String amount = arg0[0];
			res = new Utils(getActivity()).withdraw(etWithdrawAddress.getText().toString(), amount);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pd != null && pd.isShowing()) {
				pd.cancel();
			}
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (res.equals("Success")) {
						Crouton.makeText(getActivity(), "Successfully withdraw coins!", Style.CONFIRM).show();
						mTask = null;
						mTask = new getBalance();
						mTask.execute();
					} else {
						Crouton.makeText(getActivity(), res, Style.CONFIRM).show();
					}
				}
			});
		}

	}

	private class getBalance extends AsyncTask<Void, Void, Void> {
		ArrayList<String> finalBalance;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bWithdraw.setEnabled(false);
			bQr.setEnabled(false);
			tvWalletAmount.setText(Consts.REFRESHING);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			finalBalance = new Utils(getActivity()).getBalance();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (!isCancelled()) {
						if (etAmountToWithdraw.length() > 0 && etWithdrawAddress.length() > 0) {
							bWithdraw.setEnabled(true);
						} else {
							bWithdraw.setEnabled(false);
						}
						bQr.setEnabled(true);
						try {
							tvWalletAmount.setText(Math.floor(Double.parseDouble(finalBalance.get(0))) + " REDD\n" + finalBalance.get(1) + " pending");
							availableCoins = Math.floor(Double.parseDouble(finalBalance.get(0)));
							if (finalBalance.get(2).equals("1")) {
								showBannedDialog();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				String s = data.getStringExtra("SCAN_RESULT");
				Matcher m1 = Pattern.compile("reddcoin:(.*?)\\?").matcher(s);

				if (m1.find()) {
					etWithdrawAddress.setText(m1.group(1));
					SecurePreferences sp = new SecurePreferences(getActivity());
					sp.edit().putString(Consts.PREFERENCE_WITHDRAW_WALLET, m1.group(1)).commit();
				} else {
					Crouton.makeText(getActivity(), "Unable to get parse result", Style.ALERT).show();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Crouton.makeText(getActivity(), "Cancelled.", Style.ALERT).show();
			}
		}
	}
}
