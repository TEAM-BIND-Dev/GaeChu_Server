package com.teambind.gaechuserver.service;

import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.exceptions.CustomException;
import com.teambind.gaechuserver.exceptions.ErrorCode;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
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
	
	
	@Transactional
	public int like(String referenceId, int categoryId, String likerId) {
		Likes like = Likes.builder()
				.likeId(java.util.UUID.randomUUID().toString())
				.categoryId(categoryId)
				.referenceId(referenceId)
				.likerId(likerId)
				.likedAt(java.time.LocalDateTime.now())
				.build();
		likeRepository.save(like);
		
		// DB에서 atomic upsert로 증가
		return likeAccountRepository.upsertIncrementAccount(referenceId);
	}
	
	@Transactional
	public int unLike(String referenceId, int categoryId, String likerId) {
		Likes like = likeRepository.findByReferenceIdAndCategoryIdAndLikerId(referenceId, categoryId, likerId);
		if (like == null) {
			throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
		}
		likeRepository.delete(like);
		
		// DB에서 atomic upsert로 증가
		return likeAccountRepository.decrementAccountIfPositive(referenceId);
	}
}
