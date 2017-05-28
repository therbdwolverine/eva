package com.aviators.eva.src.videoapi;

public class Offers
{
    private String TotalSavings;

    private String HotelName;

    private String TravelEndDate;

    private String PercentSavings;

    private String TotalPrice;

    private String PerPassengerPackagePrice;

    private String TravelStartDate;

    private String PerPassengerSavings;

    private String HotelStarRating;

    private String HotelImageUrl;

    private String Currency;

    public String getTotalSavings ()
    {
        return TotalSavings;
    }

    public void setTotalSavings (String TotalSavings)
    {
        this.TotalSavings = TotalSavings;
    }

    public String getHotelName ()
    {
        return HotelName;
    }

    public void setHotelName (String HotelName)
    {
        this.HotelName = HotelName;
    }

    public String getTravelEndDate ()
    {
        return TravelEndDate;
    }

    public void setTravelEndDate (String TravelEndDate)
    {
        this.TravelEndDate = TravelEndDate;
    }

    public String getPercentSavings ()
    {
        return PercentSavings;
    }

    public void setPercentSavings (String PercentSavings)
    {
        this.PercentSavings = PercentSavings;
    }

    public String getTotalPrice ()
    {
        return TotalPrice;
    }

    public void setTotalPrice (String TotalPrice)
    {
        this.TotalPrice = TotalPrice;
    }

    public String getPerPassengerPackagePrice ()
    {
        return PerPassengerPackagePrice;
    }

    public void setPerPassengerPackagePrice (String PerPassengerPackagePrice)
    {
        this.PerPassengerPackagePrice = PerPassengerPackagePrice;
    }

    public String getTravelStartDate ()
    {
        return TravelStartDate;
    }

    public void setTravelStartDate (String TravelStartDate)
    {
        this.TravelStartDate = TravelStartDate;
    }

    public String getPerPassengerSavings ()
    {
        return PerPassengerSavings;
    }

    public void setPerPassengerSavings (String PerPassengerSavings)
    {
        this.PerPassengerSavings = PerPassengerSavings;
    }

    public String getHotelStarRating ()
    {
        return HotelStarRating;
    }

    public void setHotelStarRating (String HotelStarRating)
    {
        this.HotelStarRating = HotelStarRating;
    }

    public String getHotelImageUrl ()
    {
        return HotelImageUrl;
    }

    public void setHotelImageUrl (String HotelImageUrl)
    {
        this.HotelImageUrl = HotelImageUrl;
    }

    public String getCurrency ()
    {
        return Currency;
    }

    public void setCurrency (String Currency)
    {
        this.Currency = Currency;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [TotalSavings = "+TotalSavings+", HotelName = "+HotelName+", TravelEndDate = "+TravelEndDate+", PercentSavings = "+PercentSavings+", TotalPrice = "+TotalPrice+", PerPassengerPackagePrice = "+PerPassengerPackagePrice+", TravelStartDate = "+TravelStartDate+", PerPassengerSavings = "+PerPassengerSavings+", HotelStarRating = "+HotelStarRating+", HotelImageUrl = "+HotelImageUrl+", Currency = "+Currency+"]";
    }
}