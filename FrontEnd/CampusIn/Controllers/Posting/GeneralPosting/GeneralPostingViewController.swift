//
//  GeneralPostingViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/04.
//

import UIKit
import Alamofire

class GeneralPostingViewController: UIViewController {
    
    
    @IBOutlet weak var tableView: UITableView!
    
    
    var array :[PostListContent] = []
    
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
        self.getData()
        self.tabBarController?.tabBar.isHidden = true
        
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
    }
    
    // MARK: - 검색 API 요청하는 함수
    func getData(){
        BoardManager.showPostbyBoard(boardID: 2){[weak self] result in
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
    
    //MARK: 글쓰기 버튼을 누를 경우 실행
    @objc func addBtnTapped(){
        let nextVC = storyboard?.instantiateViewController(withIdentifier: "GeneralPostingAddViewController") as! GeneralPostingAddViewController
        nextVC.modalPresentationStyle = .fullScreen
        present(nextVC, animated: true, completion: nil)
        print("addBtn tapped")
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
    

    
    
}
 
//MARK: 테이블 뷰 delegate, datasource
extension GeneralPostingViewController : UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰에 몇개의 셀을 보여줄 것인지 결정하는 함수
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return array.count
    }
    
    //각 테이블 뷰 셀의 내용을 결정하는 함수
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingTableViewCell", for: indexPath) as! GeneralPostingTableViewCell
        
        let temp = array[indexPath.row]
        
        cell.titleLabel.text = temp.title
        cell.contentLabel.text = temp.content
        cell.dateLabel.text = String(temp.createdAt[1])+"/"+String(temp.createdAt[2])
        cell.userLabel.text = temp.nickname
        cell.commentLabel.text = "5"

        return cell
    }
    
    //테이블 뷰 셀이 클릭되면 어떤 동작을 할지 정하는 함수
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let nextVC = self.storyboard?.instantiateViewController(identifier: "GeneralPostingDetailViewController") as? GeneralPostingDetailViewController else { return }
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
