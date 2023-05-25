//
//  UserManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/25.
//

import Foundation
import Alamofire

class UserManager{
    
    //MARK: - 기존 회원인지 판단
    static func isExistingMember(completion: @escaping (Result<Bool, Error>) -> Void) {
        let endPoint = APIConstants.User.isExistingMember

        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .responseDecodable(of: UserResponse.self) { response in
                switch response.result {
                case .success(let userExistenceResponse):
                    let isExisting = userExistenceResponse.body.isExist
                    completion(.success(isExisting))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
        }
    
    //MARK: - 닉네임 설정
    static func setNickname(nickName: String, completion: @escaping (Result<Void, Error>) -> Void) {
        let endPoint = String(format: APIConstants.User.setNickname, nickName)
        
        AF.request(endPoint, method: .post, encoding: JSONEncoding.default, headers: APIConstants.headers).response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }

    }
    
}
