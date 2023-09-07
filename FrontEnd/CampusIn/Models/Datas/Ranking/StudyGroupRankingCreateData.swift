//
//  StudyGroupRankingCreateData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/07.
//

import Foundation

// MARK: - RankingData
struct StudyGroupRankingCreateData: Codable {
    let header: StudyGroupRankingCreateHeader
    let body: StudyGroupRankingCreateBody
}

// MARK: - Body
struct StudyGroupRankingCreateBody: Codable {
    let studyGroupRankingCreateContent: StudyGroupRankingCreateContent

    enum CodingKeys: String, CodingKey {
        case studyGroupRankingCreateContent = "스터디 그룹 랭킹 생성"
    }
}

// MARK: - 스터디그룹랭킹생성
struct StudyGroupRankingCreateContent: Codable {
    let id: Int
}

// MARK: - Header
struct StudyGroupRankingCreateHeader: Codable {
    let code: Int
    let message: String
}
