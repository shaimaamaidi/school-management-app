package com.example.backend.controller;

import com.example.backend.dto.StudiantDTO;
import com.example.backend.service.impl.StudiantService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/studiants")
@RequiredArgsConstructor
public class StudiantController {

	private final StudiantService studiantService;

	@GetMapping
	public ResponseEntity<Page<StudiantDTO>> getAllStudents(@RequestParam int page, @RequestParam int size) {
		return ResponseEntity.ok(studiantService.getAllStudiants(page, size));
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudiantDTO> getStudentById(@PathVariable Long id) {
		StudiantDTO student = studiantService.getStudiantById(id);
		if (student != null) {
			return ResponseEntity.ok(student);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<String> addStudent(@RequestBody StudiantDTO studentDTO) {
		studiantService.addStudiant(studentDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body("Studiant has been added successfully.");
	}

	@PutMapping
	public ResponseEntity<String> updateStudent(@RequestBody StudiantDTO studentDTO) {
		studiantService.updateStudiant(studentDTO);
		return ResponseEntity.ok().body("Studiant has been updated successfully.");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
		studiantService.deleteStudiant(id);
		return ResponseEntity.ok("Student has been deleted successfully.");
	}

	@GetMapping("/search/{username}")
	public ResponseEntity<Page<StudiantDTO>> searchStudents(@PathVariable String username,@RequestParam int page, @RequestParam int size) {
		return ResponseEntity.ok(studiantService.searchStudinatsById(username, page, size));
	}

	@GetMapping("/filter/{level}")
	public ResponseEntity<Page<StudiantDTO>> filterByLevel(@RequestParam String level,@RequestParam int page, @RequestParam int size) {
		return ResponseEntity.ok(studiantService.filterByLevel(level, page, size));
	}

	@PostMapping("/import")
	public ResponseEntity<String> importCSV(@RequestParam("file") MultipartFile file) {
		studiantService.importStudentsFromCSV(file);
		return ResponseEntity.ok("CSV imported successfully");
	}

	@GetMapping("/export")
	public ResponseEntity<InputStreamResource> exportCSV(){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=students.csv");

		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(new InputStreamResource(studiantService.exportStudentsToCSV()));
	}
}
