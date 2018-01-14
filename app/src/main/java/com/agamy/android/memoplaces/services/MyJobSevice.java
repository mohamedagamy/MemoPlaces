package com.agamy.android.memoplaces.services;

import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by agamy on 1/12/2018.
 */

public class MyJobSevice extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        Toast.makeText(this, "inside OnStartJob", Toast.LENGTH_SHORT).show();
        jobFinished(job,true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Toast.makeText(this, "inside OnStopJob", Toast.LENGTH_SHORT).show();
        return false;
    }
}
