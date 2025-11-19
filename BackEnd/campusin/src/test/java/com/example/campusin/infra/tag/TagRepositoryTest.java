package com.example.campusin.infra.tag;

import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagRepository")
class TagRepositoryTest extends DataJpaTestSupport {

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("태그를 저장하고 조회한다")
    void saveAndFind() {
        // given
        Tag tag = tagRepository.save(Tag.builder().tagType(TagType.IT).build());

        // when // then
        assertThat(tagRepository.findById(tag.getId())).isPresent();
    }
}
