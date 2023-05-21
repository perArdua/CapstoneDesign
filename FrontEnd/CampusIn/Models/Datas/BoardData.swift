//
//  BoardData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/22.
//

import Foundation

// MARK: - Posting
struct BoardData: Codable {
    let header: BoardHeader
    let body: BoardBody
    
}

// MARK: - Body
struct BoardBody: Codable {
    let boardID: BoardID

    enum CodingKeys: String, CodingKey {
        case boardID = "게시판 고유 id값 얻기"
    }
}

// MARK: - 게시판고유ID값얻기
struct BoardID: Codable {
    let content: [BoardContent]
    let pageable: BoardPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let sort: BoardSort
    let first: Bool
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct BoardContent: Codable {
    let boardID: Int
    let boardType: String

    enum CodingKeys: String, CodingKey {
        case boardID = "boardId"
        case boardType
    }
}

// MARK: - Pageable
struct BoardPageable: Codable {
    let sort: BoardSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct BoardSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct BoardHeader: Codable {
    let code: Int
    let message: String
}
