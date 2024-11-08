// package com.be3c.sysmetic.domain.strategy.controller;
//
// import com.be3c.sysmetic.domain.strategy.dto.FollowerTopResponseDto;
// import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
// import com.be3c.sysmetic.domain.strategy.service.MetadataService;
// import com.be3c.sysmetic.global.common.response.ApiResponse;
// import com.be3c.sysmetic.global.common.response.ErrorCode;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
//
// import java.util.List;
//
// @Controller
// @Slf4j
// @RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
// public class MetadataController {
//
//     private final MetadataService metadataService;
//
//     /*
//     메인 페이지의 정보 조회 - 팔로우 랭킹 Top 3 전략
//     */
//     @GetMapping("/meta/follower-top")
//     public ResponseEntity<ApiResponse<List<FollowerTopResponseDto>>> getFollowerTop() throws Exception {
//         List<FollowerTopResponseDto> followerTopList = metadataService.getFollowerTopThree();
//
//         if (followerTopList.isEmpty()) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "팔로우 랭킹 Top 3인 전략이 없습니다."));
//         }
//
//         return ResponseEntity.status(HttpStatus.OK)
//                 .body(ApiResponse.success(followerTopList));
//     }
// }
