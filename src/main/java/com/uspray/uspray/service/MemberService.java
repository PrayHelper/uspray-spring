package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.request.CheckPwDTO;
import com.uspray.uspray.DTO.auth.request.OauthNameDto;
import com.uspray.uspray.DTO.notification.NotificationAgreeDto;
import com.uspray.uspray.DTO.notification.NotificationInfoDto;
import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePhone(String userId, String phone) {
        memberRepository.getMemberByUserId(userId).changePhone(phone);
    }

    @Transactional(readOnly = true)
    public NotificationInfoDto getNotificationAgree(String userId) {
        return memberRepository.getMemberByUserId(userId).getNotificationSetting();
    }

    @Transactional
    public void changeNotificationAgree(String userId, NotificationAgreeDto notificationAgreeDto) {
        memberRepository.getMemberByUserId(userId).changeNotificationSetting(notificationAgreeDto);
    }

    @Transactional
    public void changeName(OauthNameDto oauthNameDto) {
        Member member = memberRepository.findBySocialId(oauthNameDto.getId())
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage()));
        member.changeName(oauthNameDto.getName());
        member.changeAuthority(Authority.ROLE_USER);
    }

    public Boolean checkPw(String userId, CheckPwDTO checkPwDto) {
        Member member = memberRepository.getMemberByUserId(userId);
        if (!passwordEncoder.matches(checkPwDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage());
        }
        return true;
    }

    @Transactional
    public void changePw(String userId, CheckPwDTO changePwDto) {
        memberRepository.getMemberByUserId(userId)
            .changePw(passwordEncoder.encode(changePwDto.getPassword()));
    }
}
