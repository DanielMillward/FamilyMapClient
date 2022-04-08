package com.example.familymapclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import Models.Event;
import Models.Person;

public class PersonAdapterClass extends RecyclerView.Adapter<PersonAdapterClass.ViewHolder>{
    private ArrayList<PersonCard> personCards;
    int rowIndex=-1;
    FullUser userInfo;
    ArrayList<Event> displayedEvents;
    PersonBinaryTree personTree;
    Context context;
    ActivityResultLauncher<Intent> cardActivityLauncher;

    // Represents data in one person card
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Represents one person?
        private TextView personName;
        private TextView personTitle;
        private ImageView personPicture;
        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            personName = itemView.findViewById(R.id.personNameText);
            personTitle = itemView.findViewById(R.id.personTitleText);
            personPicture = itemView.findViewById(R.id.personImage);

            itemView.setClickable(true);
        }

        public TextView getPersonText() {
            return personName;
        }

        public TextView getPersonTitle() {
            return personTitle;
        }

        public ImageView getPersonImage() {
            return personPicture;
        }
    }

    //Start the adapter class with ALL people in the recyclerView!
    public PersonAdapterClass(ArrayList<PersonCard> personCards, FullUser userInfo,
                              ArrayList<Event> displayedEvents, PersonBinaryTree personTree, Context context, ActivityResultLauncher<Intent> cardActivityLauncher) {
        this.userInfo = userInfo;
        this.personCards = personCards;
        this.displayedEvents = displayedEvents;
        this.personTree = personTree;
        this.context = context;
        this.cardActivityLauncher = cardActivityLauncher;
    }

    //making one new person card from layout
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.person_card_grid, viewGroup, false);

        return new ViewHolder(view);
    }

    // setting the data of a card
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Find what data we need to give it
        PersonCard cardData = personCards.get(position);
        String personName = cardData.getFirstName() + " " + cardData.getLastName();
        //set the data here
        viewHolder.personName.setText(personName);
        viewHolder.personTitle.setText(cardData.getTitle());
        //setting picture
        String pic1_location = "@android:drawable/btn_star_big_on";

        if (cardData.getGender().equals("m")) {
            viewHolder.personPicture.setBackgroundResource(com.google.android.material.R.drawable.ic_clock_black_24dp);
        } else if (cardData.getGender().equals("f")){
            viewHolder.personPicture.setBackgroundResource(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal);
        } else {
            viewHolder.personPicture.setBackgroundResource(com.google.android.material.R.drawable.abc_btn_radio_to_on_mtrl_015);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowIndex=viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if(rowIndex==position){
            //got selected! Now to switch to the respective Person or Event activity
            boolean wasEventCard;
            if (cardData.event != null) {
                //it was an event
                Event pastClickedEvent = cardData.event;
                //it was an actual event
                Bundle myBundle = new Bundle();
                Intent eventIntent = new Intent(context, EventActivity.class);
                //add people with whether they're displayed or not
                myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
                myBundle.putSerializable("personTree", (Serializable) personTree);
                myBundle.putSerializable("userData", userInfo);
                myBundle.putSerializable("pastClickedEvent", pastClickedEvent);
                eventIntent.putExtras(myBundle);
                cardActivityLauncher.launch(eventIntent);
            } else {
                //it was a person
                Person pastClickedPerson = cardData.person;
                //clicked on a person, assume makeMap func is already called
                Bundle myBundle = new Bundle();
                //add people with whether they're displayed or not
                myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
                myBundle.putSerializable("personTree", (Serializable) personTree);
                myBundle.putSerializable("userData", userInfo);
                Intent personIntent = new Intent(context, PersonActivity.class);
                //get an event to pass on
                Event pastClickedEvent = null;
                for (Event event : displayedEvents) {
                    if (event.getPersonID().equals(pastClickedPerson.getPersonID())) {
                        pastClickedEvent = event;
                    }
                }
                myBundle.putSerializable("activeEvent", pastClickedEvent);
                personIntent.putExtras(myBundle);
                cardActivityLauncher.launch(personIntent);
            }
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return personCards.size();
    }

    public void updateList(ArrayList<PersonCard> updatedPersons) {
        personCards = updatedPersons;
        notifyDataSetChanged();
    }

}
