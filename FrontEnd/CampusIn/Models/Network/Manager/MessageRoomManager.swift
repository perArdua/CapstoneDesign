//
//  MessageRoomManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/21.
//

import Foundation
import Alamofire

class MessageRoomManager{

    //MARK: - 모든 채팅방 얻기
    static func getAllMessageRooms(completion: @escaping (Result<[MessageRoom], Error>) -> Void) {
        let endPoint = APIConstants.MessageRoom.getAllMessageRooms

        AF.request(endPoint, method: .get, headers: APIConstants.headers).responseDecodable(of: MessageRoomListResponse.self) { response in
            switch response.result {
            case .success(let messageRoomListResponse):
                let messageRooms = messageRoomListResponse.body.values.flatMap { $0.content }
                completion(.success(messageRooms))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
//MARK: - 채팅방 삭제
    static func deleteMessageRoom(roomID: Int, completion: @escaping (Result<Void, Error>) -> Void) {
        let endPoint = String(format: APIConstants.MessageRoom.deleteMessageRoom, roomID)
        
        AF.request(endPoint, method: .post, encoding: JSONEncoding.default, headers: APIConstants.headers).response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

    //MARK: - 채팅방 차단
    static func blockMessageRoom(roomID: Int, completion: @escaping (Result<Void, Error>) -> Void) {
        let endPoint = String(format: APIConstants.MessageRoom.blockMessageRoom, roomID)
        
        AF.request(endPoint, method: .post, encoding: JSONEncoding.default, headers: APIConstants.headers).response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
    //MARK: - 채팅방 만들기
    static func createMessageRoom(postID: Int, userID: Int, completion: @escaping (Result<Void, Error>) -> Void){
        let endPoint = APIConstants.MessageRoom.createMessageRoom
        
        let parameters: [String: Any] = [
            "createdFrom": postID,
            "firstMessage": "대화를 시작하세요!",
            "receiverId": userID
        ]
        
        AF.request(endPoint, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers).response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

}




