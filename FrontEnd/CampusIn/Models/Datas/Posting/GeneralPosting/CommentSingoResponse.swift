//
//  CommentSingoResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/11.
//

import Foundation

// MARK: - Welcome
struct commentSingoResponse: Codable {
    let header: commentSingoHeader
    let body: commentSingobody
}

// MARK: - Body
struct commentSingobody: Codable {
    let already: String?
    let first: String?

    enum CodingKeys: String, CodingKey {
        case already = "이미 신고한 댓글입니다"
        case first = "댓글 신고 성공"
    }
}

// MARK: - Header
struct commentSingoHeader: Codable {
    let code: Int
    let message: String
}
