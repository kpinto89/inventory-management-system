package com.demo;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class InventoryManagementApplicationTest {

    @Test
    void mainDelegatesToSpringApplicationRun() {
        String[] args = {"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            InventoryManagementApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(InventoryManagementApplication.class, args));
        }
    }
}

