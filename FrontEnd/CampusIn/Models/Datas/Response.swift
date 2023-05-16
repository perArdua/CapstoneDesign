//
//  Response.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/16.
//

import Foundation

struct DataResponse: Codable {
    let body: Body
    let header: Header
}

// MARK: - Body
struct Body: Codable {
}

// MARK: - Header
struct Header: Codable {
    let code: Int
    let message: String
}
