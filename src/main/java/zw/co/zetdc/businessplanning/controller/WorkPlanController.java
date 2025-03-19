package zw.co.zetdc.businessplanning.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.ScopeUpdateRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;
import zw.co.zetdc.businessplanning.payload.response.*;
import zw.co.zetdc.businessplanning.service.DepartmentService;
import zw.co.zetdc.businessplanning.service.WorkPlanService;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/findBy/week/{week}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByWeek(@RequestParam String week) {
        return workPlanService.getWorkPlansByWeek(week);
    }

    @GetMapping("/findBy/month/{month}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByMonth(@RequestParam String month) {
        return workPlanService.getWorkPlansByMonth(month);
    }

    @GetMapping("/findBy/year/{year}")
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
        return workPlanService.getWorkPlansBySectionIdWeekMonthYear(sectionId, week, month, year);
    }
    
    // added by kuda


    // Endpoint to get overdue scopes for a work plan
    @GetMapping("/{workPlanId}/scopes/overdue")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Scope>> getOverdueScopes(@PathVariable Long workPlanId) {
        // Assuming Status.COMPLETED is an enum value for completed status
        Status completedStatus = Status.COMPLETED;
        List<Scope> overdueScopes = workPlanService.getOverdueScopes(workPlanId, completedStatus);
        return ResponseEntity.ok(overdueScopes);
    }

    // Endpoint to get in-progress scopes for a work plan
    @GetMapping("/{workPlanId}/scopes/in-progress")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Scope>> getInProgressScopes(@PathVariable Long workPlanId) {
        // Assuming Status.COMPLETED is an enum value for completed status
        Status completedStatus = Status.COMPLETED;
        List<Scope> inProgressScopes = workPlanService.getInProgressScopes(workPlanId, completedStatus);
        return ResponseEntity.ok(inProgressScopes);
    }

    // Endpoint to get scopes grouped by status and team member for a work plan
    @GetMapping("/{workPlanId}/scopes/grouped-by-status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Map<String, Map<String, Long>>> getScopesGroupedByStatusPerTeamMember(@PathVariable Long workPlanId) {
        Map<String, Map<String, Long>> groupedScopes = workPlanService.getScopesGroupedByStatusPerTeamMember(workPlanId);
        return ResponseEntity.ok(groupedScopes);
    }

    // Endpoint to get time left for each scope in a work plan
    @GetMapping("/{workPlanId}/scopes/time-left")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTimeLeftForScopes(@PathVariable Long workPlanId) {
        List<Map<String, Object>> timeLeftScopes = workPlanService.getTimeLeftForScopes(workPlanId);
        return ResponseEntity.ok(timeLeftScopes);
    }


    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getOverdueScopes() {
        List<Map<String, Object>> overdueScopes = workPlanService.getOverdueScopesWithDays();
        return ResponseEntity.ok(overdueScopes);
    }

    @GetMapping("/with-in-progress-scopes")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<WorkPlan>> getWorkPlansWithInProgressScopes() {
        List<WorkPlan> workPlans = workPlanService.getWorkPlansWithInProgressScopes();
        return ResponseEntity.ok(workPlans);
    }

    @GetMapping("/scopes/section/{sectionId}/status/{status}")
    @Operation(summary = "Get Scope By Section Id and Status",
            description = "Get all scopes for a section with particular status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Scope>> getScopesBySectionIdAndStatus(@PathVariable Long sectionId, @PathVariable Status status) {
        List<Scope> scopes = workPlanService.getScopesBySectionIdAndStatus(sectionId, status);
        return ResponseEntity.ok(scopes);
    }

    @GetMapping("/scopes/department/{departmentId}/status/{status}")
    @Operation(summary = "Get Scope By Department Id and Status",
            description = "Get all scopes for a department with particular status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Scope>> getScopesByDepartmentIdAndStatus(@PathVariable Long departmentId, @PathVariable Status status) {
        List<Scope> scopes = workPlanService.getScopesByDepartmentIdAndStatus(departmentId, status);
        return ResponseEntity.ok(scopes);
    }

    @GetMapping("/scopes/section/{sectionId}/count/status/{status}")
    @Operation(summary = "Count Scopes By Section Id and Status",
            description = "Total scopes for a section with particular status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countScopesBySectionIdAndStatus(@PathVariable Long sectionId, @PathVariable Status status) {
        Long count = workPlanService.countScopesBySectionIdAndStatus(sectionId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/scopes/department/{departmentId}/count/status/{status}")
    @Operation(summary = "Count Scopes By Department Id and Status",
            description = "Total scopes for a department with particular status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countScopesByDepartmentIdAndStatus(@PathVariable Long departmentId, @PathVariable Status status) {
        Long count = workPlanService.countScopesByDepartmentIdAndStatus(departmentId, status);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/scopes/update/{scopeId}")
    @Operation(summary = "Update Scope",
            description = "Update Scope")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Scope> updateScope(
            @PathVariable Long scopeId,
            @RequestBody ScopeUpdateRequest scopeUpdateRequest) {

        Scope updatedScope = workPlanService.updateScope(scopeId, scopeUpdateRequest);
        return ResponseEntity.ok(updatedScope);
    }

    @GetMapping("/scopes/status/{scopeId}")
    @Operation(summary = "Get Scope and Current Status Based On Date",
            description = "Check if scope is still on track")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<ScopeStatusResponse> getScopeStatus(@PathVariable Long scopeId) {
        ScopeStatusResponse response = workPlanService.getScopeStatus(scopeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scopes/overdue/team-member/{teamMemberId}")
    @Operation(summary = "Get Overdue scopes for a team member",
            description = "Get all overdue scopes for a team member using team member id")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<ScopeStatusResponse>> getOverdueScopesByTeamMemberId(@PathVariable Long teamMemberId) {
        List<ScopeStatusResponse> responses = workPlanService.getOverdueScopesByTeamMemberId(teamMemberId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/scopes/{scopeId}/workplan")
    @Operation(summary = "Get scope details by id",
            description = "Get all details of scope by id using scope id")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlanScopeResponse> getWorkPlanByScopeId(@PathVariable Long scopeId) {
        WorkPlanScopeResponse response = workPlanService.getWorkPlanByScopeId(scopeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workplans/year/{year}/departmentId/{departmentId}/quarter/{quarter}/status/{status}")
    @Operation(summary = "Get work plans by year, department id, quarter, and status",
            description = "Get all work plans for a particular quarter and status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<WorkPlan>> getWorkPlansByYearAndDepartment(
            @PathVariable String year,
            @PathVariable Long departmentId,
            @PathVariable String quarter,
            @PathVariable Status status) {

        List<WorkPlan> workPlans = workPlanService.getWorkPlansByYearAndDepartment(year, departmentId, quarter, status);
        return ResponseEntity.ok(workPlans);
    }

    @GetMapping("/workplans/count/year/{year}/departmentId/{departmentId}/quarter/{quarter}/status/{status}")
    @Operation(summary = "Get total number of work plans by year, department id, quarter, and status",
            description = "Get total number of work plans for a particular quarter by department id and status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> getWorkPlanCountByYearAndDepartment(
            @PathVariable String year,
            @PathVariable Long departmentId,
            @PathVariable String quarter,
            @PathVariable Status status) { // Use the Status enum here

        Long count = workPlanService.getWorkPlanCountByYearAndDepartment(year, departmentId, quarter, status);
        return ResponseEntity.ok(count); // Return count directly
    }


    @GetMapping("/workplans/count/year/{year}/sectionId/{sectionId}/quarter/{quarter}/status/{status}")
    @Operation(summary = "Get total number of work plans by year, section id, quarter, and status",
            description = "Get total number of work plans for a particular quarter by section id and status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> getWorkPlanCountByYearAndSection(
            @PathVariable String year,
            @PathVariable Long sectionId,
            @PathVariable String quarter,
            @PathVariable Status status) { // Use the Status enum here

        Long count = workPlanService.getWorkPlanCountByYearAndSection(year, sectionId, quarter, status);
        return ResponseEntity.ok(count); // Return count directly
    }

    @GetMapping("/workplans/year/{year}/sectionId/{sectionId}/quarter/{quarter}/status/{status}")
    @Operation(summary = "Get all work plans by year, section id, quarter, and status",
            description = "Get all work plans for a particular quarter by section id and status")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<WorkPlan>> getWorkPlansByYearAndSection(
            @PathVariable String year,
            @PathVariable Long sectionId,
            @PathVariable String quarter,
            @PathVariable Status status) { // Use the Status enum here

        List<WorkPlan> workPlans = workPlanService.getWorkPlansByYearAndSection(year, sectionId, quarter, status);
        return ResponseEntity.ok(workPlans); // Return the list of work plans
    }


    @GetMapping("/workplans/summary/department/{departmentId}")
    @Operation(summary = "Get summary of work plans for a department",
            description = "Returns total work plans, completed, in-progress, overdue counts, and budget utilization for a given department.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<DepartmentWorkPlanSummaryResponse> getDepartmentWorkPlanSummary(
            @PathVariable Long departmentId) {

        DepartmentWorkPlanSummaryResponse summary = workPlanService.getWorkPlanSummaryByDepartment(departmentId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/workplans/summary/section/{sectionId}")
    @Operation(summary = "Get summary of work plans for a section",
            description = "Returns total work plans, completed, in-progress, cancelled, and rescheduled counts, and budget utilization for a given section.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<SectionWorkPlanSummaryResponse> getSectionWorkPlanSummary(
            @PathVariable Long sectionId) {

        SectionWorkPlanSummaryResponse summary = workPlanService.getWorkPlanSummaryBySection(sectionId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/workplans/performance/department/{departmentId}/quarter/{quarter}/year/{year}")
    @Operation(summary = "Get work plan performance for a department by quarter and year",
            description = "Returns total work plans, completed, in-progress, cancelled, and rescheduled counts, and budget utilization for a given department and quarter.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlanPerformanceResponse> getWorkPlanPerformance(
            @PathVariable Long departmentId,
            @PathVariable String quarter,
            @PathVariable String year) {

        WorkPlanPerformanceResponse response = workPlanService.getPerformanceByDepartmentAndQuarter(departmentId, quarter, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workplans/performance/section/{sectionId}/quarter/{quarter}/year/{year}")
    @Operation(summary = "Get work plan performance for a section by quarter and year",
            description = "Returns total work plans, completed, in-progress, cancelled, and rescheduled counts, and budget utilization for a given section and quarter.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<SectionWorkPlanPerformanceResponse> getWorkPlanPerformanceBySection(
            @PathVariable Long sectionId,
            @PathVariable String quarter,
            @PathVariable String year) {

        SectionWorkPlanPerformanceResponse response = workPlanService.getPerformanceBySectionAndQuarter(sectionId, quarter, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workplans/status/department/{departmentIds}")
    @Operation(summary = "Get overall list of work plan summaries for departments",
            description = "Returns total work plans, completed, in-progress, cancelled, rescheduled counts, and budget utilization for specified departments.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<DepartmentWorkPlanSummaryResponse>> getWorkPlanStatusByDepartment(
            @PathVariable List<Long> departmentIds) {

        List<DepartmentWorkPlanSummaryResponse> response = workPlanService.getWorkPlanStatusByDepartments(departmentIds);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findBy/departmentId/{departmentId}/month/{month}/year/{year}")
    @Operation(summary = "Get work plan by department id, month and year",
            description = "Returns work plans by department, month and year")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansByDepartmentIdMonthYear(
            @PathVariable Long departmentId,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlansByDepartmentIdMonthYear(departmentId, month, year);
    }

    @GetMapping("/findBy/sectionId/{sectionId}/month/{month}/year/{year}")
    @Operation(summary = "Get work plan by section id, month and year",
            description = "Returns work plans by section, month and year")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<WorkPlan> getWorkPlansBySectionIdMonthYear(
            @PathVariable Long sectionId,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlansBySectionIdMonthYear(sectionId, month, year);
    }

    @GetMapping("/workplans/count/department/{departmentId}/month/{month}/year/{year}")
    @Operation(summary = "Count total number of work plans for a department by month and year",
            description = "Returns the total number of work plans for a specific department in a given month and year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countWorkPlansByDepartmentIdMonthYear(
            @PathVariable Long departmentId,
            @PathVariable String month,
            @PathVariable String year) {
        Long count = workPlanService.countWorkPlansByDepartmentIdMonthYear(departmentId, month, year);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/workplans/count/sectionId/{sectionId}/month/{month}/year/{year}")
    @Operation(summary = "Count total number of work plans for a section by month and year",
            description = "Returns the total number of work plans for a specific section in a given month and year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countWorkPlansBySectionIdMonthYear(
            @PathVariable Long sectionId,
            @PathVariable String month,
            @PathVariable String year) {
        Long count = workPlanService.countWorkPlansBySectionIdMonthYear(sectionId, month, year);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/expenditure/sectionId/{sectionId}/week/{week}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpenditures(
            @PathVariable Long sectionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresBySectionAndWeek(sectionId, week, month, year);
        return ResponseEntity.ok(expenditures);
    }



}
