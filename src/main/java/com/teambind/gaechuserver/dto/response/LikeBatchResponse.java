package com.teambind.gaechuserver.dto.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeBatchResponse {
	private Long referenceId;
	private Long likeCount;
}
