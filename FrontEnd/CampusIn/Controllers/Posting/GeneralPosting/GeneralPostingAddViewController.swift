//
//  GeneralPostingAddViewController.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/09.
//

import UIKit
//사진 다중 선택을 지원하는 imagePicker 오픈소스
import BSImagePicker
import Photos
import Alamofire

class GeneralPostingAddViewController: UIViewController, UITextViewDelegate{
    
    
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
    var imgs : [UIImageView] = []
    var img_cnt = 0
//    var post : GeneralPostingContent!
//    var PM = PostingManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
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
        
        stView0.isHidden = true
        stView1.isHidden = true
        stView2.isHidden = true
        stView3.isHidden = true
        stView4.isHidden = true
        
        
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
        let imagePicker = ImagePickerController()
        imagePicker.settings.selection.max = 5 - img_cnt
        imagePicker.settings.fetch.assets.supportedMediaTypes = [.image]
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
        if let contentData = contentTV.text{
            params["content"] = contentData
        }else{
            let alert = UIAlertController(title: "경고", message: "내용을 채워주세요", preferredStyle: .alert)
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
        
        postData(boardID: 2, params: params)
        
        let alert = UIAlertController(title: "알림", message: "글쓰기가 완료되었습니다.", preferredStyle: .alert)
        let ok = UIAlertAction(title: "확인", style: .default){_ in self.dismiss(animated: true) }
        let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
        alert.addAction(cancel)
        alert.addAction(ok)
        present(alert, animated: true)
        
        
    }
   
    func postData(boardID: Int, params: Parameters){
        BoardManager.createPost(boardID: boardID, params: params)
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


extension UIImage {

    public var base64: String {
        return self.jpegData(compressionQuality: 1.0)!.base64EncodedString()
    }

    convenience init?(base64: String, withPrefix: Bool) {
        var finalData: Data?

        if withPrefix {
            guard let url = URL(string: base64) else { return nil }
            finalData = try? Data(contentsOf: url)
        } else {
            finalData = Data(base64Encoded: base64)
        }

        guard let data = finalData else { return nil }
        self.init(data: data)
    }

}
