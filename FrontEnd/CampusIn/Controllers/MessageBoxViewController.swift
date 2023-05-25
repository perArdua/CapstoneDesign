//
//  MessageBoxViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/12.
//

import UIKit

class MessageBoxViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    
    @IBOutlet weak var tableView: UITableView!
    
    var rooms: [MessageRoom] = []
    let refreshControl = UIRefreshControl()
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "쪽지함"
        tableView.delegate = self
        tableView.dataSource = self
        
        // TabBar 숨기기
        self.tabBarController?.tabBar.isHidden = true
        //initRefreshControl()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print("진입")
        MessageRoomManager.getAllMessageRooms(){
            [weak self] result in
            
            switch result {
            case .success(let messageRooms):
                // 데이터를 받아와서 배열에 저장
                self?.rooms = messageRooms
                DispatchQueue.main.async {
                    self?.tableView.reloadData()
                }
                print(messageRooms)
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    
    //MARK: - 쪽지방 지우기
    func deleteMessageRoom(completion: @escaping (Bool) -> Void) {
        MessageRoomManager.deleteMessageRoom(roomID: 11) { result in
            switch result {
            case .success:
                print("Message room deleted successfully.")
                completion(true)
            case .failure(let error):
                print("Error deleting message room: \(error)")
                completion(false)
            }
        }
    }
    
    //MARK: - 쪽지방 차단하기
    func blockMessageRoom(completion: @escaping (Bool) -> Void){
        MessageRoomManager.blockMessageRoom(roomID: 11) { result in
            switch result {
            case .success:
                print("block successfully")
                completion(true)
            case .failure(let error):
                print("Error deleting message room: \(error)")
            }
        }
    }

    
    //MARK: - data source
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return rooms.count
    }
    
    //테이블뷰 셀의 객체(인스턴스, 뷰)를 리턴
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "MessageBoxTableViewCell", for: indexPath) as! MessageBoxTableViewCell //메모리 절약을 위해 큐에 담아 재사용
        cell.nameLabel.text = rooms[indexPath.row].interlocutorNickname
        cell.previewLabel.text = rooms[indexPath.row].lastMessageContent
        var message = rooms[indexPath.row]
        var lastMessageSentTime = message.lastMessageSentTime
        var msgDate = "\(lastMessageSentTime[1])/\(lastMessageSentTime[2]) \(lastMessageSentTime[3]):\(lastMessageSentTime[4])"

        cell.dateLabel.text = msgDate
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
        chatVC.chatTitle = rooms[indexPath.row].interlocutorNickname
        chatVC.receivedRoomID = rooms[indexPath.row].messageRoomId
        self.navigationController?.pushViewController(chatVC, animated: true)
    }
    
//    func initRefreshControl() {
//        refreshControl.addTarget(self, action: #selector(handleRefreshControl), for: .valueChanged)
//        tableView.refreshControl = refreshControl
//    }
    
//    @objc func handleRefreshControl(){
//        print("refresh")
//        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
//            self.refreshControl.endRefreshing()
//        }
//    }

    //MARK: - 스와이프 삭제
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }

    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let deleteAction = UIContextualAction(style: .destructive, title: "삭제") { (action, view, completion) in
            let alert = UIAlertController(title: "경고", message: "정말 삭제하시겠습니까?", preferredStyle: .alert)
            let success = UIAlertAction(title: "확인", style: .default) { action in
                self.deleteMessageRoom { success in
                    if success {
                        print("Message room deleted successfully.")
                        self.rooms.remove(at: indexPath.row)
                        tableView.deleteRows(at: [indexPath], with: .fade)
                        completion(true)
                        // 비동기 작업이 성공한 경우에 수행할 로직
                    } else {
                        print("Failed to delete message room.")
                        completion(false)
                        // 비동기 작업이 실패한 경우에 수행할 로직
                    }
                }
            }
            let cancel = UIAlertAction(title: "취소", style: .cancel, handler: nil)
            alert.addAction(success)
            alert.addAction(cancel)
            self.present(alert, animated: true, completion: nil)

            
        }
        deleteAction.backgroundColor = .systemRed
        
        let otherAction = UIContextualAction(style: .normal, title: "차단") { (action, view, completion) in
            let alert = UIAlertController(title: "경고", message: "정말 차단하시겠습니까?", preferredStyle: .alert)
            let success = UIAlertAction(title: "확인", style: .default) { action in
                self.blockMessageRoom { success in
                    if success {
                        print("Message room block successfully.")
                        completion(true)
                        // 비동기 작업이 성공한 경우에 수행할 로직
                    } else {
                        print("Failed to block message room.")
                        completion(false)
                        // 비동기 작업이 실패한 경우에 수행할 로직
                    }
                }
            }
            let cancel = UIAlertAction(title: "취소", style: .cancel, handler: nil)
            
            alert.addAction(success)
            alert.addAction(cancel)
            self.present(alert, animated: true, completion: nil)
            }
        otherAction.backgroundColor = .systemBlue
        
        let configuration = UISwipeActionsConfiguration(actions: [deleteAction, otherAction])
        configuration.performsFirstActionWithFullSwipe = false
        return configuration
    }

    
}
