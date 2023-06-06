//
//  ReplyCommentTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/06.
//

import UIKit

class ReplyCommentTableViewCell: UITableViewCell {

    let userImgView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()
    
    let badgeImgView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()
    
    let nameLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.boldSystemFont(ofSize: 15)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    let dateLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 10)
        label.numberOfLines = 0
        label.textAlignment = .right
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    let contentLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 15)
        label.numberOfLines = 0
        label.backgroundColor = UIColor.systemGray5
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    let mainStackView: UIStackView = {
        let sv = UIStackView()
        sv.axis = .vertical
        sv.distribution = .fill
        sv.alignment = .fill
        sv.spacing = 10
        sv.translatesAutoresizingMaskIntoConstraints = false
        return sv
    }()
    
    let nameDateSV: UIStackView = {
        let sv = UIStackView()
        sv.axis = .horizontal
        sv.distribution = .fill
        sv.alignment = .fill
        sv.spacing = 10
        sv.translatesAutoresizingMaskIntoConstraints = false
        return sv
    }()
    
    let buttonSV:UIStackView = {
        let sv = UIStackView()
        sv.axis = .vertical
        sv.distribution = .fill
        sv.alignment = .fill
        sv.spacing = 5
        sv.translatesAutoresizingMaskIntoConstraints = false
        return sv
    }()
    
    let likeBtn: UIButton = {
        let btn = UIButton()
        let thumbsUpImage = UIImage(systemName: "hand.thumbsup.fill")
        btn.setImage(thumbsUpImage, for: .normal)
        btn.translatesAutoresizingMaskIntoConstraints = false
        return btn
        
    }()
    
    let likeLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 13)
        label.text = "5"
        label.numberOfLines = 0
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    //코드로 할때 여기서 뷰 올려줘야 함
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        //super의 init을 호출, style은 원하는거 (보통 디폴트). reuseIdentifier에는 입력받은 파라미터 넣음
        super.init(style: .default, reuseIdentifier: reuseIdentifier)
        setup()
    }
    
    //지정생성자 재정의 할때는 반드시 필수 생성자도 구현해야함 (자동 상속이 안되므로)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setup(){
        self.addSubview(userImgView)
        self.addSubview(mainStackView)
        self.addSubview(buttonSV)
        setupStackView()
    }
    
    func setupStackView(){
        nameDateSV.addArrangedSubview(nameLabel)
        nameDateSV.addArrangedSubview(badgeImgView)
        nameDateSV.addArrangedSubview(dateLabel)
        
        mainStackView.addArrangedSubview(nameDateSV)
        mainStackView.addArrangedSubview(contentLabel)
        
        buttonSV.addArrangedSubview(likeBtn)
        buttonSV.addArrangedSubview(likeLabel)
    }
    //cell에서 viewDidLoad와 비슷한 역할을 하는 코드
    //만약 스토리보드로 만들때 밑 함수에 원하는 코드 작성. 모두 코드로 할거면 사용 x
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    // MARK: - 셀 오토 레이아웃 잡아주기
    //오토레이아웃 잡아주는 함수
    override func updateConstraints() {
        setConstraints()
        super.updateConstraints()
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        self.userImgView.clipsToBounds = true
        //이렇게 해주면 이미지 뷰가 동그라미가 됨. 이미지 뷰의 너비가 결정되는 시점에서 /2 만큼 radius를 줌
        self.userImgView.layer.cornerRadius = self.userImgView.frame.width / 2
    }
    
    func setConstraints(){
        NSLayoutConstraint.activate([
            userImgView.heightAnchor.constraint(equalToConstant: 50),
            userImgView.widthAnchor.constraint(equalToConstant: 50),
            userImgView.leadingAnchor.constraint(equalTo:  self.safeAreaLayoutGuide.leadingAnchor, constant: 10),
            userImgView.centerYAnchor.constraint(equalTo: self.safeAreaLayoutGuide.centerYAnchor)
        ])
        
        NSLayoutConstraint.activate([
            
            mainStackView.topAnchor.constraint(equalTo: self.safeAreaLayoutGuide.topAnchor, constant: 0),
            mainStackView.bottomAnchor.constraint(equalTo: self.safeAreaLayoutGuide.bottomAnchor, constant: 0),
            mainStackView.leadingAnchor.constraint(equalTo: userImgView.trailingAnchor, constant: 10),
            mainStackView.trailingAnchor.constraint(equalTo: buttonSV.leadingAnchor, constant: -10),
            
        ])
        
        NSLayoutConstraint.activate([
            buttonSV.centerYAnchor.constraint(equalTo: self.safeAreaLayoutGuide.centerYAnchor),
            buttonSV.trailingAnchor.constraint(equalTo: self.safeAreaLayoutGuide.trailingAnchor, constant: -10),
            buttonSV.widthAnchor.constraint(equalToConstant: 20),
        ])

        NSLayoutConstraint.activate([
            nameDateSV.topAnchor.constraint(equalTo: mainStackView.topAnchor, constant: 0),
            nameDateSV.leadingAnchor.constraint(equalTo: mainStackView.leadingAnchor, constant: 0),
            nameDateSV.trailingAnchor.constraint(equalTo: mainStackView.trailingAnchor, constant: 0),
            nameDateSV.heightAnchor.constraint(equalToConstant: 20),
            
            contentLabel.topAnchor.constraint(equalTo: nameDateSV.bottomAnchor, constant: 0),
            contentLabel.leadingAnchor.constraint(equalTo: mainStackView.leadingAnchor, constant: 0),
            contentLabel.trailingAnchor.constraint(equalTo: mainStackView.trailingAnchor, constant: 0),
        ])
        
        NSLayoutConstraint.activate([
            nameLabel.topAnchor.constraint(equalTo: nameDateSV.topAnchor, constant: 0),
            nameLabel.leadingAnchor.constraint(equalTo: nameDateSV.leadingAnchor, constant: 0),
            nameLabel.bottomAnchor.constraint(equalTo: nameDateSV.bottomAnchor, constant: 0),
            nameLabel.widthAnchor.constraint(equalToConstant: 100),
            
            badgeImgView.widthAnchor.constraint(equalToConstant: 20),
            badgeImgView.heightAnchor.constraint(equalToConstant: 20),
            badgeImgView.topAnchor.constraint(equalTo: nameDateSV.topAnchor, constant: 0),
            badgeImgView.leadingAnchor.constraint(equalTo: nameLabel.trailingAnchor, constant: 10),
            
            dateLabel.topAnchor.constraint(equalTo: nameDateSV.topAnchor, constant: 0),
            dateLabel.bottomAnchor.constraint(equalTo: nameDateSV.bottomAnchor, constant: 0),
            dateLabel.trailingAnchor.constraint(equalTo: nameDateSV.trailingAnchor, constant: -5),
            
        ])
    }
}
