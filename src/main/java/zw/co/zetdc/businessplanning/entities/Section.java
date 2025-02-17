package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sections")
@EntityListeners(AuditingEntityListener.class)
public class Section extends BaseEntity {

    private String name;

    @ManyToMany(mappedBy = "assignedSections")
    @JsonIgnore // Prevents this relationship from being serialized
    private List<Department> departments;
}
