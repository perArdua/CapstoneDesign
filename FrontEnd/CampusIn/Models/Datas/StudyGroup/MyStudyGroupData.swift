//
//  MyStudyGroupData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import Foundation

struct MyStudyGroupResponse: Codable {
    let header: MyStudyGroupHeader
    let body: MyStudyGroupResponseBody

    private enum CodingKeys: String, CodingKey {
        case header
        case body
    }
}

struct MyStudyGroupResponseBody: Codable {
    let studyGroupDetails: MyStudyGroupContent
    

    private enum CodingKeys: String, CodingKey {
        case studyGroupDetails = "내가 속한 StudyGroup 목록 조회가 완료되었습니다."
    }
}

struct MyStudyGroupContent: Codable{
    let content: [MyStudyGroupDetails]
    let pageable: MyStudyGroupPageable
    let totalPages: Int
    let totalElements: Int
    let last: Bool
    let numberOfElements: Int
    let first: Bool
    let size: Int
    let number: Int
    let sort: MyStudyGroupSort
    let empty: Bool
}

struct MyStudyGroupDetails: Codable {
    let id: Int
    let studygroupName: String
    let userName: String?
    let createdAt: [Int]
    let limitedMemberSize: Int
    let currentMemberSize: Int
}

struct MyStudyGroupPageable: Codable {
    let sort: MyStudyGroupSort
    let pageNumber: Int
    let pageSize: Int
    let offset: Int
    let paged: Bool
    let unpaged: Bool
}

struct MyStudyGroupSort: Codable {
    let sorted: Bool
    let unsorted: Bool
    let empty: Bool
}

struct MyStudyGroupHeader: Codable {
    let code: Int
    let message: String
}
