package com.dario.smartecg;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dario on 31/05/2018.
 */

public final class UserSession {
    private static String userName;
    private static String age;
    private static String gender;
    private static final String USER = "user";
    private static final String AGE = "age";
    private static final String GENDER = "gender";
    private static final String PREFER_NAME = "SessionManager";

    /*
    * Costruttore privato così può essere invocato solo tramite il metodo setSession
    * che, per costruzione, può creare una sola sessione per volta.
    */
    private UserSession(String user, String ageUser, String genderUser) {
        userName=user;
        age=ageUser;
        gender=genderUser;
    }
    /*
             * Il metodo isActiveSession verifica se c'è un'istanza di sessione nella memoria persistente.
             */
    public static boolean isActiveSession(Context c) {
        if (userName != null && gender != null && age!=null) return true;
        SharedPreferences pref = c.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        userName = pref.getString(USER, null);
        age = pref.getString(AGE,null);
        gender=pref.getString(GENDER,null);
        return userName != null &&  age!=null && gender != null ;
    }

    /*
     * Il metodo setSession istanzia una nuova sessione solo se non esiste già un'istanza.
     */
    public static void setSession(Context c, String user,  String ageUser, String genderUser) {
        if (userName != null || age != null && gender!=null || user == null ||  ageUser==null ||genderUser == null  ) return;

        new UserSession(user, ageUser, genderUser);
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(USER, user);
        editor.putString(AGE, ageUser);
        editor.putString(GENDER, genderUser);


        editor.apply();
    }

    /*
     * Il metodo expire fa scadere una sessione.
     */
    public static void expireSession(Context c) {
        new UserSession(null,null, null);
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserSession.userName = userName;
    }

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        UserSession.age = age;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        UserSession.gender = gender;
    }
}
