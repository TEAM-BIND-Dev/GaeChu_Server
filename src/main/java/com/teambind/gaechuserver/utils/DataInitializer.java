package com.teambind.gaechuserver.utils;


import com.teambind.gaechuserver.entity.Categories;
import com.teambind.gaechuserver.repository.CategoriesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
	public static final Map<Integer, Categories> categories = new HashMap<Integer, Categories>();
	//TODO IMPL REPOSITORY
	private final CategoriesRepository categoriesRepository;
	
	
	@PostConstruct
	public void init() {
		log.info("DataInitializer init");
		List<Categories> categoriesList = categoriesRepository.findAll();
		categoriesList.forEach(category -> categories.put(category.getCategory_id(), category));
		log.info("DataInitializer init end");
	}
}
