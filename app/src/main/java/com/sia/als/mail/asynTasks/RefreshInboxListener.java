package com.sia.als.mail.asynTasks;

import com.sia.als.mail.database.EmailMessage;
import com.sia.als.mail.database.User;

import java.util.ArrayList;

/**
 * Created by rish on 6/10/15.
 */
public interface RefreshInboxListener {

    void onPreRefresh();

    void onPostRefresh(boolean success, ArrayList<EmailMessage> refreshedEmails, User user);

}
