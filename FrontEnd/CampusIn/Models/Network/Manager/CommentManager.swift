//
//  CommentManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/25.
//

import Foundation
import Alamofire

class CommentManager{
    // MARK: - 헤더
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(String(KeyChain.read(key: "token")!))"]
    
    // MARK: - <##>댓글 조회 요청하는 함수
    static func readComment(postID: Int, completion: @escaping (Result<[CommentDataContent], Error>) -> Void){
        let endpoint = String(format: APIConstants.Comment.readComment, postID)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: CommentData.self) { response in
            switch response.result{
            case .success(let comment):
                print("댓글 요청 성공")
                completion(.success(comment.body.commentDataInfo.content))
            case .failure(let error):
                print("댓글 요청 실패")
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 댓글 작성 요청하는 함수
    static func postComment(postID: Int, params: Parameters, completion: @escaping (Result<String, Error>) -> Void){
        let endpoint = String(format: APIConstants.Comment.readComment, postID)
        
        AF.request(endpoint, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: CommentAddResponse.self, completionHandler: { response in
            
            switch response.result{
            case .success(let data):
                completion(.success(data.body.commentSuccess.content))
            case .failure(let err):
                completion(.failure(err))
            }
        })
    }
    
    // MARK: - <##>댓글 삭제 요청하는 함수
    static func deleteComment(postID: Int, commentID: Int){
        let endpoint = String(format: APIConstants.Comment.deleteComment, postID, commentID)
        AF.request(endpoint, method: .delete, headers: headers).responseDecodable(of: CommentDeleteResponse.self) { response in
            switch response.result{
            case .success(_):
                print("댓글 삭제 성공")
            case .failure(_):
                print("댓글 삭제 실패")
            }
        }
    }
    
    static func likeComment(cID: Int, completion: @escaping(Result<commentLikebody, Error>) -> Void){
        let endpoint = String(format: APIConstants.Posts.likeComment, cID)
        AF.request(endpoint, method: .post, headers: headers).responseDecodable(of: commentLikeResponse.self){ response in
            switch response.result{
            case .success(let res):
                print("좋아요 요청 성공")
                completion(.success(res.body))
            case .failure(let error):
                print(error)
            }
        }
    }
}
