//
//  ViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/02.
//

import UIKit
import Alamofire
class ViewController: UIViewController {

    
    
    @IBOutlet weak var todoView: UIView!
    @IBOutlet weak var generalPostingView: UIView!
    @IBOutlet weak var questionView: UIView!
    @IBOutlet weak var studyView: UIView!
    
    
    @IBOutlet weak var todoLabel: UILabel!
    @IBOutlet weak var todoLabel2: UILabel!
    @IBOutlet weak var todoLabel3: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        
        let sideBtn = UIBarButtonItem(image: UIImage(systemName: "line.3.horizontal"), style: .plain, target: self, action: #selector(rightBtnTapped))
        sideBtn.tintColor = .black
        navigationItem.rightBarButtonItem = sideBtn
        
        // TabBar 보이기
        self.tabBarController?.tabBar.isHidden = false
        setUp()
    }
    
    func setUp(){
        todoView.layer.borderColor = UIColor.systemGray3.cgColor
        todoView.layer.borderWidth = 1.0
        todoView.layer.cornerRadius = 10.0
        
        generalPostingView.layer.borderColor = UIColor.systemGray3.cgColor
        generalPostingView.layer.borderWidth = 1.0
        generalPostingView.layer.cornerRadius = 10.0

        questionView.layer.borderColor = UIColor.systemGray3.cgColor
        questionView.layer.borderWidth = 1.0
        questionView.layer.cornerRadius = 10.0

        studyView.layer.borderColor = UIColor.systemGray3.cgColor
        studyView.layer.borderWidth = 1.0
        studyView.layer.cornerRadius = 10.0
        
        //할일 목록 라벨 UI 설정
        
        todoLabel.layer.cornerRadius = 10.0
        todoLabel.layer.masksToBounds = true
        todoLabel2.layer.cornerRadius = 10.0
        todoLabel2.layer.masksToBounds = true
        todoLabel3.layer.cornerRadius = 10.0
        todoLabel3.layer.masksToBounds = true
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
        else{
            BoardManager.initBoard()
            print(defaults.value(forKey: "UserId"))
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


