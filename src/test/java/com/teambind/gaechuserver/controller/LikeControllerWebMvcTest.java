package com.teambind.gaechuserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.dto.response.LikeDetailResponse;
import com.teambind.gaechuserver.service.LikeReadService;
import com.teambind.gaechuserver.service.LikeServiceAtomic;
import com.teambind.gaechuserver.utils.DataInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LikeController.class)
class LikeControllerWebMvcTest {
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	LikeReadService likeReadService;
	
	@MockBean
	LikeServiceAtomic likeServiceAtomic;
	
	@MockBean
	DataInitializer dataInitializer;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	@DisplayName("POST like: numeric category id and isLike=true calls like and returns 204")
	void postLikeNumeric() throws Exception {
		mvc.perform(post("/api/likes/3/ABC123")
						.param("likerId", "u1")
						.param("isLike", "true"))
				.andExpect(status().isNoContent());
		verify(likeServiceAtomic).like("ABC123", 3, "u1");
	}
	
	@Test
	@DisplayName("POST like: name category id resolves via DataInitializer; isLike=false calls unLike")
	void postUnlikeNameCategory() throws Exception {
		given(dataInitializer.getCategoryIdByName("BOARD")).willReturn(Optional.of(5));
		mvc.perform(post("/api/likes/BoArD/XYZ")
						.param("likerId", "u2")
						.param("isLike", "false"))
				.andExpect(status().isNoContent());
		verify(likeServiceAtomic).unLike("XYZ", 5, "u2");
	}
	
	@Test
	@DisplayName("POST like: null and blank category resolve to 0")
	void postLikeNullBlank() throws Exception {
		mvc.perform(post("/api/likes/null/REF1")
						.param("likerId", "u3")
						.param("isLike", "true"))
				.andExpect(status().isNoContent());
		verify(likeServiceAtomic).like("REF1", 0, "u3");
		
		mvc.perform(post("/api/likes/ \t /REF2")
						.param("likerId", "u4")
						.param("isLike", "true"))
				.andExpect(status().isNoContent());
		verify(likeServiceAtomic).like("REF2", 0, "u4");
	}
	
	@Test
	@DisplayName("POST like: invalid category name maps to 400 via GlobalExceptionHandler")
	void postLikeInvalidCategory() throws Exception {
		given(dataInitializer.getCategoryIdByName("UNKNOWN")).willReturn(Optional.empty());
		mvc.perform(post("/api/likes/UNKNOWN/REF")
						.param("likerId", "u1")
						.param("isLike", "true"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("허용하지않은 카테고리입니다"));
	}
	
	@Test
	@DisplayName("GET detail: numeric forwards id; returns LikeDetailResponse")
	void getDetailNumeric() throws Exception {
		LikeDetailResponse resp = LikeDetailResponse.builder()
				.referenceId("R1").likeCount(2L).likerIds(List.of("u1", "u2")).build();
		given(likeReadService.fetchLikeDetailByReferenceIdWithCategoryId("R1", 3)).willReturn(resp);
		
		mvc.perform(get("/api/likes/detail/3/R1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.referenceId", is("R1")))
				.andExpect(jsonPath("$.likeCount", is(2)))
				.andExpect(jsonPath("$.likerIds", containsInAnyOrder("u1", "u2")));
	}
	
	@Test
	@DisplayName("GET detail: name resolves to id; null resolves to null (not 0)")
	void getDetailNameAndNull() throws Exception {
		given(dataInitializer.getCategoryIdByName("BOARD")).willReturn(Optional.of(7));
		given(likeReadService.fetchLikeDetailByReferenceIdWithCategoryId("R2", 7))
				.willReturn(LikeDetailResponse.builder().referenceId("R2").likeCount(0L).likerIds(List.of()).build());
		
		mvc.perform(get("/api/likes/detail/board/R2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.likeCount", is(0)));
		
		given(likeReadService.fetchLikeDetailByReferenceIdWithCategoryId("R3", null))
				.willReturn(LikeDetailResponse.builder().referenceId("R3").likeCount(5L).likerIds(List.of("u9")).build());
		
		mvc.perform(get("/api/likes/detail/null/R3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.likeCount", is(5)));
	}
	
	@Test
	@DisplayName("GET count/{categoryId}: list parsing and null->0 substitution")
	void getCounts() throws Exception {
		given(likeReadService.fetchAllLikeByCategoryIdAndReferenceId(0, List.of("A", "B")))
				.willReturn(List.of(new LikeCountResponse("A", 1L), new LikeCountResponse("B", 0L)));
		
		mvc.perform(get("/api/likes/count/null").param("referenceIds", "A", "B"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].referenceId", is("A")))
				.andExpect(jsonPath("$[0].likeCount", is(1)))
				.andExpect(jsonPath("$[1].referenceId", is("B")))
				.andExpect(jsonPath("$[1].likeCount", is(0)));
	}
	
	@Test
	@DisplayName("GET count/{categoryId}/{userId}: null forwarding and normal numeric parsing")
	void getCountsByUser() throws Exception {
		given(likeReadService.getLikedReferencesWithCounts(null, "user-1"))
				.willReturn(List.of(new LikeCountResponse("X", 10L)));
		mvc.perform(get("/api/likes/count/null/user-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].referenceId", is("X")));
		
		given(likeReadService.getLikedReferencesWithCounts(3, "user-2"))
				.willReturn(List.of(new LikeCountResponse("Y", 9L)));
		mvc.perform(get("/api/likes/count/3/user-2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].likeCount", is(9)));
	}
}
