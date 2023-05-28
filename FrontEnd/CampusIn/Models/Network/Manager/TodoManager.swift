//
//  TodoManager.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/26.
//

import Foundation
import Alamofire

class TodoManager {
    
    //MARK: - 모든 todo list 받아오기
    static func getAllTodoList(completion: @escaping (Result<[Todo], Error>) -> Void) {
        let endpoint = APIConstants.TodoList.getAllTodoList
        
        AF.request(endpoint, method: .get, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: TodoResponse.self) { response in
                switch response.result {
                case .success(let todoResponse):
                    let todoList = todoResponse.body.todoResponse.content
                    completion(.success(todoList))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
    }
    //MARK: - todo 추가
    static func addTodoItem(title: String, completion: @escaping (Result<Int, Error>) -> Void) {
        let endpoint = APIConstants.TodoList.addTodo
        let parameters: [String: Any] = [
            "completed": false,
            "title": title
        ]
        
        AF.request(endpoint, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers)
            .validate()
            .responseDecodable(of: TodoCreationResponse.self) { response in
                switch response.result {
                case .success(let todoCreationResponse):
                    let todoId = todoCreationResponse.body.todoCreationMessage.id
                    completion(.success(todoId))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
    }
    
    //MARK: - todo 추가
    static func updateTodoItem(title: String, isCompleted: Bool, todoId: Int, completion: @escaping (Result<Void, Error>) -> Void) {
        let endpoint = String(format: APIConstants.TodoList.editTodo, todoId)
        let parameters: [String: Any] = [
            "completed": isCompleted, 
            "title": title
        ]
        
        AF.request(endpoint, method: .patch, parameters: parameters, encoding: JSONEncoding.default, headers: APIConstants.headers)
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
    
    //MARK: - todo 삭제
    static func deleteTodoItem(todoId: Int, completion: @escaping (Result<Void, Error>) -> Void) {
        let endpoint = String(format: APIConstants.TodoList.deleteTodo, todoId)
        
        AF.request(endpoint, method: .patch, headers: APIConstants.headers)
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
}
