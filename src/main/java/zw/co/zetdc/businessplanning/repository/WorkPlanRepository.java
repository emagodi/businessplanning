package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.WorkPlan;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
}
