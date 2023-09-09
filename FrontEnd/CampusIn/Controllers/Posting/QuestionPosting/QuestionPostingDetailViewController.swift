//
//  CampusIn
//
//  Created by 이동현 on 2023/06/08.
//

import UIKit
import Alamofire

class QuestionPostingDetailViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    var postID: Int?
    var postDetail: PostDetailContent?
    var comments: [CommentDataContent] = []
    var comments_c: [CommentDataContent] = []
    var reportedCommentID: Int?
    var isManager: Bool = false
    var imgCnt: Int = 0
    
    @IBOutlet weak var commentAddTf: UITextField!
    
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
        tableView.allowsSelection = false
        print("isManager: \(isManager)")
        print("postid: \(postID)")
        pullDownBtn.addTarget(self, action: #selector(pullDownBtnTapped), for: .touchUpInside)
        let navigationItem = self.navigationItem
        //네비게이션 바의 오른쪽에 pullDownBtn을 추가한다.
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: pullDownBtn)
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
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
                let nextVC = self.storyboard?.instantiateViewController(identifier: "QuestionPostingAddViewController") as! QuestionPostingAddViewController
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
                MessageRoomManager.createMessageRoom(postID: self.postID!, userID: self.postDetail!.userID!){ result in
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
    
    @IBAction func commentAddBtnTapped(_ sender: UIButton) {
        let alert = UIAlertController(title: "알림", message: "댓글 작성이 완료되었습니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default){_ in
            var params: Parameters = [:]
            params["content"] = self.commentAddTf.text!
            params["parentId"] = "null"
            params["postId"] = self.postDetail!.postID
            
            CommentManager.postComment(postID: self.postDetail!.postID, params: params){res in
                switch res{
                case .success(let data):
                    print(data)
                    CommentManager.readComment(postID: self.postDetail!.postID) { result in
                        print("댓글 조회 리퀘 날림")
                        switch result{
                        case .success(let comments):
                            print(comments)
                            self.comments = comments
                            self.tableView.reloadData()
                        case .failure(let error):
                            print("Error: \(error)")
                        }
                    }
                case .failure(let err):
                    print(err)
                }
            }
        }
        alert.addAction(okAction)
        
        present(alert, animated: true)
    }
}

// MARK: - 테이블 뷰 설정
extension QuestionPostingDetailViewController: UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰 영역을 "게시글, 채택 답변 ,댓글" 총 3개로 분리
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    //두번째 section 일때 section 이름을 "댓글"이라고 출력
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 1{
            return "채택된 답변"
        }
        else if section == 2{
            return "댓글"
        }
        else{
            return ""
        }
    }
    
    //댓글을 한번에 몇개나 표시할 것인지 설정
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 2 {return  comments.count}
        else {return 1}
    }
    
    //table view에 표시할 내용을 정의한다.
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //section 0일 경우 게시글을 표시
        if indexPath.section == 0{
            let cell = tableView.dequeueReusableCell(withIdentifier: "QuestionPostingContentTableViewCell", for: indexPath) as! QuestionPostingContentTableViewCell
            
            cell.titleLabel.text = postDetail?.title
            cell.contentLabel.text = postDetail?.content
            cell.likeCntLabel.text = "\(Int(postDetail!.likeCount))"
            cell.tagLabel.text = ConvertTag.convert2(tag: postDetail!.tagType)
            cell.postid = postID
            
            cell.img0.isHidden = true
            cell.img1.isHidden = true
            cell.img2.isHidden = true
            cell.img3.isHidden = true
            cell.img4.isHidden = true
            
            Task {
                do {
                    let data = try await BadgeManager.getBadge(userid: (postDetail?.userID)!){ res in
                        switch res{
                        case.success(let suc):
                            
                            if let d = suc{
                                if d.count > 0{
                                    cell.badgeImg.isHidden = false
                                }
                            }
                            else{
                                cell.badgeImg.isHidden = true
                            }
                        case .failure(let err):
                            print(err)
                            cell.badgeImg.isHidden = true
                        }
                    }
                }
            }
            
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
            let cell = tableView.dequeueReusableCell(withIdentifier: "AdoptCommentTableViewCell", for: indexPath) as! AdoptCommentTableViewCell
            cell.contentLabel.isHidden = true
            cell.nameLabel.isHidden = true
            cell.dateLabel.isHidden = true
            cell.resLabel.isHidden = false
            
            cell.userImg.isHidden = true
            cell.badgeImg.isHidden = true
            cell.likeCnt.isHidden = true
            cell.likeBtn.isHidden = true
            cell.replyBtn.isHidden = true
            
            for comment in comments{
                if comment.isAdopted != nil{
                    cell.resLabel.isHidden = true
                    cell.replyBtn.isHidden = false
                    cell.contentLabel.isHidden = false
                    cell.nameLabel.isHidden = false
                    cell.replyBtn.isHidden = false
                    cell.userImg.isHidden = false
                    cell.badgeImg.isHidden = false
                    cell.nameLabel.isHidden = false
                    cell.dateLabel.isHidden = false
                    
                    cell.contentLabel.text = comment.content
                    cell.nameLabel.text = comment.name
                    cell.commentID = comment.commentID
                    cell.childComments = comment.children
                    cell.dateLabel.text = "00/00"
                    cell.resLabel.isHidden = true
                    
                    Task {
                        do {
                            let data = try await BadgeManager.getBadge(userid: comment.userID){ res in
                                switch res{
                                case.success(let suc):
                                    
                                    if let d = suc{
                                        if d.count > 0{
                                            cell.badgeImg.isHidden = false
                                        }
                                    }
                                    else{
                                        cell.badgeImg.isHidden = true
                                    }
                                case .failure(let err):
                                    print(err)
                                    cell.badgeImg.isHidden = true
                                }
                            }
                        }
                    }
                    
                    break
                }
            }
            return cell
        }
        else{
            //section 1일 경우 댓글을 표시
            let cell = tableView.dequeueReusableCell(withIdentifier: "QuestionPostingCommentTableViewCell", for: indexPath) as! QuestionPostingCommentTableViewCell
            cell.delegate = self
            cell.adoptDelegate = self

            var flag = false
            for comment in comments{
                if comment.isAdopted != nil{
                    flag = true;
                    break
                }
            }
            
            let nickname = UserDefaults.standard.string(forKey: "nickname")!
            if nickname != postDetail?.nickname{
                flag = true
            }
            
            print(indexPath.section)
            print("show section2")
            cell.contentLabel.text = comments[indexPath.row].content
            cell.nameLabel.text = comments[indexPath.row].name
            cell.commentID = comments[indexPath.row].commentID
            cell.childComments = comments[indexPath.row].children
            cell.dateLabel.text = "00/00"
            if(flag){ //채택된 답변이 있을 경우, 또는 게시글을 작성한 사람이 아닐 경우
                cell.adoptBtn.isHidden = true
            }
            else{ //채택된 답변이 없을 경우
                cell.adoptBtn.isHidden = false
            }

//            cell.dateLabel = String(comments_p[indexPath.row].c)
//            cell.likeCnt
            
            if(!isManager){
                cell.deleteBtn.isHidden = true
            }
            if(comments[indexPath.row].commentID == reportedCommentID){
                cell.backgroundColor = .red
            }

            
            Task {
                do {
                    let data = try await BadgeManager.getBadge(userid: comments[indexPath.row].userID){ res in
                        switch res{
                        case.success(let suc):
                            
                            if let d = suc{
                                if d.count > 0{
                                    cell.badgeImg.isHidden = false
                                }
                            }
                            else{
                                cell.badgeImg.isHidden = true
                            }
                        case .failure(let err):
                            print(err)
                            cell.badgeImg.isHidden = true
                        }
                    }
                }
            }
            

            return cell
        }
    }
}


// MARK: - half modal로 뷰 컨트롤러 show
extension QuestionPostingDetailViewController: QuestionReplyBtnDelegate, AdoptBtnDelegate, AdoptReplyBtnDelegate{
    func adoptBtnTapped(in cell: QuestionPostingCommentTableViewCell) {
        print("답글 채택 버튼 눌림")
        BoardManager.adoptCommnet(postID: postID!, commentID: cell.commentID!) { [self] res in
            switch res{
            case.success(_):
                print("답글 채택 성공")
                getComment(postID: postID!) {comments in
                    self.comments = comments
                    self.tableView.reloadData()
                    print(comments)
                }
            case .failure(let err):
                print("답글 채택 실패")
            }
        }
        
    }
    
    func replyBtnTapped(in cell: AdoptCommentTableViewCell) {
        print("딥글 버튼 눌림!!!!!!!!!")
        let replyVC = PostingReplyViewController()
        replyVC.reportedID = reportedCommentID
        replyVC.isManager = self.isManager
        replyVC.modalPresentationStyle = .pageSheet
        replyVC.view.backgroundColor = .white
        replyVC.array = cell.childComments ?? []
        for i in comments{
            if i.commentID == cell.commentID{
                replyVC.reportedID = reportedCommentID
                replyVC.isManager = self.isManager
                replyVC.parent_commentId = i.commentID
                replyVC.comment = i
                replyVC.postId = postDetail!.postID
                break
            }
        }
        
        if let sheet = replyVC.sheetPresentationController {
            sheet.detents = [.medium(), .large()]
            sheet.delegate = self
            sheet.prefersGrabberVisible = true
            sheet.largestUndimmedDetentIdentifier = .none
        }
        present(replyVC, animated: true, completion: nil)
        
    }
    
    
    func replyBtnTapped(in cell: QuestionPostingCommentTableViewCell){
        print("딥글 버튼 눌림")
        let replyVC = PostingReplyViewController()
        replyVC.reportedID = reportedCommentID
        replyVC.isManager = self.isManager
        print("qpVC: \(isManager)")
        replyVC.modalPresentationStyle = .pageSheet
        replyVC.view.backgroundColor = .white
        replyVC.array = cell.childComments ?? []
        for i in comments{
            if i.commentID == cell.commentID{
                replyVC.reportedID = reportedCommentID
                replyVC.isManager = self.isManager
                replyVC.parent_commentId = i.commentID
                replyVC.comment = i
                replyVC.postId = postDetail!.postID
                break
            }
        }
        
        if let sheet = replyVC.sheetPresentationController {
            sheet.detents = [.medium(), .large()]
            sheet.delegate = self
            sheet.prefersGrabberVisible = true
            sheet.largestUndimmedDetentIdentifier = .none
        }
        present(replyVC, animated: true, completion: nil)
    }
}

// MARK: - 답글 누르면 half modal 표시 관련 extension
extension QuestionPostingDetailViewController: UISheetPresentationControllerDelegate {
    func sheetPresentationControllerDidChangeSelectedDetentIdentifier(_ sheetPresentationController: UISheetPresentationController) {
        //크기 변경 됐을 경우
        print(sheetPresentationController.selectedDetentIdentifier == .large ? "large" : "medium")
    }
}

