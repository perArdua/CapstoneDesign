//
//  MessageBoxViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/12.
//

import UIKit

class MessageBoxViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    
    @IBOutlet weak var tableView: UITableView!
    let names: [String] = ["이동현", "김태호", "정소영"]
    let previews: [String] = ["ㅎㅇㅎㅇ", "ㅋㅋㅋㅋㅋㅋ", "ㅂㅇㅂㅇ"]
    let dates: [String] = ["22/03/21 14:22", "22/03/21 14:22", "22/03/21 14:22"]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "쪽지함"
        tableView.delegate = self
        tableView.dataSource = self
        
        // TabBar 숨기기
        self.tabBarController?.tabBar.isHidden = true

    }
    
    
    //MARK: - data source
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return names.count
    }
    
    //테이블뷰 셀의 객체(인스턴스, 뷰)를 리턴
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "MessageBoxTableViewCell", for: indexPath) as! MessageBoxTableViewCell //메모리 절약을 위해 큐에 담아 재사용
        cell.nameLabel.text = names[indexPath.row]
        cell.previewLabel.text = previews[indexPath.row]
        cell.dateLabel.text = dates[indexPath.row]
        //기본 선택효과 제거
        cell.selectionStyle = .none
        
        return cell
    }
    
    //MARK: - normal event
    internal func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("location of selected cell: section-", indexPath.section)
        print("location of selected cell: row-", indexPath.row)
        //move to selected chatting
        let chatVC = storyboard!.instantiateViewController(withIdentifier: "ChatViewController") as! ChatViewController
        chatVC.chatTitle = names[indexPath.row]
        self.navigationController?.pushViewController(chatVC, animated: true)
    }
    
    let refreshControl = UIRefreshControl()
    func initRefreshControl(){
        tableView.refreshControl = refreshControl
        refreshControl.addTarget(self, action: #selector(handleRefreshControl), for: .valueChanged)//값이 변경 되었을 때 적용
        
    }
    @objc func handleRefreshControl(){
        print("refresh")
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            self.refreshControl.endRefreshing()
        }
    }

    
}
