package com.example.familymapclient;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonAdapterClass extends RecyclerView.Adapter<PersonAdapterClass.ViewHolder>{
    private ArrayList<PersonCard> personCards;
    int rowIndex=-1;
    FullUser userInfo;
    // Represents data in one person card
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Represents one person?
        private TextView personName;
        private TextView personTitle;
        private ImageView personPicture;

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
    public PersonAdapterClass(ArrayList<PersonCard> personCards, FullUser userInfo) {
        this.userInfo = userInfo;
        this.personCards = personCards;
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
            if (cardData.getFirstName().contains("(")) {
                //it was an event
            } else {
                //it was a person
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
