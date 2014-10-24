package dev.blacksheep.reddmine.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dev.blacksheep.reddmine.R;

public class WalletAddressDialogFragment extends DialogFragment {

	public static WalletAddressDialogFragment newInstance(String address) {
		WalletAddressDialogFragment frag = new WalletAddressDialogFragment();
		Bundle args = new Bundle();
		args.putString("address", address);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String address = getArguments().getString("address");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.fragment_dialog_wallet_address, null);
		ImageView ivAddress = (ImageView) v.findViewById(R.id.ivAddress);
		TextView tvAddress = (TextView) v.findViewById(R.id.tvAddress);
		tvAddress.setText(address);
		Button bShareAddress = (Button) v.findViewById(R.id.bShareAddress);
		bShareAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ReddCoin Address");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, address);
				startActivity(Intent.createChooser(sharingIntent, "Share Using.."));
			}
		});
		Button bCopyAddress = (Button) v.findViewById(R.id.bCopyAddress);
		bCopyAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(address);
				Crouton.makeText(getActivity(), "Reddcoin wallet address copied to clipboard!", Style.CONFIRM).show();
			}
		});
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(address, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 1200);
		try {
			Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
			BitmapDrawable ob = new BitmapDrawable(bitmap);
			ivAddress.setBackgroundDrawable(ob);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		builder.setView(v);
		builder.setTitle("Quick Address");

		builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});

		return builder.create();
	}
}