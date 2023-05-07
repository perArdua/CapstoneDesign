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
    var post : GeneralPostingContent!
    var PM = PostingManager()
    
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
        if (titleTV.text == ""){
            let alert = UIAlertController(title: "경고", message: "제목을 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in
                print("empty title")
       }
            alert.addAction(okAction)
            present(alert, animated: true)
        }else if(contentTV.text == ""){
            let alert = UIAlertController(title: "경고", message: "내용을 채워주세요", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "확인", style: .default) { _ in
                print("empty contents")
       }
            alert.addAction(okAction)
            present(alert, animated: true)
        }
        
        let currentDate = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd"
        let dateString = dateFormatter.string(from: currentDate)
        
        switch img_cnt{
        case 0:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경")
            PM.setPostings(post: post)
            print(PM.getPostings())
        case 1:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경", img0: img0.image)
            PM.setPostings(post: post)
        case 2:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경", img0: img0.image, img1: img1.image)
            PM.setPostings(post: post)
        case 3:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경", img0: img0.image, img1: img1.image, img2: img2.image)
            PM.setPostings(post: post)
        case 4:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경", img0: img0.image, img1: img1.image, img2: img2.image, img3: img3.image)
            PM.setPostings(post: post)
        case 5:
            post = .init(title: titleTV.text, content: contentTV.text, date: dateString, user: "최다경", img0: img0.image, img1: img1.image, img2: img2.image, img3: img3.image, img4: img4.image)
            PM.setPostings(post: post)

        default:
            print("error")
        }
        let alert = UIAlertController(title: "알림", message: "글쓰기가 완료되었습니다.", preferredStyle: .alert)
        let ok = UIAlertAction(title: "확인", style: .default){_ in self.dismiss(animated: true)
        }
        let cancel = UIAlertAction(title: "취소", style: .destructive, handler: nil)
        alert.addAction(cancel)
        alert.addAction(ok)
        present(alert, animated: true)
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


