//
//  GeneralPostingContentTableViewCell.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/10.
//

import UIKit

class GeneralPostingContentTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var likeBtn: UIButton!
    
    @IBOutlet weak var likeLabel: UILabel!
    @IBOutlet weak var img0: UIImageView!
    @IBOutlet weak var img1: UIImageView!
    @IBOutlet weak var img2: UIImageView!
    @IBOutlet weak var img3: UIImageView!
    @IBOutlet weak var img4: UIImageView!
    
    var postid: Int?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    @IBAction func likePostBtnTapped(_ sender: UIButton) {
        BoardManager.likePost(postID: postid!){[weak self] result in
            switch result{
            case .success(let rspns):
                if(rspns.body.first == nil){
                    print("이미 눌림")
                    self!.showNotificationLabel(msg: "이미 좋아요를 누른 게시글 입니다.")
                }else{
                    print("처음 누름")
                    self!.showNotificationLabel(msg: "좋아요 누르기 성공!")
                    if let currentLikesText = self?.likeLabel.text, var currentLikes = Int(currentLikesText) {
                        currentLikes += 1
                        self?.likeLabel.text = "\(currentLikes)"
                        print(self?.likeLabel.text) // 디버깅용
                    }
                    print(self?.likeLabel.text)
                }
            case.failure(let error):
                print(error)
            }
        }
    }
    
    func showNotificationLabel(msg: String) {
        // 알림 메시지를 화면에 레이블로 표시
        let notificationLabel = UILabel()
        notificationLabel.text = msg
        notificationLabel.textAlignment = .center
        notificationLabel.backgroundColor = UIColor(white: 0, alpha: 0.7)
        notificationLabel.textColor = .white
        notificationLabel.layer.cornerRadius = 10
        notificationLabel.clipsToBounds = true
        
        // 레이블을 적절한 위치에 추가
        notificationLabel.frame = CGRect(x: 0, y: 100, width: 250, height: 50)
        notificationLabel.center = self.center
        self.addSubview(notificationLabel)
        
        // 1초 후에 레이블을 숨김
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            notificationLabel.removeFromSuperview()
        }
    }
    
}
