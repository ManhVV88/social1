package com.example.social.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {
	
	
	private String firstName;
    private String lastName;   
    
    @Pattern(regexp = "^((19|20|21|22)\\d\\d)-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$",message = "dateOfBirth invalid date")
    private String  dateOfBirth;   
    @Min(value = 1 , message= "Job must be at least 1")  
    private Integer job;
    private String address;
    
}
