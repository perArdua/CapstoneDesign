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
    @IBOutlet weak var msgBtn: UIButton!
    @IBOutlet weak var addBtn: UIButton!
    
    let bookImgs: [String] = ["book1", "book2", "book3"]
    let bookNames: [String] = ["세이노의 가르침", "역행자", "시장학개론"]
    let bookPrices: [Int] = [10000, 15000, 17000]
    let sellerNames: [String] = ["ooo", "ooo", "ooo"]
    var searchBar: UISearchBar?

    
    override func viewDidLoad() {
        super.viewDidLoad()
        addBtn.setTitle("", for: .normal)
        msgBtn.setTitle("", for: .normal)
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.allowsSelection = true
        bookTitle.textColor = .white
        
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
        topView.addGestureRecognizer(tapGesture)
        
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

    @objc func handleTap(_ gesture: UITapGestureRecognizer) {
            view.endEditing(true) // 키보드를 닫습니다.
        }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        if let searchText = searchBar.text {
            // 검색 버튼을 눌렀을 때 실행할 동작을 여기에 작성하세요
            print("검색어: \(searchText)")
        }
            
        searchBar.resignFirstResponder() // 키보드를 닫습니다.
    }

    @IBAction func addBtnTapped(_ sender: UIButton) {
        //책 추가 버튼 액션
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return bookImgs.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "BookStoreTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! BookStoreTableViewCell
        cell.bookImgView.image = UIImage(named: bookImgs[indexPath.row])
        cell.bookName.text = bookNames[indexPath.row]
        cell.bookPrice.text = String(bookPrices[indexPath.row])
        cell.sellerName.text = sellerNames[indexPath.row]
        print("weoifjwe;ofjao;ewfj;oaiwejf")
        print(cell)
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let detailVC = storyboard!.instantiateViewController(withIdentifier: "BookDetailViewController") as! BookDetailViewController
        print(bookImgs[indexPath.row])
        detailVC.bookName = bookNames[indexPath.row]
        detailVC.sellerName = sellerNames[indexPath.row]
        detailVC.bookImg = UIImage(named: bookImgs[indexPath.row])!
        detailVC.bookPrice = String(bookPrices[indexPath.row])
        self.navigationController?.pushViewController(detailVC, animated: true)
    }
    
}
