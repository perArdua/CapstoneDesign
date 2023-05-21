//
//  Messages.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/21.
//

import Foundation

struct ChattingMessageListResponse: Codable {
    let header: ChattingResponseHeader
    let body: ChattingMessageListResponseBody
    
    enum CodingKeys: String, CodingKey {
        case header
        case body
    }
}

struct ChattingResponseHeader: Codable {
    let code: Int
    let message: String
}

struct ChattingMessageListResponseBody: Codable {
    let 쪽지리스트조회완료: ChattingMessageListData
    
    enum CodingKeys: String, CodingKey {
        case 쪽지리스트조회완료 = "쪽지 리스트 조회 완료"
    }
}

struct ChattingMessageListData: Codable {
    let content: [ChattingMessage]
    let pageable: ChattingPageable
    let last: Bool
    let totalElements: Int
    let totalPages: Int
    let first: Bool
    let size: Int
    let number: Int
    let sort: ChattingSort
    let numberOfElements: Int
    let empty: Bool
}

struct ChattingMessage: Codable {
    let isReceived: Bool
    let createdAt: [Int]
    let content: String
}

struct ChattingPageable: Codable {
    let sort: ChattingSort
    let offset: Int
    let pageNumber: Int
    let pageSize: Int
    let paged: Bool
    let unpaged: Bool
}

struct ChattingSort: Codable {
    let empty: Bool
    let sorted: Bool
    let unsorted: Bool
}
