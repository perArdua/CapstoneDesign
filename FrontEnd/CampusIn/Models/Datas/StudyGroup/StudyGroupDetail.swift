//
//  StudyGroupDetail.swift
//  CampusIn
//
//  Created by 최다경 on 2023/08/29.
//

import Foundation

// MARK: - Welcome
struct StudyGroupDetailResponse: Codable {
    let header: StudyGroupDetailHeader
    let body: StudyGroupDetailBody
}

// MARK: - Body
struct StudyGroupDetailBody: Codable {
    let studyGroupDetail: StudyGroupDetailContent

    enum CodingKeys: String, CodingKey {
        case studyGroupDetail = "StudyGroup 상세정보 조회가 완료되었습니다."
    }
}

// MARK: - StudyGroup상세정보조회가완료되었습니다
struct StudyGroupDetailContent: Codable {
    let studyGroupID: Int
    let studyGroupName: String
    let limitedMemberSize, currentMemberSize: Int
    let leaderName: String
    let createdAt: [Int]
    let memberList: [MemberList]

    enum CodingKeys: String, CodingKey {
        case studyGroupID = "studyGroupId"
        case studyGroupName, limitedMemberSize, currentMemberSize, leaderName, createdAt, memberList
    }
}

// MARK: - MemberList
struct MemberList: Codable {
    let memberName: String
}

// MARK: - Header
struct StudyGroupDetailHeader: Codable {
    let code: Int
    let message: String
}
