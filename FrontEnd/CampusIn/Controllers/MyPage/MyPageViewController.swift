//
//  MyPageViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/01.
//

import UIKit
import Alamofire

class MyPageViewController: UIViewController {

    @IBOutlet weak var nickNameLabel: UILabel!
    var userID: Int?
    var tagIdList: [TagContent] = []
    var tagId: Int = -1;
    var isVerified = false
    
    @IBOutlet weak var badgeRequestBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "내 정보"
        nickNameLabel.text = "닉네임: " + (UserDefaults.standard.value(forKey: "nickname") as! String)
        
        UserManager.getUserID { res in
            switch res{
            case .success(let id):
                self.userID = id
                BadgeManager.getBadge(userid: id) { res in
                    switch res{
                    case .success(let data):
                        if data?.count ?? 0 > 0{
                            self.isVerified = true
                        }
                        else{self.isVerified = false}
                    case .failure(let err):
                        print(err)
                    }
                }
            case.failure(let err):
                print(err)
            }
        }
        
        BoardManager.getTags { res in
            switch res{
            case .success(let tagData):
                self.tagIdList = tagData
                self.tagId = self.getTagId(tag: "Etc")
            case .failure(let err):
                print(err)
            }
        }
        
    }
    
//MARK: - 회원 탈퇴 기능
    @IBAction func withDrawBtnTapped(_ sender: UIButton) {
    }
    
    // MARK: - 뱃지 신청 버튼
    
    @IBAction func badgeRequestBtnTapped(_ sender: Any) {
        let alert = UIAlertController(title: "알림", message: "서류를 보낸 이메일을 입력해주세요", preferredStyle: .alert)
        alert.addTextField{ textfield in textfield.placeholder = "입력해주세요."}
        
        let okAction = UIAlertAction(title: "확인", style: .default){ action in
            if let tf = alert.textFields?.first, let text = tf.text{
                print("입력한 텍스트는 : \(text)")
                
                if self.isValidEmail(text){
                    var param: Parameters = [:]
                    param["title"] = "userID: \(self.userID!)의 뱃지 요청"
                    param["content"] = "email: \(text)"
                    
                    self.postData(boardID: BoardManager.getBoardID(boardName: "AdminBadgeAccept"), params: param)
                    let alert = UIAlertController(title: "알림", message: "신청이 완료되었습니다.", preferredStyle: .alert)
                    let ok = UIAlertAction(title: "확인", style: .default){_ in self.dismiss(animated: true) }
                    alert.addAction(ok)
                    self.present(alert, animated: true)
                }
                else{
                    self.showInvalidEmailAlert()
                }
            }
        }
        
        let cancelAction = UIAlertAction(title: "취소", style: .cancel)
        
        alert.addAction(cancelAction)
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    func isValidEmail(_ email: String) -> Bool {
        let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        let emailPredicate = NSPredicate(format:"SELF MATCHES %@", emailRegex)
        return emailPredicate.evaluate(with: email)
    }
    
    func showInvalidEmailAlert() {
        let alertController = UIAlertController(title: "알림", message: "올바른 이메일 주소 형식을 입력하세요.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default, handler: nil)
        alertController.addAction(okAction)
        present(alertController, animated: true, completion: nil)
    }
    
    func getTagId(tag : String) -> Int{
        for item in tagIdList{
            if item.tagType == tag{
                return item.tagID
            }
        }
        return -1
    }
    
    func postData(boardID: Int, params: Parameters){
        BoardManager.createPost(boardID: boardID, tagID: tagId ,params: params)
    }
}
