//
//  StudyGroupRankingData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/07.
//

import Foundation

// MARK: - RankingData
struct StudyGroupRankingData: Codable {
    let header: StudyGroupRankingHeader
    let body: StudyGroupRankingBody
}

// MARK: - Body
struct StudyGroupRankingBody: Codable {
    let studyGroupRankingList: StudyGroupRankingList

    enum CodingKeys: String, CodingKey {
        case studyGroupRankingList = "랭킹 리스트 조회"
    }
}

// MARK: - 랭킹리스트조회
struct StudyGroupRankingList: Codable {
    let content: [StudyGroupRankingContent]
    let pageable: StudyGroupRankingPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements: Int
    let first: Bool
    let sort: StudyGroupRankingSort
    let size, number: Int
    let empty: Bool
}

// MARK: - Content
struct StudyGroupRankingContent: Codable {
    let rank, week, month: Int
    let studyGroupName: String
}

// MARK: - Pageable
struct StudyGroupRankingPageable: Codable {
    let sort: StudyGroupRankingSort
    let pageSize, pageNumber, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct StudyGroupRankingSort: Codable {
    let sorted, unsorted, empty: Bool
}

// MARK: - Header
struct StudyGroupRankingHeader: Codable {
    let code: Int
    let message: String
}

