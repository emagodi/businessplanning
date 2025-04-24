package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import zw.co.zetdc.businessplanning.enums.Currency;
import zw.co.zetdc.businessplanning.enums.Status;

import java.time.LocalDate;
import java.util.Date;
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

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.departmentId IN :departmentIds")
    List<WorkPlan> findByDepartmentIds(@Param("departmentIds") List<Long> departmentIds);




    @Query("SELECT wp FROM WorkPlan wp WHERE wp.year = :year AND wp.departmentId = :departmentId AND wp.month IN :months AND wp.status = :status")
    List<WorkPlan> findByYearAndDepartmentIdAndMonthsAndStatus(@Param("year") String year,
                                                               @Param("departmentId") Long departmentId,
                                                               @Param("months") List<String> months,
                                                               @Param("status") Status status);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.year = :year AND wp.departmentId = :departmentId AND wp.month IN :months AND wp.status = :status")
    Long countByYearAndDepartmentIdAndMonthsAndStatus(@Param("year") String year,
                                                      @Param("departmentId") Long departmentId,
                                                      @Param("months") List<String> months,
                                                      @Param("status") Status status);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.year = :year AND wp.sectionId = :sectionId AND wp.month IN :months AND wp.status = :status")
    Long countByYearAndSectionIdAndMonthsAndStatus(@Param("year") String year,
                                                   @Param("sectionId") Long sectionId,
                                                   @Param("months") List<String> months,
                                                   @Param("status") Status status);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.year = :year AND wp.sectionId = :sectionId AND wp.month IN :months AND wp.status = :status")
    List<WorkPlan> findByYearAndSectionIdAndMonthsAndStatus(@Param("year") String year,
                                                            @Param("sectionId") Long sectionId,
                                                            @Param("months") List<String> months,
                                                            @Param("status") Status status);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.year = :year AND wp.departmentId = :departmentId AND wp.month IN :months")
    List<WorkPlan> findByDepartmentIdAndMonthIn(@Param("year") String year,
                                                @Param("departmentId") Long departmentId,
                                                @Param("months") List<String> months);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.sectionId = :sectionId AND wp.month IN :months AND wp.year = :year")
    List<WorkPlan> findBySectionIdAndMonthIn(@Param("year") String year, @Param("sectionId") Long sectionId, @Param("months") List<String> months);

    List<WorkPlan> findByDepartmentIdAndMonthAndYear(Long departmentId, String month, String year);

    List<WorkPlan> findBySectionIdAndMonthAndYear(Long departmentId, String month, String year);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.departmentId = :departmentId AND wp.month = :month AND wp.year = :year")
    Long countByDepartmentIdAndMonthAndYear(@Param("departmentId") Long departmentId,
                                            @Param("month") String month,
                                            @Param("year") String year);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.sectionId = :sectionId AND wp.month = :month AND wp.year = :year")
    Long countBySectionIdAndMonthAndYear(@Param("sectionId") Long sectionId,
                                            @Param("month") String month,
                                            @Param("year") String year);


    @Query("SELECT wp FROM WorkPlan wp WHERE wp.sectionId = :sectionId AND wp.currency = :currency")
    List<WorkPlan> findBySectionIdAndCurrency(@Param("sectionId") Long sectionId, @Param("currency") Currency currency);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.departmentId = :departmentId AND wp.currency = :currency")
    List<WorkPlan> findByDepartmentIdAndCurrency(@Param("departmentId") Long departmentId, @Param("currency") Currency currency);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.sectionId = :sectionId AND wp.year = :year")
    List<WorkPlan> findBySectionIdAndYear(@Param("sectionId") Long sectionId, @Param("year") String year);



    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId AND wp.week = :week AND wp.month = :month AND wp.year = :year")
    List<WorkPlan> findByDivisionIdAndWeekAndMonthAndYear(@Param("divisionId") Long divisionId,
                                                          @Param("week") String week,
                                                          @Param("month") String month,
                                                          @Param("year") String year);


    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId AND wp.month = :month AND wp.year = :year")
    List<WorkPlan> findByDivisionIdAndMonthAndYear(@Param("divisionId") Long divisionId,
                                                   @Param("month") String month,
                                                   @Param("year") String year);



    List<WorkPlan> findByDivisionIdAndYear(@Param("divisionId") Long divisionId,
                                                   @Param("year") String year);


    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId AND wp.month = :month AND wp.year = :year AND wp.currency = :currency")
    List<WorkPlan> findByDivisionIdAndMonthAndYearAndCurrency(@Param("divisionId") Long divisionId,
                                                              @Param("month") String month,
                                                              @Param("year") String year,
                                                              @Param("currency") Currency currency);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId AND wp.week = :week AND wp.month = :month AND wp.year = :year AND wp.currency = :currency")
    List<WorkPlan> findByDivisionIdAndWeekAndMonthAndYearAndCurrency(@Param("divisionId") Long divisionId,
                                                                     @Param("week") String week,
                                                                     @Param("month") String month,
                                                                     @Param("year") String year,
                                                                     @Param("currency") Currency currency);


    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId AND wp.year = :year AND wp.currency = :currency")
    List<WorkPlan> findByDivisionIdAndYearAndCurrency(@Param("divisionId") Long divisionId,
                                                      @Param("year") String year,
                                                      @Param("currency") Currency currency);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId " +
            "AND wp.week = :week AND wp.month = :month AND wp.year = :year " +
            "AND wp.status NOT IN (:completed, :cancelled) " +
            "AND wp.targetCompletionDate < CURRENT_DATE")
    List<WorkPlan> findOverdueWorkPlans(@Param("divisionId") Long divisionId,
                                        @Param("week") String week,
                                        @Param("month") String month,
                                        @Param("year") String year,
                                        @Param("completed") Status completed,
                                        @Param("cancelled") Status cancelled);


    @Query("SELECT wp FROM WorkPlan wp WHERE wp.divisionId = :divisionId " +
            "AND wp.month = :month AND wp.year = :year " +
            "AND wp.status NOT IN (:completed, :cancelled) " +
            "AND wp.targetCompletionDate < CURRENT_DATE")
    List<WorkPlan> findOverdueWorkPlansByMonthYear(@Param("divisionId") Long divisionId,
                                                   @Param("month") String month,
                                                   @Param("year") String year,
                                                   @Param("completed") Status completed,
                                                   @Param("cancelled") Status cancelled);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.divisionId = :divisionId " +
            "AND wp.week = :week AND wp.month = :month AND wp.year = :year " +
            "AND wp.status NOT IN (:completed, :cancelled) " +
            "AND wp.targetCompletionDate < CURRENT_DATE")
    long countOverdueWorkPlans(@Param("divisionId") Long divisionId,
                               @Param("week") String week,
                               @Param("month") String month,
                               @Param("year") String year,
                               @Param("completed") Status completed,
                               @Param("cancelled") Status cancelled);


    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.divisionId = :divisionId " +
            "AND wp.month = :month AND wp.year = :year " +
            "AND wp.status NOT IN (:completed, :cancelled) " +
            "AND wp.targetCompletionDate < CURRENT_DATE")
    long countOverdueWorkPlansByMonthYear(@Param("divisionId") Long divisionId,
                                          @Param("month") String month,
                                          @Param("year") String year,
                                          @Param("completed") Status completed,
                                          @Param("cancelled") Status cancelled);

    @Query("SELECT COUNT(wp) FROM WorkPlan wp WHERE wp.divisionId = :divisionId " +
            "AND wp.year = :year " +
            "AND wp.status NOT IN (:completed, :cancelled) " +
            "AND wp.targetCompletionDate < CURRENT_DATE")
    long countOverdueWorkPlansByYear(@Param("divisionId") Long divisionId,
                                     @Param("year") String year,
                                     @Param("completed") Status completed,
                                     @Param("cancelled") Status cancelled);

    @Query("""
    SELECT wp, 
           (SELECT u.email FROM User u WHERE u.role = 'MANAGER' AND u.sectionId = wp.sectionId) AS managerEmail,
           (SELECT u.email FROM User u WHERE u.role = 'SENIORMANAGER' AND u.divisionId = wp.divisionId) AS seniorManagerEmail
    FROM WorkPlan wp 
    WHERE wp.targetCompletionDate < CURRENT_DATE 
    AND wp.status NOT IN (:completed, :cancelled)
""")
    List<Object[]> findOverdueWorkPlansWithUserEmails(@Param("completed") Status completed,
                                                      @Param("cancelled") Status cancelled);




    @Query("""
    SELECT 
        wp.month AS month, 
        SUM(wp.budget) AS budget, 
        SUM(wp.actualExpenditure) AS actual
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.currency = :currency
        AND wp.divisionId = :divisionId
    GROUP BY 
        wp.month
    ORDER BY 
        FIELD(wp.month, 'January', 'February', 'March', 'April', 'May', 'June', 
               'July', 'August', 'September', 'October', 'November', 'December')
""")
    List<Object[]> findBudgetVsActualByYearCurrencyAndDivision(@Param("year") String year,
                                                               @Param("currency") Currency currency,
                                                               @Param("divisionId") Long divisionId);


    @Query("""
        SELECT 
            wp.week AS week, 
            SUM(wp.budget) AS budget, 
            SUM(wp.actualExpenditure) AS actual
        FROM 
            WorkPlan wp
        WHERE 
            wp.year = :year 
            AND wp.month = :month 
            AND wp.currency = :currency
            AND wp.divisionId = :divisionId
        GROUP BY 
            wp.week
        ORDER BY 
            wp.week
    """)
    List<Object[]> findBudgetVsActualByYearMonthCurrencyAndDivision(@Param("year") String year,
                                                                    @Param("month") String month,
                                                                    @Param("currency") Currency currency,
                                                                    @Param("divisionId") Long divisionId);

    @Query("""
    SELECT 
        wp.week AS week, 
        SUM(CASE WHEN wp.currency = 0 THEN wp.budget ELSE 0 END) AS budgetUSD,
        SUM(CASE WHEN wp.currency = 0 THEN wp.actualExpenditure ELSE 0 END) AS actualUSD,
        SUM(CASE WHEN wp.currency = 1 THEN wp.budget ELSE 0 END) AS budgetZWL,
        SUM(CASE WHEN wp.currency = 1 THEN wp.actualExpenditure ELSE 0 END) AS actualZWL
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.month = :month 
        AND wp.divisionId = :divisionId
    GROUP BY 
        wp.week
    ORDER BY 
        wp.week
""")
    List<Object[]> findBudgetVsActualByYearMonthAndDivision(@Param("year") String year,
                                                            @Param("month") String month,
                                                            @Param("divisionId") Long divisionId);




    @Query("""
        SELECT 
            wp.month AS month, 
            SUM(CASE WHEN wp.currency = 0 THEN wp.budget ELSE 0 END) AS budgetUSD,
            SUM(CASE WHEN wp.currency = 0 THEN wp.actualExpenditure ELSE 0 END) AS actualUSD,
            SUM(CASE WHEN wp.currency = 1 THEN wp.budget ELSE 0 END) AS budgetZWL,
            SUM(CASE WHEN wp.currency = 1 THEN wp.actualExpenditure ELSE 0 END) AS actualZWL
        FROM 
            WorkPlan wp
        WHERE 
            wp.year = :year 
            AND wp.divisionId = :divisionId
        GROUP BY 
            wp.month
        ORDER BY 
            FIELD(wp.month, 'January', 'February', 'March', 'April', 'May', 'June', 
                   'July', 'August', 'September', 'October', 'November', 'December')
    """)
    List<Object[]> findBudgetVsActualByYearAndDivision(@Param("year") String year,
                                                       @Param("divisionId") Long divisionId);


    @Query("""
    SELECT wp FROM WorkPlan wp 
    WHERE wp.sectionId = :sectionId 
      AND wp.week = :week 
      AND wp.month = :month 
      AND wp.year = :year
""")
    List<WorkPlan> findBySectionIdAndWeekMonthYear(@Param("sectionId") Long sectionId,
                                                   @Param("week") String week,
                                                   @Param("month") String month,
                                                   @Param("year") String year);

    @Query("""
        SELECT wp FROM WorkPlan wp 
        WHERE wp.sectionId = :sectionId 
          AND wp.month = :month 
          AND wp.year = :year
    """)
    List<WorkPlan> findBySectionIdAndMonthYear(@Param("sectionId") Long sectionId,
                                               @Param("month") String month,
                                               @Param("year") String year);

    @Query("""
    SELECT wp FROM WorkPlan wp 
    WHERE wp.departmentId = :departmentId 
    AND wp.targetCompletionDate < :currentDate 
    AND wp.status NOT IN (:completed, :cancelled)
""")
    List<WorkPlan> findOverdueWorkPlans(@Param("departmentId") Long departmentId,
                                        @Param("currentDate") Date currentDate,
                                        @Param("completed") Status completed,
                                        @Param("cancelled") Status cancelled);



    @Query("""
    SELECT wp, 
           (SELECT u.email FROM User u WHERE u.role = 'MANAGER' AND u.sectionId = wp.sectionId) AS managerEmail,
           (SELECT u.email FROM User u WHERE u.role = 'SENIORMANAGER' AND u.divisionId = wp.divisionId) AS seniorManagerEmail
    FROM WorkPlan wp 
    WHERE wp.targetCompletionDate < CURRENT_DATE 
    AND wp.status NOT IN (:completed, :cancelled)
    AND wp.departmentId = :departmentId
    """)
    List<Object[]> findOverdueTasksWithEmailsByDepartment(@Param("departmentId") Long departmentId,
                                                          @Param("completed") Status completed,
                                                          @Param("cancelled") Status cancelled);



    @Query("""
    SELECT 
        wp.week AS week, 
        SUM(wp.budget) AS budget, 
        SUM(wp.actualExpenditure) AS actual
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.month = :month 
        AND wp.departmentId = :departmentId 
        AND wp.currency = :currency
    GROUP BY 
        wp.week
    ORDER BY 
        wp.week
    """)
    List<Object[]> findBudgetVsActualByYearMonthCurrencyAndDepartment(@Param("year") String year,
                                                                      @Param("month") String month,
                                                                      @Param("currency") Currency currency,
                                                                      @Param("departmentId") Long departmentId);




    @Query("""
    SELECT 
        wp.month AS month, 
        SUM(wp.budget) AS budget, 
        SUM(wp.actualExpenditure) AS actual
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.departmentId = :departmentId 
        AND wp.currency = :currency
    GROUP BY 
        wp.month
    ORDER BY 
        FIELD(wp.month, 'January', 'February', 'March', 'April', 'May', 'June', 
               'July', 'August', 'September', 'October', 'November', 'December')
    """)
    List<Object[]> findBudgetVsActualByYearCurrencyAndDepartment(@Param("year") String year,
                                                                 @Param("currency") Currency currency,
                                                                 @Param("departmentId") Long departmentId);

    @Query("""
    SELECT 
        wp.week AS week, 
        SUM(wp.budget) AS budget, 
        SUM(wp.actualExpenditure) AS actual
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.month = :month 
        AND wp.sectionId = :sectionId 
        AND wp.currency = :currency
    GROUP BY 
        wp.week
    ORDER BY 
        wp.week
    """)
    List<Object[]> findBudgetVsActualByYearMonthCurrencyAndSection(@Param("year") String year,
                                                                   @Param("month") String month,
                                                                   @Param("currency") Currency currency,
                                                                   @Param("sectionId") Long sectionId);

    @Query("""
    SELECT 
        wp.month AS month, 
        SUM(wp.budget) AS budget, 
        SUM(wp.actualExpenditure) AS actual
    FROM 
        WorkPlan wp
    WHERE 
        wp.year = :year 
        AND wp.sectionId = :sectionId 
        AND wp.currency = :currency
    GROUP BY 
        wp.month
    ORDER BY 
        FIELD(wp.month, 'January', 'February', 'March', 'April', 'May', 'June', 
               'July', 'August', 'September', 'October', 'November', 'December')
    """)
    List<Object[]> findBudgetVsActualByYearCurrencyAndSection(@Param("year") String year,
                                                              @Param("currency") Currency currency,
                                                              @Param("sectionId") Long sectionId);

}
