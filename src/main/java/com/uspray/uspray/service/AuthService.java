package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.TokenDto;
import com.uspray.uspray.DTO.auth.request.ChangePwDto;
import com.uspray.uspray.DTO.auth.request.FindIdDto;
import com.uspray.uspray.DTO.auth.request.FindPwDTO;
import com.uspray.uspray.DTO.auth.request.MemberDeleteDto;
import com.uspray.uspray.DTO.auth.request.MemberLoginRequestDto;
import com.uspray.uspray.DTO.auth.request.MemberRequestDto;
import com.uspray.uspray.DTO.auth.response.DupCheckResponseDto;
import com.uspray.uspray.DTO.auth.response.LoginTypeResponseDto;
import com.uspray.uspray.DTO.auth.response.MemberNameResponseDto;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.Enums.WithdrawReason;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Withdraw;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.WithdrawRepository;
import com.uspray.uspray.jwt.TokenProvider;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final WithdrawRepository withdrawRepository;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        if (memberRepository.existsByUserId(memberRequestDto.getUserId())) {
            throw new NotFoundException(ErrorStatus.ALREADY_EXIST_ID_EXCEPTION);
        }
        if (memberRepository.existsByPhone(memberRequestDto.getPhone())) {
            throw new NotFoundException(ErrorStatus.ALREADY_EXIST_PHONE_EXCEPTION);
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }


    @Transactional
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto) {
        try {
            // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDto.toAuthentication();

            // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
            //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
            Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // 4. RefreshToken 저장
            redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                tokenProvider.getRefreshTokenExpireTime(),
                TimeUnit.MILLISECONDS);

            return tokenDto;
        } catch (AuthenticationException e) {
            throw new NotFoundException(ErrorStatus.WRONG_LOGIN_INFO_EXCEPTION);
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public TokenDto reissue(String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new NotFoundException(ErrorStatus.REFRESH_TOKEN_NOT_VALID_EXCEPTION);
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져오기
        String refreshTokenValue = redisTemplate.opsForValue()
            .get("RT:" + authentication.getName());

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.equals(refreshTokenValue)) {
            throw new NotFoundException(ErrorStatus.REFRESH_TOKEN_NOT_VALID_EXCEPTION);
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
            tokenDto.getRefreshToken(),
            tokenProvider.getRefreshTokenExpireTime(),
            TimeUnit.MILLISECONDS);

        // 토큰 발급
        return tokenDto;
    }

    //Custom exception merge된 후 예외처리 하기
    public String findId(FindIdDto findIdDto) {
        return memberRepository.findByNameAndPhone(findIdDto.getName(), findIdDto.getPhone())
            .getUserId();
    }


    public Long findPw(FindPwDTO findPwDTO) {
        return memberRepository.findByUserIdAndPhone(
            findPwDTO.getUserId(), findPwDTO.getPhone()).getId();
    }

    @Transactional
    public void changePw(ChangePwDto changePwDto) {
        memberRepository.getMemberById(changePwDto.getId())
            .changePw(passwordEncoder.encode(changePwDto.getPassword()));
    }

    @Transactional
    public void withdrawal(String userId, MemberDeleteDto memberDeleteDto) {
        Member member = memberRepository.getMemberByUserId(userId);
        for (WithdrawReason withdrawReason : memberDeleteDto.getWithdrawReason()) {
            if (Objects.equals(withdrawReason, WithdrawReason.ETC)) {
                withdrawRepository.save(Withdraw.builder()
                    .memberId(member.getId())
                    .withdrawReason(withdrawReason)
                    .description(memberDeleteDto.getDescription())
                    .build());
            }
            withdrawRepository.save(Withdraw.builder()
                .memberId(member.getId())
                .withdrawReason(withdrawReason)
                .build());
        }
        memberRepository.delete(member);
    }

    public MemberNameResponseDto getMemberName(String userId) {
        return new MemberNameResponseDto(memberRepository.getMemberByUserId(userId).getName());
    }

    public DupCheckResponseDto dupCheck(String userId) {
        return new DupCheckResponseDto(memberRepository.existsByUserId(userId));
    }

    public LoginTypeResponseDto loginCheck(String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        return LoginTypeResponseDto.builder()
            .isSocial(member.getSocialId() != null)
            .build();
    }
}
