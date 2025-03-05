package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;

import java.util.List;
import java.util.Map;

public interface WorkPlanService {


    WorkPlan createWorkPlan(WorkPlanRequest workPlanRequest);

    WorkPlan getWorkPlanById(Long id);

    List<WorkPlan> getAllWorkPlans();

    WorkPlan updateWorkPlan(Long id, WorkPlanRequest workPlanRequest);

    void deleteWorkPlan(Long id);

    public List<Scope> addScope(Long workPlanId, ScopeRequest request);

    public Scope getScopeById(Long id);

    Scope assignTeamMembers(Long scopeId, List<Long> teamMemberIds);

    public List<TeamMember> addTeamMembersToScope(Long scopeId, TeamMemberIdsRequest request);

    List<WorkPlan> getWorkPlansByWeek(String week);

    public List<WorkPlan> getWorkPlansByMonth(String month);

    public List<WorkPlan> getWorkPlansByYear(String year);

    List<WorkPlan> getWorkPlansByWeekMonthYear(String week, String month, String year);

    List<WorkPlan> getWorkPlansBySectionId(Long sectionId);

    List<WorkPlan> getWorkPlansByDepartmentId(Long departmentId);

    List<WorkPlan> getByCreatedBy(String createdBy);

    public List<WorkPlan> getWorkPlansByDepartmentIdWeekMonthYear(Long departmentId, String week, String month, String year);
    public List<WorkPlan> getWorkPlansBySectionIdWeekMonthYear(Long sectionId, String week, String month, String year);

    // Get overdue scopes for a work plan
    List<Scope> getOverdueScopes(Long workPlanId, Status completedStatus);

    // Get in-progress scopes for a work plan
    List<Scope> getInProgressScopes(Long workPlanId, Status completedStatus);

    // Get scopes grouped by status and team member for a work plan
    Map<String, Map<String, Long>> getScopesGroupedByStatusPerTeamMember(Long workPlanId);

    // Get time left for each scope in a work plan
    List<Map<String, Object>> getTimeLeftForScopes(Long workPlanId);


    List<Map<String, Object>> getOverdueScopesWithDays();

    List<WorkPlan> getWorkPlansWithInProgressScopes();

    List<Map<String, Object>> getTasksGroupedByStatusForTeamMember(Long teamMemberId);

    List<Scope> getOverdueTasksForTeamMember(Long teamMemberId);

    List<Scope> getScopesByTeamMemberId(Long teamMemberId);

    List<Scope> getScopesBySectionIdAndStatus(Long sectionId, Status status);

    List<Scope> getScopesByDepartmentIdAndStatus(Long departmentId, Status status);

    Long countScopesBySectionIdAndStatus(Long sectionId, Status status);

}
