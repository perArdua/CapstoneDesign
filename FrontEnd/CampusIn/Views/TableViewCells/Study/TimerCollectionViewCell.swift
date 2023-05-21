//
//  TimerCollectionViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/21.
//

import UIKit

class TimerCollectionViewCell: UICollectionViewCell {
    let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(systemName: "person.fill")
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()
    
    // 사용자 이름을 표시할 UILabel
    let nameLabel: UILabel = {
        let label = UILabel()
        label.text = "이름"
        label.textAlignment = .center
        return label
    }()
    
    // 사용자 타이머 정보를 표시할 UILabel
    let timerLabel: UILabel = {
        let label = UILabel()
        label.text = "00:00:00"
        label.font = UIFont.systemFont(ofSize: 13)
        label.textAlignment = .center
        // 라벨의 설정을 추가로 구성할 수 있습니다.
        return label
    }()
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        NSLayoutConstraint.deactivate([
            imageView.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
            imageView.topAnchor.constraint(equalTo: topAnchor, constant: 5),
            imageView.widthAnchor.constraint(equalToConstant: 40),
            imageView.heightAnchor.constraint(equalToConstant: 40),
            
            nameLabel.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
            nameLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 5),
          
            timerLabel.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
            timerLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 3),
            timerLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -10)
        ])
    }

    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        contentView.addSubview(imageView)
        contentView.addSubview(nameLabel)
        contentView.addSubview(timerLabel)
        
        imageView.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        timerLabel.translatesAutoresizingMaskIntoConstraints = false
        
        
        NSLayoutConstraint.activate([
            //유저 이미지 오토 레이아웃
            imageView.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
            imageView.topAnchor.constraint(equalTo: topAnchor, constant: 5),
            imageView.widthAnchor.constraint(equalToConstant: 40),
            imageView.heightAnchor.constraint(equalToConstant: 40),
            
            //유저 이름 오토 레이아웃
            nameLabel.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
//            nameLabel.widthAnchor.constraint(equalToConstant: 80),
//            nameLabel.heightAnchor.constraint(equalToConstant: 10),
            nameLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 5),
            
            //유저 타이머 오토 레이아웃
            timerLabel.centerXAnchor.constraint(equalTo: centerXAnchor, constant: 0),
            timerLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 3),
//            timerLabel.widthAnchor.constraint(equalToConstant: 80),
//            timerLabel.heightAnchor.constraint(equalToConstant: 10),
            timerLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -10)
        ])
        
    }
        
        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
    
    
}
