//
//  SideMenuViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/17.
//

import UIKit

class SideMenuViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

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
