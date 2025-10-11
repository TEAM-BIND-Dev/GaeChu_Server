package com.teambind.gaechuserver.service;

import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.dto.response.LikeDetailResponse;
import com.teambind.gaechuserver.entity.Ids.LikeAccountId;
import com.teambind.gaechuserver.entity.LikeAccount;
import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeReadService {
	private final LikeRepository likeRepository;
	private final LikeAccountRepository likeAccountRepository;
	
	
	public List<LikeCountResponse> getLikedReferencesWithCounts(Integer categoryId, String likerId) {
		return likeRepository.findLikeCountsByCategoryAndLiker(categoryId, likerId);
	}
	
	
	public List<LikeCountResponse> fetchAllLikeByCategoryIdAndReferenceId(int categoryId, List<String> referenceIds) {
		// 1) LikeAccountId 목록 생성
		List<LikeAccountId> ids = referenceIds.stream()
				.map(ref -> {
					LikeAccountId id = new LikeAccountId();
					// LikeAccountId의 필드명이 categoryId, referenceId 여야 함
					id.setCategoryId(categoryId);
					id.setReferenceId(ref);
					return id;
				})
				.collect(Collectors.toList());
		
		// 2) like_account에서 해당 키들로 한 번에 조회
		List<LikeAccount> accounts = (List<LikeAccount>) likeAccountRepository.findAllById(ids);
		
		// 3) Map으로 변환(referenceId -> account)
		Map<String, Long> accountMap = accounts.stream()
				.collect(Collectors.toMap(a -> a.getLikeAccountId().getReferenceId(), LikeAccount::getAccount));
		
		// 4) 입력 referenceIds를 기준으로 응답 DTO 생성 (조회되지 않은 reference는 0으로 처리)
		return referenceIds.stream()
				.map(ref -> new LikeCountResponse(ref, accountMap.getOrDefault(ref, 0L)))
				.collect(Collectors.toList());
	}
	
	
	public LikeDetailResponse fetchLikeDetailByReferenceIdWithCategoryId(String referenceId, Integer categoryId) {
		// 1) likes 테이블에서 해당 reference의 liker 목록 조회
		List<Likes> likes = likeRepository.findAllByCategoryIdAndReferenceId(categoryId, referenceId);
		List<String> likerIds = likes.stream()
				.map(Likes::getLikerId)
				.collect(Collectors.toList());
		
		// 2) like_account에서 like count 조회 (임베디드 id로 조회)
		Long likeCount = likeAccountRepository
				.findByLikeAccountIdReferenceIdAndLikeAccountIdCategoryId(referenceId, categoryId)
				.map(LikeAccount::getAccount)
				.orElse(0L);
		
		// 3) DTO 반환
		return LikeDetailResponse.builder()
				.referenceId(referenceId)
				.likeCount(likeCount)
				.likerIds(likerIds)
				.build();
	}
}
