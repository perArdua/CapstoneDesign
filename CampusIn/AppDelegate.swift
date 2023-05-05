//
//  AppDelegate.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/02.
//
import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {



    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        let defaults = UserDefaults.standard
        print("app delegate \(defaults.value(forKey: "isLoggedIn"))")
        // 로그인 상태에 따라 로그인 화면으로 갈지 메인화면으로 갈지 분류
        if let loginCheck = defaults.value(forKey: "isLoggedIn") {
            if loginCheck as! Bool {
                // 이미 로그인 되어있는 경우
                print("login status: true")
            } else {
                // 로그인 되지 않은 경우
                print("login status: false")
                defaults.set(false, forKey: "isLoggedIn") // 로그인 여부에 false 저장
            }
        } else{
            //신규 유저의 경우
            print("nwe user")
            defaults.set(false, forKey: "isLoggedIn") // 로그인 여부에 false 저장
        }
        
       
        
        
        return true
    }

    // MARK: UISceneSession Lifecycle
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }


}
