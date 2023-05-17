//
//  GeneralPostingMain.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/16.
//

import Foundation
// This file was generated from JSON Schema using quicktype, do not modify it directly.
// To parse the JSON, add this file to your project and do:
//
//   let posting = try? JSONDecoder().decode(Posting.self, from: jsonData)

import Foundation

// MARK: - Posting
struct GeneralPostingMainData: Codable {
    let header: GeneralPostingMainHeader
    let body: GeneralPostingMainBody
}

// MARK: - Body
struct GeneralPostingMainBody: Codable {
    let generalPostingMainList: GeneralPostingMainList

    enum CodingKeys: String, CodingKey {
        case generalPostingMainList = "게시글 목록"
    }
}

// MARK: - 게시글목록
struct GeneralPostingMainList: Codable {
    let content: [GeneralPostingMainContent]
    let pageable: GeneralPostingMainPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let first: Bool
    let sort: GeneralPostingMainSort
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct GeneralPostingMainContent: Codable {
    let postID: Int
    let boardSimpleResponse: BoardSimpleResponse
    let userID: Int
    let writer, title, content: String

    enum CodingKeys: String, CodingKey {
        case postID = "postId"
        case boardSimpleResponse
        case userID = "userId"
        case writer, title, content
    }
}

// MARK: - BoardSimpleResponse
struct BoardSimpleResponse: Codable {
    let boardID: Int
    let boardType: String

    enum CodingKeys: String, CodingKey {
        case boardID = "boardId"
        case boardType
    }
}

// MARK: - Pageable
struct GeneralPostingMainPageable: Codable {
    let sort: GeneralPostingMainSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct GeneralPostingMainSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct GeneralPostingMainHeader: Codable {
    let code: Int
    let message: String
}
