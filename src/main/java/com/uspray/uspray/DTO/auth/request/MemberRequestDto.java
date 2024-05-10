package com.uspray.uspray.DTO.auth.request;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원가입 요청")
@Builder
public class MemberRequestDto {

    @NotBlank
    @Schema(description = "ID", example = "test")
    private String userId;

    @NotBlank
    @Schema(description = "비밀번호", example = "test")
    private String password;

    @NotBlank
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotBlank
    @Length(min = 11, max = 11)
    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "생년월일", example = "2002-01-01")
    private String birth;

    @Schema(description = "성별", example = "female")
    private String gender;


    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
            .userId(userId)
            .password(passwordEncoder.encode(password))
            .name(name)
            .phone(phone)
            .birth(birth)
            .gender(gender)
            .authority(Authority.ROLE_USER)
            .build();
    }
}
