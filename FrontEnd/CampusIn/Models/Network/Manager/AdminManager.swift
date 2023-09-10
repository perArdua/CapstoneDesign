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
    
    static func getReportedPosts(completion: @escaping (Result<[ReportedPostListContent], Error>) -> Void){
        let endpoint = APIConstants.Admin.getReportedPosts
        AF.request(endpoint, method: .get, headers: APIConstants.headers).responseDecodable(of: ReportedPostList.self){ response in
            switch response.result{
            case.success(let posts):
                print("신고 게시글 요청 성공")
                completion(.success(posts.body.postListArray))
            case.failure(let error):
                print("신고 게시글 요청 실패")
                print(error)
            }
        }
    }
    
    static func blockPost(pID: Int, completion: @escaping (Result<BlockPostBody, Error>) -> Void){
        let endpoint = String(format: APIConstants.Admin.blockPost, pID)
        AF.request(endpoint, method: .get, headers: APIConstants.headers).responseDecodable(of: BlockPostData.self){ response in
            switch response.result{
            case.success(let res):
                print("게시글 블락 성공")
                completion(.success(res.body))
            case.failure(let error):
                print(error)
            }
        }
    }
    
    static func unBlockPost(pID: Int, completion: @escaping (Result<BlockPostBody, Error>) -> Void){
        let endPoint = String(format: APIConstants.Admin.unblockPost, pID)
        AF.request(endPoint, method: .get, headers: APIConstants.headers).responseDecodable(of: BlockPostData.self){ response in
            switch response.result{
            case.success(let res):
                print("언블락 성공")
                completion(.success(res.body))
            case.failure(let error):
                print(error)
            }
        }
    }
    
    static func handleBadge(flag: String, postID:Int, completion: @escaping (Result<String, Error>) -> Void){
        let endpoint = APIConstants.Admin.handleBadge + "/\(postID)/\(flag)"
        
        AF.request(endpoint, method: .put, headers: APIConstants.headers).responseDecodable(of: HandleBadgeData.self){ res in
            switch res.result{
            case .success(let suc):
                completion(.success(suc.body.handleBadgeStatus))
            case .failure(let err):
                completion(.failure(err))
            }
        }
    }
    
    static func makeBadge(postID: Int, completion: @escaping (Result<MakeBadgeContent, Error>) -> Void){
        let endpoint = APIConstants.Admin.makeBadge + "?name=professor&postId=\(postID)"
        
        AF.request(endpoint, method: .post,  headers: APIConstants.headers).responseDecodable(of: MakeBadgeData.self){ res in
            switch res.result{
            case .success(let suc):
                completion(.success(suc.body.makeBadge))
            case .failure(let err):
                completion(.failure(err))
            }
        }
    }
}
