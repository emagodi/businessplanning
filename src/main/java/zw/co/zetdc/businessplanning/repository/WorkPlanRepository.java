package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.WorkPlan;
import java.util.List;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {

    List<WorkPlan> findByWeek(String week);
    List<WorkPlan> findByMonth(String month);
    List<WorkPlan> findByYear(String year);
    List<WorkPlan> findByWeekAndMonthAndYear(String week, String month, String year);
    List<WorkPlan> findBySectionId(Long sectionId);
    List<WorkPlan> findByDepartmentId(Long departmentId);

}
