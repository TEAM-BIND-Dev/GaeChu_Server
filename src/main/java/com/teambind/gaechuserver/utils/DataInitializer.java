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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
	public static final Map<Integer, Categories> categories = new HashMap<Integer, Categories>();
	public static final Map<String, Integer> categoryNameToId = new ConcurrentHashMap<>();
	//TODO IMPL REPOSITORY
	private final CategoriesRepository categoriesRepository;
	
	@PostConstruct
	public void init() {
		log.info("DataInitializer init");
		List<Categories> categoriesList = categoriesRepository.findAll();
		categoriesList.forEach(category -> {
			categories.put(category.getCategory_id(), category);
			if (category.getCategoryName() != null) {
				categoryNameToId.put(category.getCategoryName().toUpperCase(), category.getCategory_id());
			}
		});
		log.info("DataInitializer init end - loaded {} categories", categoriesList.size());
	}
	
	public Optional<Integer> getCategoryIdByName(String name) {
		if (name == null) return Optional.empty();
		return Optional.ofNullable(categoryNameToId.get(name.toUpperCase()));
	}
	
	/**
	 * 런타임에 카테고리가 변경될 수 있다면 외부에서 맵을 재로딩할 수 있는 메서드를 제공합니다.
	 * 재로딩 시 동시성 문제를 피하기 위해 임시 맵으로 읽어들인 뒤 교체합니다.
	 */
	public void reload() {
		List<Categories> categoriesList = categoriesRepository.findAll();
		Map<Integer, Categories> newById = new ConcurrentHashMap<>();
		Map<String, Integer> newByName = new ConcurrentHashMap<>();
		categoriesList.forEach(category -> {
			newById.put(category.getCategory_id(), category);
			if (category.getCategoryName() != null) {
				newByName.put(category.getCategoryName().toUpperCase(), category.getCategory_id());
			}
		});
		categories.clear();
		categories.putAll(newById);
		categoryNameToId.clear();
		categoryNameToId.putAll(newByName);
		log.info("DataInitializer reloaded {} categories", categoriesList.size());
	}
	
}
