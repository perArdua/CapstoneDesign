//
//  Response.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/16.
//

import Foundation

struct DataResponse: Codable {
    let header: DataHeader
    let body: DataBody
}

// MARK: - Body
struct DataBody: Codable {
    let boardInit: Bool

    enum CodingKeys: String, CodingKey {
        case boardInit = "게시판, 태그 초기화"
    }
}

// MARK: - Header
struct DataHeader: Codable {
    let code: Int
    let message: String
}
