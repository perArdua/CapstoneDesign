//
//  MyPostingViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/01.
//

import UIKit

class MyPostingViewController: UIViewController {
    
    var generalArr: [PostListContent] = []
    var bookArr: [PostListContent] = []
    var qnaArr: [PostListContent] = []
    var studyArr: [PostListContent] = []
    var ary: [Int] = [1, 2, 3, 4]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "작성한 게시글"
        getData()
    }
    
    func getData(){
        BoardManager.showMyPosting{ [weak self] result in
            switch result{
            case .success(let posts):
                print("success")
                for post in posts{
                    if(post.boardSimpleResponse.boardType == "Free"){
                        self?.generalArr.append(post)
                    }else if(post.boardSimpleResponse.boardType == "Book"){
                        self?.bookArr.append(post)
                    }else if(post.boardSimpleResponse.boardType == "Question"){
                        self?.qnaArr.append(post)
                    }else if(post.boardSimpleResponse.boardType == "Study"){
                        self?.studyArr.append(post)
                    }
                }
            case .failure(let error):
                print(error)
            }
            
        }
    }
    

    @IBAction func freeBtnTapped(_ sender: UIButton) {
        let nextSB = UIStoryboard(name: "GeneralPostingViewController", bundle: nil)
        let nextVC = nextSB.instantiateViewController(identifier: "GeneralPostingViewController") as! GeneralPostingViewController
        nextVC.array = generalArr
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    
    @IBAction func qnaBtnTapped(_ sender: Any) {
        let nextSB = UIStoryboard(name: "QuestionPostingViewController", bundle: nil)
        let nextVC = nextSB.instantiateViewController(identifier: "QuestionPostingViewController") as! QuestionPostingViewController
        nextVC.array = qnaArr
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    @IBAction func bookBtnTapped(_ sender: Any) {
        let nextSB = UIStoryboard(name: "BookStoreNavigationController", bundle: nil)
        let nextVC = nextSB.instantiateViewController(identifier: "BookStoreViewController") as! BookStoreViewController
        nextVC.array = bookArr
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    @IBAction func studyBtnTapped(_ sender: Any) {
        let nextSB = UIStoryboard(name: "StudyPostingViewController", bundle: nil)
        let nextVC = nextSB.instantiateViewController(identifier: "StudyPostingViewController") as! StudyPostingViewController
        nextVC.array = studyArr
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    
}
