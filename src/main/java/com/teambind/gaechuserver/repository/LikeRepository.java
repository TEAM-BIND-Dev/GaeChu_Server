package com.teambind.gaechuserver.repository;


import com.teambind.gaechuserver.dto.response.LikeCountResponse;
import com.teambind.gaechuserver.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Likes, String> {
	@Query("SELECT new com.teambind.gaechuserver.dto.response.LikeCountResponse(l.referenceId, la.account) " +
			"FROM Likes l JOIN LikeAccount la " +
			"ON la.likeAccountId.referenceId = l.referenceId AND la.likeAccountId.categoryId = l.categoryId " +
			"WHERE l.categoryId = :categoryId AND l.likerId = :likerId")
	List<LikeCountResponse> findLikeCountsByCategoryAndLiker(@Param("categoryId") Integer categoryId,
	                                                         @Param("likerId") String likerId);
	
	Likes findByReferenceIdAndCategoryIdAndLikerId(String referenceId, int categoryId, String likerId);
	
	List<Likes> findAllByCategoryIdAndLikerId(int categoryId, String likerId);
	
	List<Likes> findAllByCategoryIdAndReferenceId(Integer categoryId, String referenceId);
	
	
}
