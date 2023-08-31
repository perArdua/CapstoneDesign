//
//  SellBookViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/07.
//

import UIKit
import Alamofire

class SellBookViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextViewDelegate {

    @IBOutlet weak var mainTitleBabel: UILabel!
    @IBOutlet weak var bookImg: UIImageView!
    @IBOutlet weak var addImgBtn: UIButton!
    
    @IBOutlet weak var doneBtn: UIButton!
    @IBOutlet weak var tvPlaceholder: UILabel!
    @IBOutlet weak var bookInfoTV: UITextView!
    @IBOutlet weak var bookPriceTF: UITextField!
    @IBOutlet weak var bookNameTF: UITextField!
    var detail: PostDetailContent?
    var isEditable: Bool = false
    var titleString: String = "책 판매하기"
    var tempImg: UIImage?
    
    var tagIdList: [TagContent] = []
    var tagId: Int = -1;
    
    override func viewDidLoad() {
           super.viewDidLoad()
           addImgBtn.setTitle("", for: .normal)
           // 네비게이션 바 숨김
           self.navigationController?.navigationBar.isHidden = true
           bookInfoTV.delegate = self
           tvPlaceholder.isHidden = !bookInfoTV.text.isEmpty
           
           let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
           tapGesture.cancelsTouchesInView = false
           view.addGestureRecognizer(tapGesture)
           print(titleString)
           mainTitleBabel.text = titleString
           
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
           
           doneBtn.setTitle("", for: .normal)
           doneBtn.setImage(UIImage(systemName: "checkmark"), for: .normal)
           addImgBtn.addTarget(self, action: #selector(openAlbum), for: .touchUpInside)
           bookNameTF.borderStyle = .none
           bookPriceTF.borderStyle = .none
           bookPriceTF.keyboardType = .numberPad
           bookInfoTV.text = ""
           
           if(isEditable){
               setup()
           }
           
       }
       
       func setup(){
           tvPlaceholder.isHidden = true
           bookNameTF.text = detail?.title
           if let price = detail?.price {
               bookPriceTF.text = String(price)
           } else {
               bookPriceTF.text = ""
           }
           
           bookInfoTV.text = detail?.content
           bookImg.image = tempImg
       }
       
       @objc func dismissKeyboard() {
           view.endEditing(true)
       }
       
       func textViewDidChange(_ textView: UITextView) {
           tvPlaceholder.isHidden = !bookInfoTV.text.isEmpty
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
               bookImg.image = selectedImage
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
           if let titleData = bookNameTF.text{
               params["title"] = titleData
           }else{
               let alert = UIAlertController(title: "경고", message: "제목을 채워주세요", preferredStyle: .alert)
               let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty title") }
               alert.addAction(okAction)
               present(alert, animated: true)
           }
           if let contentData = bookInfoTV.text{
               params["content"] = contentData
           }else{
               let alert = UIAlertController(title: "경고", message: "내용을 채워주세요", preferredStyle: .alert)
               let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty contents")}
               alert.addAction(okAction)
               present(alert, animated: true)
           }
           
           if let priceData = bookPriceTF.text{
               let pdParseInt = Int(priceData)
               params["price"] = pdParseInt
           }else{
               let alert = UIAlertController(title: "경고", message: "가격을 입력해주세요", preferredStyle: .alert)
               let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty contents")}
               alert.addAction(okAction)
               present(alert, animated: true)
           }
           
           var img_temp: [String] = []
           
           if let image = bookImg.image {
               img_temp.append(image.base64)
           }
           
           params["photos"] = img_temp
           
           if(!isEditable){
               
               let boardID: Int = UserDefaults.standard.value(forKey: "Book") as! Int
               postData(boardID: boardID, params: params)
               let alert = UIAlertController(title: "알림", message: "글쓰기가 완료되었습니다.", preferredStyle: .alert)
               let ok = UIAlertAction(title: "확인", style: .default){_ in self.navigationController?.popViewController(animated: true) }
               let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
               alert.addAction(cancel)
               alert.addAction(ok)
               present(alert, animated: true)
           }else{
               let boardID: Int = UserDefaults.standard.value(forKey: "Book") as! Int
               print("수정")
               print(detail?.postID)
             
               //print(params)
               DispatchQueue.main.async {
                   self.patchData(postID: self.detail!.postID , params: params)
               }
               
               
               let alert = UIAlertController(title: "알림", message: "수정이 완료되었습니다.", preferredStyle: .alert)
               let ok = UIAlertAction(title: "확인", style: .default){_ in
                   if let firstViewController = self.navigationController?.viewControllers.first {
                       self.navigationController?.popToViewController(firstViewController, animated: true)
                   } }
               let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
               alert.addAction(cancel)
               alert.addAction(ok)
               present(alert, animated: true)
           }
           
           
           
       }
       
       func postData(boardID: Int, params: Parameters){
                   BoardManager.createPost(boardID: boardID, tagID: tagId ,params: params)
       }
       
       func patchData(postID: Int, params:Parameters){
           BoardManager.updatePost(postID: postID, params: params)
       }
       
       
       @IBAction func cancelBtnTapped(_ sender: UIButton) {
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
   }
