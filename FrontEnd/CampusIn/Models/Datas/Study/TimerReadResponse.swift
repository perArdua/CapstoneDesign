//
//  TimerReadResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation

// MARK: - Dataresponse
struct TimerReadResponse: Codable {
    let header: TimerReadHeader
    let body: TimerReadBody
}

// MARK: - Body
struct TimerReadBody: Codable {
    let timerReadList: TimerReadList

    enum CodingKeys: String, CodingKey {
        case timerReadList = "Timer 조회가 완료되었습니다."
    }
}

// MARK: - Timer조회가완료되었습니다
struct TimerReadList: Codable {
    let content: [TimerReadContent]
    let pageable: TimerReadPageable
    let totalPages, totalElements: Int
    let last: Bool
    let numberOfElements, number: Int
    let sort: TimerReadSort
    let first: Bool
    let size: Int
    let empty: Bool
}

// MARK: - Content
struct TimerReadContent: Codable {
    let id: Int
    let subject: String
    let elapsedTime: Int?
    let userID: Int

    enum CodingKeys: String, CodingKey {
        case id, subject, elapsedTime
        case userID = "userId"
    }
}

// MARK: - Pageable
struct TimerReadPageable: Codable {
    let sort: TimerReadSort
    let pageNumber, pageSize, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct TimerReadSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct TimerReadHeader: Codable {
    let code: Int
    let message: String
}
