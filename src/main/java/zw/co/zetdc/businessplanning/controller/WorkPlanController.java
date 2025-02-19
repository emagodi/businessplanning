package zw.co.zetdc.businessplanning.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;
import zw.co.zetdc.businessplanning.service.DepartmentService;
import zw.co.zetdc.businessplanning.service.WorkPlanService;

import java.util.List;

@Tag(name = "WORK PLAN ENDPOINTS", description = "The Work Plan APIs. Contains operations like create Work Plan, find Work Plan by id, find Work Plan by status, add scope to work plan,etc.")
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Slf4j
public class WorkPlanController {

    private final WorkPlanService workPlanService;
    private final DepartmentService departmentService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPERADMIN', 'ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlan> createWorkPlan(@RequestBody WorkPlanRequest workPlanRequest) {
        // Log the incoming request for debugging
        System.out.println("Received WorkPlanRequest: " + workPlanRequest);

        WorkPlan createdWorkPlan = workPlanService.createWorkPlan(workPlanRequest);
        return new ResponseEntity<>(createdWorkPlan, HttpStatus.CREATED);
    }

    @GetMapping(value = "/findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlan> getWorkPlanById(@PathVariable Long id) {
        WorkPlan workPlan = workPlanService.getWorkPlanById(id);
        if (workPlan != null) {
            return new ResponseEntity<>(workPlan, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<WorkPlan>> getAllWorkPlans() {
        List<WorkPlan> workPlans = workPlanService.getAllWorkPlans();
        return new ResponseEntity<>(workPlans, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlan> updateWorkPlan(@PathVariable Long id,
                                                   @RequestBody WorkPlanRequest workPlanRequest) {
        WorkPlan updatedWorkPlan = workPlanService.updateWorkPlan(id, workPlanRequest);
        if (updatedWorkPlan != null) {
            return new ResponseEntity<>(updatedWorkPlan, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> deleteWorkPlan(@PathVariable Long id) {
        workPlanService.deleteWorkPlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{workPlanId}/add/scope")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
    @Operation(summary = "Add scope to work plan", description = "Add school levels")
    public List<Scope> addScopes(
            @PathVariable("workPlanId") Long workPlanId,
            @RequestBody ScopeRequest scopes
    ) {
        return workPlanService.addScope(workPlanId, scopes);
    }

    @PostMapping("/scope/{scopeId}/add/teamMembers")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Add team members to scope", description = "Add team members by their IDs to the specified scope")
    public List<TeamMember> addTeamMembersToScope(
            @PathVariable("scopeId") Long scopeId,
            @RequestBody TeamMemberIdsRequest request
    ) {
        return workPlanService.addTeamMembersToScope(scopeId, request);
    }

    @GetMapping("/findBy/{week}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByWeek(@RequestParam String week) {
        return workPlanService.getWorkPlansByWeek(week);
    }

    @GetMapping("/findBy/{month}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByMonth(@RequestParam String month) {
        return workPlanService.getWorkPlansByMonth(month);
    }

    @GetMapping("/findBy/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByYear(@RequestParam String year) {
        return workPlanService.getWorkPlansByYear(year);
    }

    @GetMapping("/findBy/{week}/{month}/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByWeekMonthYear(
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlansByWeekMonthYear(week, month, year);
    }

    @GetMapping("/findBy/sectionId/{sectionId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansBySectionId(@PathVariable Long sectionId) {
        return workPlanService.getWorkPlansBySectionId(sectionId);
    }

    @GetMapping("/findBy/departmentId/{departmentId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByDepartmentId(@PathVariable Long departmentId) {
        return workPlanService.getWorkPlansByDepartmentId(departmentId);
    }

    @GetMapping("/findBy/{createdBy}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<WorkPlan>> getByCreatedBy(@PathVariable String createdBy) {
        List<WorkPlan> workPlans = workPlanService.getByCreatedBy(createdBy);
        return ResponseEntity.ok(workPlans);
    }


    @GetMapping("/findBy/departmentId/{departmentId}/{week}/{month}/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByDepartmentIdWeekMonthYear(
            @PathVariable Long departmentId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlansByDepartmentIdWeekMonthYear(departmentId, week, month, year);
    }

    @GetMapping("/findBy/sectionId/{sectionId}/{week}/{month}/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansBySectionIdWeekMonthYear(
            @PathVariable Long sectionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlansByDepartmentIdWeekMonthYear(sectionId, week, month, year);
    }


}
