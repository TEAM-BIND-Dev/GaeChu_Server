package com.teambind.gaechuserver.integration;

import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.dto.response.LikeDetailResponse;
import com.teambind.gaechuserver.entity.Ids.LikeAccountId;
import com.teambind.gaechuserver.entity.LikeAccount;
import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
import com.teambind.gaechuserver.service.LikeReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(LikeReadService.class)
class LikeReadIntegrationTest {
	
	@Autowired
	LikeRepository likeRepository;
	
	@Autowired
	LikeAccountRepository likeAccountRepository;
	
	@Autowired
	LikeReadService likeReadService;
	
	@BeforeEach
	void seed() {
		// category 3
		LikeAccountId id1 = new LikeAccountId();
		id1.setCategoryId(3);
		id1.setReferenceId("R1");
		likeAccountRepository.save(LikeAccount.builder().likeAccountId(id1).account(2L).version(0).build());
		
		LikeAccountId id2 = new LikeAccountId();
		id2.setCategoryId(3);
		id2.setReferenceId("R2");
		likeAccountRepository.save(LikeAccount.builder().likeAccountId(id2).account(1L).version(0).build());
		
		likeRepository.save(Likes.builder().likeId("L1").categoryId(3).referenceId("R1").likerId("u1").likedAt(LocalDateTime.now()).build());
		likeRepository.save(Likes.builder().likeId("L2").categoryId(3).referenceId("R1").likerId("u2").likedAt(LocalDateTime.now()).build());
		likeRepository.save(Likes.builder().likeId("L3").categoryId(3).referenceId("R2").likerId("u1").likedAt(LocalDateTime.now()).build());
	}
	
	@Test
	@DisplayName("getLikedReferencesWithCounts returns counts for given user and category")
	void likedReferencesWithCounts() {
		List<LikeCountResponse> res = likeReadService.getLikedReferencesWithCounts(3, "u1");
		assertThat(res).extracting(LikeCountResponse::getReferenceId).containsExactlyInAnyOrder("R1", "R2");
	}
	
	@Test
	@DisplayName("fetchLikeDetailByReferenceIdWithCategoryId returns like detail")
	void likeDetail() {
		LikeDetailResponse res = likeReadService.fetchLikeDetailByReferenceIdWithCategoryId("R1", 3);
		assertThat(res.getReferenceId()).isEqualTo("R1");
		assertThat(res.getLikeCount()).isEqualTo(2);
		assertThat(res.getLikerIds()).containsExactlyInAnyOrder("u1", "u2");
	}
}
