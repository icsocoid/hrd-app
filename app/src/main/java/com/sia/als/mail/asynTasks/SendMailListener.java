package com.sia.als.mail.asynTasks;

/**
 * Created by rish on 18/1/16.
 */
public interface SendMailListener {

    void onPreSend();

    void onPostSend(boolean success);
}
