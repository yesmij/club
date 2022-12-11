package com.nagesoft.club.tag;

import com.nagesoft.club.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Tag findnCreateTag(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);
        if(tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }
}
