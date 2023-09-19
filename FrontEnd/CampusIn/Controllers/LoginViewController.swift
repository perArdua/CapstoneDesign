//
//  LoginViewController.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/02.
//

import UIKit

class LoginViewController: UIViewController {


    
    override func viewDidLoad() {
        
    }
    override func viewWillAppear(_ animated: Bool) {
        let defaults = UserDefaults.standard
        let check = defaults.value(forKey: "isLoggedIn")
        if check as! Bool{
            self.dismiss(animated: false, completion: nil)
        }
    }


    @IBAction func googleLoginBtnTapped(_ sender: UIButton) {
        var socialURL : String
        socialURL = APIConstants.baseURL + "/oauth2/authorization/google"
        print("loginurl")
        print(socialURL)
        let socialVC = self.storyboard?.instantiateViewController(withIdentifier: "socialLogin") as! SocialViewController
        socialVC.socialURL = socialURL
        socialVC.modalTransitionStyle = .coverVertical
        socialVC.modalPresentationStyle = .fullScreen
        self.present(socialVC, animated: false, completion: nil)
    }
    
    @IBAction func kakaoLoginBtnTapped(_ sender: Any) {
        var socialURL : String
        socialURL = "https://accounts.kakao.com/login/?continue=https%3A%2F%2Fcs.kakao.com%2Fhelps%3Fcategory%3D166%26locale%3Dko%26service%3D52#webTalkLogin"
        let socialVC = self.storyboard?.instantiateViewController(withIdentifier: "socialLogin") as! SocialViewController
        socialVC.socialURL = socialURL
        socialVC.modalTransitionStyle = .coverVertical
        socialVC.modalPresentationStyle = .fullScreen
        self.present(socialVC, animated: false, completion: nil)
    }
}
