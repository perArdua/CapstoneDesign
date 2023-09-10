//
//  ReportedPostViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import UIKit

class ReportedPostViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    
    var array :[ReportedPostListContent] = []
    var blockResult: BlockPostBody?
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = "신고된 게시글"
        tableView.dataSource = self
        tableView.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        getReportedPosts()
    }
    
    func getReportedPosts(){
        AdminManager.getReportedPosts(){[weak self] result in
            switch result{
            case.success(let list):
                DispatchQueue.main.async {
                    self!.array = list
                    self!.tableView.reloadData()
                }
            case.failure(let error):
                print(error)
            }
        }
            
        
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

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        return array.count
        return array.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ReportedPostTableViewCell") as! ReportedPostTableViewCell
        cell.titleLabel.text = array[indexPath.row].title
        cell.contentLabel.text = array[indexPath.row].content
        cell.dateLabel.text = "\(array[indexPath.row].createdAt[1]) " + "/ \(array[indexPath.row].createdAt[2])"
        cell.reportCntLabel.text = "\(array[indexPath.row].reportCount)"
        
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //let boardID = array[indexPath.row].boardSimpleResponse.boardID // 나중에 boardID로 수정
        let boardID = array[indexPath.row].boardSimpleResponse.boardID
        if(boardID == UserDefaults.standard.value(forKey: "Free") as! Int){
            let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
            getPostDetail(postID: array[indexPath.row].postID) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.array[indexPath.row].postID
                    
                    // postDetail을 다음 뷰 컨트롤러로 전달
                    nextVC.postDetail = postDetail
                    nextVC.isManager = true
                    // 화면 전환 코드를 여기에서 호출
                    self.navigationController?.pushViewController(nextVC, animated: true)
                }
            }
            //self.navigationController?.pushViewController(nextVC, animated: true)
        }else if(boardID == UserDefaults.standard.value(forKey: "Question") as! Int){
            let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "QuestionPostingDetailViewController") as! QuestionPostingDetailViewController
            getPostDetail(postID: array[indexPath.row].postID) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "QuestionPostingDetailViewController") as! QuestionPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.array[indexPath.row].postID
                    
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
            getPostDetail(postID: array[indexPath.row].postID) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "StudyPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "StudyPostingDetailViewController") as! StudyPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = self.array[indexPath.row].postID
                    
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
            AdminManager.blockPost(pID: self.array[indexPath.row].postID){[weak self] result in
                switch result{
                case.success(let res):
                    DispatchQueue.main.async {
                        self!.blockResult = res
                        if(self!.blockResult!.block != nil){
                            self!.array.remove(at: indexPath.row)
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
            AdminManager.unBlockPost(pID: self.array[indexPath.row].postID){[weak self] result in
                switch result{
                case.success(let res):
                    DispatchQueue.main.async {
                        self?.blockResult = res
                        if(self!.blockResult?.unblock != nil){
                            self!.array.remove(at: indexPath.row)
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
