//
//  CommentDeleteResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/25.
//

import Foundation

struct CommentDeleteResponse: Codable {
    let header: CommentDeleteHeader
    let body: CommentDeleteBody
}

// MARK: - Body
struct CommentDeleteBody: Codable {
    let commentDeleteSuccess: String

    enum CodingKeys: String, CodingKey {
        case commentDeleteSuccess = "댓글 삭제 성공"
    }
}

// MARK: - Header
struct CommentDeleteHeader: Codable {
    let code: Int
    let message: String
}
