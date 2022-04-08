package com.example.familymapclient;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyExpanderAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> bigTitleList;
    private HashMap<String, ArrayList<PersonCard>> listOfLists;

    // constructor
    public MyExpanderAdapter(Context context, ArrayList<String> bigTitleList,
                                           HashMap<String, ArrayList<PersonCard>> listOfLists) {
        this.context = context;
        this.bigTitleList = bigTitleList;
        this.listOfLists = listOfLists;
    }

    @Override
    public int getGroupCount() {
        return this.listOfLists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listOfLists.get(this.bigTitleList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listOfLists.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        //gets PersonCard data from expander i, element number i1
        return this.listOfLists.get(this.bigTitleList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String currTitle = (String) bigTitleList.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.base_expandable, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.baseListText);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(currTitle);
        return view;
    }

    //Gives the view for a given expander > element position
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final PersonCard cardData = (PersonCard) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.person_card_grid, null);
        }
        //get elements of card, set them here
        TextView personName = view.findViewById(R.id.personNameText);
        TextView personTitle = view.findViewById(R.id.personTitleText);
        ImageView personPicture = view.findViewById(R.id.personImage);

        String personNewName = cardData.getFirstName() + " " + cardData.getLastName();
        personName.setText(personNewName);
        personTitle.setText(cardData.getTitle());
        //setting picture

        if (cardData.getGender().equals("m")) {
            personPicture.setBackgroundResource(com.google.android.material.R.drawable.ic_clock_black_24dp);
        } else if (cardData.getGender().equals("f")){
            personPicture.setBackgroundResource(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal);
        } else {
            personPicture.setBackgroundResource(com.google.android.material.R.drawable.abc_btn_radio_to_on_mtrl_015);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
