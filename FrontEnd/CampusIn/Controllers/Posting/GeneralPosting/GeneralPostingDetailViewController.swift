//
//  GeneralPostingDetailViewController.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/09.
//

import UIKit

class GeneralPostingDetailViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
//    var manager = PostingManager()
    var array :[PostListContent] = []
    var postingIndex: Int?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        //UITableView의 footer 영역을 없애줌.
        tableView.sectionFooterHeight = 0
        tableView.sectionHeaderHeight = 25
        
//        array = manager.getPostings()
    }

}

// MARK: - 테이블 뷰 설정
extension GeneralPostingDetailViewController: UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰 영역을 "게시글, 댓글" 총 2개로 분리
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    //두번째 section 일때 section 이름을 "댓글"이라고 출력
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0{
            return ""
        }
        else{
            return "comment"
        }
    }
    
    //댓글을 한번에 몇개나 표시할 것인지 설정
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {return 1}
        else {return 5}
    }

    //table view에 표시할 내용을 정의한다.
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //section 0일 경우 게시글을 표시
        if indexPath.section == 0{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingContentTableViewCell", for: indexPath) as! GeneralPostingContentTableViewCell
            
            let temp = array[postingIndex!]
            cell.titleLabel.text = temp.title
            cell.contentLabel.text = temp.content
            
            cell.img0.isHidden = true
            cell.img1.isHidden = true
            cell.img2.isHidden = true
            cell.img3.isHidden = true
            cell.img4.isHidden = true
            
//            if let img = temp.img0 {
//                cell.img0.image = img
//                cell.img0.isHidden = false
//            }
//            if let img = temp.img1 {
//                cell.img1.image = img
//                cell.img1.isHidden = false
//            }
//            if let img = temp.img2 {
//                cell.img2.image = img
//                cell.img2.isHidden = false
//            }
//            if let img = temp.img3 {
//                cell.img3.image = img
//                cell.img3.isHidden = false
//            }
//            if let img = temp.img4 {
//                cell.img4.image = img
//                cell.img4.isHidden = false
//            }
            return cell
        }
        else{
            //section 0일 경우 댓글을 표시
            let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingCommentTableViewCell", for: indexPath) as! GeneralPostingCommentTableViewCell
            print(indexPath.section)
            print("show section2")
            return cell
        }
    }
}
