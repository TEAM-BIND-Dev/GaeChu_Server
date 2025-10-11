package com.teambind.gaechuserver.entity;

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
	
	@Id
	@Column(name = "reference_id", length = 100)
	private String referenceId;
	
	@Column(name = "account", nullable = false)
	private Long account;
	
	@Version
	@Column(name = "version", nullable = false)
	private Integer version;
}
