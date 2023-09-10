//
//  BadgeListViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/10.
//

import UIKit

class BadgeListViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    var array :[PostListContent] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getData()
    }
    
    
    func getData(){
        BoardManager.showPostbyBoard(boardID: BoardManager.getBoardID(boardName: "AdminBadgeAccept")){[weak self] result in
            switch result {
            case .success(let posts):
                self?.array = posts
                self?.tableView.reloadData()
            case .failure(let error):
                print("Error: \(error)")
            }
        }
        print(array)
    }
    
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
}

extension BadgeListViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return array.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BadgeTableViewCell") as! BadgeTableViewCell
        cell.title.text = array[indexPath.row].title
        cell.userLabel.text = array[indexPath.row].nickname
        cell.dateLabel.text = "\(array[indexPath.row].createdAt[1])/\(array[indexPath.row].createdAt[2])"
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let nextVC = self.storyboard?.instantiateViewController(identifier: "BadgeDatailViewController") as? BadgeDatailViewController else { return }
        nextVC.postID = array[indexPath.row].postID
        
        getPostDetail(postID: array[indexPath.row].postID){postDetail in
            nextVC.postDetail = postDetail
            print(nextVC.postDetail!)
            self.navigationController?.pushViewController(nextVC, animated: true)
        }
    }
}
