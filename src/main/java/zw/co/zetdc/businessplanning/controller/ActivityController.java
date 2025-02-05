package zw.co.zetdc.businessplanning.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.payload.request.ActivityRequest;
import zw.co.zetdc.businessplanning.service.ActivityService;

import java.util.List;

@Tag(name = "ACTIVITY ENDPOINTS", description = "The Activity APIs. Contains operations like create Activity, find Activity by id, find Activity by status, add members to activity,etc.")
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {


    private final ActivityService activityService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SMITIRID' ,'MANAGER', 'HEAD')")
    public ResponseEntity<Activity> createActivity(@RequestBody ActivityRequest activityRequest) {
        Activity createdActivity = activityService.createActivity(activityRequest);
        return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
    }

    @GetMapping(value = "findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SMITIRID' ,'MANAGER', 'HEAD')")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity != null) {
            return new ResponseEntity<>(activity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SMITIRID' ,'MANAGER', 'HEAD')")
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @PutMapping(value = "update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SMITIRID' ,'MANAGER', 'HEAD')")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id,
                                                   @RequestBody ActivityRequest activityRequest) {
        Activity updatedActivity = activityService.updateActivity(id, activityRequest);
        if (updatedActivity != null) {
            return new ResponseEntity<>(updatedActivity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value ="delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SMITIRID' ,'MANAGER', 'HEAD')")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}