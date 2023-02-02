package com.sia.als.mail.asynTasks;

import com.sia.als.mail.database.EmailMessage;

/**
 * Created by rish on 6/10/15.
 */
public interface ViewMailListener {

    void onPreView();

    void onPostView(EmailMessage emailMessage);

}
