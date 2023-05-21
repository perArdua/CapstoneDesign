//
//  ChatMessage.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/14.
//

import Foundation

struct MessageRoomListResponse: Codable {
    let header: ResponseHeader
    let body: [String: MessageRoomListData]
}

struct ResponseHeader: Codable {
    let code: Int
    let message: String
}

struct MessageRoomListData: Codable {
    let content: [MessageRoom]
    let pageable: Pageable
    let last: Bool
    let totalPages: Int
    let totalElements: Int
    let first: Bool
    let size: Int
    let number: Int
    let sort: Sort
    let numberOfElements: Int
    let empty: Bool
}

struct MessageRoom: Codable {
    let messageRoomId: Int
    let interlocutorNickname: String
    let lastMessageSentTime: [Int]
    let lastMessageContent: String
}

struct Pageable: Codable {
    let sort: Sort
    let offset: Int
    let pageSize: Int
    let pageNumber: Int
    let paged: Bool
    let unpaged: Bool
}

struct Sort: Codable {
    let empty: Bool
    let sorted: Bool
    let unsorted: Bool
}
