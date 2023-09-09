//
//  BlockCommentData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import Foundation

// This file was generated from JSON Schema using quicktype, do not modify it directly.
// To parse the JSON, add this file to your project and do:
//
//   let welcome = try? JSONDecoder().decode(Welcome.self, from: jsonData)

import Foundation

// MARK: - Welcome
struct BlockCommentData: Codable {
    let header: BlockCommenHeader
    let body: BlockCommentBody
}

// MARK: - Body
struct BlockCommentBody: Codable {
    let block: String?
    let unblock: String?

    enum CodingKeys: String, CodingKey {
        case block = "댓글 차단"
        case unblock = "댓글 차단 해제"
    }
}

// MARK: - Header
struct BlockCommenHeader: Codable {
    let code: Int
    let message: String
}

