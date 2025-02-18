package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Activity;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.payload.request.ScopeRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.WorkPlanRequest;

import java.util.List;

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
}
