package com.example.dokkannotif;

import android.util.Log;

import java.util.Calendar;

public class staminaManager {

    public static int currentStamina;
    public static int maxStamina;
    public static int refillTime;
    public int minute = 0;
    public int diff = 0;
    public Calendar mainCalendar = Calendar.getInstance();

    public staminaManager(int currentStamina, int maxStamina, int refillTime) {
        this.currentStamina = currentStamina;
        this.maxStamina = maxStamina;
        this.refillTime = refillTime;
        minute = mainCalendar.get(Calendar.MINUTE);
    }

    public staminaManager(int currentStamina, int maxStamina) {
        this.currentStamina = currentStamina;
        this.maxStamina = maxStamina;
        this.refillTime = 5;
        minute = mainCalendar.get(Calendar.MINUTE);
    }

    public staminaManager() {
        this.currentStamina = 1;
        this.maxStamina = 2;
        this.refillTime = 1;
        minute = mainCalendar.get(Calendar.MINUTE);
    }

    public void increaseCurrentStamina() {
        this.currentStamina++;
    }

    public static int getCurrentStamina() {
        return currentStamina;
    }

    public static int getMaxStamina() {
        return maxStamina;
    }

    public void checkTime() {
        Calendar tempCalendar = Calendar.getInstance();
        int tempMinute = tempCalendar.get(Calendar.MINUTE);
        if (tempMinute > minute) {
            diff = tempMinute - minute;
        }
        if (minute > tempMinute) {
            diff = minute - tempMinute;
        }
        if (diff == refillTime) {
            increaseCurrentStamina();
            minute = tempMinute;
        }
    }
}















