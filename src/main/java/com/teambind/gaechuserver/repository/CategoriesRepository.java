package com.teambind.gaechuserver.repository;

import com.teambind.gaechuserver.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
	Optional<Categories> findByCategoryName(String categoryName);
}
