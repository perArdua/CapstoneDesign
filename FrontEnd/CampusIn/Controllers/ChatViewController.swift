//
//  ChatViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/14.
//

import UIKit

class ChatViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate {

    var chatMessages: [ChattingMessage] = []
    var receivedRoomID: Int = 0
    
    @IBOutlet weak var inputBarView: UIView!
    
    @IBOutlet weak var inputBarBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var messageTextField: UITextField!
    @IBOutlet weak var sendBtn: UIButton!
    @IBOutlet weak var tableView: UITableView!
    let refreshControl = UIRefreshControl()
    var chatTitle: String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = chatTitle
        
        tableView.dataSource = self
        tableView.delegate = self
       
        messageTextField.delegate = self
    
        sendBtn.setTitle("", for: .normal)
        sendBtn.setTitleColor(.clear, for: .normal)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTapGesture(_:)))
        self.view.addGestureRecognizer(tapGesture)
        // TabBar 숨기기
        self.tabBarController?.tabBar.isHidden = true
        initRefreshControl()
        
    }
    override func viewWillAppear(_ animated: Bool) {
        reloadTableView()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)

        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
        }
        func initRefreshControl() {
        refreshControl.addTarget(self, action: #selector(handleRefreshControl), for: .valueChanged)
        tableView.refreshControl = refreshControl
    }
    //MARK: - 당겨서 새로고침 구현
    @objc func handleRefreshControl() {
        // 당겨서 새로고침 시 수행할 작업
        MessageManager.getAllMessages(roomID: receivedRoomID) { result in
            switch result {
            case .success(let messages):
                // 메시지 배열을 받아와서 처리
                self.chatMessages = messages.reversed()
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                    self.scrollToBottom()
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
        self.refreshControl.endRefreshing()
    }

    func scrollToBottom() {
        guard chatMessages.count > 1 else { return }

        let lastIndexPath = IndexPath(row: chatMessages.count - 1, section: 0)
        tableView.scrollToRow(at: lastIndexPath, at: .bottom, animated: true)
    }

    //MARK: - 테이블 뷰 새로고침 구현
    func reloadTableView(){
        MessageManager.getAllMessages(roomID: receivedRoomID) { result in
            switch result {
            case .success(let messages):
                // 메시지 배열을 받아와서 처리
                self.chatMessages = messages.reversed()
                for message in messages {
                    // 각 메시지에 대한 처리 로직을 구현
                    print("Message: \(message.content)")
                }
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                    self.scrollToBottom()
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }

    //MARK: -   메세지 입력창 탭 제스쳐 구현
    @objc func handleTapGesture(_ gesture: UITapGestureRecognizer) {
        messageTextField.resignFirstResponder()
    }

    //MARK: - textField editing 시작하면 키보드 올라옴
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        return true
        }

    //MARK: - 메세지 전송 버튼 클릭 시 이벤트 구현
    @IBAction func sendBtnTapped(_ sender: Any) {
        sendMessage()
        tableView.reloadData()
    }

    //MARK: - 키보드에서 return 버튼 눌렀을 때 이벤트 구현
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            textField.resignFirstResponder()
            return true
        }

    //MARK: - 키보드 나타나는 에니메이션 구현
    @objc func keyboardWillShow(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let keyboardFrame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect,
              let duration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval else {
            return
        }

        let keyboardHeight = keyboardFrame.size.height
        UIView.animate(withDuration: duration) {
            self.inputBarBottomConstraint.constant = keyboardHeight + 10
            self.view.layoutIfNeeded()
        }
    }

    //MARK: - 키보드 숨기는 에니메이션 구현
    @objc func keyboardWillHide(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let duration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval else {
            return
        }

        UIView.animate(withDuration: duration) {
            self.inputBarBottomConstraint.constant = 50
            self.view.layoutIfNeeded()
        }
    }
    //MARK: - 메세지 전송 함수 구현
    func sendMessage() {
        guard let message = messageTextField.text else {
            return
        }
        print("send message 진입")
        if message != "" {
            MessageManager.sendMessage(roomID: receivedRoomID, message: message) { result in
                print(result)
                switch result {
                case .success:
                    print("Message sent successfully.")
                    
                    DispatchQueue.main.async {
                        self.reloadTableView()
                        self.scrollToBottom()
                    }
                case .failure(let error):
                    print("Error sending message: \(error)")
                }
            }
        }
        
        messageTextField.text = ""
        self.scrollToBottom()
        //messageTextField.resignFirstResponder()
    }


    // UITableViewDataSource 메서드 구현
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return chatMessages.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "ChatTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! ChatTableViewCell

        let chatMessage = chatMessages[indexPath.row]
        cell.selectionStyle = .none
        cell.configure(with: chatMessage)


        return cell
    }


    // UITableViewDelegate 메서드 구현
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }


    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }


}

