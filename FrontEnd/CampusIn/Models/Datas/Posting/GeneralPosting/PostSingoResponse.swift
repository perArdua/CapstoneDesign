//
//  PostSingoResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/11.
//

import Foundation

// MARK: - Welcome
struct postSingoData: Codable {
    let header: postSingoHeader
    let body: postSingoBody
}

// MARK: - Body
struct postSingoBody: Codable {
    let already: String?
    let first: String?

    enum CodingKeys: String, CodingKey {
        case already = "이미 신고한 게시글입니다."
        case first = "게시글 신고"
    }
}

// MARK: - Header
struct postSingoHeader: Codable {
    let code: Int
    let message: String
}
