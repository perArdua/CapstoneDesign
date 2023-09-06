//
//  StudyRecordViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/03.
//

import UIKit

class StudyRecordViewController: UIViewController {

    var array:[RecordPostListContent] = []
    var recordID: Int?
    
    var groupID: Int?
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var tableView: UITableView!
    
    var groupName: String = ""
    
    // MARK: - 스터디 기록 추가 버튼
    let addBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: 0, y: 0, width: 600, height: 600))
        btn.layer.masksToBounds = true
        btn.layer.cornerRadius = 30
        btn.tintColor = .white
        btn.backgroundColor = .gray
        btn.layer.shadowRadius = 6
        btn.layer.shadowOpacity = 0.3
        btn.setImage(UIImage(systemName: "pencil.tip.crop.circle"), for: .normal )
        btn.setPreferredSymbolConfiguration(.init(pointSize: 30, weight: .regular, scale: .default), forImageIn: .normal)
        
        return btn
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        // Do any additional setup after loading the view.
        self.navigationController?.navigationBar.isHidden = false
        getData()
        print("count")
        print(array.count)
        print(groupID)
        print(groupName)
        print("record id")
        print(UserDefaults.standard.value(forKey: "StudyRecord"))
    }
    
    override func viewWillAppear(_ animated: Bool) {
        view.addSubview(addBtn)
        
        getData()
        tableView.reloadData()
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        addBtn.frame = CGRect(x: view.frame.size.width - 75, y: view.frame.size.height - 150, width: 60, height: 60)
    }
    
    @objc func addBtnTapped(){
        print("스터디 기록 추가 버튼 tapped")
        let nextVC = storyboard?.instantiateViewController(withIdentifier: "StudyAddRecordViewController") as! StudyAddRecordViewController
        nextVC.gId = groupID
        navigationController?.pushViewController(nextVC, animated: true)
    }
    
    func getData() {
        StudyRecordManager.showPostbyGroupID(groupID: groupID!){[weak self] result in
            print("________________")
            print(self!.groupID)
            print(result)
            switch result{
            case .success(let posts):
                print("성공")
                self?.array = posts
                DispatchQueue.main.async {
                    self?.tableView.reloadData()
                }
            case .failure(let error):
                print(error)
            }
        }
    }
    

}




extension StudyRecordViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return array.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "StudyRecordTableViewCell", for: indexPath) as! StudyRecordTableViewCell
        cell.selectionStyle = .none
        cell.menuBtnTappedClosure = { [weak self] in
            self?.showMenu(for: indexPath)
            
        }
        print("title")
        print(array[indexPath.row].title)
        cell.titleLabel.text = array[indexPath.row].title
        let dateArr = array[indexPath.row].createdAt
        let dateStr: String = "\(dateArr[0]) / \(dateArr[1]) / \(dateArr[2])"
        cell.dateLabel.text = dateStr
        print(dateStr)


        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let nextVC = storyboard!.instantiateViewController(withIdentifier: "StudyDetailRecordViewController") as! StudyDetailRecordViewController
        nextVC.recordDetail = array[indexPath.row]
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    
    func showMenu(for indexPath: IndexPath) {
        print("메뉴 버튼 클릭 - indexPath: \(indexPath)")
        
        let menu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        let update = UIAlertAction(title: "수정하기", style: .default) { _ in
            print("update selected")
        }
        
        let delete = UIAlertAction(title: "삭제하기", style: .destructive) { _ in
            print("delete selected")
            
            let alert = UIAlertController(title: "알림", message: "삭제하시겠습니까?", preferredStyle: .alert)
            
            let no = UIAlertAction(title: "아니오", style: .cancel) { _ in
                print("no selected")
            }
            
            let ok = UIAlertAction(title: "예", style: .destructive) { _ in
                print("ok selected")
                
            }
            
            alert.addAction(no)
            alert.addAction(ok)
            self.present(alert, animated: true)
        }
        
        menu.addAction(update)
        menu.addAction(delete)
        present(menu, animated: true){
            let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissAlertController))
            menu.view.superview?.subviews[0].addGestureRecognizer(tapGesture)
        }
    }
    
    @objc func dismissAlertController(){
        self.dismiss(animated: true, completion: nil)
    }
}

