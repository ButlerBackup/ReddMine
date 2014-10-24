package dev.blacksheep.reddmine.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dev.blacksheep.reddmine.R;

public class HelpAdapters extends BaseAdapter {
	Context context;
	ArrayList<String> data;
	private LayoutInflater inflater;

	public HelpAdapters(Context c, ArrayList<String> data) {
		context = c;
		this.data = data;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {
		public TextView tvText;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.fragment_help_adapter_item, parent, false);
			holder.tvText = (TextView) convertView.findViewById(R.id.tvText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvText.setText(Html.fromHtml(data.get(position)));
		return convertView;
	}

}
