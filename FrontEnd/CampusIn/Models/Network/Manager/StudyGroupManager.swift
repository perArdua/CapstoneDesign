//
//  StudyGroupManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import Foundation
import Alamofire

class StudyGroupManager{
    
    //MARK: - create study group
    static func createStudyGroup(title: String, size: Int, completion: @escaping (Result<Void, Error>) -> Void){
        let endPoint = APIConstants.StudyGroup.createStudyGroup
        
//        let userId = UserDefaults.standard.value(forKey: "userId")!
//        let userIdString = String(describing: userId)
        
        let parameters: [String: Any] = [
            "limitedMemberSize": size,
            "studygroupName": title,
        ]
        
        AF.request(endPoint, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers)
            .validate()
            .response { response in
                switch response.result {
                case .success:
                    completion(.success(()))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }
    
    //MARK: - show all study group
    static func showStudyGroup(groupID: Int, completion: @escaping (Result<[StudyGroupDetails], Error>) -> Void){
        let endPoint = String(format: APIConstants.StudyGroup.showStudyGroup, groupID)
        
        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: StudyGroupResponse.self) { response in
                switch response.result {
                case .success(let studyGRes):
                    let sgResponse = studyGRes.body.studyGroupDetails
                    completion(.success(sgResponse))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }
    
    //MARK: - delete study group
    static func deleteStudyGroup(groupID: Int, size: Int, title: String, completion: @escaping (Result<Void, Error>) -> Void){
        let endPoint = String(format: APIConstants.StudyGroup.deleteStudyGroup, groupID)
        
        AF.request(endPoint, method: .patch, headers: APIConstants.headers)
            .validate()
            .response { response in
                switch response.result {
                case .success:
                    completion(.success(()))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }
    
    //MARK: - show my study group
    static func showMyStudyGroup(completion: @escaping(Result<[MyStudyGroupDetails], Error>) -> Void){
        let endPoint = APIConstants.StudyGroup.showMyStudyGroup
        
        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: MyStudyGroupResponse.self) { response in
                switch response.result {
                case .success(let studyGRes):
                    let sgResponse = studyGRes.body.studyGroupDetails.content
                    completion(.success(sgResponse))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }
    
    //MARK: - show study group detail
    static func showStudyGroupDetail(groupID: Int, completion: @escaping(Result<StudyGroupDetailContent, Error>) -> Void){
        let endPoint = String(format: APIConstants.StudyGroup.showStudyGroupDetail, groupID)
        
        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: StudyGroupDetailResponse.self) { response in
                switch response.result {
                case .success(let studyGRes):
                    let sgResponse = studyGRes.body.studyGroupDetail
                    completion(.success(sgResponse))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }
    
    //MARK: - join study group
    static func joinStudyGroup(groupID: Int, completion: @escaping (Result<Void, Error>) -> Void){
        let endPoint = APIConstants.StudyGroup.joinStudyGroupDetail
        
        let userId = UserDefaults.standard.value(forKey: "userId")!
        let userIdString = String(describing: userId)
        
        let parameters: [String: Any] = [
            "studygroupId": groupID
        ]
        
        AF.request(endPoint, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers)
            .validate()
            .response { response in
                switch response.result {
                case .success:
                    completion(.success(()))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }

    static func deleteStudyGroup(groupID: Int, completion: @escaping (Result<Void, Error>) -> Void){
        let endPoint = String(format: APIConstants.StudyGroup.deleteStudyGroup, groupID)
        
        
        AF.request(endPoint, method: .delete, encoding: JSONEncoding.default, headers: APIConstants.headers)
            .validate()
            .response { response in
                switch response.result {
                case .success:
                    completion(.success(()))
                case .failure(let error):
                    completion(.failure(error))
            }
        }
    }

    
    //MARK: - show study group detail
    static func getGroupTimer(groupID: Int, completion: @escaping(Result<[GroupTimerContent], Error>) -> Void){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        
        let endPoint = String(format: APIConstants.StudyGroup.getGroupTimer, groupID) + dateFormatter.string(from: Date())
        print(endPoint)
        AF.request(endPoint, method: .get, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: GroupTimerData.self) { response in
                switch response.result {
                case .success(let timers):
                    print("그룹 타이머 가져오기 성공")
                    completion(.success(timers.body.groupTimerList.content))
                case .failure(let error):
                    print(error)
                    completion(.failure(error))
            }
        }
    }
}
