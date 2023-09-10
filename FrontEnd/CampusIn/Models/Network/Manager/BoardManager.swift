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
//    \(KeyChain.read(key: "token"))
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(String(KeyChain.read(key: "token")!))"]
    
    // MARK: - 게시글 생성 요청 함수
    static func createPost(boardID: Int, tagID: Int,  params: Parameters){
        let endpoint = String(format: APIConstants.Board.createPost, boardID, tagID)
        print(endpoint)
        AF.request(endpoint, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: DataResponse.self, completionHandler: { response in
            print("게시글 생성요청")
            print(response)
        })
    }
    
    // MARK: - 게시글 수정 요청 함수
    static func updatePost(postID: Int, params: Parameters){
        let endpoint = String(format: APIConstants.Posts.updatePost, postID)
        
        AF.request(endpoint, method: .patch, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: DataResponse.self, completionHandler: { response in
            print("게시글 수정 요청")
            print(response)
        })
    }
    
    // MARK: - 게시글을 읽는 함수
    static func readPost(postID: Int, completion: @escaping (Result<PostDetailContent, Error>) -> Void){
        let endpoint = String(format: APIConstants.Posts.showPost, postID)

        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: PostDetail.self) { response in
            switch response.result{
            case .success(let post):
                print("게시판 상세 요청 성공")
                completion(.success(post.body.postDetailContent))
            case .failure(let error):
                print("게시판 상세 요청 실패")
                completion(.failure(error))
            }
        }
        
    }
    
    // MARK: - 게시글 삭제 요청함수
    static func deletePost(postID: Int, completion: @escaping () -> Void){
        let endpoint = String(format: APIConstants.Posts.deletePost, postID)
        
        AF.request(endpoint, method: .delete, headers: headers).responseDecodable(of: Response.self, completionHandler: { response in
            print("게시글 삭제 요청")
            print(response)
            completion()
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
                print(KeyChain.read(key: "token"))
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 내가 쓴 게시글 요청 함수
    
    static func showMyPosting(completion: @escaping (Result<[PostListContent], Error>) -> Void){
        let endpoint = APIConstants.Board.getMyPosting
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: PostList.self) { response in
            switch response.result{
            case .success(let postListArray):
                print("내가 쓴 게시글 요청 성공")
                completion(.success(postListArray.body.postListArray.content))
            case .failure(let error):
                print("내가 쓴 게시글 요청 실패")
                print(KeyChain.read(key: "token"))
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - 게시글 검색 요청 함수
    static func searchPost(boardID: Int, keyword: String, completion: @escaping (Result<[PostSearchContent], Error>) -> Void){
        let endpoint = String(format: APIConstants.Board.searchPosts, boardID, keyword.encodeUrl()!)
        print(endpoint)
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
            case .success(let res):
                print("게시판 초기화 요청 성공")
                //print(res.body.boardInit)
                getBoardID {result in
                    switch result{
                    case .success(let boardIDs):
                        let defaults = UserDefaults.standard
                        for id in boardIDs {
                            defaults.set(id.boardID, forKey: id.boardType)
                            print("게시판 아이디 가져오기 성공")

                            print("자유 게시판",getBoardID(boardName: "Free"))
                            print("질의 응답 게시판",getBoardID(boardName: "Question"))
                            print("책방",getBoardID(boardName: "Book"))
                            print("스터디 게시판",getBoardID(boardName: "Study"))
                            print("관리자 뱃지 게시판",getBoardID(boardName: "AdminBadgeAccept"))
                        }
                    case .failure(_):
                        //print("Error: \(error)")
                        print("게시판 아이디 가져오기 실패")
                    }
                }
            case .failure(_):
                print("게시판 초기화 요청 실패")
                print(KeyChain.read(key: "token"))
                print(APIConstants.Board.initBoards)
                print(response)
            }
        }
    }

    // MARK: - 게시판 아이디를 요청하는 함수
    static func getBoardID(completion: @escaping (Result<[BoardContent], Error>) -> Void){
        let endpoint = APIConstants.Board.getBoardIds

        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: BoardData.self) { response in
            switch response.result{
            case .success(let boardID):
                print("hh 요청 성공")
                completion(.success(boardID.body.boardID.content))

            case .failure(let error):
                print("게시글 검색 요청 실패")
                completion(.failure(error))
            }
        }

    }

    // MARK: - userdefault에 저장된 게시판 아이디를 얻는 함수
    static func getBoardID(boardName : String) -> Int{
        let defaults = UserDefaults.standard
        if let id = defaults.integer(forKey: boardName) as Optional<Int>{
            return id
        }
        return -1
    }
    
    // MARK: - 태그별 고유 Id값 얻기
    static func getTags(completion: @escaping (Result<[TagContent], Error>) -> Void){
        let endpoint = String(format: APIConstants.Board.getTags)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: TagData.self) { response in
            
            print(response)
            switch response.result{
            case .success(let tagData):
                print("태그 목록 요청 성공")
                completion(.success(tagData.body.tagList.content))
            case .failure(let error):
                print("태그 목록 요청 실패")
                print(error)
                completion(.failure(error))
            }
        }
    }
    
    // MARK: - tag별 필터링
    static func tagFiltering(boardID: Int, tagID: Int, completion: @escaping (Result<[PostListContent], Error>) -> Void){
        let endpoint = "http://localhost:8080/api/v1/boards/tag/\(boardID)/\(tagID)/posts"
        print(endpoint)
            
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: PostList.self) { response in
            print(response)
            switch response.result{
            case .success(let postList):
                print("태그 필터링 요청 성공")
                completion(.success(postList.body.postListArray.content))
            case .failure(let error):
                print("태그 필터링 요청 실패")
                print(KeyChain.read(key: "token"))
                print(error.responseCode)
                completion(.failure(error))
            }
        }
    }
    
    static func likePost(postID: Int, completion: @escaping (Result<postLikeResponse, Error>) -> Void){
        let endpoint = String(format: APIConstants.Board.likePost, postID)
        
        AF.request(endpoint, method: .post, headers: headers).responseDecodable(of: postLikeResponse.self){ response in
            switch response.result{
            case.success(let rspns):
                completion(.success(rspns))
            case.failure(let error):
                print(error)
            }
        }
    }
    
    
    static func adoptCommnet(postID: Int, commentID: Int, completion: @escaping(Result<String, Error>) -> Void){
        let endpoint = String(format: APIConstants.Posts.adoptComment, postID, commentID)
        
        AF.request(endpoint, method: .get, headers: headers).responseDecodable(of: CommentAdoptData.self){ response in
            switch response.result{
            case.success(let rspns):
                print(rspns.body.commentAdoptStr)
                completion(.success(rspns.body.commentAdoptStr))
            case.failure(let error):
                print(error)
            }
        }
    }
    
    static func singoPost(pID: Int, completion: @escaping(Result<postSingoBody, Error>) -> Void){
        let endpoint = String(format: APIConstants.Posts.singoPost, pID)
        AF.request(endpoint, method: .post, headers: headers).responseDecodable(of: postSingoData.self){ response in
            switch response.result{
            case .success(let res):
                print("신고 요청 성공")
                completion(.success(res.body))
            case .failure(let error):
                print(error)
            }
        }
    }
}
