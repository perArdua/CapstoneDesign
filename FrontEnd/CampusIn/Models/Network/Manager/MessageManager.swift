//
//  MessageManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/21.
//

import Foundation
import Alamofire
class MessageManager{
    
    
    //MARK: - 모든 채팅 얻어오기
    static func getAllMessages(roomID: Int, completion: @escaping (Result<[ChattingMessage], Error>) -> Void) {
        let endPoint = String(format: APIConstants.Messages.getAllMessages, roomID)
        AF.request(endPoint, method: .get, headers: APIConstants.headers).responseDecodable(of: ChattingMessageListResponse.self) { response in
            print(APIConstants.headers)
            switch response.result {
            case .success(let messageListResponse):
                
                let messages = messageListResponse.body.쪽지리스트조회완료.content
                completion(.success(messages))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
    //MARK: - 메세지 보내기
    static func sendMessage(roomID: Int, message: String, completion: @escaping (Result<Void, Error>) -> Void) {
        let endPoint = String(format: APIConstants.Messages.sendMessage, roomID)
        
        let parameters: [String: Any] = [
            "message": message
        ]
        
        AF.request(endPoint, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers).responseDecodable(of: DataResponse.self) { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
}














