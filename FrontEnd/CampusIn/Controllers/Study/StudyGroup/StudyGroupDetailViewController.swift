//
//  StudyGroupDetailViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import UIKit

class StudyGroupDetailViewController: UIViewController {

    
    @IBOutlet weak var g4: UILabel!
    @IBOutlet weak var g3: UILabel!
    @IBOutlet weak var g2: UILabel!
    @IBOutlet var g1: UIView!
    @IBOutlet weak var groupHead: UILabel!
    @IBOutlet weak var creationDate: UILabel!
    @IBOutlet weak var groupName: UILabel!
    var groupInfo: MyStudyGroupDetails?
    var groupDetail: StudyGroupDetailContent?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "그룹 상세"
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        groupName.text = groupInfo?.studygroupName
        var cDate: String = "\(String((groupInfo?.createdAt[0])!))/\(String((groupInfo?.createdAt[1])!))/\(String((groupInfo?.createdAt[2])!))"
        creationDate.text = cDate
        //groupHead.text = groupInfo.head //그룹장
        setup()
        
    }
    
    func setup(){
        StudyGroupManager.showStudyGroupDetail(groupID: groupInfo!.id) { result in
            switch result{
            case.success(let contents):
                self.groupDetail = contents;
                print("leader")
                print(contents.leaderName)
                DispatchQueue.main.async {
                    self.groupHead.text = contents.leaderName
                }
            case.failure(let error):
                print(error)
            }
        }
    }
    
//MARK: - 그룹 탈퇴 버튼 액션 구현
    @IBAction func withDrawalBtnTapped(_ sender: UIButton) {
    }
    
}
