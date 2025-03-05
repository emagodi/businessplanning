package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Status;

import java.util.List;
import java.util.Map;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {

    List<WorkPlan> findByWeek(String week);
    List<WorkPlan> findByMonth(String month);
    List<WorkPlan> findByYear(String year);
    List<WorkPlan> findByWeekAndMonthAndYear(String week, String month, String year);
    List<WorkPlan> findBySectionId(Long sectionId);
    List<WorkPlan> findByDepartmentId(Long departmentId);
    List<WorkPlan> findByCreatedBy(String createdBy);

    List<WorkPlan> findByDepartmentIdAndWeekAndMonthAndYear(Long departmentId, String week, String month, String year);
    List<WorkPlan> findBySectionIdAndWeekAndMonthAndYear(Long sectionId, String week, String month, String year);


    // Fetch All WorkPlans with In-Progress Scopes
    @Query("SELECT DISTINCT wp FROM WorkPlan wp JOIN wp.scopes s WHERE s.status = 'IN_PROGRESS'")
    List<WorkPlan> findAllWorkPlansWithInProgressScopes();

    @Query("SELECT s.status AS status, COUNT(s) AS taskCount " +
            "FROM Scope s JOIN s.assignedTeamMembers tm " +
            "WHERE tm.id = :teamMemberId " +
            "GROUP BY s.status")
    List<Map<String, Object>> findTasksGroupedByStatusForTeamMember(@Param("teamMemberId") Long teamMemberId);


    @Query("SELECT s FROM Scope s JOIN s.assignedTeamMembers tm " +
            "WHERE tm.id = :teamMemberId AND s.targetCompletionDate < CURRENT_DATE " +
            "AND s.status NOT IN ('COMPLETED', 'CANCELED')")
    List<Scope> findOverdueTasksForTeamMember(@Param("teamMemberId") Long teamMemberId);




    @Query("SELECT wp FROM WorkPlan wp WHERE wp.year = :year AND wp.departmentId = :departmentId AND wp.month IN :months")
    List<WorkPlan> findByYearAndDepartmentIdAndMonths(@Param("year") String year,
                                                      @Param("departmentId") Long departmentId,
                                                      @Param("months") List<String> months);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.year = :year AND wp.departmentId = :departmentId AND wp.month IN :months")
    Long countByYearAndDepartmentIdAndMonths(@Param("year") String year,
                                             @Param("departmentId") Long departmentId,
                                             @Param("months") List<String> months);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.year = :year AND wp.sectionId = :sectionId AND wp.month IN :months")
    Long countByYearAndSectionIdAndMonths(@Param("year") String year,
                                          @Param("sectionId") Long sectionId,
                                          @Param("months") List<String> months);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.year = :year AND wp.sectionId = :sectionId AND wp.month IN :months")
    List<WorkPlan> findByYearAndSectionIdAndMonths(@Param("year") String year,
                                                   @Param("sectionId") Long sectionId,
                                                   @Param("months") List<String> months);

}
