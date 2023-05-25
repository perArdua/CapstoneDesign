//
//  UserData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/25.
//

import Foundation

struct UserResponse: Codable {
    let header: UserHeader
    let body: UserBody
}

struct UserHeader: Codable {
    let code: Int
    let message: String
}

struct UserBody: Codable {
    let isExist: Bool
}
