package com.sia.als.mail.asynTasks;

/**
 * Created by rish on 12/6/16.
 */
public interface MailActionListener {

    void onPreMailAction();

    void onPostMailAction(boolean success);

}
