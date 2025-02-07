package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.ActivityRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberRequest;
import zw.co.zetdc.businessplanning.repository.ActivityRepository;
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.service.ActivityService;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.service.TeamMemberService;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository; // Repository for database operations

    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
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

        return activityRepository.save(activity); // Save to database
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public Activity updateActivity(Long id, ActivityRequest activityRequest) {
        Activity activity = getActivityById(id);
        if (activity != null) {
            copyNonNullProperties(activityRequest, activity);
            return activityRepository.save(activity); // Save updated activity
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id); // Delete from database
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
                    log.error("Error copying properties: {}", e.getMessage(), e); // Better logging
                }
            }
        }
    }

    @Override
    @Transactional
    public Activity assignTeamMembers(Long activityId, List<Long> teamMemberIds) {
        Activity activity = getActivityById(activityId);
        if (activity != null) {
            List<TeamMember> teamMembers = teamMemberRepository.findAllById(teamMemberIds);
            activity.setAssignedTeamMembers(teamMembers);
            return activityRepository.save(activity);
        }
        return null; // Return null if the activity is not found
    }

}