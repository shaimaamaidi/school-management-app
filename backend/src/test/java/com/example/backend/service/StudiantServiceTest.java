package com.example.backend.service;


import com.example.backend.dto.StudiantDTO;
import com.example.backend.entity.Studiant;
import com.example.backend.enumeration.Level;
import com.example.backend.exceptions.EntityNotFoundException;
import com.example.backend.exceptions.InvalidOperationException;
import com.example.backend.exceptions.ResourceAlreadyExistsException;
import com.example.backend.repository.StudiantRepository;
import com.example.backend.service.impl.StudiantService;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudiantServiceTest {

    @Mock
    private StudiantRepository studiantRepository;

    @InjectMocks
    private StudiantService studiantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStudiants() {
        Studiant s = new Studiant();
        s.setId(1L);
        s.setUsername("user1");
        s.setLevel(Level.FIRST_YEAR);

        Page<Studiant> page = new PageImpl<>(List.of(s));
        when(studiantRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<StudiantDTO> result = studiantService.getAllStudiants(0, 10);
        assertEquals(1, result.getContent().size());
        assertEquals("user1", result.getContent().get(0).getUsername());
    }

    @Test
    void testGetStudiantById_Found() {
        Studiant s = new Studiant();
        s.setId(1L);
        s.setUsername("user1");
        s.setLevel(Level.FIRST_YEAR);

        when(studiantRepository.findById(1L)).thenReturn(Optional.of(s));
        StudiantDTO dto = studiantService.getStudiantById(1L);
        assertNotNull(dto);
        assertEquals("user1", dto.getUsername());
    }

    @Test
    void testGetStudiantById_NotFound() {
        when(studiantRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(studiantService.getStudiantById(1L));
    }

    @Test
    void testAddStudiant_Success() {
        StudiantDTO dto = new StudiantDTO();
        dto.setUsername("user1");
        dto.setLevel(Level.FIRST_YEAR.toString());

        when(studiantRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(studiantRepository.save(any(Studiant.class))).thenAnswer(i -> i.getArgument(0));

        studiantService.addStudiant(dto);
        verify(studiantRepository).save(any(Studiant.class));
    }

    @Test
    void testAddStudiant_AlreadyExists() {
        StudiantDTO dto = new StudiantDTO();
        dto.setUsername("user1");

        when(studiantRepository.findByUsername("user1")).thenReturn(Optional.of(new Studiant()));

        assertThrows(ResourceAlreadyExistsException.class, () -> studiantService.addStudiant(dto));
    }

    @Test
    void testUpdateStudiant_Success() {
        StudiantDTO dto = new StudiantDTO();
        dto.setId(1L);
        dto.setUsername("user2");
        dto.setLevel(Level.SECOND_YEAR.toString());

        Studiant existing = new Studiant();
        existing.setId(1L);
        existing.setUsername("user1");
        existing.setLevel(Level.FIRST_YEAR);

        when(studiantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studiantRepository.save(any(Studiant.class))).thenReturn(existing);

        studiantService.updateStudiant(dto);

        assertEquals("user2", existing.getUsername());
        assertEquals(Level.SECOND_YEAR, existing.getLevel());
    }

    @Test
    void testUpdateStudiant_NotFound() {
        StudiantDTO dto = new StudiantDTO();
        dto.setId(1L);
        when(studiantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> studiantService.updateStudiant(dto));
    }

    @Test
    void testDeleteStudiant() {
        doNothing().when(studiantRepository).deleteById(1L);
        studiantService.deleteStudiant(1L);
        verify(studiantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchStudinatsById() {
        Studiant s = new Studiant();
        s.setUsername("user1");
        s.setLevel(Level.FIRST_YEAR);

        Page<Studiant> page = new PageImpl<>(List.of(s));
        when(studiantRepository.findByUsernameContainingIgnoreCase(eq("user"), any(Pageable.class))).thenReturn(page);

        Page<StudiantDTO> result = studiantService.searchStudinatsById("user",0,10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFilterByLevel_Success() {
        Studiant s = new Studiant();
        s.setLevel(Level.FIRST_YEAR);

        Page<Studiant> page = new PageImpl<>(List.of(s));
        when(studiantRepository.findByLevel(eq(Level.FIRST_YEAR), any(Pageable.class))).thenReturn(page);

        Page<StudiantDTO> result = studiantService.filterByLevel("FIRST_YEAR",0,10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFilterByLevel_InvalidLevel() {
        assertThrows(InvalidOperationException.class, () -> studiantService.filterByLevel("INVALID",0,10));
    }

    @Test
    void testExportStudentsToCSV() throws IOException {
        Studiant s = new Studiant();
        s.setId(1L);
        s.setUsername("user1");
        s.setLevel(Level.FIRST_YEAR);

        when(studiantRepository.findAll()).thenReturn(List.of(s));

        ByteArrayInputStream inputStream = studiantService.exportStudentsToCSV();
        assertNotNull(inputStream);

        String csvContent = new String(inputStream.readAllBytes());
        assertTrue(csvContent.contains("user1"));
        assertTrue(csvContent.contains("FIRST_YEAR"));
    }

    @Test
    void testImportStudentsFromCSV() throws IOException {
        String csv = "id,username,level\n1,user1,FIRST_YEAR\n";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                csv.getBytes()
        );

        when(studiantRepository.save(any(Studiant.class))).thenAnswer(i -> i.getArgument(0));

        studiantService.importStudentsFromCSV(file);
        verify(studiantRepository, times(1)).save(any(Studiant.class));
    }
}
