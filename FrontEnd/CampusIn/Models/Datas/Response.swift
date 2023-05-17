//
//  Response.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/16.
//

import Foundation

struct DataResponse: Codable {
    let body: DataBody
    let header: DataHeader
}

// MARK: - Body
struct DataBody: Codable {
}

// MARK: - Header
struct DataHeader: Codable {
    let code: Int
    let message: String
}
