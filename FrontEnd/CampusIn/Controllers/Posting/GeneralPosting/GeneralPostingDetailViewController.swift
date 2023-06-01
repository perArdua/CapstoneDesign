//
//  GeneralPostingDetailViewController.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/09.
//

import UIKit
import Alamofire

class GeneralPostingDetailViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    var postID: Int?
    var postDetail: PostDetailContent?
    var comments: [CommentDataContent] = []
    var imgCnt: Int = 0
    
    let pullDownBtn: UIButton = {
        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: "ellipsis.circle"), for: .normal)
        
        return button
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        //UITableView의 footer 영역을 없애줌.
        tableView.sectionFooterHeight = 0
        tableView.sectionHeaderHeight = 25
        
        pullDownBtn.addTarget(self, action: #selector(pullDownBtnTapped), for: .touchUpInside)
        let navigationItem = self.navigationItem
        //네비게이션 바의 오른쪽에 pullDownBtn을 추가한다.
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: pullDownBtn)
    
    }
    
    override func viewWillAppear(_ animated: Bool) {
        getPostDetail(postID: postID!){
            postDetail in
            self.postDetail = postDetail
            self.getComment(postID: self.postID!){
                comments in
                self.comments = comments
                self.tableView.reloadData()
                print(comments)
            }
        }
    }
    
    @objc func pullDownBtnTapped(){
        let nickname = UserDefaults.standard.string(forKey: "nickname")!
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if postDetail?.nickname == nickname{
            
            let editAction = UIAlertAction(title: "수정하기", style: .default) { [self] _ in
                let nextVC = self.storyboard?.instantiateViewController(identifier: "GeneralPostingAddViewController") as! GeneralPostingAddViewController
                nextVC.postDetail = postDetail
                
                nextVC.modalPresentationStyle = .fullScreen
                present(nextVC, animated: true)
            }
            let deleteAction = UIAlertAction(title: "삭제하기", style: .destructive) { [self]_ in
                let deleteAlert = UIAlertController(title: "알림", message: "글을 삭제하시겠습니까?", preferredStyle: .alert)
                let deleteOK = UIAlertAction(title: "예", style: .destructive){ _ in
                    BoardManager.deletePost(postID: self.postDetail!.postID){
                        self.navigationController?.popViewController(animated: true)
                    }
                }
                let deleteNO = UIAlertAction(title: "취소", style: .cancel)
                deleteAlert.addAction(deleteOK)
                deleteAlert.addAction(deleteNO)
                present(deleteAlert, animated: true)
                
            }
            alert.addAction(editAction)
            alert.addAction(deleteAction)
        }
        
        else{
            let sendMsgAction = UIAlertAction(title: "쪽지 보내기", style: .default) { _ in
                // 쪽지 보내기 버튼이 눌렸을 때의 동작을 처리하는 코드 작성
            }
            let reportAction = UIAlertAction(title: "신고하기", style: .destructive) { _ in
                // 신고하기 버튼이 눌렸을 때의 동작을 처리하는 코드 작성
            }
            alert.addAction(sendMsgAction)
            alert.addAction(reportAction)
        }
        let cancelAction = UIAlertAction(title: "취소", style: .cancel, handler: nil)
        alert.addAction(cancelAction)
        present(alert, animated: true, completion: nil)
    }
    
    // MARK: - 게시글의 정보를 가져오는 함수
    func getPostDetail(postID : Int, completion: @escaping (PostDetailContent) -> Void){
        BoardManager.readPost(postID: postID) { result in
            switch result{
            case .success(let post):
                DispatchQueue.main.async {
                    completion(post)
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    // MARK: - 게시글의 댓글을 가져오는 함수
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

// MARK: - 테이블 뷰 설정
extension GeneralPostingDetailViewController: UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰 영역을 "게시글, 댓글" 총 2개로 분리
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    //두번째 section 일때 section 이름을 "댓글"이라고 출력
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 1{
            return "comment"
        }
        else{
            return ""
        }
    }
    
    //댓글을 한번에 몇개나 표시할 것인지 설정
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {return 1}
        else if section == 1 {return comments.count}
        else {return 1}
    }

    //table view에 표시할 내용을 정의한다.
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //section 0일 경우 게시글을 표시
        if indexPath.section == 0{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingContentTableViewCell", for: indexPath) as! GeneralPostingContentTableViewCell
            
            cell.titleLabel.text = postDetail?.title
            cell.contentLabel.text = postDetail?.content
            
            cell.img0.isHidden = true
            cell.img1.isHidden = true
            cell.img2.isHidden = true
            cell.img3.isHidden = true
            cell.img4.isHidden = true
            
            imgCnt = (postDetail!.photoList.count)
            if imgCnt >= 1 {
                cell.img0.image = UIImage(base64: (postDetail?.photoList[0].content)!, withPrefix: false)
                cell.img0.isHidden = false
            }
            if imgCnt >= 2 {
                cell.img1.image = UIImage(base64: (postDetail?.photoList[1].content)!, withPrefix: false)
                cell.img1.isHidden = false
            }
            if imgCnt >= 3{
                cell.img2.image = UIImage(base64: (postDetail?.photoList[2].content)!, withPrefix: false)
                cell.img2.isHidden = false
            }
            if imgCnt >= 4{
                cell.img3.image = UIImage(base64: (postDetail?.photoList[3].content)!, withPrefix: false)
                cell.img3.isHidden = false
            }
            if imgCnt >= 5{
                cell.img4.image = UIImage(base64: (postDetail?.photoList[4].content)!, withPrefix: false)
                cell.img4.isHidden = false
            }
            
            cell.userLabel.text = postDetail!.nickname
            cell.dateLabel.text = String(postDetail!.createdAt[1]) + "/" + String(postDetail!.createdAt[2])
            
            return cell
        }
        else if indexPath.section == 1{
            //section 0일 경우 댓글을 표시
            let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingCommentTableViewCell", for: indexPath) as! GeneralPostingCommentTableViewCell
            print(indexPath.section)
            print("show section2")
//            cell.dateLabel.text = comments[indexPath.row].cr
            cell.contentLabel.text = comments[indexPath.row].content
            cell.nameLabel.text = comments[indexPath.row].name
            return cell
        }
        else{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingCommentAddTableViewCell", for: indexPath) as! GeneralPostingCommentAddTableViewCell
            cell.delegate = self
            return cell
        }
    }
}

extension GeneralPostingDetailViewController: GeneralCommentCellDelegate{
    // MARK: - 댓글 작성 버튼 누를때
    func addBtnTapped(in cell: GeneralPostingCommentAddTableViewCell) {
        
        let alert = UIAlertController(title: "알림", message: "댓글 작성이 완료되었습니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default)
        alert.addAction(okAction)
        
        //API호출
        var params: Parameters = [:]
        params["content"] = cell.textField.text!
        params["parentId"] = "null"
        params["postId"] = postDetail!.postID
        
        CommentManager.postComment(postID: postDetail!.postID, params: params){
            self.getComment(postID: self.postDetail!.postID){
                comments in
                self.comments = comments
                print(comments)
                self.tableView.reloadData()
            }
        }
        self.tableView.reloadData()
        present(alert, animated: true)
    }
}
