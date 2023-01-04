package com.nagesoft.club.modules.study.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Data
public class StudyDescriptionForm {
    @NotBlank
    @Size(max = 50)
    private String shortDescription;

    @Length
    private String fullDescription;
}
