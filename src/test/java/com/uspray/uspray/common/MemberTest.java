package com.uspray.uspray.common;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.service.AuthService;
import com.uspray.uspray.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public abstract class MemberTest {

    @Autowired
    protected MemberService memberService;
    @Autowired
    protected AuthService authService;
    @Autowired
    protected MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
            .userId("leesunshin")
            .password("test")
            .name("이순신")
            .phone("01011111111")
            .gender("female")
            .build());

        Member member2 = memberRepository.save(Member.builder()
            .userId("kimyushin")
            .password("test2")
            .name("김유신")
            .phone("01022222222")
            .gender("male")
            .build());
    }
}
