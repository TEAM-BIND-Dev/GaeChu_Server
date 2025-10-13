package com.teambind.gaechuserver.event.events;

public class LikeChangeEvent {
	private Long articleId;
	private Long likerId;
	
	public LikeChangeEvent(Long articleId, Long likerId) {
		this.articleId = articleId;
		this.likerId = likerId;
	}
}
