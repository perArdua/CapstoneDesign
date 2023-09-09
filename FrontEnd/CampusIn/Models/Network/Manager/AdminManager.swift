//
//  AdminManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import Foundation
import Alamofire

class AdminManager{
    
    static func getReportedComments(completion: @escaping (Result<[ReportedCommentDataContent], Error>) -> Void){
        let endpoint = APIConstants.Admin.getReportedComments
        print(endpoint)
        AF.request(endpoint, method: .get, headers: APIConstants.headers).responseDecodable(of: ReportedCommentData.self) { response in
            switch response.result{
            case .success(let comment):
                print("댓글 요청 성공")
                completion(.success(comment.body.reportedCommentDataContent))
            case .failure(let error):
                print("댓글 요청 실패")
                completion(.failure(error))
            }
        }
    }
    
    static func blockComment(cID: Int, completion: @escaping (Result<BlockCommentBody, Error>) -> Void){
        let endpoint = String(format: APIConstants.Admin.blockComment, cID)
        AF.request(endpoint, method: .get, headers: APIConstants.headers).responseDecodable(of: BlockCommentData.self){ response in
            switch response.result{
            case.success(let res):
                print("블락 성공")
                completion(.success(res.body))
            case.failure(let error):
                print(error)
            }
        }
    }
    
    static func unBlockComment(cID: Int, completion: @escaping (Result<BlockCommentBody, Error>) -> Void){
        let endPoint = String(format: APIConstants.Admin.unblockComment, cID)
        AF.request(endPoint, method: .get, headers: APIConstants.headers).responseDecodable(of: BlockCommentData.self){ response in
            switch response.result{
            case.success(let res):
                print("언블락 성공")
                completion(.success(res.body))
            case.failure(let error):
                print(error)
            }
        }
    }
    
}
