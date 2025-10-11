package com.teambind.gaechuserver.controller;


import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.dto.response.LikeDetailResponse;
import com.teambind.gaechuserver.exceptions.CustomException;
import com.teambind.gaechuserver.exceptions.ErrorCode;
import com.teambind.gaechuserver.service.LikeReadService;
import com.teambind.gaechuserver.service.LikeServiceAtomic;
import com.teambind.gaechuserver.utils.DataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/likes")
@Slf4j
@RequiredArgsConstructor
public class LikeController {
	private final LikeReadService likeReadService;
	private final LikeServiceAtomic likeServiceAtomic;
	private final DataInitializer dataInitializer;
	
	private Integer resolveCategoryIdNullable(String categoryKey) {
		if (categoryKey == null) return null;
		String key = categoryKey.trim();
		if (key.isEmpty() || key.equalsIgnoreCase("null")) return null;
		// 숫자면 그대로 파싱
		if (key.chars().allMatch(Character::isDigit)) {
			try {
				return Integer.parseInt(key);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		// 카테고리 이름으로 조회 (캐시 활용). 없으면 에러
		return dataInitializer.getCategoryIdByName(key)
				.orElseThrow(() -> new CustomException(ErrorCode.INVALID_CATEGORY));
	}
	
	private int resolveCategoryIdOrZero(String categoryKey) {
		Integer id = resolveCategoryIdNullable(categoryKey);
		return id == null ? 0 : id;
	}
	
	// 좋아요 또는 좋아요 취소 처리
	@PostMapping("/{categoryId}/{referenceId}")
	public void like(@PathVariable(required = true) String categoryId,
	                 @PathVariable(required = true) String referenceId,
	                 @RequestParam(required = true) String likerId,
	                 @RequestParam(required = true) boolean isLike) {
		int catId = resolveCategoryIdOrZero(categoryId);
		if (isLike) {
			likeServiceAtomic.like(referenceId, catId, likerId);
		} else {
			likeServiceAtomic.unLike(referenceId, catId, likerId);
		}
	}
	
	// referenceId + categoryId 에 대한 상세 (like count + liker ids) 반환
	@GetMapping("/detail/{categoryId}/{referenceId}")
	public LikeDetailResponse getLikeAccount(@PathVariable(required = true) String categoryId,
	                                         @PathVariable(required = true) String referenceId) {
		Integer catId = resolveCategoryIdNullable(categoryId);
		return likeReadService.fetchLikeDetailByReferenceIdWithCategoryId(referenceId, catId);
	}
	
	// 여러 referenceId에 대한 like count 일괄 조회
	@GetMapping("/count/{categoryId}")
	public List<LikeCountResponse> getLikeAccount(@PathVariable(required = true) String categoryId,
	                                              @RequestParam(required = true) List<String> referenceIds) {
		int catId = resolveCategoryIdOrZero(categoryId);
		return likeReadService.fetchAllLikeByCategoryIdAndReferenceId(catId, referenceIds);
	}
	
	// 특정 사용자(작성자)가 좋아요한 게시물 목록 및 각 게시물의 like count 반환
	@GetMapping("/count/{categoryId}/{userId}")
	public List<LikeCountResponse> getLikeAccountByUserId(@PathVariable(required = true) String categoryId,
	                                                      @PathVariable(required = true) String userId) {
		Integer catId = resolveCategoryIdNullable(categoryId);
		return likeReadService.getLikedReferencesWithCounts(catId, userId);
	}
}
