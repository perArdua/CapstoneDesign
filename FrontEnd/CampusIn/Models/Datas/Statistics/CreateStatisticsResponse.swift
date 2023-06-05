//
//  CreateStatisticsResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/05.
//

import Foundation


// MARK: - CreateStatisticsResponse
struct CreateStatisticsResponse: Codable {
    let header: CreateStatisticsHeader
    let body: CreateStatisticsBody
}

// MARK: - Body
struct CreateStatisticsBody: Codable {
    let createStatisticsID: CreateStatisticsID

    enum CodingKeys: String, CodingKey {
        case createStatisticsID = "통계 생성"
    }
}

// MARK: - 통계생성
struct CreateStatisticsID: Codable {
    let id: Int
}

// MARK: - Header
struct CreateStatisticsHeader: Codable {
    let code: Int
    let message: String
}
