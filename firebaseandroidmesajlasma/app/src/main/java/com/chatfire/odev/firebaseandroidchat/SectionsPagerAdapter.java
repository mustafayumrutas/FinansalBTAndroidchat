package com.chatfire.odev.firebaseandroidchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                RequestsFragment requestsFragment =new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragment chatsFragment =new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "İstekler";
            case 1:
                return "Mesajlasma";
            case 2:
                return "Arkadaslar";
            default:
                return null;
        }
    }
}