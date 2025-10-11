package com.teambind.gaechuserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes", uniqueConstraints = {
		@UniqueConstraint(name = "uq_likes_category_ref_liker", columnNames = {"category_id", "reference_id", "liker_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Likes {
	
	@Id
	@Column(name = "like_id")
	private String likeId;
	
	@Column(name = "category_id", nullable = false)
	private Integer categoryId;
	
	@Column(name = "reference_id", length = 100, nullable = false)
	private String referenceId;
	
	@Column(name = "liker_id", length = 100, nullable = false)
	private String likerId;
	
	@Column(name = "liked_at", nullable = false)
	private LocalDateTime likedAt;
}
