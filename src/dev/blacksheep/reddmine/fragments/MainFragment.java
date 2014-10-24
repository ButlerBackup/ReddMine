package dev.blacksheep.reddmine.fragments;

import info.hoang8f.widget.FButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.securepreferences.SecurePreferences;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dev.blacksheep.reddmine.Consts;
import dev.blacksheep.reddmine.R;
import dev.blacksheep.reddmine.Utils;

public class MainFragment extends Fragment {
	SeekBar sbWinAmount;
	TextView tvMinesResult, tvGameResult, tvReddAddress, tvReddRemaining, tvStopAndDraw;
	DecimalFormat decimalFormat;
	FButton bPlayNow;
	EditText etReddAmountToBet;
	int minesAmount = 10, timeToIncreaseMultiplier = 0, buttonsPressed = 0;
	ArrayList<Button> minesButtons;
	String shaResult = "", cryptedShaResult = "", gameTime = "";
	ArrayList<Integer> minesArray;
	SecurePreferences sp;
	double availableCoins = 0, winMultiplier = 1.5, currentGameMultipler = 1.5;
	boolean gameInProgress = false, allowCheckout = false;
	final double defaultMultiplier = 0.4;

	getBalance mTaskGetBalance;
	requestPayment mTaskRequestPayment;

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		sp = new SecurePreferences(getActivity());
		minesButtons = new ArrayList<Button>();
		decimalFormat = new DecimalFormat("##.##");
		etReddAmountToBet = (EditText) rootView.findViewById(R.id.etReddAmountToBet);
		etReddAmountToBet.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count > 0) {
					if (!tvReddRemaining.getText().toString().equals(Consts.REFRESHING) && tvReddRemaining.getText().toString().contains("REDD")) {
						bPlayNow.setEnabled(true);
					} else {
						bPlayNow.setEnabled(false);
					}
				} else {
					bPlayNow.setEnabled(false);
				}
			}

		});
		bPlayNow = (FButton) rootView.findViewById(R.id.bPlayNow);
		bPlayNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (availableCoins >= Double.parseDouble(etReddAmountToBet.getText().toString())) {
					reset();
					tvStopAndDraw.setEnabled(true);
					gameInProgress = true;
					tvStopAndDraw.setText(winMultiplier + "x");
					tvStopAndDraw.setVisibility(View.VISIBLE);
					etReddAmountToBet.setEnabled(false);
					sbWinAmount.setEnabled(false);
					bPlayNow.setEnabled(false);
					for (final Button b : minesButtons) {
						b.setEnabled(true);
						b.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								buttonsPressed++;
								timeToIncreaseMultiplier++;
								int tag = (Integer) b.getTag();
								if (minesArray.get(tag) == 0) {
									b.setBackgroundResource(R.drawable.ic_mines_nope);
									if (timeToIncreaseMultiplier % 2 == 0) {
										Log.e("HEHE", "HEHE");
										if (currentGameMultipler <= 1.5) {
											winMultiplier += new Utils(getActivity()).getRandomValue(0.01, 0.05, 2);
										} else if (currentGameMultipler > 1.5 && currentGameMultipler <= 5) {
											winMultiplier += new Utils(getActivity()).getRandomValue(0.5, 1.5, 2);
										} else if (currentGameMultipler > 5) {
											winMultiplier += new Utils(getActivity()).getRandomValue(1, 5, 2);
										} else {
											Log.e("WTF", "NOMULTILIER?!");
										}
										Log.e("WINMULTILIER", winMultiplier + "");
										// tvMinesResult.setText(Html.fromHtml("<b>"
										// + minesAmount +
										// "</b> mines, win amount <b>x" +
										// decimalFormat.format(winMultiplier) +
										// "</b>"));
									}
									if (25 - buttonsPressed == minesAmount) {
										endGame(minesArray, true);
									}
									tvStopAndDraw.setText(decimalFormat.format(winMultiplier) + "x");
									allowCheckout = true;
								} else {
									b.setBackgroundResource(R.drawable.ic_mines);
									endGame(minesArray, false);
								}
							}
						});
					}
				} else if (Double.parseDouble(etReddAmountToBet.getText().toString()) < 0.5) {
					Crouton.makeText(getActivity(), "You have to enter more than 0.5 reddcoins.", Style.INFO).show();
				} else {
					Crouton.makeText(getActivity(), "Not enough coins!", Style.ALERT).show();
				}
			}
		});
		tvMinesResult = (TextView) rootView.findViewById(R.id.tvMinesResult);
		tvMinesResult.setText(Html.fromHtml("<b>10</b> mines, win amount <b>x1.5</b>"));
		tvStopAndDraw = (TextView) rootView.findViewById(R.id.tvStopAndDraw);
		tvStopAndDraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (allowCheckout) {
					bPlayNow.setEnabled(false);
					endGame(minesArray, true);
				} else {
					Crouton.makeText(getActivity(), "You have to open at least 1 block", Style.INFO).show();
				}
			}
		});
		tvGameResult = (TextView) rootView.findViewById(R.id.tvGameResult);
		tvGameResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(tvGameResult.getText().toString());
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.convertstring.com/Hash/SHA256"));
				startActivity(browserIntent);
			}
		});
		tvReddAddress = (TextView) rootView.findViewById(R.id.tvReddAddress);
		tvReddAddress.setText(sp.getString("wallet", ""));
		tvReddAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = WalletAddressDialogFragment.newInstance(tvReddAddress.getText().toString());
				newFragment.show(getFragmentManager(), "dialog");
			}
		});
		tvReddRemaining = (TextView) rootView.findViewById(R.id.tvReddRemaining);
		tvReddRemaining.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTaskGetBalance = null;
				mTaskGetBalance = new getBalance();
				mTaskGetBalance.execute();
			}
		});
		sbWinAmount = (SeekBar) rootView.findViewById(R.id.sbWinAmount);
		sbWinAmount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setSeekBarAndMultiplier();
			}
		});
		TableLayout table = (TableLayout) rootView.findViewById(R.id.tableLayout1);
		int totalButtons = 0;
		int buttonsInRow = 0;
		int numRows = table.getChildCount();
		TableRow row = null;
		while (totalButtons < 25) {
			if (numRows > 0) {
				row = (TableRow) table.getChildAt(numRows - 1);
				buttonsInRow = row.getChildCount();
			}

			if (numRows == 0 || buttonsInRow == 5) {
				row = new TableRow(getActivity());
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				row.setLayoutParams(lp);
				row.setGravity(Gravity.CENTER);
				table.addView(row, lp);
				buttonsInRow = 0;
			}
			while (buttonsInRow < 5) {
				Button b = new Button(getActivity());
				b.setTag(totalButtons);
				b.setEnabled(false);
				minesButtons.add(b);
				row.addView(b);
				buttonsInRow++;
				totalButtons++;
			}
			buttonsInRow = 0;
		}
		mTaskGetBalance = null;
		mTaskGetBalance = new getBalance();
		mTaskGetBalance.execute();
		return rootView;
	}

	private void setSeekBarAndMultiplier() {
		int progress = sbWinAmount.getProgress();
		minesAmount = progress + 3;
		Log.e("VALUE", minesAmount + "");
		double E = 100 - (minesAmount / 0.25);
		double returnValue = (90 / E);
		returnValue = (double) Math.round(returnValue * 100) / 100;
		winMultiplier = Double.parseDouble(decimalFormat.format(returnValue));
		currentGameMultipler = winMultiplier;
		Log.e("RETURN VALYE", "" + winMultiplier);
		tvMinesResult.setText(Html.fromHtml("<b>" + minesAmount + "</b> mines, win amount <b>x" + decimalFormat.format(returnValue) + "</b>"));
	}

	private class getBalance extends AsyncTask<Void, Void, Void> {
		ArrayList<String> finalBalance;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bPlayNow.setEnabled(false);
			tvReddRemaining.setText(Consts.REFRESHING);
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
						if (etReddAmountToBet.length() > 0) {
							bPlayNow.setEnabled(true);
						}
						if (finalBalance != null && finalBalance.size() > 0) {
							try {
								tvReddRemaining.setText(finalBalance.get(0) + " REDD\n" + finalBalance.get(1) + " pending");
								availableCoins = Double.parseDouble(finalBalance.get(0));
								if (finalBalance.get(2).equals("1")) {
									showBannedDialog();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Crouton.makeText(getActivity(), "Unable to get balance. Please refresh or restart application", Style.ALERT).show();
						}
					}
				}
			});
		}

	}

	private void endGame(ArrayList<Integer> minesArray, boolean won) {
		tvStopAndDraw.setEnabled(false);
		bPlayNow.setEnabled(false);
		gameInProgress = false;

		// tvStopAndDraw.setVisibility(View.INVISIBLE);
		if (won) {
			Crouton.makeText(getActivity(), "YOU WON!", Style.CONFIRM).show();
		} else {
			Crouton.makeText(getActivity(), "YOU LOST!", Style.INFO).show();
		}
		mTaskRequestPayment = null;
		mTaskRequestPayment = new requestPayment();
		mTaskRequestPayment.execute(won);
		setSeekBarAndMultiplier();
		tvGameResult.setVisibility(View.VISIBLE);
		bPlayNow.setText("Play Again!");
		etReddAmountToBet.setEnabled(true);
		sbWinAmount.setEnabled(true);
		for (final Button b : minesButtons) {
			int tag = (Integer) b.getTag();
			if (minesArray.get(tag) == 0) {
				b.setBackgroundResource(R.drawable.ic_mines_nope);
			} else {
				b.setBackgroundResource(R.drawable.ic_mines);
			}
			b.setEnabled(false);
		}
		tvGameResult.setText(tvGameResult.getText().toString() + "\n\nYour data :\n" + shaResult);
	}

	private void reset() {
		tvStopAndDraw.setVisibility(View.INVISIBLE);
		tvStopAndDraw.setText("0");
		buttonsPressed = 0;
		timeToIncreaseMultiplier = 0;
		shaResult = "";
		cryptedShaResult = "";
		shaResult += "[" + Utils.generateString(15) + "]";
		for (final Button b : minesButtons) {
			b.setBackgroundResource(android.R.drawable.btn_default);
		}
		allowCheckout = false;
		generateRandomMinefield();
	}

	private void generateRandomMinefield() {
		minesArray = new ArrayList<Integer>();
		for (int i = 0; i < minesAmount; i++) {
			minesArray.add(1);
		}
		for (int i = 0; i < 25 - minesAmount; i++) {
			minesArray.add(0);
		}
		long seed = System.nanoTime();
		Collections.shuffle(minesArray, new Random(seed));
		shaResult += " [";
		for (Integer l : minesArray) {
			Log.e("LOL", l + "");
			shaResult += l + ",";
		}
		shaResult = shaResult.substring(0, shaResult.length() - 1);
		shaResult += "]";
		gameTime = String.valueOf(System.currentTimeMillis() / 1000L);
		shaResult += " [" + gameTime + "]";
		shaResult += " [" + new Utils(getActivity()).getUnique() + "]";
		shaResult += " [" + winMultiplier + "]";
		shaResult += " [" + etReddAmountToBet.getText().toString() + "]";
		try {
			cryptedShaResult = Utils.SHA256(shaResult);
			tvGameResult.setText("Your hash :\n" + cryptedShaResult);
			tvGameResult.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class requestPayment extends AsyncTask<Boolean, Void, Void> {
		String res = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bPlayNow.setEnabled(false);

		}

		@Override
		protected Void doInBackground(Boolean... arg0) {
			res = new Utils(getActivity()).request(arg0[0], tvStopAndDraw.getText().toString(), shaResult, gameTime);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (!isCancelled()) {
						if (!res.equals("")) {
							Crouton.makeText(getActivity(), res, Style.INFO).show();
						}
						bPlayNow.setEnabled(true);
						mTaskGetBalance = null;
						mTaskGetBalance = new getBalance();
						mTaskGetBalance.execute();
					}
				}
			});
		}
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
	public void onPause() {
		if (mTaskGetBalance != null && mTaskGetBalance.getStatus() != AsyncTask.Status.FINISHED) {
			mTaskGetBalance.cancel(true);
		}

		if (mTaskRequestPayment != null && mTaskRequestPayment.getStatus() != AsyncTask.Status.FINISHED) {
			mTaskRequestPayment.cancel(true);
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mTaskGetBalance != null && mTaskGetBalance.getStatus() != AsyncTask.Status.FINISHED) {
			mTaskGetBalance.cancel(true);
		}

		if (mTaskRequestPayment != null && mTaskRequestPayment.getStatus() != AsyncTask.Status.FINISHED) {
			mTaskRequestPayment.cancel(true);
		}
		super.onDestroy();
	}
}
