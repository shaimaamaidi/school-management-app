package com.example.backend.service.impl;

import com.example.backend.dto.AdminDTO;
import com.example.backend.entity.Admin;
import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.exceptions.InvalidEntityException;
import com.example.backend.exceptions.ResourceAlreadyExistsException;
import com.example.backend.mapper.AdminMapper;
import com.example.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin addAdmin(AdminDTO adminDTO){
        Admin adminExisting=adminRepository.findByUsername(adminDTO.getUsername()).orElse(null);
        if(adminExisting!=null){
            throw new ResourceAlreadyExistsException(
                    "Admin with username "+adminDTO.getUsername()+" already exits.",
                    ErrorCodes.ADMIN_ALREADY_EXISTS);
        }
        Admin admin= AdminMapper.toEntity(adminDTO);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
    public void deleteAdmin(Long id){
        adminRepository.deleteById(id);
    }
    public Admin findByUsername(String username){
        return adminRepository.findByUsername(username).orElseThrow(()->new InvalidEntityException(
                "Username not found.",
                ErrorCodes.ADMIN_NOT_FOUND));
    }
}
