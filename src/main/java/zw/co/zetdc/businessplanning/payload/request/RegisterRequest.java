package zw.co.zetdc.businessplanning.payload.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.enums.Role;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


    @NotBlank(message = "Firstname field can't be blank")
    private String firstname;

    private String lastname;

    @NotBlank(message = "Email field can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter proper email format!")
    private String email;

    private String cell;

//    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    private Long sectionId;

    private Long departmentId;

    private Long divisionId;

//    private List<Long> departmentIds;

}
