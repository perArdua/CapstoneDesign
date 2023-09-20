//
//  ViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/02.
//

import UIKit
import Alamofire
class ViewController: UIViewController {

    
    
    @IBOutlet weak var todoView: UIView!
    @IBOutlet weak var generalPostingView: UIView!
    @IBOutlet weak var questionView: UIView!
    @IBOutlet weak var studyView: UIView!
    
    
    @IBOutlet weak var todayDateLabel: UILabel!
    @IBOutlet var todoLabel: [UILabel]!
    
    @IBOutlet var generalTitle: [UILabel]!
    @IBOutlet var studyTitle: [UILabel]!
    @IBOutlet var questionTitle: [UILabel]!
    
    var generalPostingList: [PostListContent] = []
    var studyPostingList: [PostListContent] = []
    var questionPostingList: [PostListContent] = []
    var todoList: [Todo] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yy-MM-dd"
        todayDateLabel.text = dateFormatter.string(from: Date())
        // Do any additional setup after loading the view.
        let sideBtn = UIBarButtonItem(image: UIImage(systemName: "line.3.horizontal"), style: .plain, target: self, action: #selector(rightBtnTapped))
        sideBtn.tintColor = .black
        navigationItem.rightBarButtonItem = sideBtn
        
        // TabBar 보이기
        self.tabBarController?.tabBar.isHidden = false
        setUp()
        
    }
    
    func setUp(){
        todoView.layer.borderColor = UIColor.systemGray3.cgColor
        todoView.layer.borderWidth = 1.0
        todoView.layer.cornerRadius = 10.0
        
        generalPostingView.layer.borderColor = UIColor.systemGray3.cgColor
        generalPostingView.layer.borderWidth = 1.0
        generalPostingView.layer.cornerRadius = 10.0

        questionView.layer.borderColor = UIColor.systemGray3.cgColor
        questionView.layer.borderWidth = 1.0
        questionView.layer.cornerRadius = 10.0

        studyView.layer.borderColor = UIColor.systemGray3.cgColor
        studyView.layer.borderWidth = 1.0
        studyView.layer.cornerRadius = 10.0
        
        //할일 목록 라벨 UI 설정
        
        todoLabel[0].layer.cornerRadius = 10.0
        todoLabel[0].layer.masksToBounds = true
        todoLabel[1].layer.cornerRadius = 10.0
        todoLabel[1].layer.masksToBounds = true
        todoLabel[2].layer.cornerRadius = 10.0
        todoLabel[2].layer.masksToBounds = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        // TabBar 보이기
        self.tabBarController?.tabBar.isHidden = false
        initPostingTitle()
        
        let defaults = UserDefaults.standard
        let check = defaults.value(forKey: "isLoggedIn")
        if check as! Bool{
           setPostingTitle()
        }
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        self.navigationController?.navigationBar.clipsToBounds = true
    }
    
    func setPostingTitle(){
        TodoManager.getAllTodoList { res in
            switch res{
            case .success(let data):
                self.todoList = data
                self.setTodoLabel()
            case .failure(let err):
                print(err)
            }
        }
        
        
        BoardManager.showPostbyBoard(boardID: BoardManager.getBoardID(boardName: "Free")){[weak self] result in
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self?.generalPostingList = posts
                self?.setGeneralPostingTitle()
            case .failure(let error):
                print("자유 게시판 미리보기 Error: \(error)")
            }
        }
        
        BoardManager.showPostbyBoard(boardID: BoardManager.getBoardID(boardName: "Study")){[weak self] result in
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self?.studyPostingList = posts
                self?.setStudyPostingTitle()
            case .failure(let error):
                print("스터디 모집 게시판 미리보기 Error: \(error)")
            }
        }
        
        BoardManager.showPostbyBoard(boardID: BoardManager.getBoardID(boardName: "Question")){[weak self] result in
            switch result {
            case .success(let posts):
                // 데이터를 받아와서 배열에 저장
                self?.questionPostingList = posts
                self?.setQuestionPostingTitle()
            case .failure(let error):
                print("질의 응답 게시판 미리보기 Error: \(error)")
            }
        }
    }
    
    func initPostingTitle(){
        for i in 0..<3{
            generalTitle[i].textColor = .lightGray
            studyTitle[i].textColor = .lightGray
            questionTitle[i].textColor = .lightGray
            todoLabel[i].textColor = .lightGray
            
            
            generalTitle[i].text = "• 게시글이 없습니다."
            studyTitle[i].text = "• 게시글이 없습니다."
            questionTitle[i].text = "• 게시글이 없습니다."
            todoLabel[i].text = "- 할 일이 없습니다."
        }
    }
    
    func setTodoLabel(){
        let maxPostingCnt = 3
        
        var cnt = 0
        for i in 0..<todoList.count{
            if cnt == 3{break}
            if todoList[i].completed {continue}
            todoLabel[i].textColor = .black
            todoLabel[i].text = "- \(todoList[i].title)"
            cnt += 1
        }
    }
    
    func setGeneralPostingTitle(){
        let maxPostingCnt = 3
        
        for i in 0..<(min(maxPostingCnt, generalPostingList.count)){
            generalTitle[i].textColor = .black
            generalTitle[i].text = "• \(generalPostingList[i].title)"
        }
    }
    
    func setStudyPostingTitle(){
        let maxPostingCnt = 3
        
        for i in 0..<(min(maxPostingCnt, studyPostingList.count)){
            studyTitle[i].textColor = .black
            studyTitle[i].text = "• \(studyPostingList[i].title)"
        }
    }
    
    func setQuestionPostingTitle(){
        let maxPostingCnt = 3
        
        for i in 0..<(min(maxPostingCnt, questionPostingList.count)){
            questionTitle[i].textColor = .black
            questionTitle[i].text = "• \(questionPostingList[i].title)"
        }
    }


    override func loadView() {
        super.loadView()
        let defaults = UserDefaults.standard
        if !(defaults.value(forKey: "isLoggedIn")! as! Bool){ // 로그인 되지 않은 회원의 경우 로그인 화면으로 이동시킴
            print("login status: false")
            print("move to login page")
            
            let loginVC = storyboard!.instantiateViewController(withIdentifier: "loginViewController") as! LoginViewController
            loginVC.modalTransitionStyle = .coverVertical
            loginVC.modalPresentationStyle = .fullScreen
            
            self.present(loginVC, animated: false, completion: nil)
        }
        else{
            BoardManager.initBoard()
            print(defaults.value(forKey: "UserId"))
        }
    }

    @IBAction func rightButtonTapped(_ sender: UIBarButtonItem) {
        let pushVC = self.storyboard?.instantiateViewController(withIdentifier: "SideMenuNavigationController")

        self.navigationController?.pushViewController(pushVC!, animated: true)
    }
    
    @objc func rightBtnTapped(){
        let nextVC = self.storyboard?.instantiateViewController(withIdentifier: "SideMenuNavigationController")
        //self.navigationController?.pushViewController(nextVC!, animated: true)
        self.present(nextVC!, animated: true)
    }
}


