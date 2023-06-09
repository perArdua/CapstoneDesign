//
//  TimerManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/28.
//

import Foundation
import Alamofire

class TimerManager{
    // MARK: - 타이머를 조회 요청
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(String(KeyChain.read(key: "token")!))"]
    
    static func readTimer(completion: @escaping (Result<[TimerReadContent], Error>) -> Void){
        let endpoint = APIConstants.Timer.getTimer
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: TimerReadResponse.self) { response in
            switch response.result{
            case .success(let timers):
                print("success to get timer")
                completion(.success(timers.body.timerReadList.content))
            case .failure(let error):
                print("fail to get timer")
                print(error)
                completion(.failure(error))
            }
        }
    }
    
    
    // MARK: - 타이머 생성 요청
    static func createTimer(param: Parameters, completion: @escaping () -> Void){
        let endpoint = APIConstants.Timer.addTimer
        
        AF.request(endpoint, method: .post, parameters: param, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: TimerAddresponse.self) { response in
            print("add Timer request")
            completion()
        }
    }
    
    // MARK: - 공부 시간 추가 요청
    static func updateTimer(timerID: Int, param: Parameters, completion: @escaping () -> Void){
        let endpoint = String(format: APIConstants.Timer.updateTimer, timerID)
        
        AF.request(endpoint, method: .patch, parameters: param, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: TimerUpdateResponse.self) { response in
            print("update Timer request")
            completion()
        }
    }
    
    // MARK: - 타이머 삭제 요청
    static func deleteTimer(timerID: Int, completion: @escaping() -> Void){
        let endpoint = String(format: APIConstants.Timer.deleteTimer, timerID)
        
        AF.request(endpoint, method: .delete, headers: headers).responseDecodable(of: TimerDeleteResponse.self) { response in
            print("delete timer request")
            completion()
            print(response)
        }
    }
    
    // MARK: - 다음 날이 될때 timer를 초기화 하는 함수
    static func initTimer(completion: @escaping (Result<[TimerReadContent], Error>) -> Void){
        let endpoint = APIConstants.Timer.initTimer
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: TimerReadResponse.self) { response in
            switch response.result{
            case .success(let timers):
                completion(.success(timers.body.timerReadList.content))
            case .failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }
}
