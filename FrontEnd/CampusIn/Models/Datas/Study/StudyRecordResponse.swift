//
//  StudyRecordResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/06.
//

import Foundation

// MARK: - Posting
struct RecordPostList: Codable {
    let header: RecordPostListHeader
    let body: RecordPostListBody
}

// MARK: - Body
struct RecordPostListBody: Codable {
    let content: RecordPostListArray

    enum CodingKeys: String, CodingKey {
        case content = "게시글 목록"
    }
}

// MARK: - 게시글목록
struct RecordPostListArray: Codable {
    let content: [RecordPostListContent]
    let pageable: RecordPostListPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let first: Bool
    let sort: RecordPostListSort
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct RecordPostListContent: Codable {
    let postId, userId: Int
    let nickname, title, content, photo: String?
    let createdAt: [Int]
    let studyGroupID: Int

    enum CodingKeys: String, CodingKey {
        case postId = "postId"
        case userId = "userId"
        case nickname, title, content, photo, createdAt
        case studyGroupID = "studyGroupId"
    }
}

// MARK: - Pageable
struct RecordPostListPageable: Codable {
    let sort: RecordPostListSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct RecordPostListSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct RecordPostListHeader: Codable {
    let code: Int
    let message: String
}
