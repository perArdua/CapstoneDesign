//
//  StudyRecordViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/03.
//

import UIKit

class StudyRecordViewController: UIViewController {

    var array:[PostListContent]?
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        // Do any additional setup after loading the view.
    }
    


}

extension StudyRecordViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "StudyRecordTableViewCell", for: indexPath) as! StudyRecordTableViewCell
        
        cell.menuBtnTappedClosure = { [weak self] in
            self?.showMenu(for: indexPath)
            
        }
        
        cell.titleLabel.text = "\(indexPath.row)"
        cell.dateLabel.text = "\(indexPath.row)"
        
        return cell
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

