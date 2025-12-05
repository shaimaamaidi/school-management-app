package com.example.backend.service.impl;

import com.example.backend.dto.StudiantDTO;
import com.example.backend.entity.Studiant;
import com.example.backend.enumeration.Level;
import com.example.backend.exceptions.EntityNotFoundException;
import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.exceptions.InvalidOperationException;
import com.example.backend.exceptions.ResourceAlreadyExistsException;
import com.example.backend.mapper.StudiantMapper;
import com.example.backend.repository.StudiantRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudiantService  {
	private final StudiantRepository studiantRepository;

	public Page<StudiantDTO> getAllStudiants(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return studiantRepository.findAll(pageable)
				.map(StudiantMapper::toDTO);
	}
	public StudiantDTO getStudiantById(Long id){
		Studiant studiant=studiantRepository.findById(id).orElse(null);
		if(studiant!=null)
			return StudiantMapper.toDTO(studiant);
		return null;
	}
	public void addStudiant(StudiantDTO studiantDTO){
		Studiant studiantExisting=studiantRepository.findByUsername(studiantDTO.getUsername()).orElse(null);
		if(studiantExisting!=null){
			throw new ResourceAlreadyExistsException(
					"Studiant with username "+studiantDTO.getUsername()+" already exists.",
					ErrorCodes.STUDIANT_ALREADY_EXISTS
			);
		}
		Studiant studiant=StudiantMapper.toEntity(studiantDTO);
		studiantRepository.save(studiant);
	}
	public void updateStudiant(StudiantDTO studiantDTO){
		Studiant studiantExisting = studiantRepository.findById(studiantDTO.getId())
				.orElseThrow(()->new EntityNotFoundException(
						"Studiant not found with id: " + studiantDTO.getId(),
						ErrorCodes.STUDIANT_NOT_FOUND
				));
		Studiant studinant=StudiantMapper.toEntity(studiantDTO);

		if(studinant.getUsername()!=null && !studinant.getUsername().isBlank() && !studinant.getUsername().equals(studiantExisting.getUsername()))
			studiantExisting.setUsername(studinant.getUsername());

		if(studiantExisting.getLevel()!=studinant.getLevel())
			studiantExisting.setLevel(studinant.getLevel());

		studiantRepository.save(studiantExisting);
	}

	public void deleteStudiant(Long id){
		studiantRepository.deleteById(id);
	}

	public Page<StudiantDTO> searchStudinatsById(String username,int page,int size){
		Pageable pageable = PageRequest.of(page, size);
		if (username != null && !username.isEmpty())
			return studiantRepository.findByUsernameContainingIgnoreCase(username,pageable)
					.map(StudiantMapper::toDTO);
		else
			return Page.empty();
	}

	public Page<StudiantDTO> filterByLevel(String level,int page,int size){
		Pageable pageable = PageRequest.of(page, size);
		Level levelSearch=null;
		if(level!=null && !level.isBlank()){
			switch (level.toUpperCase()){
				case "FIRST_YEAR" ->levelSearch=Level.FIRST_YEAR;
				case "SECOND_YEAR" ->levelSearch=Level.SECOND_YEAR;
				case "THIRD_YEAR" ->levelSearch=Level.THIRD_YEAR;
				default ->throw  new InvalidOperationException("Level " + level+" is invalid", ErrorCodes.LEVEL_NOT_VALID);

			}
		}
		if(levelSearch!=null){
			return studiantRepository.findByLevel(levelSearch,pageable)
					.map(StudiantMapper::toDTO);
		}else
			return Page.empty();
	}
	public ByteArrayInputStream exportStudentsToCSV() {
		List<Studiant> students = studiantRepository.findAll();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
			String[] header = { "id", "username", "level" };
			writer.writeNext(header);

			for (Studiant s : students) {
				String[] line = {
						s.getId() != null ? s.getId().toString() : "",
						s.getUsername(),
						s.getLevel().name()
				};
				writer.writeNext(line);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while exporting CSV", e);
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public void importStudentsFromCSV(MultipartFile file) {
		try {
			File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
			try (FileOutputStream fos = new FileOutputStream(convFile)) {
				fos.write(file.getBytes());
			}

			try (CSVReader reader = new CSVReader(new FileReader(convFile))) {
				String[] line;
				boolean isHeader = true;
				while ((line = reader.readNext()) != null) {
					if (isHeader) {
						isHeader = false;
						continue;
					}

					Studiant s = new Studiant();
					if (line[0] != null && !line[0].isBlank()) {
						s.setId(Long.parseLong(line[0]));
					}
					s.setUsername(line[1]);
					s.setLevel(Level.valueOf(line[2].toUpperCase()));

					studiantRepository.save(s);
				}
			}
		} catch (IOException | CsvValidationException e) {
			throw new RuntimeException("Error while importing CSV", e);
		}
	}
}
