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
        case studyGroupDetails = "내가 속한 StudyGroup 목록 조회가 완료되었습니다."
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


//import Foundation
//
//// MARK: - Welcome
//struct StudyGroupResponse: Codable {
//    let header: StudyGroupHeader
//    let body: StudyGroupResponseBody
//}
//
//// MARK: - Body
//struct StudyGroupResponseBody: Codable {
//    let studyGroupDetails: [StudyGroupDetails]
//
//    enum CodingKeys: String, CodingKey {
//        case studyGroupDetails = "내가 속한 StudyGroup 목록 조회가 완료되었습니다."
//    }
//}
//
//// MARK: - 내가속한StudyGroup목록조회가완료되었습니다
//struct StudyGroupDetails: Codable {
//    let content: [StudyGroupContent]
//    let pageable: Pageable
//    let last: Bool
//    let totalElements, totalPages: Int
//    let first: Bool
//    let size, number: Int
//    let sort: Sort
//    let numberOfElements: Int
//    let empty: Bool
//}
//
//// MARK: - Content
//struct StudyGroupContent: Codable {
//    let id: Int
//    let studygroupName: String
//    let limitedMemberSize: Int
//    let userName: String
//    let createdAt: [Int]
//    let currentMemberSize: Int
//}
//
//// MARK: - Pageable
//struct StudyGroupPageable: Codable {
//    let sort: StudyGroupSort
//    let offset, pageNumber, pageSize: Int
//    let paged, unpaged: Bool
//}
//
//// MARK: - Sort
//struct StudyGroupSort: Codable {
//    let empty, sorted, unsorted: Bool
//}
//
//// MARK: - Header
//struct StudyGroupHeader: Codable {
//    let code: Int
//    let message: String
//}



