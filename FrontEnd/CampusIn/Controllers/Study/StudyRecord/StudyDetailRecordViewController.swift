//
//  StudyDetailRecordViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/06.
//

import UIKit

class StudyDetailRecordViewController: UIViewController {

    @IBOutlet weak var recordImage: UIImageView!
    var recordDetail: RecordPostListContent?
    
    @IBOutlet weak var detailTitle: UILabel!
    @IBOutlet weak var contentTV: UITextView!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var moreBtn: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        moreBtn.setTitle("", for: .normal)
        setup()
        detailTitle.text = "기록 상세"
        detailTitle.textColor = .white
    }
    
    override func viewWillAppear(_ animated: Bool) {
        setup()
        self.navigationController?.navigationBar.isHidden = false
    }

    func setup(){
        
        if let photo = recordDetail!.photo{
            recordImage.image = UIImage(base64: photo, withPrefix: false)
        }
        else{
            UIImage(systemName: "book.circle")
        }
        titleLabel.text = recordDetail?.title
        contentTV.text = recordDetail?.content
        if let dateArr = recordDetail?.createdAt {
            let dateStr: String = "\(dateArr[0]) / \(dateArr[1]) / \(dateArr[2])"
            dateLabel.text = dateStr
        }
        
        
    }
    
    func showMenu() {
        let menu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)

        let update = UIAlertAction(title: "수정하기", style: .default) { _ in
            print("update selected")
            let nextVC = self.storyboard!.instantiateViewController(withIdentifier: "StudyAddRecordViewController") as! StudyAddRecordViewController
            nextVC.isEditable = true
            nextVC.detail = self.recordDetail
            nextVC.tempImg = self.recordImage.image
            nextVC.postid = self.recordDetail?.postId
            nextVC.gId = self.recordDetail?.studyGroupID
            self.navigationController?.pushViewController(nextVC, animated: true)
        }
        
        let cancle = UIAlertAction(title: "취소", style: .cancel){ _ in
            print("cancle")
        }

        let delete = UIAlertAction(title: "삭제하기", style: .destructive) { _ in
            print("delete selected")

            let alert = UIAlertController(title: "알림", message: "삭제하시겠습니까?", preferredStyle: .alert)

            let no = UIAlertAction(title: "아니오", style: .cancel) { _ in
                print("no selected")
            }

            let ok = UIAlertAction(title: "예", style: .destructive) { _ in
                print("ok selected")
                self.deleteRecord(id: self.recordDetail!.postId)
            }
            
            alert.addAction(no)
            alert.addAction(ok)
            self.present(alert, animated: true, completion: nil) // UIAlertController를 화면에 표시
        }
        
        menu.addAction(cancle)
        menu.addAction(update)
        menu.addAction(delete)
        
        // UIAlertController를 화면에 표시
        self.present(menu, animated: true, completion: nil)
    }
    
    func deleteRecord(id: Int){
        BoardManager.deletePost(postID: id){
            self.navigationController?.popViewController(animated: true)
        }
    }

    @IBAction func moreBtnTapped(_ sender: Any) {
        showMenu()
    }
}
