//
//  BadgeManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/09.
//

import Foundation
import Alamofire

class BadgeManager{
    static func getBadge(userid: Int, completion: @escaping (Result<[BadgeContent]?, Error>) -> Void){
        let endpoint = String(format: APIConstants.Badge.getBadge, userid)
        
        AF.request(endpoint, method: .get, headers: APIConstants.headers).validate().responseDecodable(of: BadgeData.self) { response in
                switch response.result {
                case .success(let suc):
                    let data = suc.body.userBadges.content
                    completion(.success(data))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
    }
}
