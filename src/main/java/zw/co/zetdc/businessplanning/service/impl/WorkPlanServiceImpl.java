package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;
import zw.co.zetdc.businessplanning.repository.ScopeRepository;
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.repository.WorkPlanRepository;
import zw.co.zetdc.businessplanning.service.WorkPlanService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;

    private final ScopeRepository scopeRepository;

    private final TeamMemberRepository teamMemberRepository;


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
        workPlan.setActualExpenditure(workPlanRequest.getActualExpenditure()); // Add this line
        workPlan.setPercentOfBudget(workPlanRequest.getPercentOfBudget()); // Add this line
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



}
