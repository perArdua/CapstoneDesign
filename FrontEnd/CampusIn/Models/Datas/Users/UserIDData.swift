//
//  UserIDData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/10.
//

import Foundation

// MARK: - UserIDData
struct UserIDData: Codable {
    let header: UserIDDHeader
    let body: UserIDDBody
}

// MARK: - Body
struct UserIDDBody: Codable {
    let userID: UserID

    enum CodingKeys: String, CodingKey {
        case userID = "userId"
    }
}

// MARK: - UserID
struct UserID: Codable {
    let userID: Int

    enum CodingKeys: String, CodingKey {
        case userID = "userId"
    }
}

// MARK: - Header
struct UserIDDHeader: Codable {
    let code: Int
    let message: String
}
