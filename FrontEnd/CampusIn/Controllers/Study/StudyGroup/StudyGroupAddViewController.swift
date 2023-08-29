//
//  StudyGroupAddViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import UIKit

class StudyGroupAddViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {

    @IBOutlet weak var addBtn: UIButton!
    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var pickerView: UIPickerView!
    let numbers = ["1", "2", "3", "4"]
    var cnt: Int? = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        
        pickerView.dataSource = self
        pickerView.delegate = self
        nameTextField.delegate = self
        setDisable()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.title = "그룹 추가"
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
           return 1
       }
       
   func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
       return numbers.count
   }
   
   // MARK: - UIPickerViewDelegate
   
   func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
       return numbers[row]
   }
   
   func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
       let selectedNumber = numbers[row]
       cnt = Int(selectedNumber)
       print(cnt)
   }
    
    func setDisable(){
        addBtn.isEnabled = false
        addBtn.backgroundColor = UIColor.lightGray
        addBtn.tintColor = UIColor.white
    }
    
    func setAble(){
        addBtn.isEnabled = true
        addBtn.backgroundColor = UIColor.systemGreen
        addBtn.tintColor = UIColor.white
    }
    
    //MARK: - 그룹 추가 액션 구현
    @IBAction func addBtnTapped(_ sender: UIButton) {
        guard let groupName = nameTextField.text, !groupName.isEmpty else {
            showAlert(message: "그룹 이름을 입력해주세요.")
            return
        }
        
        StudyGroupManager.createStudyGroup(title: groupName, size: Int(self.cnt!)){ [weak self] result in
            switch result {
            case .success:
                print("success")
                self?.navigationController?.popViewController(animated: true)
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    func showAlert(message: String) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default, handler: nil)
        alert.addAction(okAction)
        present(alert, animated: true, completion: nil)
    }
}

extension StudyGroupAddViewController: UITextFieldDelegate{
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let newText = (textField.text as NSString?)?.replacingCharacters(in: range, with: string) ?? ""
        print("textfied edit")
        if newText.isEmpty{
            setDisable()
        }
        else{
            setAble()
        }
        
        return true
    }
}
