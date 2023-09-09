//
//  BadgeData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/09.
//

import Foundation

// MARK: - BadgeData
struct BadgeData: Codable {
    let header: BadgeHeader
    let body: BadgeBody
}

// MARK: - Body
struct BadgeBody: Codable {
    let userBadges: UserBadges
}

// MARK: - UserBadges
struct UserBadges: Codable {
    let content: [BadgeContent]?
    let pageable: BadgePageable
    let totalElements, totalPages: Int
    let last: Bool
    let numberOfElements: Int
    let first: Bool
    let size, number: Int
    let sort: BadgeSort
    let empty: Bool
}

// MARK: - Content
struct BadgeContent: Codable {
    let createdAt, modifiedAt: [Int]
    let deletedAt: [Int]?
    let id: Int
    let name: String
    let user: User
}

// MARK: - User
struct User: Codable {
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

// MARK: - Pageable
struct BadgePageable: Codable {
    let sort: BadgeSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct BadgeSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct BadgeHeader: Codable {
    let code: Int
    let message: String
}
