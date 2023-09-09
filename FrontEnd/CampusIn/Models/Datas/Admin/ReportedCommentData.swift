//
//  ReportedCommentData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import Foundation

// MARK: - Dataresponse
struct ReportedCommentData: Codable {
    let header: ReportedCommentDataHeader
    let body: ReportedCommentDataBody
}

// MARK: - Body
struct ReportedCommentDataBody: Codable {
    let reportedCommentDataContent: [ReportedCommentDataContent]

    enum CodingKeys: String, CodingKey {
        case reportedCommentDataContent = "댓글 조회 성공"
    }
}

// MARK: - Content
struct ReportedCommentDataContent: Codable {
    let userID: Int
    let parentID: Int?
    let commentID: Int
    let name: String
    let content: String
    let isAdopted: Bool?
    let boardId: Int?
    let postId: Int?
    let like: Int?
    let report: Int?
    let children: [CommentDataContent]
    let childrenSize: Int

    enum CodingKeys: String, CodingKey {
        case userID = "userId"
        case parentID = "parentId"
        case commentID = "commentId"
        case name, content, children, childrenSize, isAdopted, boardId, postId, like, report
    }
}

// MARK: - Header
struct ReportedCommentDataHeader: Codable {
    let code: Int
    let message: String
}
