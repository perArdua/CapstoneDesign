//
//  CommentAdoptData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/09.
//

import Foundation



// MARK: - CommentAdoptData
struct CommentAdoptData: Codable {
    let header: CommentAdoptHeader
    let body: CommentAdoptBody
}

// MARK: - Body
struct CommentAdoptBody: Codable {
    let commentAdoptStr: String

    enum CodingKeys: String, CodingKey {
        case commentAdoptStr = "답변 채택 성공"
    }
}

// MARK: - Header
struct CommentAdoptHeader: Codable {
    let code: Int
    let message: String
}
