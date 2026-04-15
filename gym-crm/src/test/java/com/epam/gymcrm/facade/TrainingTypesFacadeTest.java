package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.mapper.TrainingTypeMapper;
import com.epam.gymcrm.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypesFacadeTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypesFacade trainingTypesFacade;

    @Test
    void findAll_mapsDomainObjectsToDtoList() {
        TrainingType trainingType = new TrainingType("MMA");
        TrainingTypeDetailsDto dto = new TrainingTypeDetailsDto();
        dto.setId(UUID.randomUUID());
        dto.setTrainingTypeName("MMA");

        when(trainingTypeService.findAll()).thenReturn(List.of(trainingType));
        when(trainingTypeMapper.toTrainingTypeDto(trainingType)).thenReturn(dto);

        List<TrainingTypeDetailsDto> result = trainingTypesFacade.findAll();

        assertEquals(1, result.size());
        assertEquals("MMA", result.get(0).getTrainingTypeName());
        verify(trainingTypeService).findAll();
        verify(trainingTypeMapper).toTrainingTypeDto(trainingType);
    }
}

