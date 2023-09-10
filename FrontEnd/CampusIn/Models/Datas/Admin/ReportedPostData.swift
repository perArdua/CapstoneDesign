//
//  ReportedPostData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/10.
//

import Foundation

// MARK: - Posting
struct ReportedPostList: Codable {
    let header: ReportedPostListHeader
    let body: ReportedPostListBody
}

// MARK: - Body
struct ReportedPostListBody: Codable {
    let postListArray: [ReportedPostListContent]

    enum CodingKeys: String, CodingKey {
        case postListArray = "게시글 목록"
    }
}



// MARK: - Content
struct ReportedPostListContent: Codable {
    let postID: Int
    let boardSimpleResponse: ReportedBoardSimpleResponse
    let nickname: String?
    let title, content: String
    let createdAt: [Int]
    let price: Int?
    let studyGroupId: Int?
    let photo: String?
    let likeCount: Int
    let reportCount: Int
    let tagResponse: TagContent
    let isBadgeAccepted: Bool?
    
    enum CodingKeys: String, CodingKey {
        case postID = "postId"
        case boardSimpleResponse
//        case userID = "userId"
        case nickname, title, content, createdAt, price, photo, studyGroupId, tagResponse, likeCount, isBadgeAccepted, reportCount
    }
}

// MARK: - BoardSimpleResponse
struct ReportedBoardSimpleResponse: Codable {
    let boardID: Int
    let boardType: String

    enum CodingKeys: String, CodingKey {
        case boardID = "boardId"
        case boardType
    }
}

// MARK: - Header
struct ReportedPostListHeader: Codable {
    let code: Int
    let message: String
}
