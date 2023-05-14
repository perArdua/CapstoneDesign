//
//  ChatViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/14.
//

import UIKit
import InputBarAccessoryView

class ChatViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate {

    var chatMessages: [ChatMessage] = []
    
    @IBOutlet weak var inputBarView: UIView!
    
    @IBOutlet weak var inputBarBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var messageTextField: UITextField!
    @IBOutlet weak var sendBtn: UIButton!
    @IBOutlet weak var tableView: UITableView!
    var chatTitle: String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = chatTitle
        
        setChatMessages()
        
        tableView.dataSource = self
        tableView.delegate = self
        
        messageTextField.delegate = self
        
        sendBtn.setTitle("", for: .normal)
        sendBtn.setTitleColor(.clear, for: .normal)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        
        // TabBar 숨기기
        self.tabBarController?.tabBar.isHidden = true

        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
        }
   
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        return true
        }
    
    @IBAction func sendBtnTapped(_ sender: Any) {
        sendMessage()
        tableView.reloadData()
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            textField.resignFirstResponder()
            return true
        }
        
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
    
    func sendMessage() {
        guard let message = messageTextField.text else {
            return
        }
        
        let msg: ChatMessage = ChatMessage(text: message, isSentByCurrentUser: true)
        chatMessages.append(msg)
        // 메시지 전송 로직 실행 후 텍스트 필드 초기화
        messageTextField.text = ""
        //messageTextField.resignFirstResponder()
        
        // ...
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
    
    func setChatMessages(){
        let c1: ChatMessage = ChatMessage(text: "hihihihihihi", isSentByCurrentUser: true)
        let c2: ChatMessage = ChatMessage(text: "hellohihhi", isSentByCurrentUser: false)
        chatMessages.append(c1)
        chatMessages.append(c2)
    }

}

