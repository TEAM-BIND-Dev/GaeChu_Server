package com.teambind.gaechuserver.event.events;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeChangeEvent {
	private String articleId;
	private String likerId;
}
