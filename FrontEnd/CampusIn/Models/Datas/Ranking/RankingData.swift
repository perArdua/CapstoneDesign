//
//  RankingData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/08/31.
//

import Foundation

// MARK: - RankingData
struct RankingData: Codable {
    let header: RankingHeader
    let body: RankingBody
}

// MARK: - Body
struct RankingBody: Codable {
    let rankingList: RankingList

    enum CodingKeys: String, CodingKey {
        case rankingList = "공부시간 랭킹 리스트 조회"
    }
}

// MARK: - 공부시간랭킹리스트조회
struct RankingList: Codable {
    let content: [RankingContent]
    let pageable: RankingPageable
    let totalElements, totalPages: Int
    let last: Bool
    let numberOfElements, size, number: Int
    let sort: RankingSort
    let first, empty: Bool
}

// MARK: - Content
struct RankingContent: Codable {
    let rank: Int
    let name: String
    let week: Int
}

// MARK: - Pageable
struct RankingPageable: Codable {
    let sort: RankingSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct RankingSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct RankingHeader: Codable {
    let code: Int
    let message: String
}
