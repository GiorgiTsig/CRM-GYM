package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDto;
import com.epam.gymcrm.mappper.TrainingTypeMapper;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypesControllerTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypesController trainingTypesController;

    @Test
    void getTrainingType_shouldReturnTrainingTypeDtoList_whenTrainingTypesExist() {
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setTrainingTypeName("MMA");

        TrainingType trainingType2 = new TrainingType();
        trainingType2.setTrainingTypeName("Yoga");

        List<TrainingType> trainingTypes = List.of(trainingType1, trainingType2);

        TrainingTypeDto dto1 = new TrainingTypeDto();
        dto1.setTrainingTypeName("MMA");

        TrainingTypeDto dto2 = new TrainingTypeDto();
        dto2.setTrainingTypeName("Yoga");

        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);
        when(trainingTypeMapper.toTrainingTypeDto(trainingType1)).thenReturn(dto1);
        when(trainingTypeMapper.toTrainingTypeDto(trainingType2)).thenReturn(dto2);

        ResponseEntity<List<TrainingTypeDto>> response = trainingTypesController.getTrainingType();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto1, dto2), response.getBody());

        verify(trainingTypeRepository).findAll();
        verify(trainingTypeMapper).toTrainingTypeDto(trainingType1);
        verify(trainingTypeMapper).toTrainingTypeDto(trainingType2);
    }
}