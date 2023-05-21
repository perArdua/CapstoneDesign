//
//  ViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/02.
//

import UIKit
import Alamofire
class ViewController: UIViewController {

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        
        let sideBtn = UIBarButtonItem(image: UIImage(systemName: "line.3.horizontal"), style: .plain, target: self, action: #selector(rightBtnTapped))
        navigationItem.rightBarButtonItem = sideBtn
        
        // TabBar 보이기
        self.tabBarController?.tabBar.isHidden = false

//        let rightButton = UIBarButtonItem(image: UIImage(named: "line.3.horizontal"), style: .plain, target: self, action: #selector(rightButtonTapped))
//        navigationItem.rightBarButtonItem = rightButton
        
        
    
    }
    override func viewWillAppear(_ animated: Bool) {
        // TabBar 보이기
        self.tabBarController?.tabBar.isHidden = false
        
    }

    override func loadView() {
        super.loadView()
        let defaults = UserDefaults.standard
        if !(defaults.value(forKey: "isLoggedIn")! as! Bool){ // 로그인 되지 않은 회원의 경우 로그인 화면으로 이동시킴
            print("login status: false")
            print("move to login page")
            
            let loginVC = storyboard!.instantiateViewController(withIdentifier: "loginViewController") as! LoginViewController
            loginVC.modalTransitionStyle = .coverVertical
            loginVC.modalPresentationStyle = .fullScreen
            
            self.present(loginVC, animated: false, completion: nil)
        }
    }

    @IBAction func rightButtonTapped(_ sender: UIBarButtonItem) {
        let pushVC = self.storyboard?.instantiateViewController(withIdentifier: "SideMenuNavigationController")

        self.navigationController?.pushViewController(pushVC!, animated: true)
    }
    
    @objc func rightBtnTapped(){
        let nextVC = self.storyboard?.instantiateViewController(withIdentifier: "SideMenuNavigationController")
        //self.navigationController?.pushViewController(nextVC!, animated: true)
        self.present(nextVC!, animated: true)
    }
    

}


