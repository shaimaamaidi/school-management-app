package com.example.backend.mapper;

import com.example.backend.dto.AdminDTO;
import com.example.backend.entity.Admin;

public class AdminMapper {

    public static Admin toEntity(AdminDTO adminDTO){
        Admin admin =new Admin();
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());
        return admin;
    }
    public static AdminDTO toDto(Admin admin){
        AdminDTO adminDTO =new AdminDTO();
        adminDTO.setUsername(admin.getUsername());
        return adminDTO;
    }
}
