//
//  UserNickname.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/25.
//

import Foundation

struct UserNicknameResponse: Codable {
    let header: UserHeader
    let body: UserBody
}

struct UserNicknameHeader: Codable {
    let code: Int
    let message: String
}

struct UserNicknameBody: Codable {
    let nickname: Nickname
}

struct Nickname: Codable {
    let createdAt: [Int]
    let modifiedAt: [Int]
    let deletedAt: String?
    let id: Int
    let loginId: String
    let username: String
    let nickname: String
    let profileImageUrl: String
    let providerType: String
    let roleType: String
}
