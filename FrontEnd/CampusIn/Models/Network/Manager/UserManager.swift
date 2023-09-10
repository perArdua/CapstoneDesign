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
    static func isExistingMember(completion: @escaping (Result<String, Error>) -> Void) {
        let endPoint = APIConstants.User.isExistingMember

        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .responseDecodable(of: UserResponse.self) { response in
                switch response.result {
                case .success(let userExistenceResponse):
                    let isExisting = userExistenceResponse.body.기존회원닉네임반환성공.nickname
                    completion(.success(isExisting))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
        }
    
    //MARK: - 닉네임 설정
    static func setNickname(nickName: String, completion: @escaping (Result<Int, Error>) -> Void) {
        let endPoint = String(format: APIConstants.User.setNickname, nickName)
        
        AF.request(endPoint, method: .post, encoding: JSONEncoding.default, headers: APIConstants.headers).responseDecodable(of: UserNicknameResponse.self) { response in
            switch response.result {
            case .success(let nicknameResponse):
                let id = nicknameResponse.body.nickname.id
                completion(.success(id))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

    // MARK: - user id 반환
    //MARK: - 기존 회원인지 판단
    static func getUserID(completion: @escaping (Result<Int, Error>) -> Void) {
        let endPoint = APIConstants.User.getUserID

        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .responseDecodable(of: UserIDData.self) { response in
                switch response.result {
                case .success(let data):
                    let id = data.body.userID.userID
                    completion(.success(id))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
        }
}
