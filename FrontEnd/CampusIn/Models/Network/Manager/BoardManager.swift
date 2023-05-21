//
//  BoardManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/17.
//

import Foundation
import Alamofire

class BoardManager{
    // MARK: - 헤더
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(APIConstants.token)"]
    
    // MARK: - 게시글 생성 요청 함수
    static func createPost(boardID: Int, params: Parameters){
        let endpoint = String(format: APIConstants.Board.createPost, boardID)
        
        AF.request(endpoint, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: DataResponse.self, completionHandler: { response in
            print("***************")
            print(response)
            print("***************")
        })
    }
    
    // MARK: - 게시판 목록을 요청하는 함수
    static func showPostbyBoard(boardID: Int, completion: @escaping (Result<[PostListContent], Error>) -> Void){
        let endpoint = String(format: APIConstants.Board.showPostByBoard, boardID)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: PostList.self) { response in
            switch response.result{
            case .success(let postList):
                print("게시판 목록 요청 성공")
                completion(.success(postList.body.postListArray.content))
            case .failure(let error):
                print("게시판 목록 요청 실패")
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 게시글 검색 요청 함수
    static func searchPost(boardID: Int, keyword: String, completion: @escaping (Result<[PostSearchContent], Error>) -> Void){
        let endpoint = String(format: APIConstants.Board.searchPosts, boardID, keyword.encodeUrl()!)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: PostSearch.self) { response in
            switch response.result{
            case .success(let searchList):
                print("게시글 검색 요청 성공")
                completion(.success(searchList.body.postSearchList.content))
                
            case .failure(let error):
                print("게시글 검색 요청 실패")
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 게시판 초기화 함수
    static func initBoard(){
        AF.request(APIConstants.Board.initBoards, method: .get, headers: headers).responseDecodable(of: DataResponse.self) { response in
            switch response.result{
            case .success(_):
                print("게시판 목록 요청 성공")
            case .failure(_):
                print("게시판 목록 요청 실패")
            }
        }
    }
}
