//
//  StudyGroupManagementViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import UIKit

class StudyGroupManagementViewController: UIViewController {
    
    
    
    @IBOutlet weak var tableView: UITableView!
    var groupItems: [MyStudyGroupDetails] = []
    let addBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: 0, y: 0, width: 600, height: 600))
        btn.layer.masksToBounds = true
        btn.layer.cornerRadius = 30
        btn.tintColor = .white
        btn.backgroundColor = .gray
        btn.layer.shadowRadius = 6
        btn.layer.shadowOpacity = 0.3
        btn.setImage(UIImage(systemName: "plus.circle.fill"), for: .normal )
        btn.setPreferredSymbolConfiguration(.init(pointSize: 30, weight: .regular, scale: .default), forImageIn: .normal)
        
        return btn
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        // Do any additional setup after loading the view.
    }
    
    //MARK: - set tableView
    func prepareTableView() {
        StudyGroupManager.showMyStudyGroup { [weak self] result in
                switch result {
                case .success(let contents):
                    // Todo 목록을 성공적으로 가져온 경우
                    self?.groupItems = contents.reversed()
                    print("success")
                    print(self?.groupItems)
                    DispatchQueue.main.async {
                        self?.tableView.reloadData()
                    }
                case .failure(let error):
                    // Todo 목록을 가져오는데 실패한 경우
                    print("Error: \(error)")
                }
            }
        }
    
    override func viewWillAppear(_ animated: Bool) {
        view.addSubview(addBtn)
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        addBtn.frame = CGRect(x: view.frame.size.width - 75, y: view.frame.size.height - 200, width: 60, height: 60)
        self.navigationItem.title = "그룹 관리"
        prepareTableView()
    }
    
    @objc func addBtnTapped(){
        let addVC = storyboard!.instantiateViewController(withIdentifier: "StudyGroupAddViewController") as! StudyGroupAddViewController
        self.navigationController?.pushViewController(addVC, animated: true)
    }
    
}

extension StudyGroupManagementViewController: UITableViewDataSource, UITableViewDelegate {
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        print(groupItems.count)
        return groupItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "StudyGroupTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! StudyGroupTableViewCell
       
        let group = groupItems[indexPath.row]
        print("cell usere id")
        print(group.userId)
        cell.groupName.text = String(group.studygroupName)
        print(group.studygroupName)
        cell.groupSize.text = String(group.limitedMemberSize) + " / 4"
        //cell.selectionStyle = .none
       
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let detailVC = storyboard!.instantiateViewController(withIdentifier: "StudyGroupDetailViewController") as! StudyGroupDetailViewController
        detailVC.groupInfo = groupItems[indexPath.row]
        navigationController?.pushViewController(detailVC, animated: true)
    }


}


