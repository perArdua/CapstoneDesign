//
//  StudyPostingViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/07.
//

import UIKit
import Alamofire

class StudyPostingViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    var tagIdList: [TagContent] = []
    var tagId: Int = -1;
    var tagEng: String?
    
    var array :[PostListContent] = []
    let tagData = ["선택하세요", "IT", "수학", "자연과학", "공학", "경제", "인문", "예체능", "기타"]
    let tagPickerView = UIPickerView()
    let tagDoneView = UIView()
    let tagDoneBtn = UIButton(type: .system)
    var tag: String?
    @IBOutlet weak var tagLabel: UILabel!
    
    let addBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: 0, y: 0, width: 600, height: 600))
        btn.layer.masksToBounds = true
        btn.layer.cornerRadius = 30
        btn.tintColor = .white
        btn.backgroundColor = .gray
        btn.layer.shadowRadius = 6
        btn.layer.shadowOpacity = 0.3
        btn.setImage(UIImage(systemName: "pencil.tip.crop.circle"), for: .normal )
        btn.setPreferredSymbolConfiguration(.init(pointSize: 30, weight: .regular, scale: .default), forImageIn: .normal)
        
        return btn
    }()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // TabBar 숨기기
        if(!isPrevVCMyPage()){
            self.getData()
        }else{
            print("from my page")
        }
        
        self.tabBarController?.tabBar.isHidden = true
        setUpTagPickerView()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("게시판 목록 화면 출력")
        print(KeyChain.read(key: "token"))
        tableView.dataSource = self
        tableView.delegate = self
        
        view.addSubview(addBtn)
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        addBtn.frame = CGRect(x: view.frame.size.width - 75, y: view.frame.size.height - 105, width: 60, height: 60)
        print("array count")
        print(array.count)
        
        print("reload")
        
        self.getData()
        tableView.reloadData()
        
        BoardManager.getTags { res in
            switch res{
            case .success(let tagData):
                self.tagIdList = tagData
            case .failure(let err):
                print("태그 불러오기 실패")
            }
        }
        
    }
    
    // MARK: - 이전 뷰 컨트롤러가 마이페이지인지 판단하는 함수
    func isPrevVCMyPage() -> Bool{
        if let navigationController = self.navigationController {
            let viewControllers = navigationController.viewControllers
            if viewControllers.count >= 2 {
                let previousViewController = viewControllers[viewControllers.count - 2]
                let prevSB = UIStoryboard(name: "Main", bundle: nil)
                if let viewController = prevSB.instantiateViewController(withIdentifier: "MyPostingViewController") as? MyPostingViewController {
                    if type(of: previousViewController) == type(of: viewController) {
                        return true
                    } else {
                        return false
                    }
                }
            }
        }
        return false
    }
    
    
    // MARK: - pickerView 레이아웃 설정
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
    
    
    // MARK: - 검색 API 요청하는 함수
    func getData(){
        BoardManager.showPostbyBoard(boardID: BoardManager.getBoardID(boardName: "Study")){[weak self] result in
            // 데이터를 받아온 후 실행되는 완료 핸들러
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self?.array = posts
                DispatchQueue.main.async {
                    self?.tableView.reloadData()
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
        print(array)
    }
    
    // MARK: - 태그 필터링 요청
    func getTagFilteringData(tagID: Int){
        BoardManager.tagFiltering(boardID: BoardManager.getBoardID(boardName: "Study"), tagID: tagID) { [weak self] res in
            switch res{
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self?.array = posts
                DispatchQueue.main.async {
                    self?.tableView.reloadData()
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    
    //MARK: 글쓰기 버튼을 누를 경우 실행
    @objc func addBtnTapped(){
        let nextVC = storyboard?.instantiateViewController(withIdentifier: "StudyPostingAddViewController") as! StudyPostingAddViewController
        nextVC.modalPresentationStyle = .fullScreen
        present(nextVC, animated: true, completion: nil)
        print("addBtn tapped")
    }
    
    @IBAction func tagBtnTapped(_ sender: UIButton) {
        tagPickerView.isHidden = false
        tagDoneView.isHidden = false
        
        tagPickerView.alpha = 0.0
        tagDoneView.alpha = 0.0
        UIView.animate(withDuration: 0.3) {
            self.tagPickerView.alpha = 1.0
            self.tagDoneView.alpha = 1.0
        }
    }
    
    @objc func tagDoneBtnTapped(){
        tagPickerView.isHidden = true
        tagDoneView.isHidden = true
        
        if let tagText = tag{
            tagLabel.text = tagText
            if tagText == "선택하세요"{
                getData()
                tagLabel.textColor = .systemGray2
            }
            else{
                getTagFilteringData(tagID: tagId)
                tagLabel.textColor = .black
            }
        }
        else{
            tagLabel.text = nil
        }
        
        UIView.animate(withDuration: 0.3) {
            self.tagPickerView.alpha = 0.0
            self.tagDoneView.alpha = 0.0
        }
    }
    
    // MARK: - 테이블 뷰에서 게시글을 탭했을때 게시글의 정보를 가져오는 함수
    func getPostDetail(postID : Int, completion: @escaping (PostDetailContent) -> Void){
        BoardManager.readPost(postID: postID) { result in
            switch result{
            case .success(let post):
                DispatchQueue.main.async {
                    completion(post)
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    func getComment(postID : Int, completion: @escaping ([CommentDataContent]) -> Void){
        CommentManager.readComment(postID: postID) { result in
            switch result{
            case .success(let comments):
                DispatchQueue.main.async {
                    completion(comments)
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    func getTagId(tag : String) -> Int{
        for item in tagIdList{
            if item.tagType == tag{
                return item.tagID
            }
        }
        return -1
    }
}
 
//MARK: 테이블 뷰 delegate, datasource
extension StudyPostingViewController : UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰에 몇개의 셀을 보여줄 것인지 결정하는 함수
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return array.count
    }
    
    //각 테이블 뷰 셀의 내용을 결정하는 함수
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "StudyPostingTableViewCell", for: indexPath) as! StudyPostingTableViewCell
        
        let temp = array[indexPath.row]
        
        cell.titleLabel.text = temp.title
        cell.contentLabel.text = temp.content
        cell.dateLabel.text = String(temp.createdAt[1])+"/"+String(temp.createdAt[2])
        cell.userLabel.text = "작성자: \(temp.nickname!)"
        cell.commentLabel.text = "5"
        cell.tagLabel.text = "#\(ConvertTag.convert2(tag: temp.tagResponse.tagType))"

        return cell
    }
    
    //테이블 뷰 셀이 클릭되면 어떤 동작을 할지 정하는 함수
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let nextVC = self.storyboard?.instantiateViewController(identifier: "StudyPostingDetailViewController") as? StudyPostingDetailViewController else { return }
        nextVC.postID = array[indexPath.row].postID
        
        getPostDetail(postID: array[indexPath.row].postID){ [self]
            postDetail in
            nextVC.postDetail = postDetail
            print(nextVC.postDetail)
            getComment(postID: array[indexPath.row].postID){
                comments in
                nextVC.comments = comments
                print(comments)
                self.navigationController?.pushViewController(nextVC, animated: true)
            }
        }
    }
}


extension StudyPostingViewController: UIPickerViewDelegate, UIPickerViewDataSource{
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return tagData.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return tagData[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        
        self.tag = tagData[row]
        self.tagEng = ConvertTag.convert(tag: tag!)
        self.tagId = getTagId(tag: tagEng!)
        print(tagEng!, tagId)

    }
}
