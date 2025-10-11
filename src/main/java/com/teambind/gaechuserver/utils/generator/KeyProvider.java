package com.teambind.gaechuserver.utils.generator;

import org.springframework.stereotype.Component;

@Component
public interface KeyProvider {
	
	String generateKey();
	
	Long generateLongKey();
}
