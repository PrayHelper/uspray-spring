package com.uspray.uspray.controller;


import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.facade.PrayFacade;
import com.uspray.uspray.service.PrayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pray")
@Tag(name = "Pray", description = "기도제목 관련 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class PrayController {

    private final PrayService prayService;
    private final PrayFacade prayFacade;

    @Operation(summary = "기도제목 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 목록 반환",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @GetMapping
    public ApiResponseDto<List<PrayListResponseDto>> getPrayList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "기도제목 종류(personal, shared)", required = true, example = "personal") String prayType
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            prayFacade.getPrayList(user.getUsername(), prayType));
    }

    @GetMapping("/{prayId}")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 조회",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @Operation(summary = "기도제목 조회")
    public ApiResponseDto<PrayResponseDto> getPrayDetail(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_SUCCESS,
            prayService.getPrayDetail(prayId, user.getUsername()));
    }

    @PostMapping
    @ApiResponse(
        responseCode = "201",
        description = "기도제목 생성",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @Operation(summary = "기도제목 생성")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<PrayResponseDto> createPray(
        @RequestBody @Valid PrayRequestDto prayRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.CREATE_PRAY_SUCCESS,
            prayFacade.createPray(prayRequestDto, user.getUsername()));
    }

    @DeleteMapping("/{prayId}")
    @ApiResponse(responseCode = "204", description = "기도제목 삭제")
    @Operation(summary = "기도제목 삭제")
    public ApiResponseDto<PrayResponseDto> deletePray(
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.DELETE_PRAY_SUCCESS,
            prayFacade.deletePray(prayId, user.getUsername()));
    }

    @PutMapping("/{prayId}")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 수정",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @Operation(summary = "기도제목 수정")
    public ApiResponseDto<PrayResponseDto> updatePray(
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
        @RequestBody @Valid PrayUpdateRequestDto prayUpdateRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_PRAY_SUCCESS,
            prayFacade.updatePray(prayId, user.getUsername(), prayUpdateRequestDto));
    }

    @Operation(summary = "오늘 기도하기")
    @ApiResponse(
        responseCode = "200",
        description = "오늘 기도하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @PutMapping("/{prayId}/today")
    public ApiResponseDto<List<PrayListResponseDto>> todayPray(
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.INCREASE_PRAY_COUNT_SUCCESS,
            prayFacade.todayPray(prayId, user.getUsername()));
    }

    @Operation(summary = "기도 완료하기")
    @ApiResponse(
        responseCode = "200",
        description = "기도제목 완료하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @PutMapping("/{prayId}/complete")
    public ApiResponseDto<List<PrayListResponseDto>> completePray(
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        prayFacade.createHistory(user.getUsername(), prayId);
        return ApiResponseDto.success(SuccessStatus.GET_PRAY_LIST_SUCCESS,
            prayFacade.completePray(prayId, user.getUsername()));
    }

    @Operation(summary = "기도제목 취소하기")
    @ApiResponse(
        responseCode = "200",
        description = "오늘 기도 취소하기",
        content = @Content(schema = @Schema(implementation = PrayResponseDto.class)))
    @PutMapping("/{prayId}/cancel")
    public ApiResponseDto<List<PrayListResponseDto>> cancelPray(
        @Parameter(description = "기도제목 ID", required = true) @PathVariable("prayId") Long prayId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.CANCEL_PRAY_SUCCESS,
            prayFacade.cancelPray(prayId, user.getUsername()));
    }
}
