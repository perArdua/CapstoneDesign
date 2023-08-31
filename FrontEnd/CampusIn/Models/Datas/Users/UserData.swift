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
    let 기존회원닉네임반환성공: userNick

    enum CodingKeys: String, CodingKey {
        case 기존회원닉네임반환성공 = "기존 회원 닉네임 반환 성공"
    }
}

struct userNick: Codable {
    let nickname: String
}
