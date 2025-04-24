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
import zw.co.zetdc.businessplanning.enums.Currency;
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


    @GetMapping("/expenditure/sectionId/{sectionId}/week/{week}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the week for a section and comparison to allocated budget",
            description = "Total expenditure for the section for that particular week in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByWeekSection(
            @PathVariable Long sectionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresBySectionAndWeek(sectionId, week, month, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/expenditure/sectionId/{sectionId}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the month for a section and comparison to allocated budget",
            description = "Total expenditure for the section for that particular month in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByMonthSection(
            @PathVariable Long sectionId,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresBySectionAndMonth(sectionId, month, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/expenditure/sectionId/{sectionId}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the year for a section and comparison to allocated budget",
            description = "Total expenditure for the section for that particular year in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByYearSection(
            @PathVariable Long sectionId,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresBySectionAndYear(sectionId, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/expenditure/departmentId/{departmentId}/week/{week}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the week for a department and comparison to allocated budget",
            description = "Total expenditure for the department for that particular week in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByWeekAndDepartment(
            @PathVariable Long departmentId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresByDepartmentAndWeek(departmentId, week, month, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/expenditure/departmentId/{departmentId}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the month for a department and comparison to allocated budget",
            description = "Total expenditure for the department for that particular month in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByMonth(
            @PathVariable Long departmentId,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresByDepartmentAndMonth(departmentId, month, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/expenditure/departmentId/{departmentId}/year/{year}/currency/{currency}")
    @Operation(summary = "Total expenditure for the year for a department and comparison to allocated budget",
            description = "Total expenditure for the department for that particular year in comparison to the allocated budget")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getTotalExpendituresByYear(
            @PathVariable Long departmentId,
            @PathVariable String year,
            @PathVariable Currency currency) {

        List<Map<String, Object>> expenditures = workPlanService.getTotalExpendituresByDepartmentAndYear(departmentId, year, currency);
        return ResponseEntity.ok(expenditures);
    }

    @GetMapping("/graphs/member-workplan-count/section/{sectionId}/year/{year}")
    @Operation(summary = "Get Work Plan Count by Member for a Section, Month and Year",
            description = "Retrieves the number of work plans assigned to each team member for a specified section and year. The response includes member IDs, names, and their respective work plan counts for each month. This endpoint is useful for tracking team performance and workload distribution within the specified section.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getWorkPlanCountByMember(
            @PathVariable Long sectionId,
            @PathVariable String year) {

        List<Map<String, Object>> response = workPlanService.getWorkPlanCountByMemberForYear(sectionId, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section-summary-overdue/workplans/department/{departmentId}")
    @Operation(summary = "Get overdue tasks summary for a department's sections",
            description = "Fetches the count of overdue tasks grouped by section for the specified department.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Map<String, Object>> getOverdueTasksSummaryByDepartment(
            @PathVariable Long departmentId) {

        Map<String, Object> overdueTasksSummary = workPlanService.getOverdueTasksSummaryByDepartment(departmentId);
        return ResponseEntity.ok(overdueTasksSummary);
    }

    @GetMapping("/section-list-overdue/workplans/department/{departmentId}")
    @Operation(summary = "Get overdue work plans for a specific department",
            description = "Fetches details of overdue work plans, including work plan ID, scope details, assigned team members, target completion date, and number of overdue days for the specified department.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Map<String, Object>>> getOverdueWorkPlansByDepartment(@PathVariable Long departmentId) {
        List<Map<String, Object>> overdueWorkPlans = workPlanService.getOverdueWorkPlanDetailsByDepartment(departmentId);
        return ResponseEntity.ok(overdueWorkPlans);
    }

    @GetMapping("/division-summary-overdue/workplans/division/{divisionId}")
    @Operation(summary = "Get overdue work plans for a specific division",
            description = "Get summary details of overdue work plans, percentage contributed by each department, and total number of overdue tasks.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Map<String, Object>> getOverdueWorkPlansSummaryByDivision(@PathVariable Long divisionId) {
        // Fetch the overdue tasks summary by division
        Map<String, Object> overdueWorkPlansSummary = workPlanService.getOverdueTasksSummaryByDivision(divisionId);

        return ResponseEntity.ok(overdueWorkPlansSummary);
    }

    @GetMapping("/division-summary/division/{divisionId}/week/{week}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Get work plan summary for a specific division",
            description = "Retrieve summary details of work plans including total work plans, overdue tasks, budget, and expenditure by division for that week of the month and particular year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlanSummaryResponse> getWorkPlanWeekSummary(
            @PathVariable Long divisionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) { // Include currency parameter
        // Fetch the work plan summary
        WorkPlanSummaryResponse workPlanSummary = workPlanService.getWorkPlanWeekSummary(divisionId, week, month, year, currency);

        return ResponseEntity.ok(workPlanSummary);
    }

    @GetMapping("/division-summary/division/{divisionId}/month/{month}/year/{year}/currency/{currency}")
    @Operation(summary = "Get work plan summary for a specific division",
            description = "Retrieve summary details of work plans including total work plans, overdue tasks, budget, and expenditure by division for that month of the year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlanSummaryResponse> getWorkPlanMonthSummary(
            @PathVariable Long divisionId,
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable Currency currency) {
        // Fetch the work plan summary
        WorkPlanSummaryResponse workPlanSummary = workPlanService.getWorkPlanMonthSummary(divisionId, month, year, currency);

        return ResponseEntity.ok(workPlanSummary);
    }

    @GetMapping("/division-summary/division/{divisionId}/year/{year}/currency/{currency}")
    @Operation(summary = "Get work plan summary for a specific division",
            description = "Retrieve summary details of work plans including total work plans, overdue tasks, budget, and expenditure by division for that year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<WorkPlanSummaryResponse> getWorkPlanYearSummary(
            @PathVariable Long divisionId,
            @PathVariable String year,
            @PathVariable Currency currency) { // Include currency parameter
        // Fetch the work plan summary
        WorkPlanSummaryResponse workPlanSummary = workPlanService.getWorkPlanYearSummary(divisionId, year, currency);

        return ResponseEntity.ok(workPlanSummary);
    }

    @GetMapping("/findBy/division/{divisionId}/week/{week}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get work plans by division ID, week, month, and year",
            description = "Retrieve all work plans for a specific division, week, month, and year.")
    public ResponseEntity<List<WorkPlan>> getWorkPlansByDivisionIdWeekMonthYear(
            @PathVariable Long divisionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        List<WorkPlan> workPlans = workPlanService.getWorkPlansByDivisionIdWeekMonthYear(divisionId, week, month, year);
        return ResponseEntity.ok(workPlans);
    }

    @GetMapping("/findBy/division/{divisionId}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get work plans by division ID, month, and year",
            description = "Retrieve all work plans for a specific division, month, and year.")
    public ResponseEntity<List<WorkPlan>> getWorkPlansByDivisionIdMonthYear(
            @PathVariable Long divisionId,
            @PathVariable String month,
            @PathVariable String year) {
        List<WorkPlan> workPlans = workPlanService.getWorkPlansByDivisionIdMonthYear(divisionId, month, year);
        return ResponseEntity.ok(workPlans);
    }

    @GetMapping("/findBy/division/{divisionId}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get work plans by division ID and year",
            description = "Retrieve all work plans for a specific division and year.")
    public ResponseEntity<List<WorkPlan>> getWorkPlansByDivisionIdYear(
            @PathVariable Long divisionId,
            @PathVariable String year) {
        List<WorkPlan> workPlans = workPlanService.getWorkPlansByDivisionIdYear(divisionId, year);
        return ResponseEntity.ok(workPlans);
    }


    @GetMapping("/overdue/division/{divisionId}/week/{week}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get overdue work plans by division ID, week, month, and year",
            description = "Retrieve all overdue work plans for a specific division, week, month, and year.")
    public ResponseEntity<List<WorkPlan>> getOverdueWorkPlans(
            @PathVariable Long divisionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        List<WorkPlan> overdueWorkPlans = workPlanService.getOverdueWorkPlans(divisionId, week, month, year);
        return ResponseEntity.ok(overdueWorkPlans);
    }

    @GetMapping("/overdue/division/{divisionId}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get overdue work plans by division ID, month, and year",
            description = "Retrieve all overdue work plans for a specific division, month, and year.")
    public ResponseEntity<List<WorkPlan>> getOverdueWorkPlansByMonthYear(
            @PathVariable Long divisionId,
            @PathVariable String month,
            @PathVariable String year) {
        List<WorkPlan> overdueWorkPlans = workPlanService.getOverdueWorkPlansByMonthYear(divisionId, month, year);
        return ResponseEntity.ok(overdueWorkPlans);
    }

    @GetMapping("/overdue-count/division/{divisionId}/week/{week}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get count of overdue work plans by division ID, week, month, and year",
            description = "Retrieve the count of overdue work plans for a specific division, week, month, and year.")
    public ResponseEntity<Long> countOverdueWorkPlans(
            @PathVariable Long divisionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        long count = workPlanService.countOverdueWorkPlans(divisionId, week, month, year);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/overdue-count/division/{divisionId}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get count of overdue work plans by division ID, month, and year",
            description = "Retrieve the count of overdue work plans for a specific division, month, and year.")
    public ResponseEntity<Long> countOverdueWorkPlansByMonthYear(
            @PathVariable Long divisionId,
            @PathVariable String month,
            @PathVariable String year) {
        long count = workPlanService.countOverdueWorkPlansByMonthYear(divisionId, month, year);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/overdue-count/division/{divisionId}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get count of overdue work plans by division ID and year",
            description = "Retrieve the count of overdue work plans for a specific division and year.")
    public ResponseEntity<Long> countOverdueWorkPlansByYear(
            @PathVariable Long divisionId,
            @PathVariable String year) {
        long count = workPlanService.countOverdueWorkPlansByYear(divisionId, year);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/overdue/tasks/emails")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get overdue tasks and emails of relevant users",
            description = "Retrieve overdue tasks along with emails of the corresponding MANAGER and SENIORMANAGER.")
    public ResponseEntity<List<OverdueTaskResponse>> getOverdueTasksWithEmails() {
        List<OverdueTaskResponse> overdueTasks = workPlanService.getOverdueTasksWithEmails();
        return ResponseEntity.ok(overdueTasks);
    }

    @GetMapping("/division/budgetVsActual/year/{year}/currency/{currency}/division/{divisionId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get budget vs actual expenditure for a specific year, currency, and division",
            description = "Retrieve a comparison of budget and actual expenditure for each month of the specified year, currency, and division.")
    public ResponseEntity<List<BudgetVsActualResponse>> getBudgetVsActualByYearCurrencyAndDivision(
            @PathVariable String year,
            @PathVariable Currency currency,
            @PathVariable Long divisionId) {
        List<BudgetVsActualResponse> budgetVsActual = workPlanService.getBudgetVsActualByYearCurrencyAndDivision(year, currency, divisionId);
        return ResponseEntity.ok(budgetVsActual);
    }

    @GetMapping("/division/budgetVsActual/year/{year}/month/{month}/currency/{currency}/division/{divisionId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get budget vs actual expenditure for a specific month and year",
            description = "Retrieve budget and actual expenditure comparison for each week of the specified month, year, currency, and division.")
    public ResponseEntity<List<BudgetVsActualMonthResponse>> getBudgetVsActualByYearMonthCurrencyAndDivision(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable Currency currency,
            @PathVariable Long divisionId) {
        List<BudgetVsActualMonthResponse> budgetVsActual = workPlanService.getBudgetVsActualByYearMonthCurrencyAndDivision(year, month, currency, divisionId);
        return ResponseEntity.ok(budgetVsActual);
    }

    @GetMapping("/division/budgetVsActual/year/{year}/month/{month}/division/{divisionId}")
    @Operation(summary = "Get budget vs actual expenditure for a specific month, year, and division",
            description = "Retrieve budget and actual expenditure comparison for both USD and ZWL.")
    public ResponseEntity<BudgetVsActualCurrencyResponse> getBudgetVsActualByYearMonthAndDivision(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable Long divisionId) {
        BudgetVsActualCurrencyResponse response = workPlanService.getBudgetVsActualByYearMonthAndDivision(year, month, divisionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/division/budgetVsActual/year/{year}/division/{divisionId}")
    @Operation(summary = "Get budget vs actual expenditure for all months of a specific year and division",
            description = "Retrieve budget and actual expenditure comparison for both USD and ZWL for each month of the year.")
    public ResponseEntity<BudgetVsActualYearResponse> getBudgetVsActualByYearAndDivision(
            @PathVariable String year,
            @PathVariable Long divisionId) {
        BudgetVsActualYearResponse response = workPlanService.getBudgetVsActualByYearAndDivision(year, divisionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workplans/summary/section/{sectionId}/week/{week}/month/{month}/year/{year}")
    @Operation(summary = "Get summary of work plans for a section filtered by week, month, and year",
            description = "Returns total work plans, completed, in-progress, cancelled, and rescheduled counts, and budget utilization for a given section and week.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<SectionWorkPlanSummaryResponse> getSectionWorkPlanSummary(
            @PathVariable Long sectionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {

        SectionWorkPlanSummaryResponse summary = workPlanService.getWorkPlanSummaryBySectionWeekMonthYear(sectionId, week, month, year);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/workplans/summary/section/{sectionId}/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<SectionWorkPlanSummaryResponse> getSectionWorkPlanSummary(
            @PathVariable Long sectionId,
            @PathVariable String month,
            @PathVariable String year) {

        SectionWorkPlanSummaryResponse summary = workPlanService.getWorkPlanSummaryBySectionMonthYear(sectionId, month, year);
        return ResponseEntity.ok(summary);
    }


    @GetMapping("/workplans/summary/section/{sectionId}/year/{year}")
    public ResponseEntity<SectionWorkPlanSummaryResponse> getSectionWorkPlanSummaryByYear(
            @PathVariable Long sectionId,
            @PathVariable String year) {

        SectionWorkPlanSummaryResponse summary = workPlanService.getWorkPlanSummaryBySectionYear(sectionId, year);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/workplans/summary/department/{departmentId}/sections")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<SectionSummaryResponse>> getSectionSummariesForDepartment(@PathVariable Long departmentId) {
        List<SectionSummaryResponse> summaries = workPlanService.getSectionSummariesForDepartment(departmentId);
        return ResponseEntity.ok(summaries);
    }


    @GetMapping("/section-summary-overdue/workplans/department/{departmentId}/notifications/")
    @Operation(summary = "Get overdue tasks summary for a department's sections",
            description = "Fetches the count of overdue tasks grouped by section for the specified department along with manager emails.")
    public ResponseEntity<List<NotificationTaskResponse>> getOverdueTasksSummaryByDepartmentEmails(@PathVariable Long departmentId) {
        List<NotificationTaskResponse> overdueTasks = workPlanService.getOverdueTasksSummaryByDepartmentEmails(departmentId);
        return ResponseEntity.ok(overdueTasks);
    }


    @GetMapping("/department/budgetVsActual/year/{year}/month/{month}/currency/{currency}/departmentId/{departmentId}")
    @Operation(summary = "Get Budget Vs Expenditure per week for the department",
            description = "Returns Budget Vs Expenditure per week for the department.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<BudgetVsActualResponse>> getBudgetVsActualByDepartment(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable Currency currency,
            @PathVariable Long departmentId) {
        List<BudgetVsActualResponse> response = workPlanService.getBudgetVsActualByYearMonthAndDepartment(year, month, currency, departmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/budgetVsActual/year/{year}/currency/{currency}/departmentId/{departmentId}")
    @Operation(summary = "Get Budget Vs Expenditure per month for the department",
            description = "Returns Budget Vs Expenditure per month for the department.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<BudgetVsActualResponse>> getBudgetVsActualByDepartment(
            @PathVariable String year,
            @PathVariable Currency currency,
            @PathVariable Long departmentId) {
        List<BudgetVsActualResponse> response = workPlanService.getBudgetVsActualByYearAndDepartment(year, currency, departmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/budgetVsActual/year/{year}/month/{month}/currency/{currency}/sectionId/{sectionId}")
    @Operation(summary = "Get Budget Vs Expenditure per week for the section",
            description = "Returns Budget Vs Expenditure per week for the section.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<BudgetVsActualResponse>> getBudgetVsActualBySection(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable Currency currency,
            @PathVariable Long sectionId) {
        List<BudgetVsActualResponse> response = workPlanService.getBudgetVsActualByYearMonthAndSection(year, month, currency, sectionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/budgetVsActual/year/{year}/currency/{currency}/sectionId/{sectionId}")
    @Operation(summary = "Get Budget Vs Expenditure per month for the section",
            description = "Returns Budget Vs Expenditure per month for the section.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<BudgetVsActualResponse>> getBudgetVsActualBySection(
            @PathVariable String year,
            @PathVariable Currency currency,
            @PathVariable Long sectionId) {
        List<BudgetVsActualResponse> response = workPlanService.getBudgetVsActualByYearAndSection(year, currency, sectionId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/count/departmentId/{departmentId}/week/{week}/month/{month}/year/{year}")
    @Operation(summary = "Get section workplan summary with names of managers for the week of that month and year ",
            description = "Get section workplan summary with names of managers for the week of that month and year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummary(
            @PathVariable Long departmentId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlanSummaryByDepartmentAndPeriod(departmentId, week, month, year);
    }


    @GetMapping("/count/departmentId/{departmentId}/month/{month}/year/{year}")
    @Operation(summary = "Get section workplan summary with names of managers for the month of that year ",
            description = "Get section workplan summary with names of managers for the month of that year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummary(
            @PathVariable Long departmentId,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getWorkPlanSummaryByDepartmentAndMonth(departmentId, month, year);
    }

    @GetMapping("/count/departmentId/{departmentId}/year/{year}")
    @Operation(summary = "Get section workplan summary with names of managers for that year ",
            description = "Get section workplan summary with names of managers for that year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndYear(
            @PathVariable Long departmentId,
            @PathVariable String year) {
        return workPlanService.getWorkPlanSummaryByDepartmentAndYear(departmentId, year);
    }

    @GetMapping("/count/team-member/sectionId/{sectionId}/week/{week}/month/{month}/year/{year}")
    @Operation(summary = "Get section workplan totals for team members per week of the month and year",
            description = "Get section workplan totals for team members per week of the month and year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<TeamMemberSummaryResponse> getTeamMemberSummary(
            @PathVariable Long sectionId,
            @PathVariable String week,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getTeamMemberSummary(sectionId, week, month, year);
    }

    @GetMapping("/count/team-member/sectionId/{sectionId}/month/{month}/year/{year}")
    @Operation(summary = "Get section workplan totals for team members per month of the year",
            description = "Get section workplan totals for team members per month of the year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByMonth(
            @PathVariable Long sectionId,
            @PathVariable String month,
            @PathVariable String year) {
        return workPlanService.getTeamMemberSummaryByMonth(sectionId, month, year);
    }

    @GetMapping("/count/team-member/sectionId/{sectionId}/year/{year}")
    @Operation(summary = "Get section workplan totals for team members per year",
            description = "Get section workplan totals for team members per year.")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByYear(
            @PathVariable Long sectionId,
            @PathVariable String year) {
        return workPlanService.getTeamMemberSummaryByYear(sectionId, year);
    }


}
