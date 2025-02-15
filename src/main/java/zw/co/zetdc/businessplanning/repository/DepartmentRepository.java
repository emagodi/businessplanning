package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
