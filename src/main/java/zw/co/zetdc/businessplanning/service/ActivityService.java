package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.payload.request.ActivityRequest;

import java.util.List;

public interface ActivityService {


    Activity createActivity(ActivityRequest activityRequest);

    Activity getActivityById(Long id);

    List<Activity> getAllActivities();

    Activity updateActivity(Long id, ActivityRequest activityRequest);

    void deleteActivity(Long id);


}
