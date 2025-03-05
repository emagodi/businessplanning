package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScopeStatusResponse {
    private Scope scope;
    private String dueStatus;
    private long daysOverdue;

}
