//
//  PostingSearch.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/17.
//
//import Foundation
//// MARK: - 검색 결과 json 파싱용 구조체
//
//// MARK: - Posting
//struct PostSearch: Codable {
//    let header: PostSearchHeader
//    let body: PostSearchBody
//}
//
//// MARK: - Body
//struct PostSearchBody: Codable {
//    let postSearchList: PostSearchList
//
//    enum CodingKeys: String, CodingKey {
//        case postSearchList = "게시판별 게시글 목록/검색"
//    }
//}
//
//// MARK: - 게시판별게시글목록검색
//struct PostSearchList: Codable {
//    let content: [PostSearchContent]
//    let pageable: PostSearchPageable
//    let totalPages, totalElements: Int
//    let last: Bool
//    let numberOfElements, size: Int
//    let first: Bool
//    let sort: PostSearchSort
//    let number: Int
//    let empty: Bool
//}
//
//// MARK: - Content
//struct PostSearchContent: Codable {
//    let postID: Int
//    let boardSimpleResponse: PostSearchBoardSimpleResponse
//    let nickname: String?
//    let title, content: String
//    let createdAt: [Int]
//    let price: Int?
//    let postImage: String?
//
//    enum CodingKeys: String, CodingKey {
//        case postID = "postId"
//        case boardSimpleResponse, nickname, title, content, createdAt, price, postImage
//    }
//}
//
//// MARK: - BoardSimpleResponse
//struct PostSearchBoardSimpleResponse: Codable {
//    let boardID: Int
//    let boardType: String
//
//    enum CodingKeys: String, CodingKey {
//        case boardID = "boardId"
//        case boardType
//    }
//}
//
//// MARK: - Pageable
//struct PostSearchPageable: Codable {
//    let sort: PostSearchSort
//    let pageNumber, pageSize, offset: Int
//    let paged, unpaged: Bool
//}
//
//// MARK: - Sort
//struct PostSearchSort: Codable {
//    let sorted, unsorted, empty: Bool
//}
//
//// MARK: - Header
//struct PostSearchHeader: Codable {
//    let code: Int
//    let message: String
//}


import Foundation
// MARK: - 검색 결과 json 파싱용 구조체

// MARK: - Posting
struct PostSearch: Codable {
    let header: PostSearchHeader
    let body: PostSearchBody
}

// MARK: - Body
struct PostSearchBody: Codable {
    let postSearchList: PostSearchList

    enum CodingKeys: String, CodingKey {
        case postSearchList = "게시글 목록"
    }
}

// MARK: - 게시판별게시글목록검색
struct PostSearchList: Codable {
    let content: [PostSearchContent]
    let pageable: PostSearchPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, size: Int
    let first: Bool
    let sort: PostSearchSort
    let number: Int
    let empty: Bool
}

// MARK: - Content
struct PostSearchContent: Codable {
    let postID: Int
    let boardSimpleResponse: PostSearchBoardSimpleResponse
    let nickname: String?
    let title, content: String
    let createdAt: [Int]
    let price: Int?
    let studyGroupId: Int?
    let photo: String?
    let likeCount: Int
    let tagResponse: TagContent
    let reportCount: Int
    let isBadgeAccepted: Bool?
    

    enum CodingKeys: String, CodingKey {
        case postID = "postId"
        case boardSimpleResponse
        case nickname, title, content, createdAt, price, photo, studyGroupId, tagResponse, likeCount, isBadgeAccepted, reportCount
    }
}

// MARK: - BoardSimpleResponse
struct PostSearchBoardSimpleResponse: Codable {
    let boardID: Int
    let boardType: String

    enum CodingKeys: String, CodingKey {
        case boardID = "boardId"
        case boardType
    }
}

// MARK: - Pageable
struct PostSearchPageable: Codable {
    let sort: PostSearchSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct PostSearchSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct PostSearchHeader: Codable {
    let code: Int
    let message: String
}
