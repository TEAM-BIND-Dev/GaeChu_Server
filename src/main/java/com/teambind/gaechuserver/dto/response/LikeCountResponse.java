package com.teambind.gaechuserver.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class LikeCountResponse {
	private String referenceId;
	private Long likeCount;
	
	// JPQL constructor expression 에 맞는 생성자 추가
	public LikeCountResponse(String referenceId, Long likeCount) {
		this.referenceId = referenceId;
		this.likeCount = likeCount;
	}
}
