//
//  StudyPostingAddViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/08.
//

import UIKit
import BSImagePicker
import Photos
import Alamofire

class StudyPostingAddViewController: UIViewController, UITextViewDelegate {
    var postDetail: PostDetailContent?
    
    @IBOutlet weak var img0: UIImageView!
    @IBOutlet weak var img1: UIImageView!
    @IBOutlet weak var img2: UIImageView!
    @IBOutlet weak var img3: UIImageView!
    @IBOutlet weak var img4: UIImageView!
    
    @IBOutlet weak var stView0: UIStackView!
    @IBOutlet weak var stView1: UIStackView!
    @IBOutlet weak var stView2: UIStackView!
    @IBOutlet weak var stView3: UIStackView!
    @IBOutlet weak var stView4: UIStackView!
    
    @IBOutlet weak var titleTV: UITextView!
    @IBOutlet weak var contentTV: UITextView!
    
    @IBOutlet weak var tagLabel0: UILabel!
    @IBOutlet weak var tagLabel: UILabel!
    @IBOutlet weak var studyGroupLabel: UILabel!
    
    let tagData = ["선택하세요", "IT", "수학", "자연과학", "공학", "경제", "인문", "예체능", "기타"]
    var studyGroupData: [MyStudyGroupDetails] = []
    var studyGroupID : Int?
    
    //tag 버튼 누르면 나오는 피커뷰
    let tagPickerView = UIPickerView()
    let tagDoneView = UIView()
    let tagDoneBtn = UIButton(type: .system)
    
    //tag 버튼 누르면 나오는 피커뷰
    let studyPickerView = UIPickerView()
    let studyDoneView = UIView()
    let studyDoneBtn = UIButton(type: .system)
    
    
    var imgs : [UIImageView] = []
    var img_cnt = 0
    //태그 라벨에 들어갈 텍스트
    var tag: String?
    //스터디 그룹 라벨에 들어갈 텍스트
    var studyGroup: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setUpTagPickerView()
        setUpStudyPickerView()
    
        stView0.isHidden = true
        stView1.isHidden = true
        stView2.isHidden = true
        stView3.isHidden = true
        stView4.isHidden = true
        tagLabel0.isHidden = true
        
        if let detail = postDetail{
            titleTV.text = detail.title
            contentTV.text = detail.content
            img_cnt = detail.photoList.count
            print(img_cnt)
            
            if img_cnt >= 1 {
                img0.image = UIImage(base64: (postDetail?.photoList[0].content)!, withPrefix: false)
                imgs.insert(img0, at: 0)
                stView0.isHidden = false
            }
            if img_cnt >= 2 {
                img1.image = UIImage(base64: (postDetail?.photoList[1].content)!, withPrefix: false)
                imgs.insert(img1, at: 1)
                stView1.isHidden = false
            }
            if img_cnt >= 3{
                img2.image = UIImage(base64: (postDetail?.photoList[2].content)!, withPrefix: false)
                imgs.insert(img2, at: 2)
                stView2.isHidden = false
            }
            if img_cnt >= 4{
                img3.image = UIImage(base64: (postDetail?.photoList[3].content)!, withPrefix: false)
                imgs.insert(img3, at: 3)
                stView3.isHidden = false
            }
            if img_cnt >= 5{
                img4.image = UIImage(base64: (postDetail?.photoList[4].content)!, withPrefix: false)
                imgs.insert(img4, at: 4)
                stView4.isHidden = false
            }
        }else{
            titleTV.delegate = self
            titleTV.text = "제목"
            titleTV.textColor = UIColor.lightGray
            contentTV.delegate = self
            contentTV.text = "내용"
            contentTV.textColor = UIColor.lightGray
            
            imgs.insert(img0, at: 0)
            imgs.insert(img1, at: 1)
            imgs.insert(img2, at: 2)
            imgs.insert(img3, at: 3)
            imgs.insert(img4, at: 4)
        }
        
        StudyGroupManager.showMyStudyGroup { res in
            switch res{
            case .success(let data):
                print("나의 스터디 그룹 목록 불러오기 성공")
                for item in data{
                    print(item.studygroupName)
                }
                self.studyGroupData = data
            case .failure(let err):
                print("나의 스터디 그룹 목록 불러오기 성공")
                print(err)
            }
        }
    }
    
    // MARK: - tag의 pickerView UI 세팅
    func setUpTagPickerView(){
        tagPickerView.delegate = self
        tagPickerView.dataSource = self
        view.addSubview(tagPickerView)
        tagPickerView.translatesAutoresizingMaskIntoConstraints = false
        tagPickerView.backgroundColor = .white
        tagPickerView.layer.borderColor = UIColor.gray.cgColor
        tagPickerView.isHidden = true
        NSLayoutConstraint.activate([
        
            // Picker View 제약 조건
            tagPickerView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            tagPickerView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            tagPickerView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: 0),
            tagPickerView.heightAnchor.constraint(equalToConstant: 200)
        ])
        
        tagDoneView.backgroundColor = .systemGray5
        view.addSubview(tagDoneView)
        tagDoneView.translatesAutoresizingMaskIntoConstraints = false
        tagDoneView.isHidden = true
        NSLayoutConstraint.activate([
            tagDoneView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            tagDoneView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            tagDoneView.bottomAnchor.constraint(equalTo: tagPickerView.topAnchor, constant: 0),
            tagDoneView.heightAnchor.constraint(equalToConstant: 30)
        ])
        tagDoneView.addSubview(tagDoneBtn)
        
        tagDoneBtn.tintColor = .systemBlue
        tagDoneBtn.setTitle("완료", for: .normal)
        
        tagDoneBtn.translatesAutoresizingMaskIntoConstraints = false
        tagDoneBtn.addTarget(self, action: #selector(tagDoneBtnTapped), for: .touchUpInside)
        NSLayoutConstraint.activate([
            tagDoneBtn.trailingAnchor.constraint(equalTo: tagDoneView.trailingAnchor, constant: 0),
            tagDoneBtn.topAnchor.constraint(equalTo: tagDoneView.topAnchor, constant: 0),
            tagDoneBtn.bottomAnchor.constraint(equalTo: tagDoneView.bottomAnchor, constant: 0),
            tagDoneBtn.widthAnchor.constraint(equalToConstant: 80)
        ])
    }
    
    // MARK: - study group의 pickerView UI 세팅
    func setUpStudyPickerView(){
        studyPickerView.delegate = self
        studyPickerView.dataSource = self
        view.addSubview(studyPickerView)
        studyPickerView.translatesAutoresizingMaskIntoConstraints = false
        studyPickerView.backgroundColor = .white
        studyPickerView.layer.borderColor = UIColor.gray.cgColor
        studyPickerView.isHidden = true
        NSLayoutConstraint.activate([
        
            // Picker View 제약 조건
            studyPickerView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            studyPickerView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            studyPickerView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: 0),
            studyPickerView.heightAnchor.constraint(equalToConstant: 200)
        ])
        
        studyDoneView.backgroundColor = .systemGray5
        view.addSubview(studyDoneView)
        studyDoneView.translatesAutoresizingMaskIntoConstraints = false
        studyDoneView.isHidden = true
        NSLayoutConstraint.activate([
            studyDoneView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            studyDoneView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            studyDoneView.bottomAnchor.constraint(equalTo: studyPickerView.topAnchor, constant: 0),
            studyDoneView.heightAnchor.constraint(equalToConstant: 30)
        ])
        studyDoneView.addSubview(studyDoneBtn)
        
        studyDoneBtn.tintColor = .systemBlue
        studyDoneBtn.setTitle("완료", for: .normal)
        
        studyDoneBtn.translatesAutoresizingMaskIntoConstraints = false
        studyDoneBtn.addTarget(self, action: #selector(studyDoneBtnTapped), for: .touchUpInside)
        NSLayoutConstraint.activate([
            studyDoneBtn.trailingAnchor.constraint(equalTo: studyDoneView.trailingAnchor, constant: 0),
            studyDoneBtn.topAnchor.constraint(equalTo: studyDoneView.topAnchor, constant: 0),
            studyDoneBtn.bottomAnchor.constraint(equalTo: studyDoneView.bottomAnchor, constant: 0),
            studyDoneBtn.widthAnchor.constraint(equalToConstant: 80)
        ])
    }
                                      
    @IBAction func cancelBtnTapped(_ sender: UIButton) {
        
        let alert = UIAlertController(title: "알림", message: "글쓰기를 취소 하시겠습니까?", preferredStyle: .alert)
        let ok = UIAlertAction(title: "확인", style: .destructive){_ in self.dismiss(animated: true)
        }
        let cancel = UIAlertAction(title: "취소", style: .default, handler: nil)
        alert.addAction(ok)
        alert.addAction(cancel)
        present(alert, animated: true)
    }
    
    // MARK: - <##>x버튼을 눌러 사진 삭제시 동작
    @IBAction func imgDeleteBtnTapped(_sender: UIButton){
        let btn_number = _sender.titleLabel!.text!
        let number = Int(btn_number)!
        for i in number..<img_cnt - 1{
            imgs[i].image = imgs[i + 1].image
        }
        if img_cnt == 1{
            stView0.isHidden = true
        }
        if img_cnt == 2{
            stView1.isHidden = true
        }
        if img_cnt == 3{
            stView2.isHidden = true
        }
        if img_cnt == 4{
            stView3.isHidden = true
        }
        if img_cnt == 5{
            stView4.isHidden = true
        }
        img_cnt -= 1
        print("img_cnt: \(img_cnt)")
    }
    
    // MARK: - 사진 추가 버튼 누르기
    @IBAction func photoAlbumBtnTapped(_ sender: UIButton) {
        print("tkw")
        let imagePicker = ImagePickerController()
        print("tkw2")
        imagePicker.settings.selection.max = 5 - img_cnt
        imagePicker.settings.fetch.assets.supportedMediaTypes = [.image]
        print("tkw3")
        presentImagePicker(imagePicker, select: { (asset) in
            //이미지 피커뷰에서 사진을 선택시 동작을 정의
        }, deselect: { (asset) in
            //이미지 피커뷰에서 사진을 선택해제시 동작을 정의
        }, cancel: { (assets) in
            //이미지 피커뷰를 나갈때 동작을 정의
        }, finish: { (assets) in
            //사진 선택 완료시 동작을 정의
            if assets.count != 0 {
                for i in 0..<assets.count {
                    //BSImagePicker의 경우 사진을 PHAsset 형식으로 반환하기 때문에 UIImage 형식으로 바꾸어주어야 함
                    print(assets.count)
                    let temp_img = self.getAssetThumbnail(asset: assets[i])
                    self.imgs[self.img_cnt].image = temp_img
                    if self.img_cnt == 0{self.stView0.isHidden = false}
                    if self.img_cnt == 1{self.stView1.isHidden = false}
                    if self.img_cnt == 2{self.stView2.isHidden = false}
                    if self.img_cnt == 3{self.stView3.isHidden = false}
                    if self.img_cnt == 4{self.stView4.isHidden = false}
                    self.img_cnt += 1
                }
            }
        })
    }
    // PHAsset 형식으로 반환하기 때문에 UIImage 형식으로 바꾸어주는 함수
    func getAssetThumbnail(asset: PHAsset) -> UIImage {
        let manager = PHImageManager.default()
        let option = PHImageRequestOptions()
        var thumbnail = UIImage()
        option.isSynchronous = true
        manager.requestImage(for: asset, targetSize: CGSize(width: 100.0, height: 100.0), contentMode: .aspectFit, options: option, resultHandler: {(result, info)->Void in
            thumbnail = result!
        })
        return thumbnail
    }
    
    
    
    @IBAction func doneBtnTapped(_ sender: UIButton) {
        print("done tapped")
        var params: Parameters = [:]
        
        if let titleData = titleTV.text{
            params["title"] = titleData
        }else{
            let alert = UIAlertController(title: "경고", message: "제목을 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty title") }
            alert.addAction(okAction)
            present(alert, animated: true)
        }
        if let contentData = contentTV.text, let tag = tagLabel.text, let studyGroup = studyGroupLabel.text{
            params["content"] = contentData
            //api 받으면 prameter에 태그, 스터디 그룹 넣어주기
            params["studyGroupId"] = studyGroupID
        }else{
            let alert = UIAlertController(title: "경고", message: "내용을 모두 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in print("empty contents")}
            alert.addAction(okAction)
            present(alert, animated: true)
        }
    
        var img_temp : [String] = []
        print("img_cnt : \(img_cnt)")
        for i in 0..<img_cnt{
            img_temp.append(imgs[i].image!.base64)
        }
        params["photos"] = img_temp
        
        if let detail = postDetail{
            patchData(postID: detail.postID, params: params)
            let alert = UIAlertController(title: "알림", message: "수정이 완료되었습니다.", preferredStyle: .alert)
            let ok = UIAlertAction(title: "확인", style: .default){_ in self.dismiss(animated: true) }
            let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
            alert.addAction(cancel)
            alert.addAction(ok)
            present(alert, animated: true)
        }
        else{
            postData(boardID: BoardManager.getBoardID(boardName: "Study"), params: params)
            let alert = UIAlertController(title: "알림", message: "글쓰기가 완료되었습니다.", preferredStyle: .alert)
            let ok = UIAlertAction(title: "확인", style: .default){_ in self.dismiss(animated: true) }
            alert.addAction(ok)
            present(alert, animated: true)
        }
    }
   
    func postData(boardID: Int, params: Parameters){
        BoardManager.createPost(boardID: boardID, params: params)
    }
    
    func patchData(postID: Int, params:Parameters){
        BoardManager.updatePost(postID: postID, params: params)
    }
    
    @IBAction func tagBtnTapped(_ sender: UIButton) {
        tagPickerView.reloadAllComponents()
        tagPickerView.isHidden = false
        tagDoneView.isHidden = false
        
        tagPickerView.alpha = 0.0
        tagDoneView.alpha = 0.0
        UIView.animate(withDuration: 0.3) {
            self.tagPickerView.alpha = 1.0
            self.tagDoneView.alpha = 1.0
        }
    }
    
    @IBAction func studyBtnTapped(_ sender: Any) {
        studyPickerView.reloadAllComponents()
        studyPickerView.isHidden = false
        studyDoneView.isHidden = false
        
        studyPickerView.alpha = 0.0
        studyDoneView.alpha = 0.0
        UIView.animate(withDuration: 0.3) {
            self.studyPickerView.alpha = 1.0
            self.studyDoneView.alpha = 1.0
        }
    }
    
    @objc func tagDoneBtnTapped(){
        tagPickerView.isHidden = true
        tagDoneView.isHidden = true
        
        if let tagText = tag{
            if tagText == "선택하세요"{
                tagLabel.text = nil
                tagLabel0.isHidden = true
            }
            else{
                tagLabel0.isHidden = false
                tagLabel.text = tagText
            }
        }
        else{
            tagLabel.text = nil
            tagLabel0.isHidden = true
        }
                
        UIView.animate(withDuration: 0.3) {
            self.tagPickerView.alpha = 0.0
            self.tagDoneView.alpha = 0.0
        }
    }
    
    @objc func studyDoneBtnTapped(){
        studyPickerView.isHidden = true
        studyDoneView.isHidden = true
        
        if let studyGroupText = studyGroup{
            if studyGroupText == "선택하세요"{
                studyGroupLabel.text = nil
            }
            else{
                studyGroupLabel.text = studyGroupText
            }
        }
        else{
            studyGroupLabel.text = nil
        }
                
        UIView.animate(withDuration: 0.3) {
            self.studyPickerView.alpha = 0.0
            self.studyDoneView.alpha = 0.0
        }
    }
    
    
    
    //MARK: - title textView의 place holder 기능
    func textViewDidBeginEditing(_ textView: UITextView) {
        if titleTV.textColor == UIColor.lightGray {
            titleTV.text = nil
            titleTV.textColor = UIColor.black
        }
        if contentTV.textColor == UIColor.lightGray {
            contentTV.text = nil
            contentTV.textColor = UIColor.black
        }
    }

    func textViewDidEndEditing(_ textView: UITextView) {
        if titleTV.text.isEmpty {
            titleTV.text = "제목"
            titleTV.textColor = UIColor.lightGray
        }
        if contentTV.text.isEmpty {
            contentTV.text = "내용"
            contentTV.textColor = UIColor.lightGray
        }
    }

}

extension StudyPostingAddViewController: UIPickerViewDelegate, UIPickerViewDataSource{
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == tagPickerView{
            return tagData.count
        }
        else{
            return studyGroupData.count
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == tagPickerView{
            return tagData[row]
        }
        else{
            return studyGroupData[row].studygroupName
        }
    }

    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == tagPickerView{
            self.tag = tagData[row]
        }
        else{
            self.studyGroup = studyGroupData[row].studygroupName
            self.studyGroupID = studyGroupData[row].id
        }
    }
    
}
