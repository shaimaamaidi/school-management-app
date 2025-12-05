package com.example.backend.controller;

import com.example.backend.service.impl.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @DeleteMapping("/add/{id}")
    public ResponseEntity<String> deleteAdmin(@RequestParam Long id){
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin has been deleted successfully.");
    }
}
