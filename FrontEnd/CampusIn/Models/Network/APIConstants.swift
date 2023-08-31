//
//  APIConstants.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/17.
//

import Foundation
import Alamofire

//message 관련 api 완성 시 구조체 추가 요망

struct APIConstants {
    static let baseURL = "http://localhost:8080/api/v1"
    static let token = String(KeyChain.read(key: "token")!)
    static let headers: HTTPHeaders = ["Authorization": "Bearer \(token)"]
    
    struct Board{
        static let showPostByBoard = baseURL + "/boards/%d/posts" // 모든 게시글 불러오기
        static let createPost = baseURL + "/boards/%d/posts/%d" // 게시글 생성(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Board.createPost, boardId)

        static let searchPosts = baseURL + "/boards/%d/posts/search?keyword=%@" // 게시글 찾기(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Board.searchPosts, boardId, keyword)

        static let getBoardIds = baseURL + "/boards/boards/ids" // 게시판 id 얻기
        static let initBoards = baseURL + "/boards/init" // 게시판 초기화
        
        static let tagFiltering = baseURL + "/boards/tag/%d/posts" // 태그별 게시글 목록 조회
        static let getTags = baseURL + "/boards/tags/ids" // 태그별 고유 id값 얻기
    }
    
    struct Auth{
        static let login = baseURL + "/auth/login" // 로그인
        static let refresh = baseURL + "/auth/refresh" // 토큰 리프레시
    }
    
    struct Comment{
        static let readComment = baseURL + "/posts/%d/comments"
        static let postComment = baseURL + "/posts/%d/comments" // 댓글 달기(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Post.getComments, postId)
        static let deleteComment = baseURL + "/posts/%d/comments/%d" // 댓글 삭제(아래 사용 예시 참고)
        //사용 예시
        //let endpoint = String(format: APIConstants.Post.getComment, postId, commentId)
    }
    
    struct User{
        static let getUserInfo = baseURL + "/users" // 유저 정보 가져오기
        static let isExistingMember = baseURL + "/users/nickname"
        static let setNickname = baseURL + "/users/nickname?nickname=%@"
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
    
    struct MessageRoom{
        static let getAllMessageRooms = baseURL + "/message-rooms" // 전체 쪽지방 조회
        static let createMessageRoom = baseURL + "/message-rooms" // 쪽지방 생성
        static let getMessageInfo = baseURL + "/message-rooms/%d" // 쪽지방 정보 및 최근 쪽지 조회
        static let blockMessageRoom = baseURL + "/message-rooms/%d/block" // 쪽지방 차단하기
        static let deleteMessageRoom = baseURL + "/message-rooms/%d/delete" // 쪽지방 삭제하기
    }
    
    struct Messages{
        static let getAllMessages = baseURL + "/message-rooms/%d/messages" // 모든 쪽지 조회
        static let sendMessage = baseURL + "/message-rooms/%d/messages" // 쪽지 보내기
        static let sendRedirectMessage = baseURL + "/message-rooms/%d/redirect-message" // 리다이렉트 쪽지 보내기
    }
    
    struct TodoList{
        static let getAllTodoList = baseURL + "/todo"
        static let addTodo = baseURL + "/todo"
        static let editTodo = baseURL + "/todo/%d"
        static let deleteTodo = baseURL + "/todo/%d/delete"
    }
    
    struct Timer{
        static let getTimer = baseURL + "/timer" //timer 조회
        static let addTimer = baseURL + "/timer" //timer 추가
        static let deleteTimer = baseURL + "/timer/%d" //timer 삭제
        static let updateTimer = baseURL + "/timer/%d" //timer 시간 갱신
        static let initTimer = baseURL + "/timer/init" //timer 시간 갱신 
    }
    
    struct StudyGroup{
        static let createStudyGroup = baseURL + "/studygroup"
        static let showStudyGroup = baseURL + "/studygroup/%d"
        static let deleteStudyGroup = baseURL + "/studygroup/%d"
        static let showMyStudyGroup = baseURL + "/studygroup/mystudygroup"
        static let showStudyGroupDetail = baseURL + "/studygroup/%d"
        static let joinStudyGroupDetail = baseURL + "/studygroup/join"
    }
    
    struct Statistics{
        static let getStatistics = baseURL + "/statistics/%d"
        static let createStatistics = baseURL + "/statistics/create"
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
