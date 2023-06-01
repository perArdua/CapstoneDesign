//
//  TimerAddResponse.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation

struct TimerAddresponse: Codable {
    let header: TimerAddHeader
    let body: TimerAddBody
}

// MARK: - Body
struct TimerAddBody: Codable {
    let timerAddID: TimerAddID

    enum CodingKeys: String, CodingKey {
        case timerAddID = "Timer 생성이 완료되었습니다."
    }
}

// MARK: - Timer생성이완료되었습니다
struct TimerAddID: Codable {
    let id: Int
}

// MARK: - Header
struct TimerAddHeader: Codable {
    let code: Int
    let message: String
}
