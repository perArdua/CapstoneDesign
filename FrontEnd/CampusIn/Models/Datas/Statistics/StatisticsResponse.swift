//
//  StatisticsResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/05.
//

import Foundation


// MARK: - StatisticsResponse
struct StatisticsResponse: Codable {
    let header: StatisticsHeader
    let body: StatisticsBody
}

// MARK: - Body
struct StatisticsBody: Codable {
    let statisticsData: StatisticsData

    enum CodingKeys: String, CodingKey {
        case statisticsData = "통계 조회"
    }
}

// MARK: - 통계조회
struct StatisticsData: Codable {
    let totalElapsedTime, numberOfQuestions, numberOfAnswers, numberOfAdoptedAnswers: Int
}

// MARK: - Header
struct StatisticsHeader: Codable {
    let code: Int
    let message: String
}
