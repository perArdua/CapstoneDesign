//
//  APIConstants.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/17.
//

import Foundation

//message 관련 api 완성 시 구조체 추가 요망

struct APIConstants {
    static let baseURL = "http://localhost:8080/api/v1"
    
    struct Board{
        static let showPostByBoard = baseURL + "/boards/%d/posts" // 모든 게시글 불러오기
        static let createPost = baseURL + "/boards/%d/posts" // 게시글 생성(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Board.createPost, boardId)

        static let searchPosts = baseURL + "/boards/%d/posts/search?keyword=%@" // 게시글 찾기(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Board.searchPosts, boardId, keyword)

        static let getBoardIds = baseURL + "/boards/boards/ids" // 게시판 id 얻기
        static let initBoards = baseURL + "/boards/init" // 게시판 초기화
    }
    
    struct Auth{
        static let login = baseURL + "/auth/login" // 로그인
        static let refresh = baseURL + "/auth/refresh" // 토큰 리프레시
    }
    
    struct Comment{
        static let postComment = baseURL + "/post/%d/comments" // 댓글 달기(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Post.getComments, postId)
        static let deleteComment = baseURL + "/post/%d/comments/%d" // 댓글 삭제(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Post.getComment, postId, commentId)
    }
    
    struct User{
        static let getUserInfo = baseURL + "/users" // 유저 정보 가져오기
    }
    
    struct Posts{
        static let search = baseURL + "/posts?keyword=%@" // 게시글 찾기(아래 사용 예시 참고)
        //let endpoint = String(format: APIConstants.Posts.search, keyword)
        
        static let deletePost = baseURL + "/posts/%d" // 게시글 삭제
        static let showPost = baseURL + "/posts/%d" // 게시글 검색
        static let updatePost = baseURL + "/posts/%d"// 게시글 수정
        //사용 예시
        //let endpoint = String(format: APIConstants.Posts.deletePost or showPost or updatePost, postId)
        
    }
    
}


extension String
{
    func encodeUrl() -> String?
    {
        return self.addingPercentEncoding( withAllowedCharacters: .urlQueryAllowed)
    }
    func decodeUrl() -> String?
    {
        return self.removingPercentEncoding
    }
}
