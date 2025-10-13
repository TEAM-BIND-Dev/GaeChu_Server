package com.teambind.gaechuserver.event.publisher;

import com.teambind.gaechuserver.event.events.LikeChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LikeChangeEventPublisher {
	
	private final EventPublisher eventPublisher;
	//TODO : implement this
	public void likeIncreaseEventPublish(LikeChangeEvent event) {
		eventPublisher.publish("like-created", event);
	}
	
	public void likeDecreaseEventPublish(LikeChangeEvent event) {
		eventPublisher.publish("like-deleted", event);
	}
}
