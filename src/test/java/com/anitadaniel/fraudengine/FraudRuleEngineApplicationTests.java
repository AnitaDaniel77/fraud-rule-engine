package com.anitadaniel.fraudengine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // use the in-memory test database
class FraudRuleEngineApplicationTests {

	@Test
	void contextLoads() {
	}

}