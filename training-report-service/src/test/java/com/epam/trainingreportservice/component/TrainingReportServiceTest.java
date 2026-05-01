package com.epam.trainingreportservice.component;

import com.epam.trainingreportservice.service.TrainerSummaryService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TrainingReportServiceTest {

    @MockitoBean
    private TrainerSummaryService trainerSummaryService;
}
