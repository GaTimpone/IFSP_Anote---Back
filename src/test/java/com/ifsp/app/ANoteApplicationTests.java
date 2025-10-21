package com.ifsp.app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Ignorando o teste de contexto - não necessário para testes unitários")
class ANoteApplicationTests {

	@Test
	void contextLoads() {
	}

}
