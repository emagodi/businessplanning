package zw.co.zetdc.businessplanning.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.*;
import zw.co.zetdc.businessplanning.enums.Role;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.ScopeUpdateRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;
import zw.co.zetdc.businessplanning.payload.response.*;
import zw.co.zetdc.businessplanning.repository.*;
import zw.co.zetdc.businessplanning.service.WorkPlanService;
import zw.co.zetdc.businessplanning.enums.Currency;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;

    private final ScopeRepository scopeRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final SectionRepository sectionRepository;

    private final DepartmentRepository departmentRepository;

    private final DivisionRepository divisionRepository;

    private final UserRepository userRepository;


    @Override
    @Transactional
    public WorkPlan createWorkPlan(WorkPlanRequest workPlanRequest) {
        WorkPlan workPlan = new WorkPlan();
        workPlan.setMonth(workPlanRequest.getMonth());
        workPlan.setWeek(workPlanRequest.getWeek());
        workPlan.setYear(workPlanRequest.getYear());
        workPlan.setPlanName(workPlanRequest.getPlanName());
        workPlan.setWeeklyTarget(workPlanRequest.getWeeklyTarget());
        workPlan.setActualWorkDone(workPlanRequest.getActualWorkDone());


        // Calculate percentage completion right after setting actualWorkDone
        workPlan.updatePercentageComplete();

//        workPlan.setPercentageComplete(workPlanRequest.getPercentageComplete());

        workPlan.setBudget(workPlanRequest.getBudget());
        workPlan.setActualExpenditure(workPlanRequest.getActualExpenditure());


        // Calculate percentage of budget used
        workPlan.updatePercentOfBudget();

//        workPlan.setPercentOfBudget(workPlanRequest.getPercentOfBudget());
        workPlan.setCurrency(workPlanRequest.getCurrency());
        workPlan.setRemarks(workPlanRequest.getRemarks());
        workPlan.setSectionId(workPlanRequest.getSectionId());
        workPlan.setDepartmentId(workPlanRequest.getDepartmentId());
        workPlan.setDivisionId(workPlanRequest.getDivisionId());
        workPlan.setStatus(workPlanRequest.getStatus());
        workPlan.setUnit(workPlanRequest.getUnit());
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

            // Update percentage complete if actual work done is set
            if (workPlanRequest.getActualWorkDone() != null) {
                workPlan.updatePercentageComplete(); // Calculate percentage completion
            }

            // Update percent of budget if budget or actual expenditure is set
            if (workPlanRequest.getBudget() != null || workPlanRequest.getActualExpenditure() != null) {
                workPlan.updatePercentOfBudget(); // Calculate percent of budget
            }

            return workPlanRepository.save(workPlan); // Save updated work plan
        }
        return null; // Return null if not found
    }


    private void copyNonNullProperties(WorkPlanRequest source, WorkPlan target) {
        for (Method method : WorkPlanRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    // Check for null for all types, including Integer
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = WorkPlan.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (NoSuchMethodException e) {
                    log.error("Setter not found for method: {}", method.getName(), e);
                } catch (Exception e) {
                    log.error("Error copying properties: {}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteWorkPlan(Long id) {
        workPlanRepository.deleteById(id); // Delete from database
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

    @Override
    public List<Map<String, Object>> getTotalExpendituresBySectionAndWeek(Long sectionId, String week, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndCurrency(sectionId, currency);

        // Filter work plans based on the provided week, month, and year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getWeek().equals(week) && wp.getMonth().equals(month) && wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;

        // Budget exceeding logic
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("sectionId", sectionId);
        response.put("week", week);
        response.put("month", month);
        response.put("year", year);
        response.put("currency", currency.name()); // Add currency
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getTotalExpendituresBySectionAndMonth(Long sectionId, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndCurrency(sectionId, currency);

        // Filter work plans based on the provided month and year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getMonth().equals(month) && wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;

        // Budget exceeding logic
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("sectionId", sectionId);
        response.put("month", month);
        response.put("year", year);
        response.put("currency", currency.name()); // Add currency
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getTotalExpendituresBySectionAndYear(Long sectionId, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndCurrency(sectionId, currency);

        // Filter work plans based on the provided year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;

        // Budget exceeding logic
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("sectionId", sectionId);
        response.put("year", year);
        response.put("currency", currency.name()); // Add currency
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getTotalExpendituresByDepartmentAndWeek(Long departmentId, String week, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIdAndCurrency(departmentId, currency);

        // Filter work plans based on the provided week, month, and year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getWeek().equals(week) && wp.getMonth().equals(month) && wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;

        // Budget exceeding logic
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("departmentId", departmentId);
        response.put("week", week);
        response.put("month", month);
        response.put("year", year);
        response.put("currency", currency.name()); // Add currency
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getTotalExpendituresByDepartmentAndMonth(Long departmentId, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIdAndCurrency(departmentId, currency);

        // Filter work plans based on the provided month and year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getMonth().equals(month) && wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;

        // Budget exceeding logic
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("departmentId", departmentId);
        response.put("month", month);
        response.put("year", year);
        response.put("currency", currency.name()); // Add currency
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getTotalExpendituresByDepartmentAndYear(Long departmentId, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIdAndCurrency(departmentId, currency);

        // Filter work plans based on the provided year
        List<WorkPlan> filteredWorkPlans = workPlans.stream()
                .filter(wp -> wp.getYear().equals(year))
                .collect(Collectors.toList());

        Double totalExpenditure = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getActualExpenditure)
                .sum();

        Double totalBudget = filteredWorkPlans.stream()
                .mapToDouble(WorkPlan::getBudget)
                .sum();

        Double percentOfBudgetUsed = (totalBudget > 0) ? (totalExpenditure / totalBudget) * 100 : 0.0;
        Double budgetDifference = totalExpenditure - totalBudget;
        Boolean exceedsBudget = budgetDifference > 0;
        Double percentExceeded = (totalBudget > 0) ? (budgetDifference / totalBudget) * 100 : 0.0;

        // Count how many work plans exceed budget
        long countExceedingBudget = filteredWorkPlans.stream()
                .filter(wp -> wp.getActualExpenditure() > wp.getBudget())
                .count();

        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("departmentId", departmentId);
        response.put("year", year);
        response.put("currency", currency.name());
        response.put("totalExpenditure", totalExpenditure);
        response.put("totalBudget", totalBudget);
        response.put("percentOfBudgetUsed", percentOfBudgetUsed);
        response.put("exceedsBudget", exceedsBudget);
        response.put("budgetDifference", exceedsBudget ? budgetDifference : 0.0);
        response.put("percentExceeded", exceedsBudget ? percentExceeded : 0.0);
        response.put("countExceedingBudget", countExceedingBudget); // Count of work plans exceeding budget

        return Collections.singletonList(response); // Return as a list for consistency
    }

    @Override
    public List<Map<String, Object>> getWorkPlanCountByMemberForYear(Long sectionId, String year) {
        // Fetch work plans for the specified section and year
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndYear(sectionId, year);

        // Initialize a map to hold the count of work plans per member per month
        Map<Long, Map<String, Integer>> memberWorkPlanCount = new HashMap<>();

        // Collect member IDs from the scopes in the work plans
        Set<Long> memberIds = new HashSet<>();

        for (WorkPlan wp : workPlans) {
            String month = wp.getMonth();
            for (Scope scope : wp.getScopes()) {
                for (TeamMember member : scope.getAssignedTeamMembers()) {
                    Long memberId = member.getId();
                    memberIds.add(memberId); // Collect member IDs

                    // Initialize inner map if it doesn't exist
                    memberWorkPlanCount.putIfAbsent(memberId, new HashMap<>());
                    memberWorkPlanCount.get(memberId).put(month, memberWorkPlanCount.get(memberId).getOrDefault(month, 0) + 1);
                }
            }
        }

        // Pre-fetch relevant team members from the repository
        List<TeamMember> teamMembers = teamMemberRepository.findAllById(memberIds);
        Map<Long, TeamMember> memberMap = teamMembers.stream()
                .collect(Collectors.toMap(TeamMember::getId, Function.identity()));

        // Prepare the response
        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<Long, Map<String, Integer>> entry : memberWorkPlanCount.entrySet()) {
            Long memberId = entry.getKey();
            Map<String, Integer> monthlyCounts = entry.getValue();

            TeamMember member = memberMap.get(memberId);
            if (member != null) {
                Map<String, Object> memberResponse = new HashMap<>();
                memberResponse.put("memberId", memberId);
                memberResponse.put("memberName", member.getFirstname() + " " + member.getLastname());
                memberResponse.put("sectionId", sectionId);
                memberResponse.put("year", year);
                memberResponse.put("monthlyCounts", monthlyCounts);
                response.add(memberResponse);
            }
        }

        return response;
    }

    @Override
    public Map<String, Object> getOverdueTasksSummaryByDepartment(Long departmentId) {
        // Fetch the department using the repository
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("Department not found with ID: " + departmentId));

        // Fetch all work plans associated with the specified department
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentId(departmentId);

        // Initialize the response structure
        Map<String, Object> response = new HashMap<>();
        response.put("departmentId", departmentId);
        response.put("departmentName", department.getName()); // Use the actual department name

        // Map to hold overdue counts by section
        Map<Long, String> sectionNames = new HashMap<>();
        Map<Long, Integer> overdueCounts = new HashMap<>();

        // Fetch sections for the department
        List<Section> sections = sectionRepository.findByDepartmentsId(departmentId);

        // Populate section names mapping
        for (Section section : sections) {
            sectionNames.put(section.getId(), section.getName());
        }

        // Populate overdue count map
        int totalOverdueCount = 0; // To track the total overdue count

        for (WorkPlan workPlan : workPlans) {
            // Check if the work plan status is not COMPLETED or CANCELLED
            if (workPlan.getStatus() != Status.COMPLETED && workPlan.getStatus() != Status.CANCELLED) {
                for (Scope scope : workPlan.getScopes()) {
                    if (scope.getTargetCompletionDate() != null) {
                        LocalDate targetDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        LocalDate currentDate = LocalDate.now();

                        // Check if the scope is overdue
                        if (targetDate.isBefore(currentDate)) {
                            Long sectionId = workPlan.getSectionId();

                            // Increment the overdue count for the section
                            overdueCounts.put(sectionId, overdueCounts.getOrDefault(sectionId, 0) + 1);
                            totalOverdueCount++;
                        }
                    }
                }
            }
        }

        // Build the overdue tasks summary
        List<Map<String, Object>> overdueTasks = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : overdueCounts.entrySet()) {
            Map<String, Object> taskSummary = new HashMap<>();
            taskSummary.put("sectionId", entry.getKey());
            taskSummary.put("sectionName", sectionNames.get(entry.getKey())); // Get section name
            taskSummary.put("overdueCount", entry.getValue());

            // Calculate the percentage contribution
            double percentageContribution = totalOverdueCount > 0 ?
                    (entry.getValue() / (double) totalOverdueCount) * 100 : 0.0;
            taskSummary.put("percentageContribution", Math.round(percentageContribution * 100.0) / 100.0); // Round to 2 decimal places

            overdueTasks.add(taskSummary);
        }

        response.put("overdueTasks", overdueTasks);
        return response;
    }


    @Override
    public List<Map<String, Object>> getOverdueWorkPlanDetailsByDepartment(Long departmentId) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentId(departmentId);
        List<Map<String, Object>> overdueWorkPlanDetails = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (WorkPlan workPlan : workPlans) {
            // Check if the work plan status is not COMPLETED or CANCELLED
            if (workPlan.getStatus() != Status.COMPLETED && workPlan.getStatus() != Status.CANCELLED) {
                for (Scope scope : workPlan.getScopes()) {
                    if (scope.getTargetCompletionDate() != null) {
                        LocalDate targetDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Check if the scope is overdue
                        if (targetDate.isBefore(currentDate)) {
                            long overdueDays = ChronoUnit.DAYS.between(targetDate, currentDate);

                            // Prepare the details map
                            Map<String, Object> detailMap = new HashMap<>();
                            detailMap.put("departmentName", departmentRepository.findById(departmentId)
                                    .map(Department::getName)
                                    .orElse("Unknown Department"));
                            detailMap.put("workPlanId", workPlan.getId());
                            detailMap.put("scopeDetails", scope.getDetails());
                            detailMap.put("teamMembers", scope.getAssignedTeamMembers().stream()
                                    .map(member -> member.getFirstname() + " " + member.getLastname())
                                    .collect(Collectors.toList()));
                            detailMap.put("targetCompletionDate", targetDate);
                            detailMap.put("overdueDays", overdueDays);
                            detailMap.put("sectionName", sectionRepository.findById(workPlan.getSectionId())
                                    .map(Section::getName)
                                    .orElse("Unknown Section")); // Fetch section name

                            // Add the details to the list
                            overdueWorkPlanDetails.add(detailMap);
                        }
                    }
                }
            }
        }

        return overdueWorkPlanDetails;
    }

    @Override
    public Map<String, Object> getOverdueTasksSummaryByDivision(Long divisionId) {
        // Fetch the division using the repository
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new NotFoundException("Division not found with ID: " + divisionId));

        // Initialize the response structure
        Map<String, Object> response = new HashMap<>();
        response.put("divisionId", divisionId);
        response.put("divisionName", division.getName()); // Use the actual division name

        // Map to hold overdue counts by department
        Map<Long, String> departmentNames = new HashMap<>();
        Map<Long, Integer> overdueCounts = new HashMap<>();

        // Fetch departments for the division
        List<Department> departments = division.getAssignedDepartments();

        // Populate department names mapping
        for (Department department : departments) {
            departmentNames.put(department.getId(), department.getName());
        }

        // Initialize total overdue count
        int totalOverdueCount = 0;

        // Iterate through each department to fetch their work plans
        for (Department department : departments) {
            List<WorkPlan> workPlans = workPlanRepository.findByDepartmentId(department.getId());

            for (WorkPlan workPlan : workPlans) {
                // Check if the work plan status is not COMPLETED or CANCELLED
                if (workPlan.getStatus() != Status.COMPLETED && workPlan.getStatus() != Status.CANCELLED) {
                    for (Scope scope : workPlan.getScopes()) {
                        if (scope.getTargetCompletionDate() != null) {
                            LocalDate targetDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            LocalDate currentDate = LocalDate.now();

                            // Check if the scope is overdue
                            if (targetDate.isBefore(currentDate)) {
                                Long departmentId = department.getId();

                                // Increment the overdue count for the department
                                overdueCounts.put(departmentId, overdueCounts.getOrDefault(departmentId, 0) + 1);
                                totalOverdueCount++;
                            }
                        }
                    }
                }
            }
        }

        // Build the overdue tasks summary
        List<Map<String, Object>> overdueTasks = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : overdueCounts.entrySet()) {
            Map<String, Object> taskSummary = new HashMap<>();
            taskSummary.put("departmentId", entry.getKey());
            taskSummary.put("departmentName", departmentNames.get(entry.getKey())); // Get department name
            taskSummary.put("overdueCount", entry.getValue());

            // Calculate the percentage contribution
            double percentageContribution = totalOverdueCount > 0 ?
                    (entry.getValue() / (double) totalOverdueCount) * 100 : 0.0;
            taskSummary.put("percentageContribution", Math.round(percentageContribution * 100.0) / 100.0); // Round to 2 decimal places

            overdueTasks.add(taskSummary);
        }

        response.put("overdueTasks", overdueTasks);
        return response;
    }

    @Override
    public WorkPlanSummaryResponse getWorkPlanWeekSummary(Long divisionId, String week, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDivisionIdAndWeekAndMonthAndYearAndCurrency(divisionId, week, month, year, currency);

        // Calculate totals and percentages
        int totalWorkPlans = workPlans.size();
        int totalOverdue = (int) workPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();
        double totalBudget = workPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
        double totalExpenditure = workPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
        double remainingBudget = totalBudget - totalExpenditure; // Calculate remaining budget

        // Calculate percentage of remaining budget
        double percentageRemainingBudget = totalBudget > 0 ? (remainingBudget / totalBudget) * 100 : 0.0;

        // Fetch actual division name
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new EntityNotFoundException("Division not found"));

        // Group by departments and fetch their names
        var departmentSummary = workPlans.stream()
                .collect(Collectors.groupingBy(WorkPlan::getDepartmentId))
                .entrySet().stream()
                .map(entry -> {
                    Long departmentId = entry.getKey();
                    List<WorkPlan> departmentPlans = entry.getValue();

                    double departmentBudget = departmentPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
                    int workPlansCount = departmentPlans.size();
                    int overdueTasks = (int) departmentPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();

                    double percentageBudget = (departmentBudget / totalBudget) * 100;
                    double percentageOverdue = (overdueTasks / (double) totalOverdue) * 100;

                    // Calculate remaining budget and percentage for the department
                    double departmentExpenditure = departmentPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
                    double departmentRemainingBudget = departmentBudget - departmentExpenditure;
                    double departmentPercentageRemainingBudget = departmentBudget > 0 ? (departmentRemainingBudget / departmentBudget) * 100 : 0.0;

                    // Fetch actual department name
                    Department department = departmentRepository.findById(departmentId)
                            .orElseThrow(() -> new EntityNotFoundException("Department not found"));

                    // Calculate percentages for the department
                    return new DepartmentSummaryResponse(
                            departmentId,
                            department.getName(),
                            workPlansCount,
                            overdueTasks,
                            calculatePercentageComplete(departmentPlans),
                            calculatePercentagePending(departmentPlans),
                            calculatePercentageInProgress(departmentPlans),
                            calculatePercentageCancelled(departmentPlans),
                            calculatePercentageReScheduled(departmentPlans),
                            departmentBudget,
                            percentageBudget,
                            percentageOverdue,
                            departmentRemainingBudget,
                            departmentPercentageRemainingBudget // Use the renamed variable
                    );
                })
                .collect(Collectors.toList());

        // Create response
        WorkPlanSummaryResponse response = new WorkPlanSummaryResponse();
        response.setDivisionId(divisionId);
        response.setDivisionName(division.getName());
        response.setWeek(week);
        response.setMonth(month);
        response.setYear(year);
        response.setTotalWorkPlans(totalWorkPlans);
        response.setTotalOverdue(totalOverdue);
        response.setTotalBudget(totalBudget);
        response.setTotalExpenditure(totalExpenditure);
        response.setCurrency(currency.name()); // Include currency in the response
        response.setDepartments(departmentSummary);

        return response;
    }

    @Override
    public WorkPlanSummaryResponse getWorkPlanMonthSummary(Long divisionId, String month, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDivisionIdAndMonthAndYearAndCurrency(divisionId, month, year, currency);

        // Calculate totals and percentages
        int totalWorkPlans = workPlans.size();
        int totalOverdue = (int) workPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();
        double totalBudget = workPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
        double totalExpenditure = workPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
        double remainingBudget = totalBudget - totalExpenditure; // Calculate remaining budget

        // Calculate percentage of remaining budget
        double percentageRemainingBudget = totalBudget > 0 ? (remainingBudget / totalBudget) * 100 : 0.0;

        // Fetch actual division name
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new EntityNotFoundException("Division not found"));

        // Group by departments and fetch their names
        var departmentSummary = workPlans.stream()
                .collect(Collectors.groupingBy(WorkPlan::getDepartmentId))
                .entrySet().stream()
                .map(entry -> {
                    Long departmentId = entry.getKey();
                    List<WorkPlan> departmentPlans = entry.getValue();

                    double departmentBudget = departmentPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
                    int workPlansCount = departmentPlans.size();
                    int overdueTasks = (int) departmentPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();

                    double percentageBudget = (departmentBudget / totalBudget) * 100;
                    double percentageOverdue = (overdueTasks / (double) totalOverdue) * 100;

                    // Calculate remaining budget and percentage for the department
                    double departmentExpenditure = departmentPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
                    double departmentRemainingBudget = departmentBudget - departmentExpenditure;
                    double departmentPercentageRemainingBudget = departmentBudget > 0 ? (departmentRemainingBudget / departmentBudget) * 100 : 0.0;

                    // Fetch actual department name
                    Department department = departmentRepository.findById(departmentId)
                            .orElseThrow(() -> new EntityNotFoundException("Department not found"));

                    // Calculate percentages for the department
                    return new DepartmentSummaryResponse(
                            departmentId,
                            department.getName(),
                            workPlansCount,
                            overdueTasks,
                            calculatePercentageComplete(departmentPlans),
                            calculatePercentagePending(departmentPlans),
                            calculatePercentageInProgress(departmentPlans),
                            calculatePercentageCancelled(departmentPlans),
                            calculatePercentageReScheduled(departmentPlans),
                            departmentBudget,
                            percentageBudget,
                            percentageOverdue,
                            departmentRemainingBudget,
                            departmentPercentageRemainingBudget // Use the renamed variable
                    );
                })
                .collect(Collectors.toList());

        // Create response
        WorkPlanSummaryResponse response = new WorkPlanSummaryResponse();
        response.setDivisionId(divisionId);
        response.setDivisionName(division.getName());
        response.setMonth(month);
        response.setYear(year);
        response.setTotalWorkPlans(totalWorkPlans);
        response.setTotalOverdue(totalOverdue);
        response.setTotalBudget(totalBudget);
        response.setTotalExpenditure(totalExpenditure);
        response.setCurrency(currency.name()); // Include currency in the response
        response.setDepartments(departmentSummary);

        return response;
    }


    @Override
    public WorkPlanSummaryResponse getWorkPlanYearSummary(Long divisionId, String year, Currency currency) {
        List<WorkPlan> workPlans = workPlanRepository.findByDivisionIdAndYearAndCurrency(divisionId, year, currency);

        // Calculate totals and percentages
        int totalWorkPlans = workPlans.size();
        int totalOverdue = (int) workPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();
        double totalBudget = workPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
        double totalExpenditure = workPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
        double remainingBudget = totalBudget - totalExpenditure; // Calculate remaining budget

        // Calculate percentage of remaining budget
        double percentageRemainingBudget = totalBudget > 0 ? (remainingBudget / totalBudget) * 100 : 0.0;

        // Fetch actual division name
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new EntityNotFoundException("Division not found"));

        // Group by departments and fetch their names
        var departmentSummary = workPlans.stream()
                .collect(Collectors.groupingBy(WorkPlan::getDepartmentId))
                .entrySet().stream()
                .map(entry -> {
                    Long departmentId = entry.getKey();
                    List<WorkPlan> departmentPlans = entry.getValue();

                    double departmentBudget = departmentPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
                    int workPlansCount = departmentPlans.size();
                    int overdueTasks = (int) departmentPlans.stream().filter(wp -> wp.getStatus() != Status.COMPLETED).count();

                    // Calculate remaining budget and percentage for the department
                    double departmentExpenditure = departmentPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
                    double departmentRemainingBudget = departmentBudget - departmentExpenditure;
                    double departmentPercentageRemainingBudget = departmentBudget > 0 ? (departmentRemainingBudget / departmentBudget) * 100 : 0.0;

                    // Fetch actual department name
                    String departmentName = getDepartmentName(departmentId); // Adjust this method as needed

                    // Calculate percentages for the department
                    return new DepartmentSummaryResponse(
                            departmentId,
                            departmentName,
                            workPlansCount,
                            overdueTasks,
                            calculatePercentageComplete(departmentPlans),
                            calculatePercentagePending(departmentPlans),
                            calculatePercentageInProgress(departmentPlans),
                            calculatePercentageCancelled(departmentPlans),
                            calculatePercentageReScheduled(departmentPlans),
                            departmentBudget,
                            (departmentBudget / totalBudget) * 100,
                            (overdueTasks / (double) totalOverdue) * 100,
                            departmentRemainingBudget, // Remaining budget
                            departmentPercentageRemainingBudget // Percentage of remaining budget
                    );
                })
                .collect(Collectors.toList());

        // Create response
        WorkPlanSummaryResponse response = new WorkPlanSummaryResponse();
        response.setDivisionId(divisionId);
        response.setDivisionName(division.getName());
        response.setYear(year);
        response.setTotalWorkPlans(totalWorkPlans);
        response.setTotalOverdue(totalOverdue);
        response.setTotalBudget(totalBudget);
        response.setTotalExpenditure(totalExpenditure);
        response.setCurrency(currency.name()); // Include currency in the response
        response.setDepartments(departmentSummary);

        return response;
    }

    private String getDepartmentName(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        return department.getName();
    }


    private double calculatePercentageComplete(List<WorkPlan> plans) {
        long completedCount = plans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        return totalPercentage(completedCount, plans.size());
    }


    private double calculatePercentagePending(List<WorkPlan> plans) {
        long pendingCount = plans.stream().filter(wp -> wp.getStatus() == Status.PENDING).count();
        return totalPercentage(pendingCount, plans.size());
    }

    private double calculatePercentageInProgress(List<WorkPlan> plans) {
        long inProgressCount = plans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        return totalPercentage(inProgressCount, plans.size());
    }

    private double calculatePercentageCancelled(List<WorkPlan> plans) {
        long cancelledCount = plans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        return totalPercentage(cancelledCount, plans.size());
    }

    private double calculatePercentageReScheduled(List<WorkPlan> plans) {
        long rescheduledCount = plans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();
        return totalPercentage(rescheduledCount, plans.size());
    }

    private double totalPercentage(long count, int total) {
        return total > 0 ? (count / (double) total) * 100 : 0.0;
    }

    @Override
    public List<WorkPlan> getWorkPlansByDivisionIdWeekMonthYear(Long divisionId, String week, String month, String year) {
        return workPlanRepository.findByDivisionIdAndWeekAndMonthAndYear(divisionId, week, month, year);
    }

    @Override
    public List<WorkPlan> getWorkPlansByDivisionIdMonthYear(Long divisionId, String month, String year) {
        return workPlanRepository.findByDivisionIdAndMonthAndYear(divisionId, month, year);
    }

    @Override
    public List<WorkPlan> getWorkPlansByDivisionIdYear(Long divisionId, String year) {
        return workPlanRepository.findByDivisionIdAndYear(divisionId, year);
    }

    @Override
    public List<WorkPlan> getOverdueWorkPlans(Long divisionId, String week, String month, String year) {
        return workPlanRepository.findOverdueWorkPlans(divisionId, week, month, year, Status.COMPLETED, Status.CANCELLED);
    }

    @Override
    public List<WorkPlan> getOverdueWorkPlansByMonthYear(Long divisionId, String month, String year) {
        return workPlanRepository.findOverdueWorkPlansByMonthYear(divisionId, month, year, Status.COMPLETED, Status.CANCELLED);
    }

    @Override
    public long countOverdueWorkPlans(Long divisionId, String week, String month, String year) {
        return workPlanRepository.countOverdueWorkPlans(divisionId, week, month, year, Status.COMPLETED, Status.CANCELLED);
    }


    @Override
    public long countOverdueWorkPlansByMonthYear(Long divisionId, String month, String year) {
        return workPlanRepository.countOverdueWorkPlansByMonthYear(divisionId, month, year, Status.COMPLETED, Status.CANCELLED);
    }

    @Override
    public long countOverdueWorkPlansByYear(Long divisionId, String year) {
        return workPlanRepository.countOverdueWorkPlansByYear(divisionId, year, Status.COMPLETED, Status.CANCELLED);
    }


    @Override
    public List<OverdueTaskResponse> getOverdueTasksWithEmails() {
        // Fetch overdue work plans with user emails
        List<Object[]> results = workPlanRepository.findOverdueWorkPlansWithUserEmails(Status.COMPLETED, Status.CANCELLED);
        List<OverdueTaskResponse> responseList = new ArrayList<>();

        // Process the results and map to DTOs
        for (Object[] result : results) {
            WorkPlan workPlan = (WorkPlan) result[0];
            String managerEmail = (String) result[1];
            String seniorManagerEmail = (String) result[2];

            responseList.add(new OverdueTaskResponse(workPlan, managerEmail, seniorManagerEmail));
        }

        return responseList;
    }


    @Override
    public List<BudgetVsActualResponse> getBudgetVsActualByYearCurrencyAndDivision(String year, Currency currency, Long divisionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearCurrencyAndDivision(year, currency, divisionId);
        List<BudgetVsActualResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String month = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualResponse(month, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }


    @Override
    public List<BudgetVsActualMonthResponse> getBudgetVsActualByYearMonthCurrencyAndDivision(String year, String month, Currency currency, Long divisionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearMonthCurrencyAndDivision(year, month, currency, divisionId);
        List<BudgetVsActualMonthResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String week = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualMonthResponse(week, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }

    @Override
    public BudgetVsActualCurrencyResponse getBudgetVsActualByYearMonthAndDivision(String year, String month, Long divisionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearMonthAndDivision(year, month, divisionId);
        List<BudgetVsActualCurrencyResponse.WeeklyBudgetVsActual> weeklyData = new ArrayList<>();

        for (Object[] result : results) {
            String week = (String) result[0];
            Double budgetUSD = (Double) result[1];
            Double actualUSD = (Double) result[2];
            Double budgetZWL = (Double) result[3];
            Double actualZWL = (Double) result[4];

            Double differenceUSD = actualUSD - budgetUSD;
            Double differenceZWL = actualZWL - budgetZWL;

            weeklyData.add(new BudgetVsActualCurrencyResponse.WeeklyBudgetVsActual(
                    week, budgetUSD, actualUSD, differenceUSD, budgetZWL, actualZWL, differenceZWL));
        }

        return new BudgetVsActualCurrencyResponse(month, year, divisionId, weeklyData);
    }

    @Override
    public BudgetVsActualYearResponse getBudgetVsActualByYearAndDivision(String year, Long divisionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearAndDivision(year, divisionId);
        List<BudgetVsActualYearResponse.MonthlyBudgetVsActual> monthlyData = new ArrayList<>();

        for (Object[] result : results) {
            String month = (String) result[0];
            Double budgetUSD = (Double) result[1];
            Double actualUSD = (Double) result[2];
            Double budgetZWL = (Double) result[3];
            Double actualZWL = (Double) result[4];

            Double differenceUSD = actualUSD - budgetUSD;
            Double differenceZWL = actualZWL - budgetZWL;

            monthlyData.add(new BudgetVsActualYearResponse.MonthlyBudgetVsActual(
                    month, budgetUSD, actualUSD, differenceUSD, budgetZWL, actualZWL, differenceZWL));
        }

        return new BudgetVsActualYearResponse(year, divisionId, monthlyData);
    }


    @Override
    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionWeekMonthYear(Long sectionId, String week, String month, String year) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndWeekMonthYear(sectionId, week, month, year);

        // Initialize summary fields
        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();

        // Calculate average percent of budget utilized
        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;

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
        summaryResponse.setOverallCompletionRate(overallCompletionRate);

        return summaryResponse;
    }

    @Override
    @Transactional
    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionMonthYear(Long sectionId, String month, String year) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndMonthYear(sectionId, month, year);

        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();

        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;

        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        SectionWorkPlanSummaryResponse summaryResponse = new SectionWorkPlanSummaryResponse();
        summaryResponse.setSectionId(sectionId);
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
    @Transactional
    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionYear(Long sectionId, String year) {
        List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndYear(sectionId, year);

        Long totalWorkPlans = (long) workPlans.size();
        Long completedWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
        Long inProgressWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.IN_PROGRESS).count();
        Long cancelledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.CANCELLED).count();
        Long rescheduledWorkPlans = workPlans.stream().filter(wp -> wp.getStatus() == Status.RE_SCHEDULED).count();

        Double averagePercentOfBudgetUtilized = totalWorkPlans > 0 ?
                workPlans.stream().mapToDouble(WorkPlan::getPercentOfBudget).average().orElse(0) : 0;

        Double overallCompletionRate = totalWorkPlans > 0 ?
                (completedWorkPlans.doubleValue() / totalWorkPlans) * 100 : 0;

        SectionWorkPlanSummaryResponse summaryResponse = new SectionWorkPlanSummaryResponse();
        summaryResponse.setSectionId(sectionId);
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
    public List<SectionSummaryResponse> getSectionSummariesForDepartment(Long departmentId) {
        List<Section> sections = sectionRepository.findByDepartmentId(departmentId);

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().toString(); // Get current month as String
        String currentYear = String.valueOf(currentDate.getYear()); // Convert year to String

        return sections.stream().map(section -> {
            List<WorkPlan> workPlans = workPlanRepository.findBySectionIdAndMonthAndYear(section.getId(), currentMonth, currentYear);

            // Summary calculations
            int totalWorkPlans = workPlans.size();
            int completedWorkPlans = (int) workPlans.stream().filter(wp -> wp.getStatus() == Status.COMPLETED).count();
            int pendingWorkPlans = (int) workPlans.stream().filter(wp -> wp.getStatus() == Status.PENDING).count();

            double totalBudget = workPlans.stream().mapToDouble(WorkPlan::getBudget).sum();
            double totalActualUsed = workPlans.stream().mapToDouble(WorkPlan::getActualExpenditure).sum();
            double totalBudgetZWL = 0; // Adjust if needed
            double totalActualUsedZWL = 0; // Adjust if needed

            // Fetch section head details
            User sectionHead = userRepository.findBySectionId(section.getId()); // Assume this method exists

            // Prepare section summary response
            SectionSummaryResponse response = new SectionSummaryResponse();
            response.setSectionId(section.getId());
            response.setSectionName(section.getName());
            response.setYear(Integer.parseInt(currentYear)); // If needed as Integer
            response.setTotalWorkPlans(totalWorkPlans);
            response.setCompletedWorkPlans(completedWorkPlans);
            response.setPendingWorkPlans(pendingWorkPlans);
            response.setTotalBudgetUSD(totalBudget); // Adjust based on your currency logic
            response.setTotalActualUsedUSD(totalActualUsed);
            response.setTotalBudgetZWL(totalBudgetZWL);
            response.setTotalActualUsedZWL(totalActualUsedZWL);
            response.setSectionHeadEmail(sectionHead.getEmail());
            response.setFirstname(sectionHead.getFirstname());
            response.setLastname(sectionHead.getLastname());

            return response;
        }).collect(Collectors.toList());
    }



    @Override
    @Transactional
    public List<NotificationTaskResponse> getOverdueTasksSummaryByDepartmentEmails(Long departmentId) {
        List<Object[]> results = workPlanRepository.findOverdueTasksWithEmailsByDepartment(departmentId, Status.COMPLETED, Status.CANCELLED);

        // Map to hold unique responses by section ID
        Map<Long, NotificationTaskResponse> responseMap = new HashMap<>();

        for (Object[] result : results) {
            WorkPlan workPlan = (WorkPlan) result[0];
            String managerEmail = (String) result[1];
            String seniorManagerEmail = (String) result[2];

            // Get section name using sectionId
            String sectionName = sectionRepository.findById(workPlan.getSectionId())
                    .map(section -> section.getName())
                    .orElse("Unknown Section");

            // Get department name using departmentId
            String departmentName = departmentRepository.findById(departmentId)
                    .map(department -> department.getName())
                    .orElse("Unknown Department");

            // Create or update the response in the map
            NotificationTaskResponse notificationTask = responseMap.computeIfAbsent(workPlan.getSectionId(), id ->
                    NotificationTaskResponse.builder()
                            .departmentId(departmentId)
                            .departmentName(departmentName)
                            .managerEmail(managerEmail)
                            .seniorManagerEmail(seniorManagerEmail)
                            .sectionId(id)
                            .sectionName(sectionName)
                            .overdueCount(0) // Initialize
                            .build()
            );

            // Increment the overdue count for the section
            notificationTask.setOverdueCount(notificationTask.getOverdueCount() + 1);
        }

        // Return the values as a list
        return new ArrayList<>(responseMap.values());
    }


    @Override
    @Transactional
    public List<BudgetVsActualResponse> getBudgetVsActualByYearMonthAndDepartment(String year, String month, Currency currency, Long departmentId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearMonthCurrencyAndDepartment(year, month, currency, departmentId);
        List<BudgetVsActualResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String week = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualResponse(week, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }


    @Override
    @Transactional
    public List<BudgetVsActualResponse> getBudgetVsActualByYearAndDepartment(String year, Currency currency, Long departmentId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearCurrencyAndDepartment(year, currency, departmentId);
        List<BudgetVsActualResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String month = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualResponse(month, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }


    @Override
    @Transactional
    public List<BudgetVsActualResponse> getBudgetVsActualByYearMonthAndSection(String year, String month, Currency currency, Long sectionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearMonthCurrencyAndSection(year, month, currency, sectionId);
        List<BudgetVsActualResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String week = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualResponse(week, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }



    @Override
    @Transactional
    public List<BudgetVsActualResponse> getBudgetVsActualByYearAndSection(String year, Currency currency, Long sectionId) {
        List<Object[]> results = workPlanRepository.findBudgetVsActualByYearCurrencyAndSection(year, currency, sectionId);
        List<BudgetVsActualResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            String month = (String) result[0];
            Double budget = (Double) result[1];
            Double actual = (Double) result[2];

            double difference = actual - budget;
            double percentageDifference = (budget > 0) ? (difference / budget) * 100 : 0.0;

            responseList.add(new BudgetVsActualResponse(month, budget, actual, difference, percentageDifference));
        }

        return responseList;
    }

    @Override
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndPeriod(Long departmentId, String week, String month, String year) {
        List<Object[]> results = workPlanRepository.findWorkPlanCountsBySectionAndPeriod(departmentId, week, month, year,
                Status.COMPLETED, Status.PENDING, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<SectionWorkPlanSummaryResponsePeriod> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1]; // Get the section name
            Long totalCount = (Long) result[2];
            Long completed = (Long) result[3];
            Long pending = (Long) result[4];
            Long inProgress = (Long) result[5];
            Long cancelled = (Long) result[6];
            Long reScheduled = (Long) result[7];
            Long overdue = (Long) result[8];

            // Fetch the manager details
            User manager = userRepository.findBySectionIdAndRole(sectionId, Role.MANAGER);
            String managerFirstname = manager != null ? manager.getFirstname() : "Unknown";
            String managerLastname = manager != null ? manager.getLastname() : "Unknown";

            SectionWorkPlanSummaryResponsePeriod response = SectionWorkPlanSummaryResponsePeriod.builder()
                    .sectionId(sectionId)
                    .sectionName(sectionName) // Set the section name
                    .managerFirstname(managerFirstname)
                    .managerLastname(managerLastname)
                    .totalCount(totalCount)
                    .completed(completed)
                    .pending(pending)
                    .inProgress(inProgress)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .overdue(overdue)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndMonth(Long departmentId, String month, String year) {
        List<Object[]> results = workPlanRepository.findWorkPlanCountsByDepartmentAndMonth(departmentId, month, year,
                Status.COMPLETED, Status.PENDING, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<SectionWorkPlanSummaryResponsePeriod> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1]; // Get the section name
            Long totalCount = (Long) result[2];
            Long completed = (Long) result[3];
            Long pending = (Long) result[4];
            Long inProgress = (Long) result[5];
            Long cancelled = (Long) result[6];
            Long reScheduled = (Long) result[7];
            Long overdue = (Long) result[8];

            // Fetch the manager details
            User manager = userRepository.findBySectionIdAndRole(sectionId, Role.MANAGER);
            String managerFirstname = manager != null ? manager.getFirstname() : "Unknown";
            String managerLastname = manager != null ? manager.getLastname() : "Unknown";

            SectionWorkPlanSummaryResponsePeriod response = SectionWorkPlanSummaryResponsePeriod.builder()
                    .sectionId(sectionId)
                    .sectionName(sectionName) // Set the section name
                    .managerFirstname(managerFirstname)
                    .managerLastname(managerLastname)
                    .totalCount(totalCount)
                    .completed(completed)
                    .pending(pending)
                    .inProgress(inProgress)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .overdue(overdue)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndYear(Long departmentId, String year) {
        List<Object[]> results = workPlanRepository.findWorkPlanCountsByDepartmentAndYear(departmentId, year,
                Status.COMPLETED, Status.PENDING, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<SectionWorkPlanSummaryResponsePeriod> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1]; // Get the section name
            Long totalCount = (Long) result[2];
            Long completed = (Long) result[3];
            Long pending = (Long) result[4];
            Long inProgress = (Long) result[5];
            Long cancelled = (Long) result[6];
            Long reScheduled = (Long) result[7];
            Long overdue = (Long) result[8];

            // Fetch the manager details
            User manager = userRepository.findBySectionIdAndRole(sectionId, Role.MANAGER);
            String managerFirstname = manager != null ? manager.getFirstname() : "Unknown";
            String managerLastname = manager != null ? manager.getLastname() : "Unknown";

            SectionWorkPlanSummaryResponsePeriod response = SectionWorkPlanSummaryResponsePeriod.builder()
                    .sectionId(sectionId)
                    .sectionName(sectionName) // Set the section name
                    .managerFirstname(managerFirstname)
                    .managerLastname(managerLastname)
                    .totalCount(totalCount)
                    .completed(completed)
                    .pending(pending)
                    .inProgress(inProgress)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .overdue(overdue)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }


    @Override
    public List<TeamMemberSummaryResponse> getTeamMemberSummary(Long sectionId, String week, String month, String year) {
        List<Object[]> results = workPlanRepository.findTeamMemberSummaries(sectionId, week, month, year,
                Status.COMPLETED, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<TeamMemberSummaryResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long teamMemberId = (Long) result[0];
            String firstname = (String) result[1];
            String lastname = (String) result[2];
            Integer totalWorkPlans = ((Number) result[3]).intValue();
            Integer completed = ((Number) result[4]).intValue();
            Integer inProgress = ((Number) result[5]).intValue();
            Integer overdue = ((Number) result[6]).intValue();
            Integer cancelled = ((Number) result[7]).intValue();
            Integer reScheduled = ((Number) result[8]).intValue();

            TeamMemberSummaryResponse response = TeamMemberSummaryResponse.builder()
                    .teamMemberId(teamMemberId)
                    .firstname(firstname)
                    .lastname(lastname)
                    .totalWorkPlans(totalWorkPlans)
                    .completed(completed)
                    .inProgress(inProgress)
                    .overdue(overdue)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByMonth(Long sectionId, String month, String year) {
        List<Object[]> results = workPlanRepository.findTeamMemberSummariesByMonth(sectionId, month, year,
                Status.COMPLETED, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<TeamMemberSummaryResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long teamMemberId = (Long) result[0];
            String firstname = (String) result[1];
            String lastname = (String) result[2];
            Integer totalWorkPlans = ((Number) result[3]).intValue();
            Integer completed = ((Number) result[4]).intValue();
            Integer inProgress = ((Number) result[5]).intValue();
            Integer overdue = ((Number) result[6]).intValue();
            Integer cancelled = ((Number) result[7]).intValue();
            Integer reScheduled = ((Number) result[8]).intValue();

            TeamMemberSummaryResponse response = TeamMemberSummaryResponse.builder()
                    .teamMemberId(teamMemberId)
                    .firstname(firstname)
                    .lastname(lastname)
                    .totalWorkPlans(totalWorkPlans)
                    .completed(completed)
                    .inProgress(inProgress)
                    .overdue(overdue)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }


    @Override
    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByYear(Long sectionId, String year) {
        List<Object[]> results = workPlanRepository.findTeamMemberSummariesByYear(sectionId, year,
                Status.COMPLETED, Status.IN_PROGRESS, Status.CANCELLED, Status.RE_SCHEDULED);
        List<TeamMemberSummaryResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Long teamMemberId = (Long) result[0];
            String firstname = (String) result[1];
            String lastname = (String) result[2];
            Integer totalWorkPlans = ((Number) result[3]).intValue();
            Integer completed = ((Number) result[4]).intValue();
            Integer inProgress = ((Number) result[5]).intValue();
            Integer overdue = ((Number) result[6]).intValue();
            Integer cancelled = ((Number) result[7]).intValue();
            Integer reScheduled = ((Number) result[8]).intValue();

            TeamMemberSummaryResponse response = TeamMemberSummaryResponse.builder()
                    .teamMemberId(teamMemberId)
                    .firstname(firstname)
                    .lastname(lastname)
                    .totalWorkPlans(totalWorkPlans)
                    .completed(completed)
                    .inProgress(inProgress)
                    .overdue(overdue)
                    .cancelled(cancelled)
                    .reScheduled(reScheduled)
                    .build();
            responseList.add(response);
        }
        return responseList;
    }


    @Override
    @Transactional
    public List<Map<String, Object>> getBudgetUsageByDepartment(String year, String month, String week, Currency currency, Long departmentId) {
        // Fetch actual expenditure for sections in the specified department
        List<Object[]> results = workPlanRepository.findAllSectionsWithActualExpenditureByDepartment(year, month, week, departmentId);
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Calculate total actual expenditure for the department
        double totalDepartmentExpenditure = results.stream()
                .mapToDouble(result -> (Double) result[2]) // actualExpenditure
                .sum();

        // Create the response list with percentage calculations for each section
        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1];
            Double actualExpenditure = (Double) result[2];

            // Calculate percentage contribution based on the total department expenditure
            Double percentageUsage = (totalDepartmentExpenditure > 0)
                    ? (actualExpenditure / totalDepartmentExpenditure) * 100
                    : 0.0;

            Map<String, Object> response = Map.of(
                    "sectionId", sectionId,
                    "sectionName", sectionName,
                    "actualExpenditure", actualExpenditure,
                    "percentageUsage", percentageUsage
            );

            responseList.add(response);
        }

        return responseList;
    }


    @Transactional
    @Override
    public List<Map<String, Object>> getBudgetUsageByMonth(String year, String month, Currency currency, Long departmentId) {
        // Fetch actual expenditure for sections in the specified department and month
        List<Object[]> results = workPlanRepository.findAllSectionsWithActualExpenditureByDepartmentAndMonth(year, month, departmentId);
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Calculate total actual expenditure for the department
        double totalDepartmentExpenditure = results.stream()
                .mapToDouble(result -> (Double) result[2]) // actualExpenditure
                .sum();

        // Create the response list with percentage calculations for each section
        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1];
            Double actualExpenditure = (Double) result[2];

            // Calculate percentage contribution based on the total department expenditure
            Double percentageUsage = (totalDepartmentExpenditure > 0)
                    ? (actualExpenditure / totalDepartmentExpenditure) * 100
                    : 0.0;

            Map<String, Object> response = Map.of(
                    "sectionId", sectionId,
                    "sectionName", sectionName,
                    "actualExpenditure", actualExpenditure,
                    "percentageUsage", percentageUsage
            );

            responseList.add(response);
        }

        return responseList;
    }


    @Transactional
    @Override
    public List<Map<String, Object>> getBudgetUsageByYear(String year, Currency currency, Long departmentId) {
        // Fetch actual expenditure for sections in the specified department and year
        List<Object[]> results = workPlanRepository.findAllSectionsWithActualExpenditureByDepartmentAndYear(year, departmentId);
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Calculate total actual expenditure for the department
        double totalDepartmentExpenditure = results.stream()
                .mapToDouble(result -> (Double) result[2]) // actualExpenditure
                .sum();

        // Create the response list with percentage calculations for each section
        for (Object[] result : results) {
            Long sectionId = (Long) result[0];
            String sectionName = (String) result[1];
            Double actualExpenditure = (Double) result[2];

            // Calculate percentage contribution based on the total department expenditure
            Double percentageUsage = (totalDepartmentExpenditure > 0)
                    ? (actualExpenditure / totalDepartmentExpenditure) * 100
                    : 0.0;

            Map<String, Object> response = Map.of(
                    "sectionId", sectionId,
                    "sectionName", sectionName,
                    "actualExpenditure", actualExpenditure,
                    "percentageUsage", percentageUsage
            );

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public List<OverdueEmailResponse> getOverdueEmailSummaryByDepartment(Long departmentId) {
        List<Map<String, Object>> results = workPlanRepository.findOverdueWorkPlansByDepartment(departmentId, Status.COMPLETED, Status.CANCELLED);

        Map<Long, OverdueEmailResponse> sectionMap = new HashMap<>();

        for (Map<String, Object> result : results) {
            Long sectionId = (Long) result.get("sectionId");
            String sectionName = (String) result.get("sectionName");

            OverdueEmailResponse response = sectionMap.computeIfAbsent(sectionId, id -> {
                OverdueEmailResponse newResponse = new OverdueEmailResponse();
                newResponse.setSectionId(id);
                newResponse.setSectionName(sectionName);
                newResponse.setTotalWorkPlans(0);
                newResponse.setOverdueWorkPlans(0);
                newResponse.setPercentageOverdue(0.0);
                newResponse.setSectionManager((String) result.get("sectionManager"));
                newResponse.setSeniorManagerEmail((String) result.get("seniorManagerEmail")); // Set senior manager email
                newResponse.setSectionManagerEmail((String) result.get("sectionManagerEmail")); // Set section manager email
                newResponse.setTeamMembers(new ArrayList<>());
                return newResponse;
            });

            response.setTotalWorkPlans(response.getTotalWorkPlans() + ((Number) result.get("totalWorkPlans")).intValue());
            response.setOverdueWorkPlans(response.getOverdueWorkPlans() + ((Number) result.get("overdueWorkPlans")).intValue());
            response.setPercentageOverdue((double) response.getOverdueWorkPlans() / response.getTotalWorkPlans() * 100); // Recalculate percentage

            String teamMember = (String) result.get("teamMember");
            int count = ((Number) result.get("memberCount")).intValue();
            response.getTeamMembers().add(teamMember + "(" + count + ")");
        }

        return new ArrayList<>(sectionMap.values()); // Return all sections
    }

    @Override
    public List<AboveBudgetResponse> getSectionsAboveBudget(String week, String month, String year, Long departmentId, Currency currency) {
        return workPlanRepository.findSectionsAboveBudget(week, month, year, departmentId, currency);
    }


    //Email service

    @Override
    public EmailDepartmentSectionSummaryResponse getDepartmentSectionSummary(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        EmailDepartmentSectionSummaryResponse response = new EmailDepartmentSectionSummaryResponse();
        response.setDepartmentId(departmentId);
        response.setDepartmentName(department.getName());

        // Fetch a WorkPlan associated with the Department to get the divisionId
        WorkPlan workPlan = workPlanRepository.findFirstByDepartmentId(departmentId);

        if (workPlan != null) {
            // Find Senior Manager using the divisionId from the WorkPlan
            List<Object[]> seniorManagerResults = workPlanRepository.findSeniorManagerByDivisionId(workPlan.getDivisionId());
            if (!seniorManagerResults.isEmpty()) {
                Object[] seniorManagerData = seniorManagerResults.get(0);
                response.setSeniorManagerFullName((String) seniorManagerData[0] + " " + (String) seniorManagerData[1]);
                response.setSeniorManagerEmail((String) seniorManagerData[2]);
            }
        }

        List<EmailSectionSummary> sectionSummaries = new ArrayList<>();
        List<Section> sections = sectionRepository.findByDepartmentsId(departmentId);

        for (Section section : sections) {
            EmailSectionSummary sectionSummary = new EmailSectionSummary();
            sectionSummary.setSectionName(section.getName());

            // Find Section Manager
            List<Object[]> sectionManagerResults = workPlanRepository.findSectionManagerBySectionId(section.getId());
            if (!sectionManagerResults.isEmpty()) {
                Object[] sectionManagerData = sectionManagerResults.get(0);
                sectionSummary.setSectionManagerFullName((String) sectionManagerData[0] + " " + (String) sectionManagerData[1]);
                sectionSummary.setSectionManagerEmail((String) sectionManagerData[2]);
            }

            // Overdue Tasks
            List<Object[]> overdueTaskResults = workPlanRepository.findOverdueTasksBySectionId(section.getId(), Status.COMPLETED, Status.CANCELLED);
            List<EmailOverdueTask> overdueTasks = new ArrayList<>();
            for (Object[] result : overdueTaskResults) {
                EmailOverdueTask overdueTask = new EmailOverdueTask();
                overdueTask.setWorkPlanId(((Number) result[0]).longValue());
                overdueTask.setPlanName((String) result[1]);
                List<String> teamMemberNames = new ArrayList<>();
                teamMemberNames.add((String) result[2] + " " + (String) result[3]);
                overdueTask.setTeamMemberNames(teamMemberNames);
                overdueTask.setTargetCompletionDate((Date) result[4]);

                Date targetCompletionDate = (Date) result[4];
                Date currentDate = new Date();

                // Calculate days overdue
                long daysOverdue = ChronoUnit.DAYS.between(
                        targetCompletionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                );
                overdueTask.setDaysOverdue(daysOverdue);

                overdueTasks.add(overdueTask);
            }
            sectionSummary.setOverdueTasks(overdueTasks);

            // Team Member with Highest Work Plans
            List<Object[]> highestWorkPlanResults = workPlanRepository.findTeamMemberWithHighestWorkPlans(section.getId());
            if (!highestWorkPlanResults.isEmpty()) {
                EmailTeamMemberSummary teamMemberSummary = new EmailTeamMemberSummary();
                teamMemberSummary.setTeamMemberName((String) highestWorkPlanResults.get(0)[0] + " " + (String) highestWorkPlanResults.get(0)[1]);
                teamMemberSummary.setWorkPlanCount(((Number) highestWorkPlanResults.get(0)[2]).intValue());
                sectionSummary.setTeamMemberWithHighestWorkPlans(teamMemberSummary);
            }

            // Team Member with Lowest Work Plans
            List<Object[]> lowestWorkPlanResults = workPlanRepository.findTeamMemberWithLowestWorkPlans(section.getId());
            if (!lowestWorkPlanResults.isEmpty()) {
                EmailTeamMemberSummary teamMemberSummary = new EmailTeamMemberSummary();
                teamMemberSummary.setTeamMemberName((String) lowestWorkPlanResults.get(0)[0] + " " + (String) lowestWorkPlanResults.get(0)[1]);
                teamMemberSummary.setWorkPlanCount(((Number) lowestWorkPlanResults.get(0)[2]).intValue());
                sectionSummary.setTeamMemberWithLowestWorkPlans(teamMemberSummary);
            }

            // Above Budget Work Plans
            List<Object[]> aboveBudgetResults = workPlanRepository.findAboveBudgetWorkPlansBySectionId(section.getId());
            List<EmailAboveBudgetWorkPlan> aboveBudgetWorkPlans = new ArrayList<>();
            for (Object[] result : aboveBudgetResults) {
                EmailAboveBudgetWorkPlan aboveBudgetWorkPlan = new EmailAboveBudgetWorkPlan();
                aboveBudgetWorkPlan.setPlanName((String) result[0]);
                List<String> teamMemberNames = new ArrayList<>();
                teamMemberNames.add((String) result[1] + " " + (String) result[2]);
                aboveBudgetWorkPlan.setTeamMemberNames(teamMemberNames);
                aboveBudgetWorkPlan.setBudget((Double) result[3]);
                aboveBudgetWorkPlan.setActualExpenditure((Double) result[4]);
                aboveBudgetWorkPlan.setDifference(aboveBudgetWorkPlan.getActualExpenditure() - aboveBudgetWorkPlan.getBudget());
                aboveBudgetWorkPlan.setCurrency(result[5].toString());
                aboveBudgetWorkPlans.add(aboveBudgetWorkPlan);
            }
            sectionSummary.setAboveBudgetWorkPlans(aboveBudgetWorkPlans);

            sectionSummaries.add(sectionSummary);
        }

        response.setSections(sectionSummaries);
        return response;
    }


    @Override
    @Transactional
    public List<Map<String, Object>> calculateWorkPlanContribution(String year, String month, Status status, Long departmentId) {
        List<WorkPlan> workPlans = workPlanRepository.findByDepartmentIdAndMonthAndYear(departmentId, month, year);

        // Total work plans for the department
        int totalWorkPlans = workPlans.size();

        // Calculate contributions by section
        Map<Long, Long> sectionCounts = workPlans.stream()
                .collect(Collectors.groupingBy(WorkPlan::getSectionId, Collectors.counting()));

        // Prepare response
        List<Map<String, Object>> response = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : sectionCounts.entrySet()) {
            Long sectionId = entry.getKey();
            Long count = entry.getValue();

            double percentage = totalWorkPlans > 0 ? (count.doubleValue() / totalWorkPlans) * 100 : 0;

            Map<String, Object> sectionContribution = new HashMap<>();
            sectionContribution.put("sectionId", sectionId);
            sectionContribution.put("count", count);
            sectionContribution.put("percentage", percentage);

            // You can also fetch the section name if needed
            String sectionName = sectionRepository.findById(sectionId).map(Section::getName).orElse("Unknown Section");
            sectionContribution.put("sectionName", sectionName);

            response.add(sectionContribution);
        }

        return response;
    }


    @Override
    public List<Map<String, Object>> getOverdueWorkPlanDetailsBySection(Long sectionId) {
        // Fetch work plans associated with the specified section
        List<WorkPlan> workPlans = workPlanRepository.findBySectionId(sectionId);
        List<Map<String, Object>> overdueWorkPlanDetails = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (WorkPlan workPlan : workPlans) {
            // Check if the work plan status is not COMPLETED or CANCELLED
            if (workPlan.getStatus() != Status.COMPLETED && workPlan.getStatus() != Status.CANCELLED) {
                for (Scope scope : workPlan.getScopes()) {
                    if (scope.getTargetCompletionDate() != null) {
                        LocalDate targetDate = Instant.ofEpochMilli(scope.getTargetCompletionDate().getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Check if the scope is overdue
                        if (targetDate.isBefore(currentDate)) {
                            long overdueDays = ChronoUnit.DAYS.between(targetDate, currentDate);

                            // Prepare the details map
                            Map<String, Object> detailMap = new HashMap<>();
                            detailMap.put("sectionName", sectionRepository.findById(sectionId)
                                    .map(Section::getName)
                                    .orElse("Unknown Section")); // Fetch section name
                            detailMap.put("workPlanId", workPlan.getId());
                            detailMap.put("scopeDetails", scope.getDetails());
                            detailMap.put("teamMembers", scope.getAssignedTeamMembers().stream()
                                    .map(member -> member.getFirstname() + " " + member.getLastname())
                                    .collect(Collectors.toList()));
                            detailMap.put("targetCompletionDate", targetDate);
                            detailMap.put("overdueDays", overdueDays);

                            // Add the details to the list
                            overdueWorkPlanDetails.add(detailMap);
                        }
                    }
                }
            }
        }

        return overdueWorkPlanDetails;
    }


}



