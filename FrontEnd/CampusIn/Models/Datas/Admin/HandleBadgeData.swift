//
//  HandleBadgeData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/10.
//

import Foundation


// MARK: - HandleBadgeData
struct HandleBadgeData: Codable {
    let header: HandleBadgeHeader
    let body: HandleBadgeBody
}

// MARK: - Body
struct HandleBadgeBody: Codable {
    let handleBadgeStatus: String

    enum CodingKeys: String, CodingKey {
        case handleBadgeStatus = "뱃지 요청 게시글 상태 변경"
    }
}

// MARK: - Header
struct HandleBadgeHeader: Codable {
    let code: Int
    let message: String
}
