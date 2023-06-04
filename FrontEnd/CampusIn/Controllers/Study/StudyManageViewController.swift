//
//  StudyManageViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/15.
//

import UIKit
import Alamofire

class StudyManageViewController: UIViewController{

    var array: [TimerReadContent] = []
    var timerFlag: Bool?
    var haveToInit: Bool?
    var runningTime: Int?
    var totalTime: Int?
    
    @IBOutlet weak var totalTimeLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    // MARK: - 타이머 추가 버튼
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
        totalTime = 0
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        haveToInit = false
        
        let date = Date()
        let dateString = dateFormatter.string(from: date)
        
        if let storedDateString = UserDefaults.standard.string(forKey: "dateString") {
            if dateString != storedDateString{
                //날짜가 다르기 때문에 viewWillAppear에서 타이머 초기화 실시
                haveToInit = true
            }
        }
        else{UserDefaults.standard.set(dateString, forKey: "dateString")}
        
    
       // array = timerManager.getArray()
        tableView.dataSource = self
        tableView.delegate = self
        
        timerFlag = false
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationBarAppearance = UINavigationBarAppearance()
        navigationBarAppearance.backgroundColor = .systemGreen
        navigationController?.navigationBar.standardAppearance = navigationBarAppearance
        navigationController?.navigationBar.scrollEdgeAppearance = navigationBarAppearance
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        
        if haveToInit!{
            //타이머 초기화 후 타이머 가져오기
            TimerManager.initTimer { result in
                switch result{
                case .success(let res):
                    print("타이머 초기화 성공")
                    //타이머 초기화 성공했다면 타이머 가져오기
                    self.getTimerData()
                    
                    
                case .failure(let error):
                    print("타이머 초기화 실패")
                    self.getTimerData()
                }
            }
        }
        else{
            //타이머 가져오기
            getTimerData()
        }

//        array = timerManager.getArray()
        view.addSubview(addBtn)
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        addBtn.frame = CGRect(x: view.frame.size.width - 75, y: view.frame.size.height - 250, width: 60, height: 60)
    }
    
    // MARK: - 타이머 추가 버튼을 눌렀을때 작동하는 함수
    @objc func addBtnTapped(){
        let alert = UIAlertController(title: "이름을 입력하세요.", message: "", preferredStyle: .alert)
        
        let ok = UIAlertAction(title: "추가", style: .default) { _ in
            guard let textField = alert.textFields?.first, let name = textField.text?.trimmingCharacters(in: .whitespacesAndNewlines) else {return}
            var param : Parameters = [:]
            param["subject"] = name
            TimerManager.createTimer(param: param) {
                self.getTimerData()
            }
            self.totalTime = 0
            self.tableView.reloadData()
        }
        let cancel = UIAlertAction(title: "취소", style: .default)
        
        ok.isEnabled = false
        alert.addAction(ok)
        alert.addAction(cancel)
        alert.addTextField{ tf in
            tf.placeholder = "enter name"
            tf.addTarget(self, action: #selector(self.textFieldEditingChanged(_:)), for: .editingChanged)
        }
        self.present(alert, animated: true)
    }
    
    // 타이머 추가시 아무것도 입력을 안하거나, 이미 존재하는 타이머를 추가하려고 하면 alert의 ok 버튼이 비활성화됨
    @objc func textFieldEditingChanged(_ sender: UITextField) {
        if let alert = presentedViewController as? UIAlertController,
           let ok = alert.actions.first,
           let text = sender.text?.trimmingCharacters(in: .whitespacesAndNewlines) {
            // 텍스트가 비어있거나 구조체 배열에 이미 존재하는 경우 OK 액션을 비활성화
            ok.isEnabled = !text.isEmpty && !(array.contains(where: { $0.subject == text }) )
        }
    }

    @IBAction func todoBtnTapped(_ sender: UIButton) {
        print("tap!")
        let todoVC = storyboard!.instantiateViewController(withIdentifier: "ToDoViewController") as! ToDoViewController
        self.navigationController?.pushViewController(todoVC, animated: true)
        print(self.navigationController)
//        let todoVC = self.storyboard?.instantiateViewController(withIdentifier: "ToDoViewController")
//        todoVC?.modalPresentationStyle = .fullScreen
//        todoVC?.modalTransitionStyle = .coverVertical
//        self.present(todoVC!, animated: true, completion: nil)
    }
    
    
    @IBAction func recordBtnTapped(_ sender: UIButton) {
        let recordVC = storyboard?.instantiateViewController(identifier: "StudyRecordViewController") as! StudyRecordViewController
        self.navigationController?.pushViewController(recordVC, animated: true)
    }
    
    func getTimerData(){
        TimerManager.readTimer { timerdatas in
            switch timerdatas{
            case.success(let timers):
                print("타이머 조회 성공")
                self.array = timers
                self.totalTime = 0
                self.tableView.reloadData()
                
            case.failure(let err):
                print("타이머 조회 실패")
                self.totalTime = 0
                self.tableView.reloadData()
            }
        }
    }
}


extension StudyManageViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        array.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //타이머 시간을 "HH:mm:ss" 형식으로 바꾸기 위해 dateformatter 사용
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        //GMT 기준으로 시간을 출력해야 00:00:00부터 시작한다.
        formatter.timeZone = TimeZone(identifier: "GMT")
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "TimerTableViewCell", for: indexPath) as! TimerTableViewCell
        cell.delegate = self
        
        let temp = array[indexPath.row]
        cell.titleLabel.text = temp.subject
        cell.cnt = temp.elapsedTime
        let date = Date(timeIntervalSinceReferenceDate: TimeInterval(cell.cnt))
        let formattedCnt = formatter.string(from: date)
        cell.timerLabel.text = formattedCnt
        cell.timerID = temp.id
        
        //총 공부 시간 label 갱신
        totalTime! += temp.elapsedTime
        let formattedTotal = formatter.string(from: Date(timeIntervalSinceReferenceDate: TimeInterval(totalTime!)))
        totalTimeLabel.text = formattedTotal
        
        return cell
    }
    
    // MARK: - 테이블 뷰 셀 밀어서 삭제하는 함수
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let delete_action = UIContextualAction(style: .destructive, title: nil) { action, view, completion in
            TimerManager.deleteTimer(timerID: self.array[indexPath.row].id) {
                self.getTimerData()
            }
            self.totalTime = 0
            tableView.reloadData()
            completion(true)
        }
        delete_action.title = "삭제"
//        delete_action.backgroundColor = .red
        let swipe_configuration = UISwipeActionsConfiguration(actions: [delete_action])
        return swipe_configuration
    }
}

// MARK: - 테이블 뷰 셀 안의 timer 시작 버튼 눌렀을때 동작 정의
extension StudyManageViewController: TimerTableViewCellDelegate{
    
    func timerBtnTapped(in cell: TimerTableViewCell) {
        
        
        //현재 다른 타이머가 작동 중인 상태라면 (timerflag 가 true인 상태)
        if timerFlag!{
            if !cell.flag{return}
        }
        
        cell.flag.toggle()
        //타이머가 활성화 된 상태하면
        if(cell.flag){
            runningTime = 0
            timerFlag!.toggle()
            cell.startBtn.setImage(UIImage(systemName: "stop.circle.fill"), for: .normal)
            cell.timer?.invalidate()
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm:ss"
            formatter.timeZone = TimeZone(identifier: "GMT")
            
            cell.timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true, block: { timer in
                cell.cnt  += 1
                self.runningTime! += 1
                let date = Date(timeIntervalSinceReferenceDate: TimeInterval(cell.cnt))
                let formattedCnt = formatter.string(from: date)
                cell.timerLabel.text = formattedCnt
                
                //총 공부 시간 label 갱신
                self.totalTime! += 1
                let formattedTotal = formatter.string(from: Date(timeIntervalSinceReferenceDate: TimeInterval(self.totalTime!)))
                self.totalTimeLabel.text = formattedTotal
            })
        }
        //타이머가 비활성화 된 상태라면
        else{
            var param: Parameters = [:]
            param["elapsedTime"] = runningTime!
            print(runningTime)
            print(cell.timerID)
            TimerManager.updateTimer(timerID: cell.timerID!, param: param){
                self.getTimerData()
            }
            timerFlag!.toggle()
            cell.startBtn.setImage(UIImage(systemName: "play.circle.fill"), for: .normal)
            cell.timer?.invalidate()
            
            //API콜로 타이머 종료를 알림
        }
    }
}
