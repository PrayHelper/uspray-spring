package com.uspray.uspray.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("회원 아이디로 조회 시 존재하지 않는 아이디일 경우 예외를 던진다.")
    void existsByUserId() {
        // Given
        String userId = "leesunshin";
        String notExistUserId = "unknown";

        // When
        boolean existsByUserId = memberRepository.existsByUserId(userId);
        boolean notExistsByUserId = memberRepository.existsByUserId(notExistUserId);

        // Then
        assertThat(existsByUserId).isTrue();
        assertThat(notExistsByUserId).isFalse();
    }

}
