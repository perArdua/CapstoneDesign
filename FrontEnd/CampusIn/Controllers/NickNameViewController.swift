//
//  NickNameViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/29.
//

import UIKit

class NickNameViewController: UIViewController, UITextFieldDelegate {

    
    @IBOutlet weak var warningLabel: UILabel!
    @IBOutlet weak var nickNameTF: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        nickNameTF.becomeFirstResponder()
        nickNameTF.delegate = self
        setUpTF()
        NotificationCenter.default.addObserver(self, selector: #selector(textDidChange(_:)), name: UITextField.textDidChangeNotification, object: nickNameTF)
    }
    
    //MARK: - text field setup
    func setUpTF(){
        nickNameTF.borderStyle = .none
        let border = CALayer()
        border.frame = CGRect(x: 0, y: nickNameTF.frame.size.height - 10, width: nickNameTF.frame.width, height: 2)
        border.borderWidth = 1
        border.backgroundColor = UIColor.blue.cgColor
        nickNameTF.layer.addSublayer(border)
    }
    
    //MARK: - 텍스트 필드 변경 시 이벤트 처리
    @objc private func textDidChange(_ notification: Notification) {
            if let textField = notification.object as? UITextField {
                if let text = textField.text {

                    if text.count < 2 {
                        warningLabel.text = "2글자 이상 입력해주세요"
                        warningLabel.textColor = .systemRed

                    }
                    else if text.count > 7{
                        warningLabel.text = "8글자 이하로 입력해주세요"
                        warningLabel.textColor = .systemRed
                    }
                    else if isValidNickName(nickName: text) {
                        warningLabel.text = "사용 가능한 닉네임입니다."
                        warningLabel.textColor = .systemGreen
                    }
                    else{
                        warningLabel.text = "닉네임 생성 규칙을 지켜주세요"
                        warningLabel.textColor = .systemRed
                    }
                }
            }
        }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    //MARK: - 유효한 닉네임인지 검사
    func isValidNickName(nickName: String?) -> Bool {
        guard nickName != nil else { return false }

        let nickRegEx = "[가-힣A-Za-z0-9]{2,7}"
        let pred = NSPredicate(format:"SELF MATCHES %@", nickRegEx)
        return pred.evaluate(with: nickName)
    }
    
    //MARK: - 완료 버튼 클릭 시 이벤트 구현
    @IBAction func doneBtnTapped(_ sender: UIButton) {
        let defaults = UserDefaults.standard
            if isValidNickName(nickName: nickNameTF.text) {
                UserManager.setNickname(nickName: String(nickNameTF.text!)) { result in
                    switch result {
                    case .success(let id):
                        defaults.set(String(self.nickNameTF.text!), forKey: "nickname")
                        defaults.set(id, forKey: "userId") // id 값을 UserDefaults에 저장
                        print("nickname setup complete, \(defaults.value(forKey: "nickname")), userId: \(id)")
                        let alert = UIAlertController(title: "알림", message: "닉네임 설정이 완료되었습니다.", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "확인", style: .default) { action in
                            self.dismiss(animated: false, completion: nil)
                        })
                        self.present(alert, animated: true, completion: nil)
                    case .failure:
                        print("nickname setting fail")
                        let alert = UIAlertController(title: "오류", message: "서버 에러 발생", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "확인", style: .default) { action in
                        })
                        self.present(alert, animated: true, completion: nil)
                    }
                }
            } else {
                let alert = UIAlertController(title: "오류", message: "닉네임 규칙을 지켜 다시 입력해주세요.", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "확인", style: .default) { action in
                })
                self.present(alert, animated: true, completion: nil)
            }
    }
    
}

