package com.teambind.gaechuserver.utils;

import com.teambind.gaechuserver.entity.Categories;
import com.teambind.gaechuserver.repository.CategoriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DataInitializerTest {
	
	private CategoriesRepository categoriesRepository;
	private DataInitializer dataInitializer;
	
	@BeforeEach
	void setUp() {
		categoriesRepository = Mockito.mock(CategoriesRepository.class);
		dataInitializer = new DataInitializer(categoriesRepository);
	}
	
	private Categories cat(int id, String name) {
		Categories c = new Categories();
		c.setCategory_id(id);
		c.setCategoryName(name);
		return c;
	}
	
	@Test
	@DisplayName("init loads categories and builds maps (case-insensitive)")
	void initBuildsMaps() {
		when(categoriesRepository.findAll()).thenReturn(List.of(
				cat(1, "Board"),
				cat(2, "Notice"),
				cat(3, null)
		));
		
		dataInitializer.init();
		
		assertThat(DataInitializer.categories).hasSize(3);
		assertThat(DataInitializer.categoryNameToId).containsEntry("BOARD", 1).containsEntry("NOTICE", 2);
		assertThat(dataInitializer.getCategoryIdByName("board")).isEqualTo(Optional.of(1));
		assertThat(dataInitializer.getCategoryIdByName("BoArD")).isEqualTo(Optional.of(1));
		assertThat(dataInitializer.getCategoryIdByName("unknown")).isEmpty();
		assertThat(dataInitializer.getCategoryIdByName(null)).isEmpty();
	}
	
	@Test
	@DisplayName("reload replaces maps atomically")
	void reloadReplacesMaps() {
		when(categoriesRepository.findAll()).thenReturn(List.of(cat(1, "Board")));
		dataInitializer.init();
		assertThat(DataInitializer.categoryNameToId).containsEntry("BOARD", 1);
		
		when(categoriesRepository.findAll()).thenReturn(List.of(cat(2, "QnA")));
		dataInitializer.reload();
		
		assertThat(DataInitializer.categoryNameToId).doesNotContainKey("BOARD");
		assertThat(DataInitializer.categoryNameToId).containsEntry("QNA", 2);
	}
}
