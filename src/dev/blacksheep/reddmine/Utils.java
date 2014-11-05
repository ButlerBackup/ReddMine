package dev.blacksheep.reddmine;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
import com.securepreferences.SecurePreferences;

import dev.blacksheep.reddmine.classes.MCrypt;

public class Utils {
	Context context;

	public Utils(Context c) {
		context = c;
	}

	public ArrayList<String> getBalance() {
		ArrayList<String> finalBalance = new ArrayList<String>();
		try {
			Future<String> res = Ion.with(context).load(Consts.WEBSITE_MAIN + "?action=getBalance&id=" + getUnique()).asString();
			// Log.e("URL", Consts.WEBSITE_MAIN + "?action=getBalance&id=" +
			// getUnique());
			String result = res.get();
			// Log.e("getBalance", result);
			JSONObject jObject = new JSONObject(result);
			String time = jObject.getString("time");
			MCrypt mcrypt = new MCrypt(time);
			finalBalance.add(new String(mcrypt.decrypt(jObject.getString("message"))));
			finalBalance.add(new String(mcrypt.decrypt(jObject.getString("pending"))));
			finalBalance.add(new String(mcrypt.decrypt(jObject.getString("status"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalBalance;
	}

	public double getRandomValue(double lowerBound, double upperBound, int decimalPlaces) {
		Random random = new Random(System.nanoTime());
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			// Log.e("ERROR", "Error getRandomValue");
			throw new IllegalArgumentException("Error");
		}

		double dbl = ((random == null ? new Random() : random).nextDouble() //
				* (upperBound - lowerBound))
				+ lowerBound;
		// Log.e("Random no", dbl + "");
		return Double.valueOf(String.format("%." + decimalPlaces + "f", dbl));

	}

	public static String md5(String text) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(text.getBytes("UTF-8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getHash(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		digest.reset();
		return digest.digest(password.getBytes());
	}

	private static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
	}

	public static String SHA256(String text) {
		return bin2hex(getHash(text));
	}

	public static String generateString(int length) {
		Random rng = new Random(System.nanoTime());
		String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	public String getUnique() {
		SecurePreferences sp = new SecurePreferences(context);
		if (!sp.getString("unique", "").equals("")) {
			return sp.getString("unique", "");
		} else {
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			final String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			String deviceId = deviceUuid.toString();
			return deviceId;
		}
	}

	public static String base64String(String text) {
		String ret = "";
		byte[] data = null;
		try {
			data = text.getBytes("UTF-8");
			ret = Base64.encodeToString(data, Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean isNumeric(String s) {
		return s.matches("\\d+");
	}

	public String withdraw(String address, String amount) {
		String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
		try {
			MCrypt mcrypt = new MCrypt(currentTime);
			String encrypted = Utils.base64String(MCrypt.bytesToHex(mcrypt.encrypt(address + "|" + amount + "|" + getUnique() + "|" + currentTime)));
			Future<String> res = Ion.with(context).load(Consts.WEBSITE_MAIN + "?action=withdraw&t=" + currentTime + "&x=" + encrypted + "&id=" + getUnique()).asString();
			// Log.e("URL", Consts.WEBSITE_MAIN + "?action=withdraw&t=" +
			// currentTime + "&x=" + encrypted + "&id=" + getUnique());
			String result = res.get();
			JSONObject jObject = new JSONObject(result);
			return jObject.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String request(boolean won, String amount, String origString, String gameTime) {
		String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
		if (!won) { // lose
			origString += " [0] [" + amount.replace("x", "") + "]";
		} else {
			origString += " [1] [" + amount.replace("x", "") + "]";
		}

		try {
			MCrypt mcrypt = new MCrypt(currentTime);
			String encrypted = Utils.base64String(MCrypt.bytesToHex(mcrypt.encrypt(origString)));
			Future<String> res = Ion.with(context).load(Consts.WEBSITE_MAIN + "?action=tx&t=" + currentTime + "&x=" + encrypted + "&id=" + getUnique() + "&gt=" + gameTime).asString();
			// Log.e("URL", Consts.WEBSITE_MAIN + "?action=tx&t=" + currentTime
			// + "&x=" + encrypted + "&id=" + getUnique() + "&gt=" + gameTime);
			String result = res.get();
			// Log.e("RESULT", result);
			JSONObject jObject = new JSONObject(result);
			return jObject.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
