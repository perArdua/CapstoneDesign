package com.example.campusin.application.studygroup;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupCreateRequest;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupIdResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;


    @Transactional
    public StudyGroupIdResponse createStudyGroup(Long userId, StudyGroupCreateRequest request){
        User user = findUser(userId);
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.builder()
                    .studygroupName(request.getStudygroupName())
                    .LimitedMemberSize(request.getLimitedMemberSize())
                    .user(user)
                    .build());
        return new StudyGroupIdResponse(studyGroupRepository.save(studyGroup).getId());
    }

    @Transactional
    public void deleteStudyGroup(Long userId, Long studyGroupId){
        findUser(userId);
        findStudyGroup(studyGroupId);
        studyGroupRepository.deleteById(studyGroupId);
    }

    @Transactional(readOnly = true)
    public Page<StudyGroupResponse> getAllStudyGroupList(Long userId, Pageable pageable) {
        findUser(userId);
        Page<StudyGroup> studyGroup = studyGroupRepository.findStudyGroupByUserId(userId, pageable);

        return studyGroup.map(StudyGroupResponse::new);
    }

    @Transactional(readOnly = true)
    public StudyGroupResponse showStudyGroup(Long studygroupId) {
        StudyGroup studyGroup = findStudyGroup(studygroupId);
        return new StudyGroupResponse(studyGroup);

    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId).orElseThrow(
                () -> new IllegalArgumentException("STUDYGROUP NOT FOUND")
        );
    }


    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND")
        );
    }

}
