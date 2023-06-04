//
//  StudyGroupData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import Foundation

struct StudyGroupResponse: Codable {
    let header: StudyGroupHeader
    let body: StudyGroupResponseBody

    private enum CodingKeys: String, CodingKey {
        case header
        case body
    }
}

struct StudyGroupResponseBody: Codable {
    let studyGroupDetails: [StudyGroupDetails]

    private enum CodingKeys: String, CodingKey {
        case studyGroupDetails = "StudyGroup 상세정보 조회가 완료되었습니다."
    }
}

struct StudyGroupDetails: Codable {
    let id: Int
    let studygroupName: String
    let userId: Int
    let createdAt: [Int]
    let limitedMemberSize: Int
}

struct StudyGroupHeader: Codable {
    let code: Int
    let message: String
}


