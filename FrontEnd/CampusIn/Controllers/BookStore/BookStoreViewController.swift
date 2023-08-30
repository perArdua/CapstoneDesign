//
//  BookStoreViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/06.
//

import UIKit

class BookStoreViewController: UIViewController, UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView: UITableView!

    
    @IBOutlet weak var bookTitle: UILabel!
    @IBOutlet weak var topView: UIView!
    
    let bookImgs: [String] = ["book1", "book2", "book3"]
    let bookNames: [String] = ["세이노의 가르침", "역행자", "시장학개론"]
    let bookPrices: [Int] = [10000, 15000, 17000]
    let sellerNames: [String] = ["ooo", "ooo", "ooo"]
    var searchBar: UISearchBar?
    var array: [PostListContent] = []
    var searchArr: [PostSearchContent] = []
    var srch: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("nick")
        let plusImage = UIImage(systemName: "plus")
        let plusButton = UIBarButtonItem(image: plusImage, style: .plain, target: self, action: #selector(plusButtonTapped))
        plusButton.tintColor = .white
        navigationItem.rightBarButtonItem = plusButton
        
        srch = false
        bookTitle.textColor = .white
        getData()
        //self.navigationController?.navigationBar.tintColor = .white
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
        topView.addGestureRecognizer(tapGesture)
        
    }
    
    @objc func plusButtonTapped() {
        // 오른쪽 버튼이 눌렸을 때 수행할 동작
        let nextVC = storyboard!.instantiateViewController(withIdentifier: "SellBookViewController") as! SellBookViewController
        navigationController?.pushViewController(nextVC, animated: true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.navigationBar.isHidden = false
        self.tabBarController?.tabBar.isHidden = false
        srch = false
        getData()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.allowsSelection = true
        //print(array)
        tableView.reloadData()
    }
    
    override func viewDidLayoutSubviews() {
            super.viewDidLayoutSubviews()
            if searchBar == nil {
                // UISearchBar 생성 및 추가
                let searchBar = UISearchBar(frame: CGRect(x: 0, y: topView.frame.maxY + 5, width: view.frame.width, height: 40))
                searchBar.placeholder = "검색어를 입력하세요"
                searchBar.delegate = self
                searchBar.showsSearchResultsButton = true // 검색 버튼을 표시합니다
                view.addSubview(searchBar)
                self.searchBar = searchBar
                
                
            }
        }
    // MARK: - 책 data 받아오는 함수
    func getData(){
        var boardID: Int = UserDefaults.standard.value(forKey: "Book") as! Int
        print(boardID)
        print("getdata")
        BoardManager.showPostbyBoard(boardID: boardID){[weak self] result in
            // 데이터를 받아온 후 실행되는 완료 핸들러
            print("getdata")
            //print(result)
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
        //print(array)
    }
    
    // MARK: - 책 detail 받아오는 함수
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

    @objc func handleTap(_ gesture: UITapGestureRecognizer) {
            view.endEditing(true) // 키보드를 닫습니다.
        }
    
    func getSearchData(keyword: String){
        srch = true
        BoardManager.searchPost(boardID: BoardManager.getBoardID(boardName: "Book"), keyword: keyword){result in
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                DispatchQueue.main.async {
                    self.searchArr = []
                    self.searchArr = posts
                    self.tableView.reloadData()
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        if let searchText = searchBar.text {
            // 검색 버튼을 눌렀을 때 실행할 동작을 여기에 작성하세요
            //두글자 미만 입력시 에러 메세지 출력
            let alert = UIAlertController(title: "알림", message: "두 글자 이상 입력해주세요.", preferredStyle: .alert)
            let ok = UIAlertAction(title: "확인", style: .default)
            alert.addAction(ok)
            
            guard let keyword = searchBar.text else{
                present(alert, animated: true)
                return
            }
            if keyword.count < 2 {
                present(alert, animated: true)
                return
            }
            
            self.getSearchData(keyword: keyword)
            
            print("검색어: \(searchText)")
        }
        
        //검색 액션 구현
            
        searchBar.resignFirstResponder() // 키보드를 닫습니다.
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(srch){
            return searchArr.count
        }
        return array.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "BookStoreTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! BookStoreTableViewCell
        print(indexPath.row)
        
        if(srch){
            let searchInfo = searchArr[indexPath.row]
            cell.bookName.text = searchInfo.title
            cell.bookPrice.text = String(describing: searchInfo.price)
            cell.sellerName.text = searchInfo.nickname
            cell.bookPrice.text = String(searchInfo.price!) + " 원"
            cell.selectionStyle = .none
        }else{
            let bookInfo = array[indexPath.row]
            print("price")
            print(bookInfo.price)
    //        cell.bookImgView.image = UIImage(base64: (bookInfo.photo)!, withPrefix: false)
            cell.bookName.text = bookInfo.title
            cell.bookPrice.text = String(describing: bookInfo.price)
            cell.sellerName.text = bookInfo.nickname
            cell.bookPrice.text = String(bookInfo.price!) + " 원"
            cell.selectionStyle = .none
            print("weoifjwe;ofjao;ewfj;oaiwejf")
        }
        
        
        //print(cell)
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let detailVC = storyboard!.instantiateViewController(withIdentifier: "BookDetailViewController") as! BookDetailViewController
        
        if(srch){
            let bookInfo = searchArr[indexPath.row]
            getPostDetail(postID: bookInfo.postID){ [self]
                postDetail in
                print("res")
                
                //print(postDetail)
                detailVC.bookDetail = postDetail
                detailVC.bookName = bookInfo.title
                detailVC.sellerName = bookInfo.nickname
                //detailVC.bookImg = UIImage(base64: (bookInfo.postImage)!, withPrefix: false)
                detailVC.bookPrice = String(bookInfo.price!)
                self.navigationController?.pushViewController(detailVC, animated: true)
                
            }
        }else{
            let bookInfo = array[indexPath.row]
            getPostDetail(postID: bookInfo.postID){ [self]
                postDetail in
                print("res")
                
                //print(postDetail)
                detailVC.bookDetail = postDetail
                detailVC.bookName = bookInfo.title
                detailVC.sellerName = bookInfo.nickname
                //detailVC.bookImg = UIImage(base64: (bookInfo.postImage)!, withPrefix: false)
                detailVC.bookPrice = String(bookInfo.price!)
                self.navigationController?.pushViewController(detailVC, animated: true)
                
            }
        }
        
       
    }
    
}
