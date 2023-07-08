package com.example.tattomobile.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.tattomobile.model.LoginSubModel;

public class Prefs {

    private static final String FULL_NAME = "full_name";
    private static final String ADDRESS = "Address";
    private static final String MOBILE_NUMBER = "mobile_number";
    private static final String TOKEN = "token";
    private static final String USER_ID = "user_id";
    private static final String CITY = "city";
    private static final String EMAIL_ID = "email_id";
    private static final String REFERAL_CODE = "referal_code";
    private static final String USER_IMG = "user_img";
    private static final String EARNED_POINTS = "earned_points";
    private static final String OTP_NUMBER = "otp_number";
    private static final String KEY = "key";
    private static final String SECRETS = "secrets";
    private static SharedPreferences prefs;


    public static void init(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveUser(LoginSubModel loginSubModel) {
        prefs.edit().putString(FULL_NAME, loginSubModel.getFullName()).apply();
        prefs.edit().putString(ADDRESS, loginSubModel.getAddress()).apply();
        prefs.edit().putString(TOKEN, loginSubModel.getAuthToken()).apply();
        prefs.edit().putString(USER_ID, loginSubModel.getUserId().toString()).apply();
        prefs.edit().putString(CITY, loginSubModel.getCity()).apply();
        prefs.edit().putString(EMAIL_ID, loginSubModel.getEmailId()).apply();
        prefs.edit().putString(REFERAL_CODE, loginSubModel.getUserReferalCode()).apply();
        prefs.edit().putString(USER_IMG, (String) loginSubModel.getUserImg()).apply();
        prefs.edit().putInt(EARNED_POINTS, loginSubModel.getEarnedPoints()).apply();
    }

    public static void logoutUser() {
        prefs.edit().putString(FULL_NAME, "").apply();
        prefs.edit().putString(ADDRESS, "").apply();
        prefs.edit().putString(TOKEN, "").apply();
        prefs.edit().putString(USER_ID, "").apply();
        prefs.edit().putString(CITY, "").apply();
        prefs.edit().putString(EMAIL_ID, "").apply();
        prefs.edit().putString(REFERAL_CODE, "").apply();
        prefs.edit().putString(USER_IMG, "").apply();
        prefs.edit().putInt(EARNED_POINTS, 0).apply();
    }

    public static void OtpSaved(String otp) {
        prefs.edit().putString(OTP_NUMBER, otp).apply();
    }
    public static void keySaved(String key) {
        prefs.edit().putString(KEY, key).apply();
    }
    public static void secretsSaved(String secrets) {
        prefs.edit().putString(SECRETS, secrets).apply();
    }
    public static String getUserName() {
        return prefs.getString(FULL_NAME, "");
    }

    public static String getUserEmail() {
        return prefs.getString(EMAIL_ID, "");
    }

    public static int getEarnedPoints() {
        return prefs.getInt(EARNED_POINTS, 0);
    }

    public static void logout() {
        prefs.edit().putString(FULL_NAME, "").apply();
    }

    public static boolean isLoggedIn() {
        return !getUserName().isEmpty();
    }

    public static void saveToken(String token) {
        prefs.edit().putString(TOKEN, token).apply();
    }
    public static String getToken() {
        return prefs.getString(TOKEN, "");
    }
    public static String getUserId() {
        return prefs.getString(USER_ID, "");
    }
    public static String getOTPNumber() {
        return prefs.getString(OTP_NUMBER, "");
    }
    public static String getKey() {
        return prefs.getString(KEY, "");
    }
    public static String getSecrets() {
        return prefs.getString(SECRETS, "");
    }
}