package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.payload.request.ActivityRequest;
import zw.co.zetdc.businessplanning.service.ActivityService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private List<Activity> activityList = new ArrayList<>();

    @Override
    public Activity createActivity(ActivityRequest activityRequest) {
        Activity activity = Activity.builder()
                .activityName(activityRequest.getActivityName())
                .weeklyTarget(activityRequest.getWeeklyTarget())
                .actualWorkDone(activityRequest.getActualWorkDone())
                .percentageComplete(activityRequest.getPercentageComplete())
                .actualExpenditure(activityRequest.getActualExpenditure())
                .percentOfBudget(activityRequest.getPercentOfBudget())
                .remarks(activityRequest.getRemarks())
                .build();

        activityList.add(activity);
        return activity;
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityList.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null); // Return null if not found
    }

    @Override
    public List<Activity> getAllActivities() {
        return new ArrayList<>(activityList); // Return a copy of the list
    }

    @Override
    public Activity updateActivity(Long id, ActivityRequest activityRequest) {
        Activity activity = getActivityById(id);
        if (activity != null) {
            copyNonNullProperties(activityRequest, activity);
            return activity; // No need to set updatedAt and updatedBy manually if using Spring's auditing
        }
        return null; // Return null if not found
    }

    @Override
    public void deleteActivity(Long id) {
        activityList.removeIf(activity -> activity.getId().equals(id));
    }

    private void copyNonNullProperties(ActivityRequest source, Activity target) {
        for (Method method : ActivityRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = Activity.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Handle exceptions appropriately in production code
                }
            }
        }
    }
}