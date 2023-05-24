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

    //cell의 재약조건을 저장할 배열
    private var layoutConstraintsCache: [String: [NSLayoutConstraint]] = [:]

    override func awakeFromNib() {
        super.awakeFromNib()

        // bubbleView 모서리를 둥글게 설정
        bubbleView.layer.cornerRadius = 8
        bubbleView.clipsToBounds = true

    }

    func configure(with chatMessage: ChattingMessage) {
        //기존의 Auto Layout 비활성화
        bubbleView?.translatesAutoresizingMaskIntoConstraints = false
        messageLabel?.translatesAutoresizingMaskIntoConstraints = false

        //메세지 내용 할당
        messageLabel?.text = chatMessage.content

        //cacheKey를 통해 기존 셀에 대한 접근을 가능하게 해줌(재사용 되어도 이 값은 동일)
        let cacheKey = reuseIdentifier ?? ""
        
        //캐시값 있을 경우 기존의 Layout을 deactivate, 없으면 새로 할당할 수 있게 제약조건 비 활성화
        if let cachedConstraints = layoutConstraintsCache[cacheKey] {
            NSLayoutConstraint.deactivate(cachedConstraints)
        } else {
            bubbleView?.translatesAutoresizingMaskIntoConstraints = false
            messageLabel?.translatesAutoresizingMaskIntoConstraints = false
        }

        //sender가 누구인지에 관련없이 동일한 bubbleView의 top, bottom, messageLabel의 상, 하, 좌, 우의 제약조건은 미리 설정해도 무관
        bubbleView?.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 10).isActive = true
        bubbleView?.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -10).isActive = true

        messageLabel?.leadingAnchor.constraint(equalTo: bubbleView.leadingAnchor, constant: 10).isActive = true
        messageLabel?.trailingAnchor.constraint(equalTo: bubbleView.trailingAnchor, constant: -10).isActive = true
        messageLabel?.topAnchor.constraint(equalTo: bubbleView.topAnchor, constant: 10).isActive = true
        messageLabel?.bottomAnchor.constraint(equalTo: bubbleView.bottomAnchor, constant: -10).isActive = true

        //sender를 구분하여 bubbleView의 backgroundColor 및 messageLabel의 textColor 조정
        bubbleView?.backgroundColor = chatMessage.isReceived ? UIColor.systemGray5 : UIColor.systemBlue
        messageLabel?.textColor = chatMessage.isReceived ? UIColor.black : UIColor.white

        //sender에 따라 변하는 bubbleView의 좌, 우 제약조건에 대해 설정
        let leadingConstraint: NSLayoutConstraint?
        let trailingConstraint: NSLayoutConstraint?

        if !chatMessage.isReceived {
            // 발신자 레이아웃 설정
            leadingConstraint = bubbleView?.leadingAnchor.constraint(greaterThanOrEqualTo: contentView.leadingAnchor, constant: 10)
            trailingConstraint = bubbleView?.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -10)
            messageLabel?.textAlignment = .right
        } else {
            // 수신자 레이아웃 설정
            leadingConstraint = bubbleView?.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 10)
            trailingConstraint = bubbleView?.trailingAnchor.constraint(lessThanOrEqualTo: contentView.trailingAnchor, constant: -10)
            messageLabel?.textAlignment = .left
        }

        //위에서 설정한 제약조건을 activate
        leadingConstraint?.isActive = true
        trailingConstraint?.isActive = true

        //제약조건을 담을 배열을 생성
        let constraints = [
            leadingConstraint,
            trailingConstraint,
            bubbleView?.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 10),
            bubbleView?.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -10),
            messageLabel?.leadingAnchor.constraint(equalTo: bubbleView.leadingAnchor, constant: 10),
            messageLabel?.trailingAnchor.constraint(equalTo: bubbleView.trailingAnchor, constant: -10),
            messageLabel?.topAnchor.constraint(equalTo: bubbleView.topAnchor, constant: 10),
            messageLabel?.bottomAnchor.constraint(equalTo: bubbleView.bottomAnchor, constant: -10)
        ].compactMap { $0 }//compactMap 사용 시 Optional 값 제거됨 -> 따라서 유효한 제약조건만 남게됨

        //유효한 제약조건을 activate 시키고, 캐시에 현재 cell의 제약조건을 저장
        NSLayoutConstraint.activate(constraints)
        layoutConstraintsCache[cacheKey] = constraints
    }

}
