package com.sia.als.mail.asynTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sia.als.mail.database.EmailMessage;
import com.sia.als.mail.database.User;
import com.sia.als.mail.network.RestAPI;

/**
 * Created by rish on 6/10/15.
 */
public class ViewMailManager extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ViewMailListener viewMailListener;
    private EmailMessage emailMessage;
    private User user;

    public ViewMailManager(User user, Context context, ViewMailListener viewMailListener, EmailMessage emailMessage) {
        this.viewMailListener = viewMailListener;
        this.context = context;
        this.emailMessage = emailMessage;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        viewMailListener.onPreView();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RestAPI restAPI = new RestAPI(user, context);
        emailMessage = restAPI.fetchEmailContent(emailMessage);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        viewMailListener.onPostView(emailMessage);
    }
}