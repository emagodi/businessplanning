package zw.co.zetdc.businessplanning.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.ScopeUpdateRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;
import zw.co.zetdc.businessplanning.payload.response.*;
import zw.co.zetdc.businessplanning.repository.ScopeRepository;
import zw.co.zetdc.businessplanning.repository.SectionRepository;
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.repository.WorkPlanRepository;
import zw.co.zetdc.businessplanning.service.WorkPlanService;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;

    private final ScopeRepository scopeRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final SectionRepository sectionRepository;


    @Override
    @Transactional
    public WorkPlan createWorkPlan(WorkPlanRequest workPlanRequest) {
        WorkPlan workPlan = new WorkPlan();
        workPlan.setMonth(workPlanRequest.getMonth());
        workPlan.setWeek(workPlanRequest.getWeek());
        workPlan.setYear(workPlanRequest.getYear());
        workPlan.setWeeklyTarget(workPlanRequest.getWeeklyTarget()); // Add this line
        workPlan.setActualWorkDone(workPlanRequest.getActualWorkDone()); // Add this line
        workPlan.setPercentageComplete(workPlanRequest.getPercentageComplete()); // Add this line
        workPlan.setBudget(workPlanRequest.getBudget());
        workPlan.setActualExpenditure(workPlanRequest.getActualExpenditure()); // Add this line
        workPlan.setPercentOfBudget(workPlanRequest.getPercentOfBudget()); // Add this line
        workPlan.setCurrency(workPlanRequest.getCurrency());
        workPlan.setRemarks(workPlanRequest.getRemarks()); // Add this line
        workPlan.setSectionId(workPlanRequest.getSectionId());
        workPlan.setDepartmentId(workPlanRequest.getDepartmentId());
        workPlan.setStatus(workPlanRequest.getStatus());
        workPlan.setStartDate(workPlanRequest.getStartDate());
        workPlan.setTargetCompletionDate(workPlanRequest.getTargetCompletionDate());
        workPlan.setActualCompletionDate(workPlanRequest.getActualCompletionDate());

        for (WorkPlanRequest.ScopeRequest scopeRequest : workPlanRequest.getScopes()) {
            Scope scope = new Scope();
            scope.setDetails(scopeRequest.getDetails());

            if (scopeRequest.getStartDate() != null) {
                if (scopeRequest.getStartDate().toInstant().isBefore(Instant.now())) {
                    scope.setStatus(Status.IN_PROGRESS);
                } else {
                    scope.setStatus(Status.PENDING);
                }
            }
            scope.setStatus(scopeRequest.getStatus());
            scope.setStartDate(scopeRequest.getStartDate());
            scope.setTargetCompletionDate(scopeRequest.getTargetCompletionDate());
            scope.setActualCompletionDate(scopeRequest.getActualCompletionDate());

            // Set the workPlan reference in the scope
            scope.setWorkPlan(workPlan);

            // Adding team members
            for (Long memberId : scopeRequest.getAssignedTeamMemberIds()) {
                TeamMember teamMember = teamMemberRepository.findById(memberId)
                        .orElseThrow(() -> new NotFoundException("TeamMember not found with ID: " + memberId));
                scope.getAssignedTeamMembers().add(teamMember); // Now safe to add
            }

            workPlan.getScopes().add(scope);
        }

        // Save the workPlan, which will cascade and save scopes
        return workPlanRepository.save(workPlan);
    }


    @Override
    public WorkPlan getWorkPlanById(Long id) {
        return workPlanRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<WorkPlan> getAllWorkPlans() {
        return workPlanRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public WorkPlan updateWorkPlan(Long id, WorkPlanRequest workPlanRequest) {
        WorkPlan workPlan = getWorkPlanById(id);
        if (workPlan != null) {
            copyNonNullProperties(workPlanRequest, workPlan);
            return workPlanRepository.save(workPlan); // Save updated work plan
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteWorkPlan(Long id) {
        workPlanRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(WorkPlanRequest source, WorkPlan target) {
        for (Method method : WorkPlanRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = WorkPlan.class.getDeclaredMethod(setterName, method.getReturnType());
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
    public List<Scope> addScope(Long workPlanId, ScopeRequest request) {
        WorkPlan existingWorkPlan = workPlanRepository.findById(workPlanId)
                .orElseThrow(() -> new NotFoundException("WorkPlan with id:: " + workPlanId + " not found"));

        Scope scope = new Scope();
        scope.setDetails(request.getDetails());
        scope.setStatus(request.getStatus());
        scope.setStartDate(request.getStartDate());
        scope.setTargetCompletionDate(request.getTargetCompletionDate());
        scope.setActualCompletionDate(request.getActualCompletionDate());
        scope.setWorkPlan(existingWorkPlan);

        // Initialize the assignedTeamMembers list if it's not already done
        if (scope.getAssignedTeamMembers() == null) {
            scope.setAssignedTeamMembers(new ArrayList<>());
        }

        // Adding team members based on IDs received in the request
        for (Long memberId : request.getAssignedTeamMemberIds()) {
            TeamMember teamMember = teamMemberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException("TeamMember with id:: " + memberId + " not found"));
            scope.getAssignedTeamMembers().add(teamMember);
        }

        existingWorkPlan.getScopes().add(scope);
        workPlanRepository.save(existingWorkPlan);

        return existingWorkPlan.getScopes();
    }

    @Override
    public Scope getScopeById(Long id) {
        return scopeRepository.findById(id)
                .orElse(null); // Return null if not found
    }


    @Override
    @Transactional
    public Scope assignTeamMembers(Long scopeId, List<Long> teamMemberIds) {
        Scope scope = getScopeById(scopeId);
        if (scope != null) {
            List<TeamMember> teamMembers = teamMemberRepository.findAllById(teamMemberIds);
            scope.setAssignedTeamMembers(teamMembers);
            return scopeRepository.save(scope);
        }
        return null; // Return null if the activity is not found
    }


    @Override
    @Transactional
    public List<TeamMember> addTeamMembersToScope(Long scopeId, TeamMemberIdsRequest request) {
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new NotFoundException("Scope with id:: " + scopeId + " not found"));

        // Initialize the assignedTeamMembers list if it's not already done
        if (scope.getAssignedTeamMembers() == null) {
            scope.setAssignedTeamMembers(new ArrayList<>());
        }

        // Adding team members based on IDs received in the request
        for (Long memberId : request.getTeamMemberIds()) {
            TeamMember teamMember = teamMemberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException("TeamMember with id:: " + memberId + " not found"));
            scope.getAssignedTeamMembers().add(teamMember);
        }

        // Save the scope to persist the changes
        scopeRepository.save(scope);

        return scope.getAssignedTeamMembers(); // Return the updated list of team members
    }

    @Override
    public List<WorkPlan> getWorkPlansByWeek(String week) {
        return workPlanRepository.findByWeek(week);
    }

    @Override
    public List<WorkPlan> getWorkPlansByMonth(String month) {
        return workPlanRepository.findByMonth(month);
    }

    @Override
    public List<WorkPlan> getWorkPlansByYear(String year) {
        return workPlanRepository.findByYear(year);
    }

    @Override
    public List<WorkPlan> getWorkPlansByWeekMonthYear(String week, String month, String year) {
        return workPlanRepository.findByWeekAndMonthAndYear(week, month, year);
    }

    @Override
    public List<WorkPlan> getWorkPlansBySectionId(Long sectionId) {
        return workPlanRepository.findBySectionId(sectionId);
    }

    @Override
    public List<WorkPlan> getWorkPlansByDepartmentId(Long departmentId) {
        return workPlanRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<WorkPlan> getByCreatedBy(String createdBy) {
        return workPlanRepository.findByCreatedBy(createdBy); // Implement the method
    }


    @Override
    public List<WorkPlan> getWorkPlansByDepartmentIdWeekMonthYear(Long departmentId, String week, String month, String year) {
        return workPlanRepository.findByDepartmentIdAndWeekAndMonthAndYear(departmentId, week, month, year);
    }

    @Override
    public List<WorkPlan> getWorkPlansBySectionIdWeekMonthYear(Long sectionId, String week, String month, String year) {
        return workPlanRepository.findBySectionIdAndWeekAndMonthAndYear(sectionId, week, month, year);
    }


    // Get overdue scopes for a work plan
    @Override
    public List<Scope> getOverdueScopes(Long workPlanId, Status completedStatus) {
        return scopeRepository.findOverdueScopesByWorkPlan(workPlanId, completedStatus);
    }

    // Get in-progress scopes for a work plan
    @Override
    public List<Scope> getInProgressScopes(Long workPlanId, Status completedStatus) {
        return scopeRepository.findInProgressScopesByWorkPlan(workPlanId, completedStatus);
    }

    // Get scopes grouped by status and team member for a work plan
    @Override
    public Map<String, Map<String, Long>> getScopesGroupedByStatusPerTeamMember(Long workPlanId) {
        List<Object[]> results = scopeRepository.findScopesGroupedByStatusAndTeamMemberByWorkPlan(workPlanId);

        // Structure the response
        Map<String, Map<String, Long>> groupedScopes = new HashMap<>();

        for (Object[] result : results) {
            String status = ((Status) result[0]).name(); // Convert Enum to String
            String teamMember = ((TeamMember) result[1]).getFirstname() + ((TeamMember) result[1]).getLastname(); // Assuming TeamMember has a getName() method
            Long count = (Long) result[2];

            groupedScopes
                    .computeIfAbsent(teamMember, k -> new HashMap<>())
                    .put(status, count);
        }

        return groupedScopes;
    }

    // Get time left for each scope in a work plan
    @Override
    public List<Map<String, Object>> getTimeLeftForScopes(Long workPlanId) {
        List<Scope> scopes = scopeRepository.findByWorkPlanId(workPlanId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Scope scope : scopes) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), scope.getTargetCompletionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            Map<String, Object> scopeInfo = new HashMap<>();
            scopeInfo.put("scopeId", scope.getId());
            scopeInfo.put("details", scope.getDetails());
            scopeInfo.put("daysLeft", daysLeft); // Will be negative if target date has passed
            scopeInfo.put("status", scope.getStatus());

            response.add(scopeInfo);
        }

        return response;
    }


    @Override
    public List<Map<String, Object>> getOverdueScopesWithDays() {
        List<Scope> overdueScopes = scopeRepository.findAllOverdueScopes();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Scope scope : overdueScopes) {
            Map<String, Object> scopeDetails = new HashMap<>();
            scopeDetails.put("scopeId", scope.getId());
            scopeDetails.put("details", scope.getDetails());
            scopeDetails.put("status", scope.getStatus());
            scopeDetails.put("startDate", scope.getStartDate());
            scopeDetails.put("targetCompletionDate", scope.getTargetCompletionDate());
            scopeDetails.put("actualCompletionDate", scope.getActualCompletionDate());

            // Calculate overdue days
            long overdueDays = ChronoUnit.DAYS.between(scope.getTargetCompletionDate().toInstant(), Instant.now());
            scopeDetails.put("overdueDays", overdueDays);

            result.add(scopeDetails);
        }

        return result;
    }


    @Override
    public List<WorkPlan> getWorkPlansWithInProgressScopes() {
        return workPlanRepository.findAllWorkPlansWithInProgressScopes();
    }

    @Override
    public List<Map<String, Object>> getTasksGroupedByStatusForTeamMember(Long teamMemberId) {
        return workPlanRepository.findTasksGroupedByStatusForTeamMember(teamMemberId);
    }

    @Override
    public List<Scope> getOverdueTasksForTeamMember(Long teamMemberId) {
        return workPlanRepository.findOverdueTasksForTeamMember(teamMemberId);
    }

    @Override
    public List<Scope> getScopesByTeamMemberId(Long teamMemberId) {
        List<Scope> allScopes = scopeRepository.findAll(); // Fetch all scopes from the database
        List<Scope> memberScopes = new ArrayList<>();

        for (Scope scope : allScopes) {
            for (TeamMember member : scope.getAssignedTeamMembers()) {
                if (member.getId().equals(teamMemberId)) {
                    memberScopes.add(scope);
                    break; // No need to continue checking other members for this scope
                }
            }
        }

        return memberScopes;
    }

    @Override
    public List<Scope> getScopesBySectionIdAndStatus(Long sectionId, Status status) {
        return scopeRepository.findScopesBySectionIdAndStatus(sectionId, status);
    }

    @Override
    public List<Scope> getScopesByDepartmentIdAndStatus(Long departmentId, Status status) {
        return scopeRepository.findScopesByDepartmentIdAndStatus(departmentId, status);
    }

    @Override
    public Long countScopesBySectionIdAndStatus(Long sectionId, Status status) {
        return scopeRepository.countScopesBySectionIdAndStatus(sectionId, status);
    }

    @Override
    public Long countScopesByDepartmentIdAndStatus(Long departmentId, Status status) {
        return scopeRepository.countScopesByDepartmentIdAndStatus(departmentId, status);
    }

    @Override
    @Transactional
    public Scope updateScope(Long scopeId, ScopeUpdateRequest scopeUpdateRequest) {
        Scope existingScope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new NotFoundException("Scope not found with ID: " + scopeId));

        copyNonNullProperties(scopeUpdateRequest, existingScope);

        return scopeRepository.save(existingScope);
    }

    private void copyNonNullProperties(ScopeUpdateRequest source, Scope target) {
        for (Method method : ScopeUpdateRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = Scope.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    log.error("Error copying properties: {}", e.getMessage(), e); // Better logging
                }
            }
        }
    }

    @Override
    public ScopeStatusResponse getScopeStatus(Long scopeId) {
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new NotFoundException("Scope not found with ID: " + scopeId));

        LocalDate currentDate = LocalDate.now();
        LocalDate targetCompletionDate = null;

        if (scope.getTargetCompletionDate() != null) {
            // Convert Date to LocalDate
            targetCompletionDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        ScopeStatusResponse response = new ScopeStatusResponse();
        response.setScope(scope);

        if (targetCompletionDate != null) {
            long daysOverdue = ChronoUnit.DAYS.between(targetCompletionDate, currentDate);
            if (daysOverdue > 0) {
                response.setDueStatus("OVER_DUE");
                response.setDaysOverdue(daysOverdue);
            } else {
                response.setDueStatus("ON_TRACK");
                response.setDaysOverdue(0);
            }
        } else {
            response.setDueStatus("NO_DUE_DATE");
            response.setDaysOverdue(0);
        }

        return response;
    }


    @Override
    public List<ScopeStatusResponse> getOverdueScopesByTeamMemberId(Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new NotFoundException("Team member not found with ID: " + teamMemberId));

        List<Scope> scopes = scopeRepository.findByAssignedTeamMembersContaining(teamMember);

        LocalDate currentDate = LocalDate.now();

        return scopes.stream()
                .filter(scope -> scope.getTargetCompletionDate() != null)
                .map(scope -> {
                    LocalDate targetCompletionDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    long daysOverdue = ChronoUnit.DAYS.between(targetCompletionDate, currentDate);
                    if (daysOverdue > 0) {
                        ScopeStatusResponse response = new ScopeStatusResponse();
                        response.setScope(scope);
                        response.setDueStatus("OVER_DUE");
                        response.setDaysOverdue(daysOverdue);
                        return response;
                    } else {
                        return null; // Filter out non-overdue items
                    }
                })
                .filter(response -> response != null) // Remove null responses
                .collect(Collectors.toList());
    }

    @Override
    public WorkPlanScopeResponse getWorkPlanByScopeId(Long scopeId) {
        // Fetch the scope by its ID
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new NotFoundException("Scope not found with ID: " + scopeId));

        // Get the associated WorkPlan
        WorkPlan workPlan = scope.getWorkPlan();
        if (workPlan == null) {
            throw new NotFoundException("WorkPlan not found for the given Scope ID: " + scopeId);
        }

        // Create the response object
        WorkPlanScopeResponse response = new WorkPlanScopeResponse();
        response.setWorkPlan(workPlan);

        // Set only the specific scope in the response (not as a list)
        response.setScope(scope);

        // Set the assigned team members for the specific scope
        response.setAssignedTeamMembers(scope.getAssignedTeamMembers());

        return response; // Return the formatted response
    }

    @Override
    public List<WorkPlan> getWorkPlansByYearAndDepartment(String year, Long departmentId, String quarter, Status status) {
        List<String> months = new ArrayList<>();

        // Determine the months based on the quarter
        switch (quarter.toUpperCase()) {
            case "Q1":
                months.add("January");
                months.add("February");
                months.add("March");
                break;
            case "Q2":
                months.add("April");
                months.add("May");
                months.add("June");
                break;
            case "Q3":
                months.add("July");
                months.add("August");
                months.add("September");
                break;
            case "Q4":
                months.add("October");
                months.add("November");
                months.add("December");
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Fetch work plans based on year, department, months, and status
        return workPlanRepository.findByYearAndDepartmentIdAndMonthsAndStatus(year, departmentId, months, status);
    }

    @Override
    public Long getWorkPlanCountByYearAndDepartment(String year, Long departmentId, String quarter, Status status) {
        List<String> months = new ArrayList<>();

        // Determine the months based on the quarter
        switch (quarter.toUpperCase()) {
            case "Q1":
                months.add("January");
                months.add("February");
                months.add("March");
                break;
            case "Q2":
                months.add("April");
                months.add("May");
                months.add("June");
                break;
            case "Q3":
                months.add("July");
                months.add("August");
                months.add("September");
                break;
            case "Q4":
                months.add("October");
                months.add("November");
                months.add("December");
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Count work plans based on year, department, months, and status
        return workPlanRepository.countByYearAndDepartmentIdAndMonthsAndStatus(year, departmentId, months, status);
    }

    @Override
    public Long getWorkPlanCountByYearAndSection(String year, Long sectionId, String quarter, Status status) {
        List<String> months = new ArrayList<>();

        // Determine the months based on the quarter
        switch (quarter.toUpperCase()) {
            case "Q1":
                months.add("January");
                months.add("February");
                months.add("March");
                break;
            case "Q2":
                months.add("April");
                months.add("May");
                months.add("June");
                break;
            case "Q3":
                months.add("July");
                months.add("August");
                months.add("September");
                break;
            case "Q4":
                months.add("October");
                months.add("November");
                months.add("December");
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Count work plans based on year, section, months, and status
        return workPlanRepository.countByYearAndSectionIdAndMonthsAndStatus(year, sectionId, months, status);
    }


    @Override
    public List<WorkPlan> getWorkPlansByYearAndSection(String year, Long sectionId, String quarter, Status status) {
        List<String> months = new ArrayList<>();

        // Determine the months based on the quarter
        switch (quarter.toUpperCase()) {
            case "Q1":
                months.add("January");
                months.add("February");
                months.add("March");
                break;
            case "Q2":
                months.add("April");
                months.add("May");
                months.add("June");
                break;
            case "Q3":
                months.add("July");
                months.add("August");
                months.add("September");
                break;
            case "Q4":
                months.add("October");
                months.add("November");
                months.add("December");
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Fetch work plans based on year, section, months, and status
        return workPlanRepository.findByYearAndSectionIdAndMonthsAndStatus(year, sectionId, months, status);
    }

    @Override
    public DepartmentWorkPlanSummaryResponse getWorkPlanSummaryByDepartment(Long departmentId) {
        // Fetch work plans for the department
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentId(departmentId);

        // Initialize summary fields
        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();

        // Calculate average percent of budget utilized
        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream()
                        .mapToDouble(WorkPlan::getPercentOfBudget)
                        .average()
                        .orElse(0) : 0;

        // Calculate total budget allocated using the int weeklyTarget
        Double totalBudgetAllocated = workPlans.stream()
                .mapToDouble(WorkPlan::getWeeklyTarget)
                .sum();

        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        // Create the response object
        DepartmentWorkPlanSummaryResponse summaryResponse = new DepartmentWorkPlanSummaryResponse();
        summaryResponse.setDepartmentId(departmentId);
        summaryResponse.setTotalWorkPlans(totalWorkPlans);
        summaryResponse.setCompletedWorkPlans(completedWorkPlans);
        summaryResponse.setInProgressWorkPlans(inProgressWorkPlans);
        summaryResponse.setCancelledWorkPlans(cancelledWorkPlans);
        summaryResponse.setRescheduledWorkPlans(rescheduledWorkPlans);
        summaryResponse.setAveragePercentOfBudgetUtilized(averagePercentOfBudgetUtilized);
        summaryResponse.setOverallCompletionRate(overallCompletionRate);

        return summaryResponse;
    }

    @Override
    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySection(Long sectionId) {
        // Fetch work plans for the section
        List<WorkPlan> workPlans = workPlanRepository.findBySectionId(sectionId);

        // Get the section to retrieve the department IDs
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        // If multiple departments are associated, you might want to decide which one to use
        Long departmentId = section.getDepartments() != null && !section.getDepartments().isEmpty()
                ? section.getDepartments().get(0).getId() // Get the first department's ID
                : null;

        // Initialize summary fields
        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();

        // Calculate average percent of budget utilized
        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream()
                        .mapToDouble(WorkPlan::getPercentOfBudget)
                        .average()
                        .orElse(0) : 0;

        // Calculate overall completion rate
        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        // Create the response object
        SectionWorkPlanSummaryResponse summaryResponse = new SectionWorkPlanSummaryResponse();
        summaryResponse.setSectionId(sectionId); // Set the section ID
        summaryResponse.setTotalWorkPlans(totalWorkPlans);
        summaryResponse.setCompletedWorkPlans(completedWorkPlans);
        summaryResponse.setInProgressWorkPlans(inProgressWorkPlans);
        summaryResponse.setCancelledWorkPlans(cancelledWorkPlans);
        summaryResponse.setRescheduledWorkPlans(rescheduledWorkPlans);
        summaryResponse.setAveragePercentOfBudgetUtilized(averagePercentOfBudgetUtilized);
        summaryResponse.setOverallCompletionRate(overallCompletionRate); // Set the overall completion rate

        return summaryResponse;
    }

    @Override
    public WorkPlanPerformanceResponse getPerformanceByDepartmentAndQuarter(Long departmentId, String quarter, String year) {
        List<String> months = new ArrayList<>();

        switch (quarter.toUpperCase()) {
            case "Q1":
                months.addAll(Arrays.asList("January", "February", "March"));
                break;
            case "Q2":
                months.addAll(Arrays.asList("April", "May", "June"));
                break;
            case "Q3":
                months.addAll(Arrays.asList("July", "August", "September"));
                break;
            case "Q4":
                months.addAll(Arrays.asList("October", "November", "December"));
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Fetch work plans within the specified year and months
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIdAndMonthIn(year, departmentId, months);

        // Calculate performance metrics
        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();
        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;
        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        return WorkPlanPerformanceResponse.builder()
                .departmentId(departmentId)
                .quarter(quarter)
                .year(Integer.parseInt(year)) // Convert year to int
                .totalWorkPlans(totalWorkPlans)
                .completedWorkPlans(completedWorkPlans)
                .inProgressWorkPlans(inProgressWorkPlans)
                .cancelledWorkPlans(cancelledWorkPlans)
                .rescheduledWorkPlans(rescheduledWorkPlans)
                .averagePercentOfBudgetUtilized(averagePercentOfBudgetUtilized)
                .overallCompletionRate(overallCompletionRate)
                .build();
    }

    @Override
    public SectionWorkPlanPerformanceResponse getPerformanceBySectionAndQuarter(Long sectionId, String quarter, String year) {
        List<String> months = new ArrayList<>();

        switch (quarter.toUpperCase()) {
            case "Q1":
                months.addAll(Arrays.asList("January", "February", "March"));
                break;
            case "Q2":
                months.addAll(Arrays.asList("April", "May", "June"));
                break;
            case "Q3":
                months.addAll(Arrays.asList("July", "August", "September"));
                break;
            case "Q4":
                months.addAll(Arrays.asList("October", "November", "December"));
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }

        // Fetch work plans within the specified year and months
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndMonthIn(year, sectionId, months);

        // Calculate performance metrics
        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();
        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;
        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        return SectionWorkPlanPerformanceResponse.builder()
                .sectionId(sectionId)
                .quarter(quarter)
                .year(Integer.parseInt(year)) // Convert year to int
                .totalWorkPlans(totalWorkPlans)
                .completedWorkPlans(completedWorkPlans)
                .inProgressWorkPlans(inProgressWorkPlans)
                .cancelledWorkPlans(cancelledWorkPlans)
                .rescheduledWorkPlans(rescheduledWorkPlans)
                .averagePercentOfBudgetUtilized(averagePercentOfBudgetUtilized)
                .overallCompletionRate(overallCompletionRate)
                .build();
    }

    @Override
    public List<DepartmentWorkPlanSummaryResponse> getWorkPlanStatusByDepartments(List<Long> departmentIds) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIds(departmentIds);

        Map<Long, List<WorkPlan>> workPlansByDepartment = workPlans.stream()
                .collect(Collectors.groupingBy(WorkPlan::getDepartmentId));

        return workPlansByDepartment.entrySet().stream().map(entry -> {
            Long departmentId = entry.getKey();
            List<WorkPlan> plans = entry.getValue();
            Long totalWorkPlans = (long) plans.size();
            Long completedWorkPlans = plans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
            Long inProgressWorkPlans = plans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
            Long cancelledWorkPlans = plans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
            Long rescheduledWorkPlans = plans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();
            Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                    plans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;
            Double overallCompletionRate = totalWorkPlans > 0 ?
                    (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

            return new DepartmentWorkPlanSummaryResponse(
                    departmentId,
                    totalWorkPlans,
                    completedWorkPlans,
                    inProgressWorkPlans,
                    cancelledWorkPlans,
                    rescheduledWorkPlans,
                    averagePercentOfBudgetUtilized,
                    overallCompletionRate
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<WorkPlan> getWorkPlansByDepartmentIdMonthYear(Long departmentId, String month, String year) {
        return workPlanRepository.findByDepartmentIdAndMonthAndYear(departmentId, month, year);
    }

    @Override
    public List<WorkPlan> getWorkPlansBySectionIdMonthYear(Long sectionId, String month, String year) {
        return workPlanRepository.findBySectionIdAndMonthAndYear(sectionId, month, year);
    }

    @Override
    public Long countWorkPlansByDepartmentIdMonthYear(Long departmentId, String month, String year) {
        return workPlanRepository.countByDepartmentIdAndMonthAndYear(departmentId, month, year);
    }

    @Override
    public Long countWorkPlansBySectionIdMonthYear(Long sectionId, String month, String year) {
        return workPlanRepository.countBySectionIdAndMonthAndYear(sectionId, month, year);
    }

}
