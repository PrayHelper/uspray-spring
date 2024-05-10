package com.uspray.uspray.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.uspray.uspray.DTO.auth.request.FindIdDto;
import com.uspray.uspray.DTO.auth.request.FindPwDTO;
import com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto;
import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.common.MemberTest;
import com.uspray.uspray.exception.model.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuthServiceTest extends MemberTest {

    @Test
    @DisplayName("회원 가입 시 중복된 아이디가 있을 경우 예외를 던진다.")
    void signUpWithDupId() {
        // Given
        String userId = "leesunshin";

        // When
        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
            .userId(userId)
            .password("leesunshin")
            .name("이순신")
            .phone("01011111111")
            .build();

        // Then
        assertThatThrownBy(() -> authService.signup(memberRequestDto))
            .isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("회원 가입 시 중복된 전화번호가 있을 경우 예외를 던진다.")
    void signUpWithDupPhone() {
        // Given
        String phone = "01011111111";

        // When
        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
            .userId("test2")
            .password("test2")
            .name("김유신")
            .phone(phone)
            .build();

        // Then
        assertThatThrownBy(() -> authService.signup(memberRequestDto))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("로그인 시 정보가 틀렸을 경우 예외를 던진다.")
    void login() {
        // Given
        String userId = "unknown";
        String password = "unknown";

        // When
        MemberLoginRequestDto memberLoginRequestDto = MemberLoginRequestDto.builder()
            .userId(userId)
            .password(password)
            .build();

        // Then
        assertThatThrownBy(() -> authService.login(memberLoginRequestDto))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("이름과 전화번호로 아이디를 찾는다.")
    void findId() {
        // Given
        String userid = "leesunshin";
        String name = "이순신";
        String phone = "01011111111";

        // When
        FindIdDto findIdDto = FindIdDto.builder()
            .name(name)
            .phone(phone)
            .build();

        // Then
        assertThat(authService.findId(findIdDto)).isEqualTo(userid);
    }

    @Test
    @DisplayName("아이디과 전화번호로 회원여부를 확인한다.")
    void findPw() {
        // Given
        String userid = "leesunshin";
        String phone = "01011111111";

        // When
        FindPwDTO findPwDTO = FindPwDTO.builder()
            .userId(userid)
            .phone(phone)
            .build();

        // Then
        assertThat(authService.findPw(findPwDTO)).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("비밀번호를 변경한다.")
    void changePw() {
    }

    @Test
    void getName() {
    }

    @Test
    void withdrawal() {
    }

    @Test
    void dupCheck() {
    }

    @Test
    void loginCheck() {
    }
}
