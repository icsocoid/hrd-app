package com.sia.als.mail.asynTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sia.als.mail.database.User;
import com.sia.als.mail.network.SoapAPI;

/**
 * Created by rish on 12/6/16.
 */
public class MailAction extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "MailAction";

    private MailActionListener mailActionListener;
    private Context context;
    private boolean result = false;
    private User currentUser;
    private String mailAction;
    private String contentID;

    public MailAction(Context context, User currentUser, String mailAction, String contentID, MailActionListener mailActionListener) {
        this.mailActionListener = mailActionListener;
        this.context = context;
        this.currentUser = currentUser;
        this.mailAction = mailAction;
        this.contentID = contentID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mailActionListener.onPreMailAction();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        result = new SoapAPI(context, currentUser).performMailAction(mailAction, contentID);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mailActionListener.onPostMailAction(result);
    }
}