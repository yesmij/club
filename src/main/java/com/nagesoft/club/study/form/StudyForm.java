package com.nagesoft.club.study.form;

import com.nagesoft.club.domain.Tag;
import com.nagesoft.club.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder @NoArgsConstructor
@AllArgsConstructor
@Data
public class StudyForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank
    @Length(min = 3, max = 50)
    private String title;

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;

    @NotBlank
    @Size(max = 50)
    private String shortDescription;

    @Length
    private String fullDescription;
}
