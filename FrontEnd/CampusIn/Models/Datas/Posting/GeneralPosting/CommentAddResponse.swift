//
//  CommentResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/25.
//

import Foundation

struct CommentAddResponse: Codable {
    let header: CommentAddHeader
    let body: CommentAddBody
}

// MARK: - Body
struct CommentAddBody: Codable {
    let commentSuccess: CommentAddSuccess

    enum CodingKeys: String, CodingKey {
        case commentSuccess = "댓글 생성 success"
    }
}

// MARK: - 댓글생성Success
struct CommentAddSuccess: Codable {
    let commentID: Int
    let content: String
    let userID: Int
    let nickname: String?

    enum CodingKeys: String, CodingKey {
        case commentID = "commentId"
        case content
        case userID = "userId"
        case nickname
    }
}

// MARK: - Header
struct CommentAddHeader: Codable {
    let code: Int
    let message: String
}
