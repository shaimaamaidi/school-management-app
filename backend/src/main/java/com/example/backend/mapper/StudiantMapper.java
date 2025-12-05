package com.example.backend.mapper;

import com.example.backend.dto.StudiantDTO;
import com.example.backend.entity.Studiant;
import com.example.backend.enumeration.Level;
import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.exceptions.InvalidOperationException;

public class StudiantMapper {

    public static Studiant toEntity(StudiantDTO studiantDTO){
        Studiant studiant=new Studiant();
        studiant.setId(studiantDTO.getId());
        studiant.setUsername(studiantDTO.getUsername());
        String level= studiantDTO.getLevel();
        if(level!=null && !level.isBlank()){
            switch (level.toUpperCase()){
                case "FIRST_YEAR" ->studiant.setLevel(Level.FIRST_YEAR);
                case "SECOND_YEAR" ->studiant.setLevel(Level.SECOND_YEAR);
                case "THIRD_YEAR" ->studiant.setLevel(Level.THIRD_YEAR);
                default ->throw  new InvalidOperationException("Level " + level+" is invalid", ErrorCodes.LEVEL_NOT_VALID);

            }
        }
        return studiant;
    }
    public static  StudiantDTO toDTO(Studiant studiant){
        StudiantDTO studiantDTO=new StudiantDTO();
        studiantDTO.setId(studiant.getId());
        studiantDTO.setUsername(studiant.getUsername());
        studiantDTO.setLevel(studiant.getLevel().toString());
        return studiantDTO;
    }

}
