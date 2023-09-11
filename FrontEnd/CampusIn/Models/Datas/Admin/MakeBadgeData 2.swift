//
//  MakeBadgeData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/11.
//

import Foundation


// MARK: - MakeBadgeData
struct MakeBadgeData: Codable {
    let header: MakeBadgeHeader
    let body: MakeBadgeBody
}

// MARK: - Body
struct MakeBadgeBody: Codable {
    let makeBadge: MakeBadgeContent
}

// MARK: - MakeBadge
struct MakeBadgeContent: Codable {
    let createdAt, modifiedAt: [Int]
    let deletedAt: [Int]?
    let id: Int
    let name: String
    let user: MakeBadgeUser
}

// MARK: - User
struct MakeBadgeUser: Codable {
    let createdAt, modifiedAt: [Int]
    let deletedAt: [Int]?
    let id: Int
    let loginID, username, nickname: String
    let profileImageURL: String
    let providerType, roleType: String

    enum CodingKeys: String, CodingKey {
        case createdAt, modifiedAt, deletedAt, id
        case loginID = "loginId"
        case username, nickname
        case profileImageURL = "profileImageUrl"
        case providerType, roleType
    }
}

// MARK: - Header
struct MakeBadgeHeader: Codable {
    let code: Int
    let message: String
}
