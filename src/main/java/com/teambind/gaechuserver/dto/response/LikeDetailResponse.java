package com.teambind.gaechuserver.dto.response;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDetailResponse {
	private String referenceId;
	private Long likeCount;
	private List<String> likerIds;
}
