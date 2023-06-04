//
//  StatisticsManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/05.
//

import Foundation
import Alamofire

class StatisticsManager{
    // MARK: - 타이머를 조회 요청
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(String(KeyChain.read(key: "token")!))"]
    
    
}
