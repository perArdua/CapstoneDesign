//
//  CampusIn++Bundle.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/19.
//

import Foundation

extension Bundle {
    
    var apiKey: String {
        guard let file = self.path(forResource: "secret", ofType: "plist") else { return "" }
        guard let resource = NSDictionary (contentsOfFile: file) else { return "" }
        guard let key = resource["API_KEY"] as? String else { fatalError ("key가 존재하지 않음")}
        return key
    }
    
    var loginURL: String {
        guard let file = self.path(forResource: "secret", ofType: "plist") else { return "" }
        guard let resource = NSDictionary (contentsOfFile: file) else { return "" }
        guard let key = resource["Login_URL"] as? String else { fatalError ("key가 존재하지 않음")}
        return key
    }
}
