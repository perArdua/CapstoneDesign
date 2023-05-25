//
//  CommetData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/22.
//

import Foundation

// MARK: - Dataresponse
struct CommentData: Codable {
    let header: CommentDataHeader
    let body: CommentDataBody
}

// MARK: - Body
struct CommentDataBody: Codable {
    let commentDataInfo: CommentDataInfo

    enum CodingKeys: String, CodingKey {
        case commentDataInfo = "댓글 조회 성공"
    }
}

// MARK: - 댓글조회성공
struct CommentDataInfo: Codable {
    let content: [CommentDataContent]
    let pageable: CommentDataPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let sort: CommentDataSort
    let first: Bool
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct CommentDataContent: Codable {
    let userID: Int
    let parentID: Int?
    let commentID: Int
    let name: String?
    let content: String
    let children: [Int]
    let childrenSize: Int

    enum CodingKeys: String, CodingKey {
        case userID = "userId"
        case parentID = "parentId"
        case commentID = "commentId"
        case name, content, children, childrenSize
    }
}

// MARK: - Pageable
struct CommentDataPageable: Codable {
    let sort: CommentDataSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct CommentDataSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct CommentDataHeader: Codable {
    let code: Int
    let message: String
}
