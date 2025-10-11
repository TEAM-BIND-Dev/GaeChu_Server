package com.teambind.gaechuserver.repository;


import com.teambind.gaechuserver.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, String> {
	Likes findByReferenceIdAndCategoryIdAndLikerId(String referenceId, int categoryId, String likerId);
}
