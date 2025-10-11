package com.teambind.gaechuserver.entity;

import com.teambind.gaechuserver.entity.Ids.LikeAccountId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeAccount {
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "categoryId", column = @Column(name = "category_id", nullable = false)),
			@AttributeOverride(name = "referenceId", column = @Column(name = "reference_id", length = 100, nullable = false))
	})
	private LikeAccountId likeAccountId;
	
	@Column(name = "account", nullable = false)
	private Long account;
	
	@Version
	@Column(name = "version", nullable = false)
	private Integer version;
}
