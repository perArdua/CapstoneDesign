//
//  StudyGroupDetailViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import UIKit

class StudyGroupDetailViewController: UIViewController {

    
    @IBOutlet weak var deleteBtn: UIButton!
    @IBOutlet weak var g4: UILabel!
    @IBOutlet weak var g3: UILabel!
    @IBOutlet weak var g2: UILabel!
    @IBOutlet weak var g1: UILabel!
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
                    if(self.groupDetail?.currentMemberSize == 1){
                        self.g1.text = self.groupDetail?.memberList[0].memberName
                        self.g2.isHidden = true;
                        self.g3.isHidden = true;
                        self.g4.isHidden = true;
                    }else if(self.groupDetail?.currentMemberSize == 2){
                        self.g2.text = self.groupDetail?.memberList[1].memberName
                        self.g1.text = self.groupDetail?.memberList[0].memberName
                        self.g3.isHidden = true;
                        self.g4.isHidden = true;
                    }else if(self.groupDetail?.currentMemberSize == 3){
                        self.g3.text = self.groupDetail?.memberList[2].memberName
                        self.g2.text = self.groupDetail?.memberList[1].memberName
                        self.g1.text = self.groupDetail?.memberList[0].memberName
                        self.g4.isHidden = true;
                    }else{
                        self.g4.text = self.groupDetail?.memberList[3].memberName
                        self.g3.text = self.groupDetail?.memberList[2].memberName
                        self.g2.text = self.groupDetail?.memberList[1].memberName
                        self.g1.text = self.groupDetail?.memberList[0].memberName
                    }
                    self.groupHead.text = contents.leaderName
                    if(self.groupInfo?.userName == self.groupDetail?.leaderName){
                        self.deleteBtn.setTitle("그룹 삭제하기", for: .normal)
                    }
                }
            case.failure(let error):
                print(error)
            }
        }
    }
    
//MARK: - 그룹 탈퇴 버튼 액션 구현
    @IBAction func withDrawalBtnTapped(_ sender: UIButton) {
        StudyGroupManager.deleteStudyGroup(groupID: groupInfo!.id) { result in
            switch result{
            case .success():
                print("삭제 성공")
                let alert = UIAlertController(title: "알림", message: "삭제가 완료되었습니다.", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "확인", style: .default) { action in
                    self.navigationController?.popViewController(animated: true)
                })
                self.present(alert, animated: true, completion: nil)
            case .failure(let error):
                print(error)
            }
        }
    }
    
}
