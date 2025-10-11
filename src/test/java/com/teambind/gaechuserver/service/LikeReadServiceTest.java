package com.teambind.gaechuserver.service;

import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.dto.response.LikeDetailResponse;
import com.teambind.gaechuserver.entity.Ids.LikeAccountId;
import com.teambind.gaechuserver.entity.LikeAccount;
import com.teambind.gaechuserver.entity.Likes;
import com.teambind.gaechuserver.repository.LikeAccountRepository;
import com.teambind.gaechuserver.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LikeReadServiceTest {
	
	private LikeRepository likeRepository;
	private LikeAccountRepository likeAccountRepository;
	private LikeReadService service;
	
	@BeforeEach
	void setUp() {
		likeRepository = mock(LikeRepository.class);
		likeAccountRepository = mock(LikeAccountRepository.class);
		service = new LikeReadService(likeRepository, likeAccountRepository);
	}
	
	@Test
	@DisplayName("fetchAllLikeByCategoryIdAndReferenceId returns counts for existing and 0 for missing")
	void fetchAllCounts() {
		LikeAccountId id1 = new LikeAccountId();
		id1.setCategoryId(3);
		id1.setReferenceId("r1");
		LikeAccount la1 = LikeAccount.builder().likeAccountId(id1).account(10L).build();
		LikeAccountId id2 = new LikeAccountId();
		id2.setCategoryId(3);
		id2.setReferenceId("r2");
		LikeAccount la2 = LikeAccount.builder().likeAccountId(id2).account(0L).build();
		
		when(likeAccountRepository.findAllById(anyIterable())).thenReturn(List.of(la1, la2));
		
		List<LikeCountResponse> result = service.fetchAllLikeByCategoryIdAndReferenceId(3, List.of("r1", "r2", "r3"));
		assertThat(result).containsExactly(
				new LikeCountResponse("r1", 10L),
				new LikeCountResponse("r2", 0L),
				new LikeCountResponse("r3", 0L)
		);
	}
	
	@Test
	@DisplayName("fetchLikeDetailByReferenceIdWithCategoryId returns liker ids and count; missing count as 0")
	void fetchLikeDetail() {
		when(likeRepository.findAllByCategoryIdAndReferenceId(3, "ref")).thenReturn(List.of(
				Likes.builder().referenceId("ref").categoryId(3).likerId("u1").build(),
				Likes.builder().referenceId("ref").categoryId(3).likerId("u2").build()
		));
		when(likeAccountRepository.findByLikeAccountIdReferenceIdAndLikeAccountIdCategoryId("ref", 3))
				.thenReturn(Optional.of(LikeAccount.builder().likeAccountId(new LikeAccountId()).account(2L).build()));
		
		LikeDetailResponse res = service.fetchLikeDetailByReferenceIdWithCategoryId("ref", 3);
		assertThat(res.getReferenceId()).isEqualTo("ref");
		assertThat(res.getLikeCount()).isEqualTo(2);
		assertThat(res.getLikerIds()).containsExactlyInAnyOrder("u1", "u2");
	}
	
	@Test
	@DisplayName("fetchLikeDetailByReferenceIdWithCategoryId handles null categoryId and missing account")
	void fetchLikeDetailNullCategoryOrMissingAccount() {
		when(likeRepository.findAllByCategoryIdAndReferenceId(null, "ref")).thenReturn(List.of());
		when(likeAccountRepository.findByLikeAccountIdReferenceIdAndLikeAccountIdCategoryId("ref", null))
				.thenReturn(Optional.empty());
		
		LikeDetailResponse res = service.fetchLikeDetailByReferenceIdWithCategoryId("ref", null);
		assertThat(res.getLikeCount()).isEqualTo(0);
		assertThat(res.getLikerIds()).isEmpty();
	}
}
