//
//  Response.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation


struct Response: Codable {
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
