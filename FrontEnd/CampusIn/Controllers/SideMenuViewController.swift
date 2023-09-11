//
//  SideMenuViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/17.
//

import UIKit

class SideMenuViewController: UIViewController {
    
    var isManager: Bool = false

    @IBOutlet weak var adminPage: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        AdminManager.getReportedPosts { res in
            switch res{
            case .success(let data):
                print(data)
                print("관리자입니다.")
                self.adminPage.isHidden = false
            case .failure(_):
                print("관리자가 아닙니다")
                self.adminPage.isHidden = true
            }
        }
        

    }
    
    @IBAction func logoutBtnTapped(_ sender: UIButton) {
        let defaults = UserDefaults.standard

        defaults.set(false, forKey: "isLoggedIn")
        
        print("login status changed: \(defaults.value(forKey: "isLoggedIn"))")

        KeyChain.delete(key: "token")
        print("token deleted")
        
        let loginVC = storyboard!.instantiateViewController(withIdentifier: "loginViewController") as! LoginViewController
        loginVC.modalTransitionStyle = .coverVertical
        loginVC.modalPresentationStyle = .fullScreen
        
        print("move to LoginViewController")
        self.present(loginVC, animated: false, completion: nil)
    }
    
}
