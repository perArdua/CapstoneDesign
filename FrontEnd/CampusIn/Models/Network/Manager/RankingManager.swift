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
    
    // MARK: - 개인 랭킹 생성
    static private func createPersonalRanking(param: Parameters ,completion: @escaping(Result<String, Error>) -> Void){
        let endpoint = APIConstants.Ranking.createPersonalRanking
        
        AF.request(endpoint, method: .post, parameters: param, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: RankingCreateData.self) { res in
            switch res.result{
            case .success(_):
                print("개인 랭킹 생성 성공")
                completion(.success("success"))
            case.failure(let err):
                print(err)
                print("개인 랭킹 생성 실패")
                completion(.failure(err))
            }
        }
    }
    
    static func createGroupRanking(dateString: String, groupID: Int, completion: @escaping(Result<String, Error>) -> Void){
        let endpoint = String(format: APIConstants.Ranking.createGroupRanking, groupID)
        var p: Parameters = ["localDate" : dateString]

        AF.request(endpoint, method: .post, parameters: p, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: RankingCreateData.self){ res in
            switch res.result{
            case.success(_):
                print("그룹 랭킹 불러오기 성공")
                completion(.success("성공"))
            case .failure(let err):
                print("그룹 랭킹 불러오기 실패")
                print(err)
                completion(.failure(err))
            }
        }
    }
    
    static func getGroupRanking(completion: @escaping(Result<[RankingContent], Error>) -> Void){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        let endpoint = APIConstants.Ranking.getStudyGroupRanking + "/?localDate=\(dateFormatter.string(from: Date()))"
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self){ res in
            switch res.result{
            case .success(let data):
                print("그룹 랭킹 불러오기 성공")
                completion(.success(data.body.rankingList.content))
            case .failure(let err):
                print("그룹 랭킹 불러오기 실패")
                print(err)
                completion(.failure(err))
            }
        }
    }
    
    
    static func getPersonalStudyRanking(completion: @escaping (Result<[RankingContent], Error>) -> Void){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        var param: Parameters = ["localDate" : dateFormatter.string(from: Date())]
        let endpoint = APIConstants.Ranking.getPersonalStudyRanking + "/?localDate=\(dateFormatter.string(from: Date()))"
        
        StatisticsManager.createStatistics(param: param) { res in
            switch res{
            case .success(_):
                createPersonalRanking(param: param){r in
                    switch r{
                    case .success(_):
                        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self) { res in
                            switch res.result{
                            case .success(let data):
                                print(data)
                                completion(.success(data.body.rankingList.content))
                            case .failure(let err):
                                print(err)
                                print("개인 공부 시간 랭킹 불러오기 실패")
                                completion(.failure(err))
                            }
                        }
                    case .failure(let err):
                        print(err)
                    }
                }
            case .failure(let err):
                print(err)
            }
        }
    }
    
    static func getPersonalQuesRanking(completion: @escaping (Result<[RankingContent], Error>) -> Void){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        var param: Parameters = ["localDate" : dateFormatter.string(from: Date())]
        
        let endpoint = APIConstants.Ranking.getPrevPersonalQuesRanking + "/?localDate=\(dateFormatter.string(from: Date()))"
        
        StatisticsManager.createStatistics(param: param) { res in
            switch res{
            case .success(_):
                createPersonalRanking(param: param){r in
                    switch r{
                    case .success(_):
                        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self) { res in
                            switch res.result{
                            case .success(let data):
                                completion(.success(data.body.rankingList.content))
                            case .failure(let err):
                                print(err)
                                print("개인 질의 응답 랭킹 불러오기 실패")
                                completion(.failure(err))
                            }
                        }
                    case .failure(let err):
                        print(err)
                    }
                }
            case .failure(let err):
                print(err)
            }
        }
    }
    
    
    
    static func prevPersonalStudyRanking(paramString: String , completion: @escaping(Result<[RankingContent], Error>) -> Void){
        let param: Parameters = ["localDate" : paramString]
        
        let endpoint = APIConstants.Ranking.getPrevPersonalStudyRanking + "/?localDate=\(paramString)"
        
        StatisticsManager.createStatistics(param: param) { res in
            switch res{
            case .success(_):
                createPersonalRanking(param: param){r in
                    switch r{
                    case .success(_):
                        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self) { res in
                            switch res.result{
                            case .success(let data):
                                completion(.success(data.body.rankingList.content))
                            case .failure(let err):
                                print(err)
                                print("특정 주차 스터디 랭킹 불러오기 실패")
                                completion(.failure(err))
                            }
                        }
                    case .failure(let err):
                        print(err)
                    }
                }
            case .failure(let err):
                print(err)
            }
        }
    }
    
    static func prevPersonalQuesRanking(paramString: String, completion: @escaping(Result<[RankingContent], Error>) -> Void){
        let param: Parameters = ["localDate" : paramString]
        
        let endpoint = APIConstants.Ranking.getPrevPersonalQuesRanking + "/?localDate=\(paramString)"
        
        StatisticsManager.createStatistics(param: param) { res in
            switch res{
            case .success(_):
                createPersonalRanking(param: param){r in
                    switch r{
                    case .success(_):
                        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: RankingData.self) { res in
                            switch res.result{
                            case .success(let data):
                                print(data)
                                completion(.success(data.body.rankingList.content))
                            case .failure(let err):
                                print(err)
                                print("특정 주차 스터디 랭킹 불러오기 실패")
                                completion(.failure(err))
                            }
                        }
                    case .failure(let err):
                        print(err)
                    }
                }
            case .failure(let err):
                print(err)
            }
        }
    }
}
