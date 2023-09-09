//
//  PostingReplyViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/08.
//

import UIKit
import Alamofire

class PostingReplyViewController: UIViewController {

    var postId: Int?
    var parent_commentId: Int?
    var comment: CommentDataContent?
    var array: [CommentDataContent] = []
    weak var updateReplyDelegate: UpdateReplyDelegate?
    
    let stackView: UIStackView = {
        let sv = UIStackView()
        sv.axis = .horizontal
        sv.distribution = .fill
        sv.alignment = .fill
        sv.spacing = 10
        sv.translatesAutoresizingMaskIntoConstraints = false
        return sv
    }()
    
    let addBtn: UIButton = {
        let btn = UIButton()
        let paperplane = UIImage(systemName: "paperplane.circle")
        btn.setImage(paperplane, for: .normal)
        btn.translatesAutoresizingMaskIntoConstraints = false
        return btn
        
    }()
    
    let textField: UITextField = {
        let tf = UITextField()
//        tf.font = UIFont.systemFont(ofSize: 13)
        tf.placeholder = "답글을 입력하세요"
        tf.textAlignment = .left
        tf.translatesAutoresizingMaskIntoConstraints = false
        return tf
    }()
    
    let tableView: UITableView = {
        let tableview = UITableView()
        return tableview
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        setup()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }

    private func setup(){
        print("asdasdsad")
        stackView.addArrangedSubview(textField)
        stackView.addArrangedSubview(addBtn)
        
        
        view.addSubview(tableView)
        view.addSubview(stackView)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 500
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.allowsSelection = false
        tableView.register(ReplyCommentTableViewCell.self, forCellReuseIdentifier: "replyCell")
        
        NSLayoutConstraint.activate([
            tableView.leadingAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            tableView.trailingAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            tableView.topAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.topAnchor, constant: 10),
            tableView.bottomAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.bottomAnchor, constant: -50)
        ])
        
        NSLayoutConstraint.activate([
            stackView.leadingAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.leadingAnchor, constant: 10),
            stackView.trailingAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.trailingAnchor, constant: -10),
            stackView.topAnchor.constraint(equalTo:  tableView.bottomAnchor, constant: 0),
            stackView.bottomAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.bottomAnchor, constant: 0)
        ])
        
        NSLayoutConstraint.activate([
            addBtn.leadingAnchor.constraint(equalTo:  textField.trailingAnchor, constant: 10),
            addBtn.trailingAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.trailingAnchor, constant: -10),
            addBtn.bottomAnchor.constraint(equalTo:  self.view.safeAreaLayoutGuide.bottomAnchor, constant: 0),
            addBtn.heightAnchor.constraint(equalToConstant: 50),
            addBtn.widthAnchor.constraint(equalToConstant: 50),
        ])
        
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
    }

    @objc func addBtnTapped() {
        let alert = UIAlertController(title: "알림", message: "댓글 작성이 완료되었습니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default){_ in
            var params: Parameters = [:]
            params["content"] = self.textField.text!
            params["parentId"] = String(self.parent_commentId!)
            params["postId"] = self.postId!
            
            CommentManager.postComment(postID: self.postId!, params: params){res in
                print("댓글 생성 리퀘 날림")
                switch res{
                case .success(let data):
                    print(data)
                    CommentManager.readComment(postID: self.postId!) { result in
                        print("댓글 조회 리퀘 날림")
                        switch result{
                        case .success(let comments):
                            for comment in comments{
                                if comment.commentID == self.parent_commentId{
                                    print(comment.children)
                                    self.array = comment.children
                                    self.tableView.reloadData()
                                }
                            }
                        case .failure(let error):
                            print("Error: \(error)")
                        }
                    }
                case .failure(let err):
                    print(err)
                }
                
                self.updateReplyDelegate?.updateReply()
            }
        }
        alert.addAction(okAction)
        
        
        present(alert, animated: true)
    }
    
    func getComment(postID : Int, completion: @escaping ([CommentDataContent]) -> Void){
        CommentManager.readComment(postID: postID) { result in
            switch result{
            case .success(let comments):
                DispatchQueue.main.async {
                    completion(comments)
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }

}

extension PostingReplyViewController: UITableViewDelegate, UITableViewDataSource{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 1{
            return "답글"
        }
        else{
            return ""
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0{return 1}
        else{return array.count}
        
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        print(indexPath.row)
        if indexPath.section == 0{
            let cell = ReplyCommentTableViewCell(style: .default, reuseIdentifier: "replyCell")
            cell.badgeImgView.image = UIImage(systemName: "checkmark.circle.fill")
            cell.userImgView.image = UIImage(systemName: "person.fill")
            cell.dateLabel.text = "06/01"
            cell.likeLabel.text = "5"
            cell.nameLabel.text = comment?.name
            cell.contentLabel.text = comment?.content
            let spacing: CGFloat = 10.0 // 원하는 간격 값으로 수정해주세요.
            cell.separatorInset = UIEdgeInsets(top: 0, left: 0, bottom: spacing, right: 0)
            return cell
        }
        else{
            let cell = ReplyCommentTableViewCell(style: .default, reuseIdentifier: "replyCell")
            cell.badgeImgView.image = UIImage(systemName: "checkmark.circle.fill")
            cell.userImgView.image = UIImage(systemName: "person.fill")
            cell.dateLabel.text = "06/01"
            cell.likeLabel.text = "5"
            cell.nameLabel.text = array[indexPath.row].name
            cell.contentLabel.text = array[indexPath.row].content
            return cell
        }
    }
}
