//
//  BlockPostData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/10.
//

import Foundation

// MARK: - Welcome
struct BlockPostData: Codable {
    let header: BlockPostHeader
    let body: BlockPostBody
}

// MARK: - Body
struct BlockPostBody: Codable {
    let block: String?
    let unblock: String?

    enum CodingKeys: String, CodingKey {
        case block = "게시글 차단"
        case unblock = "게시글 차단 해제"
    }
}

// MARK: - Header
struct BlockPostHeader: Codable {
    let code: Int
    let message: String
}
