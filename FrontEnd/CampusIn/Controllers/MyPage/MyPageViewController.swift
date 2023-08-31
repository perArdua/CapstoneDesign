//
//  MyPageViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/01.
//

import UIKit

class MyPageViewController: UIViewController {

    @IBOutlet weak var nickNameLabel: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "내 정보"
        nickNameLabel.text = "닉네임: " + (UserDefaults.standard.value(forKey: "nickname") as! String)
    }
    
//MARK: - 회원 탈퇴 기능
    @IBAction func withDrawBtnTapped(_ sender: UIButton) {
    }
    
}
