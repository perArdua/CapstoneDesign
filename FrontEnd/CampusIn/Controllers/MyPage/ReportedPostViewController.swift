//
//  ReportedPostViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import UIKit

class ReportedPostViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    var array :[PostListContent] = []
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = "신고된 게시글"
        tableView.dataSource = self
        tableView.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
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
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ReportedPostTableViewCell") as! ReportedPostTableViewCell
//        cell.titleLabel.text = array[indexPath.row].title
//        cell.contentLabel.text = array[indexPath.row].content
//        cell.dateLabel.text = "\(array[indexPath.row].createdAt[1]) " + "/ \(array[indexPath.row].createdAt[2])"
//        cell.reportCntLabel.text = array[indexPath.row].reportCnt
        cell.titleLabel.text = "tmp"
        cell.contentLabel.text = "tmp"
        cell.dateLabel.text = "9 / 10"
        cell.reportCntLabel.text = "10"
        
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //let boardID = array[indexPath.row].boardSimpleResponse.boardID // 나중에 boardID로 수정
        let boardID = 2
        if(boardID == UserDefaults.standard.value(forKey: "Free") as! Int){
            let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
            getPostDetail(postID: 17) { postDetail in
                // postDetail을 사용하여 다른 작업 수행
                DispatchQueue.main.async {
                    let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
                    let nextVC = nextSB.instantiateViewController(withIdentifier: "GeneralPostingDetailViewController") as! GeneralPostingDetailViewController
                    
                    // postID를 그대로 사용
                    nextVC.postID = 17
                    
                    // postDetail을 다음 뷰 컨트롤러로 전달
                    nextVC.postDetail = postDetail
                    // 화면 전환 코드를 여기에서 호출
                    self.navigationController?.pushViewController(nextVC, animated: true)
                }
            }
            //self.navigationController?.pushViewController(nextVC, animated: true)
        }else if(boardID == UserDefaults.standard.value(forKey: "Question") as! Int){
            let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "QuestionPostingDetailViewController") as! QuestionPostingDetailViewController
            self.navigationController?.pushViewController(nextVC, animated: true)
        }else if(boardID == UserDefaults.standard.value(forKey: "Study") as! Int){
            let nextSB = UIStoryboard(name: "StudyPostingViewController", bundle: nil)
            let nextVC = nextSB.instantiateViewController(withIdentifier: "StudyPostingDetailViewController") as! StudyPostingDetailViewController
            self.navigationController?.pushViewController(nextVC, animated: true)
        }
    }
    
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let deleteAction = UIContextualAction(style: .destructive, title: "댓글 삭제하기") { (action, view, completion) in
            // 삭제 동작을 여기에 추가
            print("삭제")
            completion(true)
        }
        
        return UISwipeActionsConfiguration(actions: [deleteAction])
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100.0
    }

}
