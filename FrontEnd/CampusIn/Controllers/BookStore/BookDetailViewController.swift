//
//  BookDetailViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/06.
//

import UIKit

class BookDetailViewController: UIViewController {

    @IBOutlet weak var bookTitle: UILabel!
    var bookName: String?
    var sellerName: String?
    var bookPrice: String?
    var bookImg: UIImage?
    var bookDetail: PostDetailContent?
    
    @IBOutlet weak var sendMsgBtn: UIButton!
    @IBOutlet weak var detailPrice: UILabel!
    @IBOutlet weak var detailImg: UIImageView!
    @IBOutlet weak var msgBtn: UIButton!
    @IBOutlet weak var detailSellerName: UILabel!
    @IBOutlet weak var detailName: UILabel!
    @IBOutlet weak var bookInfoTV: UITextView!
    override func viewDidLoad() {
        super.viewDidLoad()
        bookTitle.textColor = .white
        bookInfoTV.text = bookDetail?.content
        print("content")
        print(bookInfoTV.text = bookDetail?.content)
        //print(bookDetail)
        
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.tabBarController?.tabBar.isHidden = true
        self.navigationController?.navigationBar.isHidden = false
        //본인 게시글이면 메세지 전송 버튼 안보이게
        var nickname: String = UserDefaults.standard.value(forKey: "nickname") as! String
        if(sellerName == nickname){
            sendMsgBtn.isHidden = true
        }
        
        
        let editImage = UIImage(systemName: "plus")
        let editButton = UIBarButtonItem(image: editImage, style: .plain, target: self, action: #selector(editButtonTapped))
        editButton.tintColor = .white
        navigationItem.rightBarButtonItem = editButton

        msgBtn.tintColor = UIColor(named: "btnColor")
        detailImg.image = bookImg
        detailPrice.text = bookPrice! + " 원"
        detailName.text = bookName
        detailSellerName.text = sellerName
    }
    
    @objc func editButtonTapped() {
        // 오른쪽 버튼이 눌렸을 때 수행할 동작
        let nextVC = storyboard!.instantiateViewController(withIdentifier: "SellBookViewController") as! SellBookViewController
        nextVC.titleString = "책 정보 수정하기"
        nextVC.isEditable = true
        nextVC.detail = bookDetail
        navigationController?.pushViewController(nextVC, animated: true)
    }

    //MARK: - 메세지 전송 버튼 액션 구현
    @IBAction func sendMsgBtnTapped(_ sender: UIButton) {
        let alert = UIAlertController(title: "쪽지 보내기", message: "\(String(sellerName!)) 님께 쪽지를 보내시겠습니까?", preferredStyle: .alert)

        // 확인 버튼
        let confirmAction = UIAlertAction(title: "확인", style: .default) { (action) in
            MessageRoomManager.createMessageRoom(postID: self.bookDetail!.postID, userID: (self.bookDetail?.userID)!){ result in
                switch result{
                case .success:
                    print("create new message room!")
                    let chatVC = self.storyboard!.instantiateViewController(withIdentifier: "MessageBoxViewController") as! MessageBoxViewController
                    self.navigationController?.pushViewController(chatVC, animated: true)
                case .failure(let error):
                    print(error)
                }
            }
        }

        // 취소 버튼
        let cancelAction = UIAlertAction(title: "취소", style: .cancel) { (action) in
        }

        // 알림창에 액션 추가
        alert.addAction(confirmAction)
        alert.addAction(cancelAction)

        // 알림창 표시
        present(alert, animated: true, completion: nil)

    }
    
}
