//
//  ChatTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/14.
//

import UIKit

class ChatTableViewCell: UITableViewCell {

        
    
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var bubbleView: UIView!
    
    override func awakeFromNib() {
        print("set")
        super.awakeFromNib()
        bubbleView.translatesAutoresizingMaskIntoConstraints = false
        messageLabel.translatesAutoresizingMaskIntoConstraints = false
        // bubbleView 모서리를 둥글게 설정
        bubbleView.layer.cornerRadius = 8
        bubbleView.clipsToBounds = true
    }
    
    
    
    func configure(with chatMessage: ChatMessage) {
        print("configure")
        print(chatMessage)
        messageLabel?.text = chatMessage.text
        
        // 채팅 메시지의 소유자에 따라 적절한 스타일 적용
        if chatMessage.isSentByCurrentUser { //수신, 발신 따른 bubbleView 및 messageLabel 변화
            print("발신")
            
            //bubbleView 상하좌우 사이즈 조정(글자 수에 맞게)
            bubbleView?.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -10).isActive = true
            bubbleView?.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 10).isActive = true
            bubbleView?.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -10).isActive = true
            //messageLabel 상하좌우 사이즈 조정
            messageLabel?.leadingAnchor.constraint(equalTo: bubbleView.leadingAnchor, constant: 10).isActive = true
            messageLabel?.trailingAnchor.constraint(equalTo: bubbleView.trailingAnchor, constant: -10).isActive = true
            messageLabel?.topAnchor.constraint(equalTo: bubbleView.topAnchor, constant: 10).isActive = true
            messageLabel?.bottomAnchor.constraint(equalTo: bubbleView.bottomAnchor, constant: -10).isActive = true
            
            //텍스트 위치 조정
            messageLabel?.textAlignment = .right
            
            //말풍선 색상 조정
            bubbleView?.backgroundColor = UIColor.systemBlue
            messageLabel?.textColor = UIColor.white
        } else {
            print("수신")
            
            //bubbleView 상하좌우 사이즈 조정(글자 수에 맞게)
            bubbleView?.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 10).isActive = true
            bubbleView?.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 10).isActive = true
            bubbleView?.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -10).isActive = true
            //messageLabel 상하좌우 사이즈 조정
            messageLabel?.leadingAnchor.constraint(equalTo: bubbleView.leadingAnchor, constant: 10).isActive = true
            messageLabel?.trailingAnchor.constraint(equalTo: bubbleView.trailingAnchor, constant: -10).isActive = true
            messageLabel?.topAnchor.constraint(equalTo: bubbleView.topAnchor, constant: 10).isActive = true
            messageLabel?.bottomAnchor.constraint(equalTo: bubbleView.bottomAnchor, constant: -10).isActive = true
            messageLabel?.textAlignment = .left
            bubbleView?.backgroundColor = UIColor.systemGray5
            messageLabel?.textColor = UIColor.black
        }
    }

}
