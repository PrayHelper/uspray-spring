package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.querydsl.PrayRepositoryImpl;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

  private final PrayRepository prayRepository;
  private final MemberRepository memberRepository;
  private final PrayRepositoryImpl prayRepositoryImpl;

  @Transactional
  public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
    Member member = memberRepository.getMemberByUserId(username);
    Pray pray = prayRequestDto.toEntity(member);
    prayRepository.save(pray);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto getPrayDetail(Long prayId, String username) {
    Pray pray = prayRepository.findById(prayId)
        .orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto deletePray(Long prayId, String username) {
    Pray pray = prayRepository.findById(prayId)
        .orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    prayRepository.delete(pray);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto updatePray(Long prayId, String username, PrayRequestDto prayRequestDto) {
    Pray pray = prayRepository.findById(prayId)
        .orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    pray.update(prayRequestDto);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public List<PrayResponseDto> getPrayList(String username, String orderType) {
    return prayRepositoryImpl.findAllWithOrder(orderType, username).stream()
        .map(PrayResponseDto::of)
        .collect(Collectors.toList());
  }
}
