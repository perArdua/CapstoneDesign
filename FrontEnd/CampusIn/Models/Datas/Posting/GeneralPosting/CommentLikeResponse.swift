//
//  CommentLikeResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/10.
//

import Foundation

// MARK: - Welcome
struct commentLikeResponse: Codable {
    let header: commentLikeHeader
    let body: commentLikebody
}

// MARK: - Body
struct commentLikebody: Codable {
    let already: String?
    let first: String?

    enum CodingKeys: String, CodingKey {
        case already = "이미 좋아요를 누른 댓글입니다"
        case first = "댓글 좋아요 생성 성공"
    }
}

// MARK: - Header
struct commentLikeHeader: Codable {
    let code: Int
    let message: String
}
