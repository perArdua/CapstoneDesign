//
//  PostingSearch.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/17.
//
import Foundation

// MARK: - Posting
struct PostingSearch: Codable {
    let header: PostingSearchHeader
    let body: PostingSearchBody
}

// MARK: - Body
struct PostingSearchBody: Codable {
    let postingSearchList: PostingSearchList

    enum CodingKeys: String, CodingKey {
        case postingSearchList = "게시판별 게시글 목록/검색"
    }
}

// MARK: - 게시판별게시글목록검색
struct PostingSearchList: Codable {
    let content: [PostingSearchContent]
    let pageable: PostingSearchPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements: Int
    let sort: PostingSearchSort
    let size, number: Int
    let first, empty: Bool
}

// MARK: - Content
struct PostingSearchContent: Codable {
    let postID: Int
    let boardSimpleResponse: PostingSearchBoardSimpleResponse
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
struct PostingSearchBoardSimpleResponse: Codable {
    let boardID: Int
    let boardType: String

    enum CodingKeys: String, CodingKey {
        case boardID = "boardId"
        case boardType
    }
}

// MARK: - Pageable
struct PostingSearchPageable: Codable {
    let sort: PostingSearchSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct PostingSearchSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct PostingSearchHeader: Codable {
    let code: Int
    let message: String
}
