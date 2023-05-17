//
//  GeneralPostingMain.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/16.
//

import Foundation
// MARK: - 게시글 목록 json 파싱용 구조체

// MARK: - Posting
struct PostList: Codable {
    let header: PostListHeader
    let body: PostListBody
}

// MARK: - Body
struct PostListBody: Codable {
    let postListArray: PostListArray

    enum CodingKeys: String, CodingKey {
        case postListArray = "게시글 목록"
    }
}

// MARK: - 게시글목록
struct PostListArray: Codable {
    let content: [PostListContent]
    let pageable: PostListPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let first: Bool
    let sort: PostListSort
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct PostListContent: Codable {
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
struct PostListPageable: Codable {
    let sort: PostListSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct PostListSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct PostListHeader: Codable {
    let code: Int
    let message: String
}
