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
    
    var manager = PostingManager()
    var array :[GeneralPostingMainContent] = []
    
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
    
    func getData(){
        let token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTc1OTAxODUxODIzMjI0MzE0NTEiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjg0MzI3MzIxfQ.9aFPgAxWK8eK8xO8lMgAcEz8r_2Xjyu57CiuXYTD60Y"
        let url = "http://localhost:8080/api/v1/boards/2/posts"
        
        AF.request(url, method: .get, headers: HTTPHeaders(["Authorization": "Bearer \(token)"])).responseDecodable(of: GeneralPostingMainData.self, completionHandler: { response in
            print("***************")
            print(type(of: response.value))
            print(response.result)
            if let res = response.value{
                self.array = res.body.generalPostingMainList.content
                self.tableView.reloadData()
            }
            else{
                print("정보 없음")
                print(response.result)
            }
            
            print(self.array.count)
//            print(self.array)
            print("***************")
            
        
        })
    }
    
    //MARK: 글쓰기 버튼을 누를 경우 실행
    @objc func addBtnTapped(){
        let nextVC = storyboard?.instantiateViewController(withIdentifier: "GeneralPostingAddViewController") as! GeneralPostingAddViewController
        nextVC.modalPresentationStyle = .fullScreen
        present(nextVC, animated: true, completion: nil)
        print("addBtn tapped")
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
        cell.dateLabel.text = "날짜 없음"
        cell.userLabel.text = temp.writer
        cell.commentLabel.text = "5"

        return cell
    }
    
    //테이블 뷰 셀이 클릭되면 어떤 동작을 할지 정하는 함수
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let nextVC = self.storyboard?.instantiateViewController(identifier: "GeneralPostingDetailViewController") as? GeneralPostingDetailViewController else { return }
        nextVC.postingIndex = indexPath.row
                self.navigationController?.pushViewController(nextVC, animated: true)
    }
}
