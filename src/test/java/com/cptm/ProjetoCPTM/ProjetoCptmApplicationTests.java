package com.cptm.ProjetoCPTM;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:context;DB_CLOSE_DELAY=-1","rail.scheduler.enabled=false","rail.security.admin-password=test-admin-password"})
class ProjetoCptmApplicationTests {

	@Test
	void contextLoads() {
	}

}
