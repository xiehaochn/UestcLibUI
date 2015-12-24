package com.hawx.uestclibui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/28.
 */
public class LeftMenuAdapter extends ArrayAdapter<MenuItem> {
    private LayoutInflater mInflater;
    private int mSelected;
    public LeftMenuAdapter(Context context, int resource, MenuItem[] objects) {
        super(context, resource, objects);
        mInflater=LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.menu_list, parent, false);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.id_item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.id_item_title);
        title.setText(getItem(position).text);
        iv.setImageResource(getItem(position).icon);
        convertView.setBackgroundColor(Color.TRANSPARENT);

        if (position == mSelected) {
            iv.setImageResource(getItem(position).iconSelected);
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.state_menu_item_selected));
        }

        return convertView;
    }
    public void setSelected(int position) {
        this.mSelected = position;
        notifyDataSetChanged();
    }

}
