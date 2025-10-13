package com.teambind.gaechuserver.service;

import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.event.events.LikeChangeEvent;
import com.teambind.gaechuserver.event.publisher.LikeChangeEventPublisher;
import com.teambind.gaechuserver.exceptions.CustomException;
import com.teambind.gaechuserver.exceptions.ErrorCode;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
import com.teambind.gaechuserver.utils.generator.KeyProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceAtomic {
	private final LikeRepository likeRepository;
	private final LikeAccountRepository likeAccountRepository;
	private final KeyProvider keyProvider;
	private final LikeChangeEventPublisher likeChangeEventPublisher;
	
	
	@Transactional
	public int like(String referenceId, int categoryId, String likerId) {
		// 1) 부모 테이블(like_account)을 먼저 upsert하여 FK 보장
		int affected = likeAccountRepository.upsertIncrementAccount(categoryId, referenceId);
		
		// 2) 자식 테이블(likes)에 사용자 개별 like 기록 저장
		Likes like = Likes.builder()
				.likeId(keyProvider.generateKey())
				.categoryId(categoryId)
				.referenceId(referenceId)
				.likerId(likerId)
				.likedAt(java.time.LocalDateTime.now())
				.build();
		likeRepository.saveAndFlush(like);
		
		likeChangeEventPublisher.likeIncreaseEventPublish(LikeChangeEvent.builder()
				.articleId(like.getReferenceId())
				.likerId(likerId)
				.build());
		
		return affected;
	}
	
	@Transactional
	public int unLike(String referenceId, int categoryId, String likerId) {
		Likes like = likeRepository.findByReferenceIdAndCategoryIdAndLikerId(referenceId, categoryId, likerId);
		if (like == null) {
			throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
		}
		likeRepository.delete(like);
		likeChangeEventPublisher.likeDecreaseEventPublish(LikeChangeEvent.builder()
				.articleId(like.getReferenceId())
				.likerId(likerId)
				.build());
		
		// DB에서 atomic 감소 (categoryId 추가)
		return likeAccountRepository.decrementAccountIfPositive(categoryId, referenceId);
	}
}
