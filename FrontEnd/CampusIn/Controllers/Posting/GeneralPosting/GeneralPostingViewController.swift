//
//  GeneralPostingViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/04.
//

import UIKit

class GeneralPostingViewController: UIViewController {
    
    
    @IBOutlet weak var tableView: UITableView!
    
    var manager = PostingManager()
    var array :[GeneralPostingContent] = []
    
    var addBtn = UIButton(type: .custom)
    
    //MARK: 글쓰기 버튼을 테이블 뷰 위에 띄우는 함수
    func floatingButton(){
        addBtn.frame = CGRect(x: self.view.frame.size.width - 60, y: self.view.frame.size.height - 60, width: 40, height: 40)
        addBtn.setImage(UIImage(named: "add"), for: .normal)
        addBtn.backgroundColor = .white
        addBtn.clipsToBounds = true
        addBtn.layer.cornerRadius = 4
        addBtn.layer.borderColor = UIColor.clear.cgColor
        addBtn.layer.borderWidth = 3.0
        addBtn.addTarget(self, action: #selector(addBtnTapped), for:
                .touchUpInside)
        if let window = UIApplication.shared.keyWindow {
            window.addSubview(addBtn)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        array = manager.getPostings()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        floatingButton()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        addBtn.removeFromSuperview()
    }
    
    //MARK: 글쓰기 버튼을 누를 경우 실행
    @objc func addBtnTapped(){
        let nextVC = storyboard?.instantiateViewController(withIdentifier: "GeneralPostingAddViewController") as! GeneralPostingAddViewController
        nextVC.modalPresentationStyle = .fullScreen
        present(nextVC, animated: true, completion: nil)
        print("addBtn tapped")
    }
}

//MARK: ios13 버전 이상일 경우 keyWindow가 deprecated되기 떄문에 아래 코드를 추가해주어야 한다.
extension UIWindow{
    static var key: UIWindow?{
        if #available(iOS 13, *){
            return UIApplication.shared.windows.first{ $0.isKeyWindow}
        }else{
            return UIApplication.shared.keyWindow
        }
    }
}
 
//MARK: 테이블 뷰 delegate, datasource
extension GeneralPostingViewController : UITableViewDelegate, UITableViewDataSource{
    
    //테이블 뷰에 몇개의 셀을 보여줄 것인지 결정하는 함수
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 15
    }
    
    //각 테이블 뷰 셀의 내용을 결정하는 함수
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "GeneralPostingTableViewCell", for: indexPath) as! GeneralPostingTableViewCell
        
        let temp = array[indexPath.row]
        
        cell.titleLabel.text = temp.title
        cell.contentLabel.text = temp.content
        cell.dateLabel.text = temp.date
        cell.userLabel.text = temp.user
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
