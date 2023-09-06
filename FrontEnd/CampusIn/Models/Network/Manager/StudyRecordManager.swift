//
//  StudyRecordManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/05.
//

import Foundation
import Alamofire

class StudyRecordManager{
    
    static func showPostbyGroupID(groupID: Int, completion: @escaping (Result<[RecordPostListContent], Error>) -> Void) {
        let endpint = String(format: APIConstants.StudyRecord.showStudyRecord, groupID)
        print(endpint)
        AF.request(endpint, method: .get, headers: APIConstants.headers).responseDecodable(of: RecordPostList.self) { response in
            switch response.result {
            case .success(let recordList):
                print("기록 요청 성공")
                completion(.success(recordList.body.content.content))
            case .failure(let error):
                print(error)
                completion(.failure(error))
            }
        }
    }

    
}
