package com.sia.als.mail.asynTasks;

/**
 * Created by rish on 6/10/15.
 */
public interface MasterRefreshListener {

    void onPreMasterRefresh();

    void onPostMasterRefresh(boolean success);
}
