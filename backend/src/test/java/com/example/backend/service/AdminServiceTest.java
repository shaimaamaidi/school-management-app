package com.example.backend.service;


import com.example.backend.dto.AdminDTO;
import com.example.backend.entity.Admin;
import com.example.backend.exceptions.InvalidEntityException;
import com.example.backend.exceptions.ResourceAlreadyExistsException;
import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.repository.AdminRepository;
import com.example.backend.service.impl.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddAdmin_Success() {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUsername("admin1");
        adminDTO.setPassword("pass123");

        when(adminRepository.findByUsername("admin1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Admin savedAdmin = adminService.addAdmin(adminDTO);

        assertNotNull(savedAdmin);
        assertEquals("admin1", savedAdmin.getUsername());
        assertEquals("encodedPass", savedAdmin.getPassword());

        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testAddAdmin_AlreadyExists() {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUsername("admin1");
        adminDTO.setPassword("pass123");

        Admin existingAdmin = new Admin();
        existingAdmin.setUsername("admin1");

        when(adminRepository.findByUsername("admin1")).thenReturn(Optional.of(existingAdmin));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> adminService.addAdmin(adminDTO));

        assertEquals("Admin with username admin1 already exits.", exception.getMessage());
        assertEquals(ErrorCodes.ADMIN_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void testFindByUsername_Success() {
        Admin admin = new Admin();
        admin.setUsername("admin1");

        when(adminRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));

        Admin result = adminService.findByUsername("admin1");

        assertNotNull(result);
        assertEquals("admin1", result.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        when(adminRepository.findByUsername("admin1")).thenReturn(Optional.empty());

        InvalidEntityException exception = assertThrows(InvalidEntityException.class,
                () -> adminService.findByUsername("admin1"));

        assertEquals("Username not found.", exception.getMessage());
        assertEquals(ErrorCodes.ADMIN_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testDeleteAdmin() {
        doNothing().when(adminRepository).deleteById(1L);

        adminService.deleteAdmin(1L);

        verify(adminRepository, times(1)).deleteById(1L);
    }
}
