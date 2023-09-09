//
//  ReportedCommentViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import UIKit

class ReportedCommentViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
   
//    var commentList: [CommentDataContent] = [CommentDataContent(userID: 1, parentID: nil, commentID: 17, name: "ekrud99", content: "qerqwer", isAdopted: nil, boardId: 2, postId: 16, children: [], childrenSize: 0)]
    var commentList: [ReportedCommentDataContent] = []
    
    var postDetail: PostDetailContent?
    var blockResult: BlockCommentBody?
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()
//        var cmt: CommentDataContent = CommentDataContent(userID: 1, parentID: 1, commentID: 1, name: "ekrud99", content: "foweijfiowejf", children: [], childrenSize: 0)
//        commentList.append(cmt)
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
       getComment()
    }
    
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
    
    func getComment(){
        AdminManager.getReportedComments(){[weak self] result in
            switch result{
            case.success(let list):
                DispatchQueue.main.async {
                    self!.commentList = list
                    self!.tableView.reloadData()
                }
            case.failure(let error):
                print(error)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return commentList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ReportedCommentTableViewCell") as! ReportedCommentTableViewCell
        cell.commentLabel.text = commentList[indexPath.row].content
        cell.nicknameLabel.text = commentList[indexPath.row].name
        cell.reportCntLabel.text = "신고 횟수: \(commentList[indexPath.row].report!)"
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let boardID = commentList[indexPath.row].boardId // 나중에 boardID로 수정
        if(boardID == UserDefaults.standard.value(forKey: "Free") as! Int){
            let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
            getPostDetail(postID: commentList[indexPath.row].postId!) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.commentList[indexPath.row].postId
                    nextVC.reportedCommentID = self.commentList[indexPath.row].commentID
                    // postDetail을 다음 뷰 컨트롤러로 전달
                    nextVC.postDetail = postDetail
                    nextVC.isManager = true
                    // 화면 전환 코드를 여기에서 호출
                    self.navigationController?.pushViewController(nextVC, animated: true)
                }
            }
            //self.navigationController?.pushViewController(nextVC, animated: true)
        }else if(boardID == UserDefaults.standard.value(forKey: "Question") as! Int){
            print("this is question")
            let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "QuestionPostingDetailViewController") as! QuestionPostingDetailViewController
            getPostDetail(postID: commentList[indexPath.row].postId!) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "QuestionPostingDetailViewController") as! QuestionPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.commentList[indexPath.row].postId
                    print("prev postid: \(self.commentList[indexPath.row].postId)")
                    nextVC.reportedCommentID = self.commentList[indexPath.row].commentID
                    // postDetail을 다음 뷰 컨트롤러로 전달
                    nextVC.postDetail = postDetail
                    nextVC.isManager = true
                    // 화면 전환 코드를 여기에서 호출
                    self.navigationController?.pushViewController(nextVC, animated: true)
                }
            }
            
        }else if(boardID == UserDefaults.standard.value(forKey: "Study") as! Int){
            let nextSB = UIStoryboard(name: "StudyPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "StudyPostingDetailViewController") as! StudyPostingDetailViewController
            getPostDetail(postID: commentList[indexPath.row].postId!) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "StudyPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "StudyPostingDetailViewController") as! StudyPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.commentList[indexPath.row].postId
                    print("prev postid: \(self.commentList[indexPath.row].postId)")
                    nextVC.reportedCommentID = self.commentList[indexPath.row].commentID
                    // postDetail을 다음 뷰 컨트롤러로 전달
                    nextVC.postDetail = postDetail
                    nextVC.isManager = true
                    // 화면 전환 코드를 여기에서 호출
                    self.navigationController?.pushViewController(nextVC, animated: true)
                }
            }
        }
    }
    
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let deleteAction = UIContextualAction(style: .destructive, title: "댓글 삭제하기") { (action, view, completion) in
            // 삭제 동작을 여기에 추가
            print("삭제")
            AdminManager.blockComment(cID: self.commentList[indexPath.row].commentID){[weak self] result in
                switch result{
                case.success(let res):
                    DispatchQueue.main.async {
                        self!.blockResult = res
                        if(self!.blockResult!.block != nil){
                            self!.commentList.remove(at: indexPath.row)
                            tableView.reloadData()
                            //self!.showLabel(msg: "신고 완료")
                        }else{
                            //self!.showLabel(msg: "이미 신고된 댓글입니다. ")
                        }
                    }
                case.failure(let error):
                    print(error)
                }
            }
            completion(true)
        }
        
        let unreportAction = UIContextualAction(style: .normal, title: "신고 취소") { (action, view, completion) in
            print("unblock")
            AdminManager.unBlockComment(cID: self.commentList[indexPath.row].commentID){[weak self] result in
                switch result{
                case.success(let res):
                    DispatchQueue.main.async {
                        self?.blockResult = res
                        if(self!.blockResult?.unblock != nil){
                            self!.commentList.remove(at: indexPath.row)
                            tableView.reloadData()
                        }
                    }
                case.failure(let error):
                    print(error)
                }
            }
                completion(true)
            }
        
        let actions = [deleteAction, unreportAction]
        return UISwipeActionsConfiguration(actions: actions)
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100.0
    }


}
