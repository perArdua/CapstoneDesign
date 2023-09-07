//
//  PostLikeResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/07.
//

import Foundation

// MARK: - Welcome
struct postLikeResponse: Codable {
    let header: postLikeHeader
    let body: postLikeody
}

// MARK: - Body
struct postLikeody: Codable {
    let already: String?
    let first: String?

    enum CodingKeys: String, CodingKey {
        case already = "이미 좋아요한 게시글입니다."
        case first = "게시글 좋아요"
    }
}

// MARK: - Header
struct postLikeHeader: Codable {
    let code: Int
    let message: String
}
