package com.teambind.gaechuserver.event.events;

import java.time.LocalDateTime;

public class LikeChangeEvent {
	private Long referenceId;
	private Long likerId;
	private Long likeCount;
	private LocalDateTime createdAt;
	
	public LikeChangeEvent(Long referenceId, Long likerId, Long likeCount) {
		this.referenceId = referenceId;
		this.likerId = likerId;
		this.likeCount = likeCount;
		this.createdAt = LocalDateTime.now();
	}
}
