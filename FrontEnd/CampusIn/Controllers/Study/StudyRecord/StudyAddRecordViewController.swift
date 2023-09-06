//
//  StudyAddRecordViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/06.
//

import UIKit
import Alamofire

class StudyAddRecordViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextViewDelegate {

    @IBOutlet weak var recordImg: UIImageView!
    @IBOutlet weak var explainLabel: UILabel!
    @IBOutlet weak var recordContentTV: UITextView!
    @IBOutlet weak var recordTitleTF: UITextField!
    @IBOutlet weak var titleLabel: UILabel!
    
    var postid: Int?
    var isEditable: Bool = false
    var tagIdList: [TagContent] = []
    var tagId: Int = -1;
    var detail: RecordPostListContent?
    var tempImg: UIImage?
    var gId: Int?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //self.navigationController?.navigationBar.isHidden = true
        recordContentTV.delegate = self
        explainLabel.isHidden = !recordContentTV.text.isEmpty
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        tapGesture.cancelsTouchesInView = false
        view.addGestureRecognizer(tapGesture)
        
        BoardManager.getTags { res in
            switch res{
            case .success(let tagData):
                self.tagIdList = tagData
                self.tagId = self.getTagId(tag: "Etc")
            case .failure(let err):
                print(err)
            }
        }
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        if(isEditable){
            titleLabel.text = "수정하기"
            setup()
        }else{
            titleLabel.text = "추가하기"
        }
    }
    
    func setup(){
        explainLabel.isHidden = true
        recordTitleTF.text = detail?.title

        recordContentTV.text = detail?.content
        recordImg.image = tempImg
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    func textViewDidChange(_ textView: UITextView) {
        explainLabel.isHidden = !recordContentTV.text.isEmpty
    }
    
    @objc func openAlbum() {
        let imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.sourceType = .photoLibrary
        present(imagePicker, animated: true, completion: nil)
    }
    
    // 이미지 선택이 완료되었을 때 호출되는 메서드
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true, completion: nil)
        
        if let selectedImage = info[.originalImage] as? UIImage {
            recordImg.image = selectedImage
        }
    }
    
    // 이미지 선택이 취소되었을 때 호출되는 메서드
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
    
    func getTagId(tag : String) -> Int{
        for item in tagIdList{
            if item.tagType == tag{
                return item.tagID
            }
        }
        return -1
    }
    

    @IBAction func doneBtnTapped(_ sender: UIButton) {
        print("done tapped")
        var params: Parameters = [:]
        if let titleData = recordTitleTF.text{
            params["title"] = titleData
        }else{
            let alert = UIAlertController(title: "경고", message: "제목을 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty title") }
            alert.addAction(okAction)
            present(alert, animated: true)
        }
        if let contentData = recordContentTV.text{
            params["content"] = contentData
        }else{
            let alert = UIAlertController(title: "경고", message: "내용을 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty contents")}
            alert.addAction(okAction)
            present(alert, animated: true)
        }
        
        params["studyGroupId"] = gId!
        
        var img_temp: [String] = []
        
        if let image = recordImg.image {
            img_temp.append(image.base64)
        }
        
        params["photos"] = img_temp
        
        if(!isEditable){
            
            let boardID: Int = UserDefaults.standard.value(forKey: "StudyRecord") as! Int
            postData(boardID: boardID, params: params)
            let alert = UIAlertController(title: "알림", message: "글쓰기가 완료되었습니다.", preferredStyle: .alert)
            let ok = UIAlertAction(title: "확인", style: .default){_ in self.navigationController?.popViewController(animated: true) }
            let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
            alert.addAction(cancel)
            alert.addAction(ok)
            present(alert, animated: true)
        }else{
            let boardID: Int = UserDefaults.standard.value(forKey: "StudyRecord") as! Int
            print("수정")
            print(postid)
          
            //print(params)
            DispatchQueue.main.async {
                self.patchData(postID: self.postid! , params: params)
            }
            
            
            let alert = UIAlertController(title: "알림", message: "수정이 완료되었습니다.", preferredStyle: .alert)
            let ok = UIAlertAction(title: "확인", style: .default){_ in
                self.navigationController?.popViewController(animated: true)
            }
            let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
            alert.addAction(cancel)
            alert.addAction(ok)
            present(alert, animated: true)
        }
        
        
        
    }
    @IBAction func cancleBtnTapped(_ sender: UIButton) {
        let alert = UIAlertController(title: "알림", message: "취소하시겠습니까?", preferredStyle: .alert)
        
        // 확인 버튼
        let confirmAction = UIAlertAction(title: "확인", style: .default) { (_) in
            self.navigationController?.popViewController(animated: true)
        }
        alert.addAction(confirmAction)
        
        // 취소 버튼
        let cancelAction = UIAlertAction(title: "취소", style: .cancel) { (_) in
        }
        alert.addAction(cancelAction)
        
        // 알림창 표시
        present(alert, animated: true, completion: nil)
    }
    func postData(boardID: Int, params: Parameters){
                BoardManager.createPost(boardID: boardID, tagID: tagId ,params: params)
    }

    func patchData(postID: Int, params:Parameters){
        BoardManager.updatePost(postID: postID, params: params)
    }

}
