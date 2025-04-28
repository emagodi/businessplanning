package zw.co.zetdc.businessplanning.service;

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

    Long countScopesByDepartmentIdAndStatus(Long departmentId, Status status);

    public Scope updateScope(Long scopeId, ScopeUpdateRequest scopeUpdateRequest);

    public ScopeStatusResponse getScopeStatus(Long scopeId);

    public List<ScopeStatusResponse> getOverdueScopesByTeamMemberId(Long teamMemberId);

    public WorkPlanScopeResponse getWorkPlanByScopeId(Long scopeId);

    List<WorkPlan> getWorkPlansByYearAndDepartment(String year, Long departmentId, String quarter, Status status);

    Long getWorkPlanCountByYearAndDepartment(String year, Long departmentId, String quarter, Status status);

    Long getWorkPlanCountByYearAndSection(String year, Long sectionId, String quarter, Status status);

    List<WorkPlan> getWorkPlansByYearAndSection(String year, Long sectionId, String quarter, Status status);

    public DepartmentWorkPlanSummaryResponse getWorkPlanSummaryByDepartment(Long departmentId);

    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySection(Long sectionId);

    public WorkPlanPerformanceResponse getPerformanceByDepartmentAndQuarter(Long departmentId, String quarter, String year);

    public SectionWorkPlanPerformanceResponse getPerformanceBySectionAndQuarter(Long sectionId, String quarter, String year);

    public List<DepartmentWorkPlanSummaryResponse> getWorkPlanStatusByDepartments(List<Long> departmentIds);

    public List<WorkPlan> getWorkPlansByDepartmentIdMonthYear(Long departmentId, String month, String year);

    public List<WorkPlan> getWorkPlansBySectionIdMonthYear(Long sectionId, String month, String year);


    public Long countWorkPlansByDepartmentIdMonthYear(Long departmentId, String month, String year);

    public Long countWorkPlansBySectionIdMonthYear(Long sectionId, String month, String year);


    List<Map<String, Object>> getTotalExpendituresBySectionAndWeek(Long sectionId, String week, String month, String year, Currency currency);

    List<Map<String, Object>> getTotalExpendituresBySectionAndMonth(Long sectionId, String month, String year, Currency currency);

    List<Map<String, Object>> getTotalExpendituresBySectionAndYear(Long sectionId, String year, Currency currency);

    List<Map<String, Object>> getTotalExpendituresByDepartmentAndWeek(Long departmentId, String week, String month, String year, Currency currency);

    List<Map<String, Object>> getTotalExpendituresByDepartmentAndMonth(Long departmentId, String month, String year, Currency currency);

    List<Map<String, Object>> getTotalExpendituresByDepartmentAndYear(Long departmentId, String year, Currency currency);

    List<Map<String, Object>> getWorkPlanCountByMemberForYear(Long sectionId, String year);

    Map<String, Object> getOverdueTasksSummaryByDepartment(Long departmentId);

    public List<Map<String, Object>> getOverdueWorkPlanDetailsByDepartment(Long departmentId);

    public Map<String, Object> getOverdueTasksSummaryByDivision(Long divisionId);

    public WorkPlanSummaryResponse getWorkPlanWeekSummary(Long divisionId, String week, String month, String year, Currency currency);

    public WorkPlanSummaryResponse getWorkPlanMonthSummary(Long divisionId, String month, String year, Currency currency);

    public WorkPlanSummaryResponse getWorkPlanYearSummary(Long divisionId, String year, Currency currency);

    public List<WorkPlan> getWorkPlansByDivisionIdWeekMonthYear(Long divisionId, String week, String month, String year);

    public List<WorkPlan> getWorkPlansByDivisionIdMonthYear(Long divisionId, String month, String year);

    public List<WorkPlan> getWorkPlansByDivisionIdYear(Long divisionId, String year);

    public List<WorkPlan> getOverdueWorkPlans(Long divisionId, String week, String month, String year);

    public List<WorkPlan> getOverdueWorkPlansByMonthYear(Long divisionId, String month, String year);


    public long countOverdueWorkPlans(Long divisionId, String week, String month, String year);


    public long countOverdueWorkPlansByMonthYear(Long divisionId, String month, String year);


    public long countOverdueWorkPlansByYear(Long divisionId, String year);

    public List<OverdueTaskResponse> getOverdueTasksWithEmails();

    public List<BudgetVsActualResponse> getBudgetVsActualByYearCurrencyAndDivision(String year, Currency currency, Long divisionId);

    public List<BudgetVsActualMonthResponse> getBudgetVsActualByYearMonthCurrencyAndDivision(String year, String month, Currency currency, Long divisionId);

    public BudgetVsActualCurrencyResponse getBudgetVsActualByYearMonthAndDivision(String year, String month, Long divisionId);

    public BudgetVsActualYearResponse getBudgetVsActualByYearAndDivision(String year, Long divisionId);

    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionWeekMonthYear(Long sectionId, String week, String month, String year);

    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionMonthYear(Long sectionId, String month, String year);

    public SectionWorkPlanSummaryResponse getWorkPlanSummaryBySectionYear(Long sectionId, String year);

    public List<SectionSummaryResponse> getSectionSummariesForDepartment(Long departmentId);

    public List<NotificationTaskResponse> getOverdueTasksSummaryByDepartmentEmails(Long departmentId);

    public List<BudgetVsActualResponse> getBudgetVsActualByYearMonthAndDepartment(String year, String month, Currency currency, Long departmentId);

    public List<BudgetVsActualResponse> getBudgetVsActualByYearAndDepartment(String year, Currency currency, Long departmentId);

    public List<BudgetVsActualResponse> getBudgetVsActualByYearMonthAndSection(String year, String month, Currency currency, Long sectionId);

    public List<BudgetVsActualResponse> getBudgetVsActualByYearAndSection(String year, Currency currency, Long sectionId);

    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndPeriod(Long departmentId, String week, String month, String year);

    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndMonth(Long departmentId, String month, String year);

    public List<SectionWorkPlanSummaryResponsePeriod> getWorkPlanSummaryByDepartmentAndYear(Long departmentId, String year);

    public List<TeamMemberSummaryResponse> getTeamMemberSummary(Long sectionId, String week, String month, String year);

    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByMonth(Long sectionId, String month, String year);

    public List<TeamMemberSummaryResponse> getTeamMemberSummaryByYear(Long sectionId, String year);

    public List<Map<String, Object>> getBudgetUsageByDepartment(String year, String month, String week, Currency currency, Long departmentId);

    public List<Map<String, Object>> getBudgetUsageByMonth(String year, String month, Currency currency, Long departmentId);

    public List<Map<String, Object>> getBudgetUsageByYear(String year, Currency currency, Long departmentId);

}
