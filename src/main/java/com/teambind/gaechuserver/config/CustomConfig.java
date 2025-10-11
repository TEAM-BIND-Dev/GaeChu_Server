package com.teambind.gaechuserver.config;


import com.teambind.gaechuserver.utils.generator.KeyProvider;
import com.teambind.gaechuserver.utils.generator.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfig {
	@Bean
	public KeyProvider keyGenerator() {
		return new Snowflake();
	}
}
