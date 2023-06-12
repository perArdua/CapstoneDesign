//
//  CampusIn
//
//  Created by 이동현 on 2023/06/08.
//

import UIKit
import Lottie
import Alamofire

class QuestionPostingSearchViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchTF: UITextField!
    
    var searcharry :[PostSearchContent] = []
    
    
    let tempLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 200, height: 21))
        label.text = "검색 결과가 없습니다."
        label.textAlignment = .center
        return label
    }()
    
    var animationView: LottieAnimationView?
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tempLabel.isHidden = true
        tempLabel.frame = CGRect(x: view.frame.size.width/2-75, y: view.frame.size.height/2, width: 150, height: 60)
        animationView?.frame = CGRect(x: view.frame.size.width/2-50, y: view.frame.size.height/2, width: 100, height: 100)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        
        view.addSubview(tempLabel)
        
        animationView = .init(name: "loading_check")
        animationView!.frame = view.bounds
        animationView!.contentMode = .scaleAspectFit
        animationView!.loopMode = .loop
        animationView!.animationSpeed = 1
        view.addSubview(animationView!)
        animationView!.isHidden = true
    }
    
    // MARK: - 검색 버튼 누를때 동작
    
    @IBAction func searchBtnTapped(_ sender: UIButton) {
        //두글자 미만 입력시 에러 메세지 출력
        let alert = UIAlertController(title: "알림", message: "두 글자 이상 입력해주세요.", preferredStyle: .alert)
        let ok = UIAlertAction(title: "확인", style: .default)
        alert.addAction(ok)
        
        guard let keyword = searchTF.text else{
            present(alert, animated: true)
            return
        }
        if keyword.count < 2 {
            present(alert, animated: true)
            return
        }
        
        
        animationView!.isHidden = false
        animationView!.play()
        tempLabel.isHidden = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            self.getData(keyword: keyword)
            
        }
    }

    func getData(keyword: String){
        BoardManager.searchPost(boardID: BoardManager.getBoardID(boardName: "Question"), keyword: keyword){result in
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self.searcharry = posts
                self.tableView.reloadData()
                self.handleAnimation()
                
            case .failure(let error):
                print("Error: \(error)")
                self.handleAnimation()
            }
        }
    }
    
    func handleAnimation(){
        //검색된 게시글이 없는 경우
        if searcharry.count == 0{
            animationView!.stop()
            animationView!.isHidden = true
            tempLabel.isHidden = false
        }
        //검색된 게시글이 있는 경우
        else{print("검색결과 있음")
            animationView!.stop()
            animationView!.isHidden = true
            tempLabel.isHidden = true
        }
        tableView.reloadData()
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


extension QuestionPostingSearchViewController : UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰에 몇개의 셀을 보여줄 것인지 결정하는 함수
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        print("count:\(searcharry.count)")
        return searcharry.count
    }
    
    //각 테이블 뷰 셀의 내용을 결정하는 함수
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "QuestionPostingSearchTableViewCell", for: indexPath) as! QuestionPostingSearchTableViewCell
        
        let temp = searcharry[indexPath.row]
        
        cell.titleLabel.text = temp.title
        cell.contentLabel.text = temp.content
        cell.dateLabel.text = String(temp.createdAt[1]) + "/" + String(temp.createdAt[2])
        cell.userLabel.text = temp.nickname
        cell.commentLabel.text = "5"

        return cell
    }
    
    //테이블 뷰 셀이 클릭되면 어떤 동작을 할지 정하는 함수
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let nextVC = self.storyboard?.instantiateViewController(identifier: "QuestionPostingDetailViewController") as? QuestionPostingDetailViewController else { return }
        nextVC.postID = searcharry[indexPath.row].postID
        
        getPostDetail(postID: searcharry[indexPath.row].postID){ [self]
            postDetail in
            nextVC.postDetail = postDetail
            print(nextVC.postDetail)
            getComment(postID: searcharry[indexPath.row].postID){
                comments in
                nextVC.comments = comments
                print(comments)
                self.navigationController?.pushViewController(nextVC, animated: true)
            }
        }
    }
}
