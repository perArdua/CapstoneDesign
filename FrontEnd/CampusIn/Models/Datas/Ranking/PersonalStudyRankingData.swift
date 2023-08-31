//
//  RankingData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/08/31.
//

import Foundation

// MARK: - RankingData
struct PersonalStudyRankingData: Codable {
    let header: PersonalStudyRankingHeader
    let body: PersonalStudyRankingBody
}

// MARK: - Body
struct PersonalStudyRankingBody: Codable {
    let rankingList: PersonalStudyRankingList

    enum CodingKeys: String, CodingKey {
        case rankingList = "공부시간 랭킹 리스트 조회"
    }
}

// MARK: - 공부시간랭킹리스트조회
struct PersonalStudyRankingList: Codable {
    let content: [PersonalStudyRankingContent]
    let pageable: PersonalStudyRankingPageable
    let totalElements, totalPages: Int
    let last: Bool
    let numberOfElements, size, number: Int
    let sort: PersonalStudyRankingSort
    let first, empty: Bool
}

// MARK: - Content
struct PersonalStudyRankingContent: Codable {
    let rank: Int
    let name: String
    let week: Int
}

// MARK: - Pageable
struct PersonalStudyRankingPageable: Codable {
    let sort: PersonalStudyRankingSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct PersonalStudyRankingSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct PersonalStudyRankingHeader: Codable {
    let code: Int
    let message: String
}
