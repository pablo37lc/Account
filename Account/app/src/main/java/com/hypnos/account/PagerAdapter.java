package com.hypnos.account;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PagerAdapter
        extends FragmentStateAdapter {

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    void clear() {
        fragmentList.clear();
    }

    int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

}
