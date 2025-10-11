package com.teambind.gaechuserver.service;

import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeReadService {
	private final LikeRepository likeRepository;
	private final LikeAccountRepository likeAccountRepository;
	
	
	//TODO IMPL RESPONSE
	public List<?> fetchMyLikeByCategoryId(int categoryId, String likerId) {
		List<Likes> likes = likeRepository.findAllByCategoryIdAndLikerId(categoryId, likerId);
		return likes;
	}
	
}
