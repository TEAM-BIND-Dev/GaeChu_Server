package com.teambind.gaechuserver.entity.Ids;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
public class LikeAccountId implements Serializable {
	@Column(name = "category_id", nullable = false)
	private Integer categoryId;
	@Column(name = "reference_id", length = 100, nullable = false)
	private String referenceId;
}
