//
//  RankingData.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/05.
//

import Foundation

// MARK: - RankingCreateData
struct RankingCreateData: Codable {
    let header: RankingCreateHeader
    let body: RankingCreateBody
}

// MARK: - Body
struct RankingCreateBody: Codable {
    let rankingContent: RankingCreateContent

    enum CodingKeys: String, CodingKey {
        case rankingContent = "랭킹 생성"
    }
}

// MARK: - 랭킹생성
struct RankingCreateContent: Codable {
    let id: Int
}

// MARK: - Header
struct RankingCreateHeader: Codable {
    let code: Int
    let message: String
}
