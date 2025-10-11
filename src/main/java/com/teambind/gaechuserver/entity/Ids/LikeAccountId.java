package com.teambind.gaechuserver.entity.Ids;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LikeAccountId {
	@Column(name = "category_id", nullable = false)
	private Integer categoryId;
	@Column(name = "reference_id", length = 100, nullable = false)
	private String referenceId;
}
