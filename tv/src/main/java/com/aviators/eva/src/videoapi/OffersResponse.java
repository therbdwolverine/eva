package com.aviators.eva.src.videoapi;

import java.util.List;

/**
 * Created by A-7413 on 6/16/17.
 */

public class OffersResponse {

    private Offers[] Offers;

    private String Destination;

    public Offers[] getOffers ()
    {
        return Offers;
    }

    public void setOffers (Offers[] Offers)
    {
        this.Offers = Offers;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        this.Destination = destination;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Offers = "+Offers+"]";
    }

}
