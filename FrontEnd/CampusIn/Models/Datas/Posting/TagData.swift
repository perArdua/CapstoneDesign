//
//  TagData.swift
//  CampusIn
//
//  Created by 이동현 on 2023/08/31.
//

import Foundation

// MARK: - PostDetail
struct TagData: Codable {
    let header: TagHeader
    let body: TagBody
}

// MARK: - Body
struct TagBody: Codable {
    let tagList: TagList

    enum CodingKeys: String, CodingKey {
        case tagList = "태그별 고유 id값 얻기"
    }
}

// MARK: - 태그별고유ID값얻기
struct TagList: Codable {
    let content: [TagContent]
    let pageable: TagPageable
    let totalElements, totalPages: Int
    let last: Bool
    let numberOfElements: Int
    let first: Bool
    let sort: TagSort
    let number, size: Int
    let empty: Bool
}

// MARK: - Content
struct TagContent: Codable {
    let tagID: Int
    let tagType: String

    enum CodingKeys: String, CodingKey {
        case tagID = "tagId"
        case tagType
    }
}

// MARK: - Pageable
struct TagPageable: Codable {
    let sort: TagSort
    let pageSize, pageNumber, offset: Int
    let paged, unpaged: Bool
}

// MARK: - Sort
struct TagSort: Codable {
    let unsorted, sorted, empty: Bool
}

// MARK: - Header
struct TagHeader: Codable {
    let code: Int
    let message: String
}

struct ConvertTag{
    static func convert(tag: String) -> String{
        if tag == "기타" {return "Etc"}
        else if tag == "예체능" {return "Art"}
        else if tag == "인문" {return "Humanity"}
        else if tag == "경제" {return "Economy"}
        else if tag == "공학" {return "Engineering"}
        else if tag == "자연과학" {return "Science"}
        else if tag == "수학" {return "Math"}
        else {return "IT"}
    }
    
    static func convert2(tag: String) -> String{
        if tag == "Etc" {return "기타"}
        else if tag == "Art" {return "예체능"}
        else if tag == "Humanity" {return "인문"}
        else if tag == "Economy" {return "경제"}
        else if tag == "Engineering" {return "공학"}
        else if tag == "Science" {return "자연과학"}
        else if tag == "Math" {return "수학"}
        else {return "IT"}
    }
}
