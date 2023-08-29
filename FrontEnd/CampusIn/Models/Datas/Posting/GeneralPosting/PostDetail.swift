//
//  PostDetail.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/22.
//

import Foundation

// MARK: - Dataresponse
struct PostDetail: Codable {
    let header: PostDetailHeader
    let body: PostDetailBody
}

// MARK: - Body
struct PostDetailBody: Codable {
    let postDetailContent: PostDetailContent

    enum CodingKeys: String, CodingKey {
        case postDetailContent = "게시글 상세"
    }
}

// MARK: - 게시글상세
struct PostDetailContent: Codable {
    let postID: Int
    let boardType: String
    let userID : Int?
    let nickname: String?
    let title, content: String
    let photoList: [PostDetailPhotoList]
    let createdAt: [Int]
    let studyGroupID: Int?
    let price: Int?
    
    enum CodingKeys: String, CodingKey {
        case postID = "postId"
        case boardType
        case userID = "userId"
        case studyGroupID = "studyGroupId"
        case nickname, title, content, photoList, createdAt, price
    }
}

// MARK: - PhotoList
struct PostDetailPhotoList: Codable {
    let photoID: Int
    let content: String

    enum CodingKeys: String, CodingKey {
        case photoID = "photoId"
        case content
    }
}

// MARK: - Header
struct PostDetailHeader: Codable {
    let code: Int
    let message: String
}
