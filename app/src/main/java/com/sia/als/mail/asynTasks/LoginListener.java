package com.sia.als.mail.asynTasks;


import com.sia.als.mail.database.User;

/**
 * Created by rish on 6/10/15.
 */
public interface LoginListener {

    void onPreLogin();

    void onPostLogin(boolean loginSuccess, String timeTaken, User user);

}
