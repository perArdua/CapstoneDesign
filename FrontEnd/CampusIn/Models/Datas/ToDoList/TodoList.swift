//
//  File.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/26.
//

import Foundation

struct TodoResponse: Codable {
    let header: TodoHeader
    let body: TodoResponseBody

    private enum CodingKeys: String, CodingKey {
        case header
        case body
    }
}

struct TodoResponseBody: Codable {
    let todoResponse: TodoContent

    private enum CodingKeys: String, CodingKey {
        case todoResponse = "Todo 조회가 완료되었습니다."
    }
    
}
struct TodoContent: Codable {
    let content: [Todo]
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

struct TodoHeader: Codable {
    let code: Int
    let message: String
}

struct Todo: Codable {
    let userId: Int
    let todoId: Int
    var title: String
    var completed: Bool
}

struct TodoPageable: Codable {
    let sort: TodoSort
    let offset: Int
    let pageSize: Int
    let pageNumber: Int
    let paged: Bool
    let unpaged: Bool
}

struct TodoSort: Codable {
    let empty: Bool
    let sorted: Bool
    let unsorted: Bool
}
