package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.entities.WorkPlan;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
