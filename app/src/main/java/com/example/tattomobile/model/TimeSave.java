package com.example.tattomobile.model;

public class TimeSave {
public String slot_time="";
public String CoinsEarn="";
public  String positios="";

    public String getPositios() {
        return positios;
    }

    public void setPositios(String positios) {
        this.positios = positios;
    }

    public String getCoinsEarn() {
        return CoinsEarn;
    }

    public void setCoinsEarn(String coinsEarn) {
        CoinsEarn = coinsEarn;
    }

    public static TimeSave timeSave=null;

public static TimeSave getInstance(){

   if (timeSave == null){
       timeSave= new TimeSave();
   }
   return timeSave;
}
    public String getSlot_time() {
        return slot_time;
    }

    public void setSlot_time(String slot_time) {
        this.slot_time = slot_time;
    }
}
