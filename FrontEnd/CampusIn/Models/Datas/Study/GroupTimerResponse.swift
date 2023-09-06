//
//  GroupTimerResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/06.
//

import Foundation

// MARK: - RankingData
struct GroupTimerData: Codable {
    let header: GroupTimerHeader
    let body: GroupTimerBody
}

// MARK: - Body
struct GroupTimerBody: Codable {
    let groupTimerList: GroupTimerList

    enum CodingKeys: String, CodingKey {
        case groupTimerList = "StudyGroup 멤버들의 주간 공부시간 조회가 완료되었습니다."
    }
}

// MARK: - StudyGroup멤버들의주간공부시간조회가완료되었습니다
struct GroupTimerList: Codable {
    let content: [GroupTimerContent]
    let pageable: String
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, size, number: Int
    let first: Bool
    let sort: GroupTimerSort
    let empty: Bool
}

// MARK: - Content
struct GroupTimerContent: Codable {
    let studyGroupMemberName: String
    let elapsedTime: Int
}

// MARK: - Sort
struct GroupTimerSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct GroupTimerHeader: Codable {
    let code: Int
    let message: String
}
