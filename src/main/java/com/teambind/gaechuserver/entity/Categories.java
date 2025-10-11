package com.teambind.gaechuserver.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categories {
	@Id
	@Column(name = "category_id")
	private int category_id;
	
	@Column(name = "category_name")
	private String categoryName;
	
	
}
