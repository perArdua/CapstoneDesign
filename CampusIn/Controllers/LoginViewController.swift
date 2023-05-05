//
//  LoginViewController.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/02.
//

import UIKit

class LoginViewController: UIViewController {


    
    
    override func viewWillAppear(_ animated: Bool) {
        let defaults = UserDefaults.standard
        let check = defaults.value(forKey: "isLoggedIn")
        if check as! Bool{
            self.dismiss(animated: false, completion: nil)
        }
    }
    
    @IBAction func loginButtonTapped(_ sender: UIButton) {
        let social = sender.currentTitle
        var socialURL : String
        guard let social = social else { return }
        print(social)
        switch social{
        case "google":
            socialURL = "http://localhost:8080/oauth2/authorization/google"
        case "kakao":
            socialURL = "https://accounts.kakao.com/login/?continue=https%3A%2F%2Fcs.kakao.com%2Fhelps%3Fcategory%3D166%26locale%3Dko%26service%3D52#webTalkLogin"
        default:
            socialURL = ""
        }
        
        let socialVC = self.storyboard?.instantiateViewController(withIdentifier: "socialLogin") as! SocialViewController
        socialVC.socialURL = socialURL
        print("dddd")
        
//        debugPrint(self.navigationController?.pushViewController(socialVC, animated: true))
        socialVC.modalTransitionStyle = .coverVertical
        socialVC.modalPresentationStyle = .fullScreen
        self.present(socialVC, animated: false, completion: nil)

    }
}
