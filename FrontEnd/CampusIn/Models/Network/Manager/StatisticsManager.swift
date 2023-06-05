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
    
    // MARK: - 통계 데이터의 id를 기반으로 통계 데이터를 불러온다.
    static private func getStatistics(statisticsID: Int, completion: @escaping (Result<StatisticsData, Error>) -> Void){
        let endpoint = String(format: APIConstants.Statistics.getStatistics, statisticsID)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: StatisticsResponse.self) { response in
            switch response.result{
            case .success(let data):
                completion(.success(data.body.statisticsData))
            case .failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }
    
    
    // MARK: - 특정 날짜의 통계 데이터 생성 요청. Response로 해당 통계 data의 Id를 가져온다.
    static private func createStatistics(param: Parameters, completion: @escaping (Result<CreateStatisticsID, Error>) -> Void){
        let endpoint = APIConstants.Statistics.createStatistics
        
        AF.request(endpoint, method: .post, parameters: param, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: CreateStatisticsResponse.self) { response in
            switch response.result{
            case.success(let data):
                completion(.success(data.body.createStatisticsID))
                
            
            case.failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }
    
    
    static func getStatistics(params: Parameters, completion: @escaping (StatisticsData) -> Void){
        createStatistics(param: params) { result in
            switch result{
            case .success(let statisticsID):
                print("통계 생성 성공")
                getStatistics(statisticsID: statisticsID.id) { dataResult in
                    switch dataResult{
                    case .success(let statisticsData):
                        print("통계 조회 성공")
                        completion(statisticsData)
                    case .failure(let error):
                        print("통계 조회 실패")
                    }
                }
            case .failure(let error):
                print("통계 생성 실패")
            }
        }
    }
}
