package com.mezyapps.reportanalyst.network_class;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mezyapps.reportanalyst.view.activity.LincenseActivity;

import java.util.HashMap;

public class LicenseSession {

        // Shared Preferences
        SharedPreferences pref;
        // Editor for Shared preferences
        Editor editor;
        // Context
        Context _context;

        // Shared pref mode
        int PRIVATE_MODE = 0;

        // Sharedpref file name
        private static final String NAME = "License";

        // All Shared Preferences Keys
        private static final String LOGIN = "IsLoggedIn";

        // User name (make variable public to access from outside)
        public static final String NAM = "name";

        // Email address (make variable public to access from outside)
        public static final String password = "pasword";

        // Constructor
        public LicenseSession(Context context){
            this._context = context;
            pref = _context.getSharedPreferences(NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        /**
         * Create login session
         * */
        public void createLicenseSession(String name, String pasword){
            // Storing login value as TRUE
            editor.putBoolean(LOGIN, true);

            // Storing name in pref
            editor.putString(NAM, name);

            // Storing email in pref
            editor.putString(password, pasword);

            // commit changes
            editor.commit();
        }

        /**
         * Check login method wil check user login status
         * If false it will redirect user to login page
         * Else won't do anything
         * */
        public void checkLogin(){
            // Check login status
            if(!this.isLicenseedIn()){
                // user is not logged in redirect him to Login Activity
                Intent i = new Intent(_context, LincenseActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                _context.startActivity(i);
            }

        }



        /**
         * Get stored session data
         * */
        public HashMap<String, String> getUserDetails(){
            HashMap<String, String> user = new HashMap<String, String>();
            // user name
            user.put(NAM, pref.getString(NAM, null));

            // user email id
            user.put(password, pref.getString(password, null));

            // return user
            return user;
        }

        /**
         * Clear session details
         * */
        public void logoutUser(){
            // Clearing all data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Loing Activity
            Intent i = new Intent(_context, LincenseActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }

        /**
         * Quick check for login
         * **/
        // Get Login State
        public boolean isLicenseedIn(){
            return pref.getBoolean(LOGIN, false);
        }
    }