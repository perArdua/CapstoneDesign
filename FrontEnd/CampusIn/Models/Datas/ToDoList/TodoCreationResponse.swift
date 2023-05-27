//
//  TodoCreationResponse.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/27.
//

import Foundation

struct TodoCreationResponse: Codable {
    let header: TodoCreationHeader
    let body: TodoCreationBody
}

struct TodoCreationHeader: Codable {
    let code: Int
    let message: String
}

struct TodoCreationBody: Codable {
    let todoCreationMessage: TodoCreationMessage
    
    enum CodingKeys: String, CodingKey {
        case todoCreationMessage = "Todo 생성이 완료되었습니다."
    }
}

struct TodoCreationMessage: Codable {
    let id: Int
}
