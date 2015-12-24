package com.hawx.uestclibui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by Administrator on 2015/11/28.
 */
public class LeftMenuFragment extends ListFragment {
    private final static int MENU_SIZE=5;
    private MenuItem[] menuItems=new MenuItem[MENU_SIZE];
    private LeftMenuAdapter mAdapter;
    private LayoutInflater mLayoutInflater;
    private int[] im=new int[5];
    private int[] im_sl=new int[5];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getActivity());
        MenuItem menuItem = null;

        initImResource();
        for (int i = 0; i < MENU_SIZE; i++) {
                menuItem = new MenuItem(getResources().getStringArray(R.array.menu_item)[i], false, im[i],im_sl[i]);
                menuItems[i] = menuItem;
        }
    }

    private void initImResource() {
        im[0]=R.drawable.ic_jiansuo;
        im_sl[0]=R.drawable.ic_jiansuo_selected;
        im[1]=R.drawable.ic_zhongxin;
        im_sl[1]=R.drawable.ic_zhongxin_selected;
        im[2]=R.drawable.ic_chaxun;
        im_sl[2]=R.drawable.ic_chaxun_selected;
        im[3]=R.drawable.ic_shoucang;
        im_sl[3]=R.drawable.ic_shoucang_selected;
        im[4]=R.drawable.ic_guanyu;
        im_sl[4]=R.drawable.ic_guanyu_selected;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter = new LeftMenuAdapter(getActivity(), -1,menuItems));
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mMenuItemSelectedListener != null) {
            mMenuItemSelectedListener.menuItemSelected(((MenuItem) getListAdapter().getItem(position)).text);
        }
        Log.d("ListViewClicked",""+l+";"+v+";"+position+";"+id);
        mAdapter.setSelected(position);

    }
    public interface OnMenuItemSelectedListener {
        void menuItemSelected(String title);
    }
    private OnMenuItemSelectedListener mMenuItemSelectedListener;

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener menuItemSelectedListener) {
        this.mMenuItemSelectedListener = menuItemSelectedListener;
    }
    public void setSelected(int position){
        mAdapter.setSelected(position);
    }
}
