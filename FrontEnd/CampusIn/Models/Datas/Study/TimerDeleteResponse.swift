//
//  TimerDeleteResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation

struct TimerDeleteResponse: Codable {
    let header: TimerDeleteHeader
    let body: TimerDeleteBody
}

// MARK: - Body
struct TimerDeleteBody: Codable {
    let timerDeleteMsg: String

    enum CodingKeys: String, CodingKey {
        case timerDeleteMsg = "Timer 삭제가 완료되었습니다."
    }
}

// MARK: - Header
struct TimerDeleteHeader: Codable {
    let code: Int
    let message: String
}
