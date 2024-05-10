package com.uspray.uspray.repository;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public abstract class RepositoryTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @BeforeEach
    void userSetup() {
        Member member = memberRepository.save(Member.builder()
            .userId("leesunshin")
            .password("test")
            .name("이순신")
            .phone("01011111111")
            .build());
    }

}
