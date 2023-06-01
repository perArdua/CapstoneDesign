//
//  TimerUpdateResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation

struct TimerUpdateResponse: Codable {
    let header: TimerUpdateHeader
    let body: TimerUpdateBody
}

// MARK: - Body
struct TimerUpdateBody: Codable {
    let timerUpdateMsg: TimerUpdateMsg

    enum CodingKeys: String, CodingKey {
        case timerUpdateMsg = "Timer 수정이 완료되었습니다."
    }
}

// MARK: - Timer수정이완료되었습니다
struct TimerUpdateMsg: Codable {
    let id: Int
}

// MARK: - Header
struct TimerUpdateHeader: Codable {
    let code: Int
    let message: String
}
