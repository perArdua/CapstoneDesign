//
//  RankingManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/08/31.
//

import Foundation
import Alamofire

class RankingManager{
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(String(KeyChain.read(key: "token")!))"]
    
    // MARK: - 랭킹 데이터 생성, id를 반환함
    static func createRanking(completion: @escaping (Result<Int, Error>) -> Void){
        let endpoint = APIConstants.Ranking.createRanking
        
        AF.request(endpoint, method: .post, headers: headers).responseDecodable(of: RankingCreateData.self) { response in
            switch response.result{
            case.success(let data):
                completion(.success(data.body.rankingContent.id))
                
            case.failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 현재 주차 개인 공부 시간 랭킹을 가져온다
    static func getPersonalStudyRanking(completion: @escaping (Result<[RankingContent], Error>) -> Void){
        let endpoint = APIConstants.Ranking.getPersonalStudyRanking
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self) { response in
            switch response.result{
            case .success(let data):
                completion(.success(data.body.rankingList.content))
            case .failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }

}
