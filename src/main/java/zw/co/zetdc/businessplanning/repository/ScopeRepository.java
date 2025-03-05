package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.enums.Status;

import java.util.List;
import java.util.Map;


public interface ScopeRepository extends JpaRepository<Scope, Long> {

    // Get overdue scopes for a specific work plan
    @Query("SELECT s FROM Scope s WHERE s.workPlan.id = :workPlanId AND s.targetCompletionDate < CURRENT_DATE AND s.status <> :completedStatus")
    List<Scope> findOverdueScopesByWorkPlan(@Param("workPlanId") Long workPlanId, @Param("completedStatus") Status completedStatus);

    // Get in-progress scopes for a specific work plan
    @Query("SELECT s FROM Scope s WHERE s.workPlan.id = :workPlanId AND s.actualCompletionDate > CURRENT_DATE AND s.status <> :completedStatus")
    List<Scope> findInProgressScopesByWorkPlan(@Param("workPlanId") Long workPlanId, @Param("completedStatus") Status completedStatus);

    // Group scopes by status for a specific work plan
    @Query("SELECT s.status AS status, tm AS teamMember, COUNT(s) AS count " +
            "FROM Scope s " +
            "JOIN s.assignedTeamMembers tm " +
            "WHERE s.workPlan.id = :workPlanId " +
            "GROUP BY s.status, tm")
    List<Object[]> findScopesGroupedByStatusAndTeamMemberByWorkPlan(@Param("workPlanId") Long workPlanId);

    // Get all scopes for a specific work plan
    List<Scope> findByWorkPlanId(Long workPlanId);

    // Get overdue scopes for all work plans
    @Query("SELECT s FROM Scope s WHERE s.targetCompletionDate < CURRENT_DATE AND s.status <> :completedStatus")
    List<Scope> findOverdueScopes(@Param("completedStatus") Status completedStatus);

    // Get in-progress scopes for all work plans
    @Query("SELECT s FROM Scope s WHERE s.actualCompletionDate > CURRENT_DATE AND s.status <> :completedStatus")
    List<Scope> findInProgressScopes(@Param("completedStatus") Status completedStatus);

    // Group scopes by status and team member for all work plans
    @Query("SELECT s.status AS status, tm AS teamMember, COUNT(s) AS count " +
            "FROM Scope s " +
            "JOIN s.assignedTeamMembers tm " +
            "GROUP BY s.status, tm")
    List<Object[]> findScopesGroupedByStatusAndTeamMember();

    // Get all scopes
    @Query("SELECT s FROM Scope s")
    List<Scope> findAllScopes();


//    @Query("SELECT s FROM Scope s WHERE s.targetCompletionDate < CURRENT_DATE")
//    List<Scope> findAllOverdueScopes();

    @Query("SELECT s FROM Scope s WHERE s.targetCompletionDate < CURRENT_DATE AND s.status NOT IN ('COMPLETED', 'CANCELED', 'PENDING')")
    List<Scope> findAllOverdueScopes();

// Overdue Tasks for Each Team Member
@Query("SELECT s FROM Scope s JOIN s.assignedTeamMembers tm " +
        "WHERE tm.id = :teamMemberId AND s.targetCompletionDate < CURRENT_DATE " +
        "AND s.status NOT IN ('COMPLETED', 'CANCELED')")
List<Scope> findOverdueTasksForTeamMember(@Param("teamMemberId") Long teamMemberId);

// Tasks Grouped by Status for Each Team Member
@Query("SELECT s.status AS status, COUNT(s) AS taskCount " +
        "FROM Scope s JOIN s.assignedTeamMembers tm " +
        "WHERE tm.id = :teamMemberId " +
        "GROUP BY s.status")
List<Map<String, Object>> findTasksGroupedByStatusForTeamMember(@Param("teamMemberId") Long teamMemberId);


@Query("SELECT s FROM Scope s WHERE s.workPlan.sectionId = :sectionId AND s.status = :status")
List<Scope> findScopesBySectionIdAndStatus(@Param("sectionId") Long sectionId, @Param("status") Status status);

@Query("SELECT s FROM Scope s WHERE s.workPlan.departmentId = :departmentId AND s.status = :status")
List<Scope> findScopesByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, @Param("status") Status status);

@Query("SELECT COUNT(s) FROM Scope s WHERE s.workPlan.sectionId = :sectionId AND s.status = :status")
Long countScopesBySectionIdAndStatus(@Param("sectionId") Long sectionId, @Param("status") Status status);

@Query("SELECT COUNT(s) FROM Scope s WHERE s.workPlan.departmentId = :departmentId AND s.status = :status")
Long countScopesByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, @Param("status") Status status);

}
