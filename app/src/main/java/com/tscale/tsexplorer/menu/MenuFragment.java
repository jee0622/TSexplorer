package com.tscale.tsexplorer.menu;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.slidingmenu.SlidingMenu;
import com.tscale.tsexplorer.ui.ScaleChargeActivity;
import com.tscale.tsexplorer.ui.ScaleChargeFragment;
import com.tscale.tsexplorer.setting.SettingFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private ListView menuListView;


    private MenuAdapter adapter;

    private List<Map<String, Object>> mapList = new ArrayList<>();

    private final String[] menuFrom = new String[]{"icon", "text"};

    private final int[] menuTo = new int[]{R.id.menu_item_icon, R.id.menu_item_text};

    private int index = 0;

    private SlidingMenu sm;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.menu_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuListView = (ListView) getView().findViewById(R.id.menu_listView);
        getMenuDate();
        adapter = new MenuAdapter(getActivity(), mapList, R.layout.menu_item, menuFrom, menuTo);
        menuListView.setAdapter(adapter);

        menuListView.setOnItemClickListener(this);

        sm = ScaleChargeActivity.getSlidingMenu();
    }

    private void getMenuDate() {

        String[] texts = getResources().getStringArray(R.array.menu_text);
        TypedArray drawables = getResources().obtainTypedArray(R.array.menu_icon);
        int[] icons = new int[drawables.length()];

        if (texts.length == drawables.length()) {
            for (int i = 0; i < texts.length; i++) {
                Map<String, Object> map = new HashMap<>();
                icons[i] = drawables.getResourceId(i, 0);
                map.put("icon", icons[i]);
                map.put("text", texts[i]);
                mapList.add(map);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("OnItemClickListener", position + " " + mapList.get(position).get("text"));
        sm.toggle();
        if (position == index) {
            return;
        } else {
            switchFragment(position);
            index = position;
        }
    }

    private void switchFragment(int position) {
        FragmentManager fm = getFragmentManager();
        Fragment from = getVisibleFragment();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment to;
        switch (position) {
            case 0:
                to = new ScaleChargeFragment();
                break;
            case 1:
                to = new BaseFragment();
                break;
            case 2:
                to = new SettingFragment();
                break;
            default:
                to = new BaseFragment();
                break;
        }
        ft.replace(R.id.home_container, to, to.getClass().getSimpleName());
        ft.commit();
//        Log.i("switchFragment",""+to.isAdded());
//        if (to.isAdded()){
//            ft.hide(from).show(to);
//        }
//        else {
//            ft.hide(from).add(R.id.home_container,to,to.getClass().getSimpleName());
//        }

    }


    private Fragment getVisibleFragment() {

        FragmentManager fm = getFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            Log.i("getVisibleFragment", "FragmentName " + fragment.getClass().getSimpleName() + (fragment.isVisible() ? " visiable" : " not visiable"));
            if (fragment != null && fragment.isVisible() && !(fragment instanceof MenuFragment)) {
                return fragment;
            }
        }
        return null;
    }
}
