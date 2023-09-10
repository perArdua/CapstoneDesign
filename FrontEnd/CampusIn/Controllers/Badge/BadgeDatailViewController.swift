//
//  BadgeDatailViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/10.
//

import UIKit

class BadgeDatailViewController: UIViewController {

    
    var postID: Int?
    var postDetail: PostDetailContent?
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var contentLabel: UILabel!
    
    @IBOutlet weak var nicknameLabel: UILabel!
    
    @IBOutlet weak var dateLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        titleLabel.text = postDetail?.title
        contentLabel.text = postDetail?.content
        nicknameLabel.text = "닉네임: \(postDetail!.nickname!)"
        dateLabel.text = "요청일: \(postDetail!.createdAt[0]).\(postDetail!.createdAt[1]).\(postDetail!.createdAt[2])"
        
    }
    
    @IBAction func acceptBtnTapped(_ sender: Any) {
        AdminManager.makeBadge(postID: postID!) { res in
            switch res{
            case .success(let suc):
                print("뱃지 생성 \(suc.name)")
            case .failure(let err):
                print(err)
            }
        }
        
        AdminManager.handleBadge(flag: "true", postID: postID!) { res in
            switch res{
            case .success(let suc):
                print(suc)
            case .failure(let err):
                print(err)
            }
        }
        
        let alert = UIAlertController(title: "알림", message: "처리되었습니다", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default){_ in
            BoardManager.deletePost(postID: self.postID!) {
                print("뱃지 신청 글 삭제")
            }
            DispatchQueue.main.async {
                self.dismiss(animated: true)
            }
        }
        alert.addAction(okAction)
        self.present(alert, animated: true)
        
    }
    
    @IBAction func deleteBtnTapped(_ sender: Any) {
        AdminManager.handleBadge(flag: "false", postID: postID!) { res in
            switch res{
            case .success(let suc):
                print(suc)
            case .failure(let err):
                print(err)
            }
        }
        
        let alert = UIAlertController(title: "알림", message: "처리되었습니다", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default){_ in
            BoardManager.deletePost(postID: self.postID!) {
                print("뱃지 신청 글 삭제")
            }
            DispatchQueue.main.async {
                self.dismiss(animated: true)
            }
            
        }
        alert.addAction(okAction)
        self.present(alert, animated: true)
    }
}
