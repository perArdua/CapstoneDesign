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
    var studyCnt = 0
    
    @IBOutlet weak var totalTimeLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var groupLabel: UILabel!
    @IBOutlet var groupTimers: [UIView]!
    @IBOutlet var memberNames: [UILabel]!
    @IBOutlet var memberTimers: [UILabel]!
    
    var studyGroupData: [MyStudyGroupDetails] = [MyStudyGroupDetails(id: -1, studygroupName: "선택하세요", userName: ".", createdAt: [], limitedMemberSize: -1, currentMemberSize: 0)]
    var studyGroupID : Int?
    
    let studyPickerView = UIPickerView()
    let studyDoneView = UIView()
    let studyDoneBtn = UIButton(type: .system)
    //스터디 그룹 라벨에 들어갈 텍스트
    var studyGroup: String?
    var groupTimerList: [GroupTimerContent] = []
    
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
        tableView.dataSource = self
        tableView.delegate = self
        tableView.allowsSelection = false
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
        
        
        timerFlag = false
        setUpStudyPickerView()
        setGroupTimer()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationBarAppearance = UINavigationBarAppearance()
        navigationBarAppearance.backgroundColor = .systemGreen
        navigationController?.navigationBar.standardAppearance = navigationBarAppearance
        navigationController?.navigationBar.scrollEdgeAppearance = navigationBarAppearance
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        
        StudyGroupManager.showMyStudyGroup { res in
            switch res{
            case .success(let data):
                print("나의 스터디 그룹 목록 불러오기 성공")
                for item in data{
                    print(item.studygroupName)
                }
                self.studyGroupData = [MyStudyGroupDetails(id: -1, studygroupName: "선택하세요", userName: ".", createdAt: [], limitedMemberSize: -1, currentMemberSize: 0)]
                self.studyGroupData.append(contentsOf: data)
            case .failure(let err):
                print("나의 스터디 그룹 목록 불러오기 성공")
                print(err)
            }
        }
        
        
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
    
    func setGroupTimer(){
        print("그룹원 수: \(groupTimerList.count)")
        
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        formatter.timeZone = TimeZone(identifier: "GMT")
        
        for i in 0...3{
            groupTimers[i].isHidden = true
        }
        
        
        if groupTimerList.count > 0{
            groupTimers[0].isHidden = false
            memberNames[0].text = groupTimerList[0].studyGroupMemberName
            let date = Date(timeIntervalSinceReferenceDate: TimeInterval(groupTimerList[0].elapsedTime))
            let formattedCnt = formatter.string(from: date)
            memberTimers[0].text = formattedCnt
        
        }
        if groupTimerList.count > 1{
            groupTimers[1].isHidden = false
            memberNames[1].text = groupTimerList[1].studyGroupMemberName
            let date = Date(timeIntervalSinceReferenceDate: TimeInterval(groupTimerList[1].elapsedTime))
            let formattedCnt = formatter.string(from: date)
            memberTimers[1].text = formattedCnt
        }
        if groupTimerList.count > 2{
            groupTimers[2].isHidden = false
            memberNames[2].text = groupTimerList[2].studyGroupMemberName
            let date = Date(timeIntervalSinceReferenceDate: TimeInterval(groupTimerList[2].elapsedTime))
            let formattedCnt = formatter.string(from: date)
            memberTimers[2].text = formattedCnt
        }
        if groupTimerList.count > 3{
            groupTimers[3].isHidden = false
            memberNames[3].text = groupTimerList[3].studyGroupMemberName
            let date = Date(timeIntervalSinceReferenceDate: TimeInterval(groupTimerList[3].elapsedTime))
            let formattedCnt = formatter.string(from: date)
            memberTimers[3].text = formattedCnt
        }
    }
    
    // MARK: - study group의 pickerView UI 세팅
    func setUpStudyPickerView(){
        studyPickerView.delegate = self
        studyPickerView.dataSource = self
        view.addSubview(studyPickerView)
        studyPickerView.translatesAutoresizingMaskIntoConstraints = false
        studyPickerView.backgroundColor = .white
        studyPickerView.layer.borderColor = UIColor.gray.cgColor
        studyPickerView.isHidden = true
        NSLayoutConstraint.activate([
        
            // Picker View 제약 조건
            studyPickerView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            studyPickerView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            studyPickerView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: 0),
            studyPickerView.heightAnchor.constraint(equalToConstant: 200)
        ])
        
        studyDoneView.backgroundColor = .systemGray5
        view.addSubview(studyDoneView)
        studyDoneView.translatesAutoresizingMaskIntoConstraints = false
        studyDoneView.isHidden = true
        NSLayoutConstraint.activate([
            studyDoneView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 0),
            studyDoneView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: 0),
            studyDoneView.bottomAnchor.constraint(equalTo: studyPickerView.topAnchor, constant: 0),
            studyDoneView.heightAnchor.constraint(equalToConstant: 30)
        ])
        studyDoneView.addSubview(studyDoneBtn)
        
        studyDoneBtn.tintColor = .systemBlue
        studyDoneBtn.setTitle("완료", for: .normal)
        
        studyDoneBtn.translatesAutoresizingMaskIntoConstraints = false
        studyDoneBtn.addTarget(self, action: #selector(studyDoneBtnTapped), for: .touchUpInside)
        NSLayoutConstraint.activate([
            studyDoneBtn.trailingAnchor.constraint(equalTo: studyDoneView.trailingAnchor, constant: 0),
            studyDoneBtn.topAnchor.constraint(equalTo: studyDoneView.topAnchor, constant: 0),
            studyDoneBtn.bottomAnchor.constraint(equalTo: studyDoneView.bottomAnchor, constant: 0),
            studyDoneBtn.widthAnchor.constraint(equalToConstant: 80)
        ])
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
        if(groupLabel.text == ""){
            let alertController = UIAlertController(title: "그룹 선택", message: "그룹을 선택해주세요", preferredStyle: .alert)
                    
                    // 확인 액션 추가
                    alertController.addAction(UIAlertAction(title: "확인", style: .default) { _ in
                        // 확인을 눌렀을 때 실행할 코드를 여기에 작성
                        print("확인이 눌렸습니다.")
                    })
            present(alertController, animated: true, completion: nil)
        }else{
            recordVC.groupName = groupLabel.text!
            recordVC.groupID = studyGroupID
            self.navigationController?.pushViewController(recordVC, animated: true)
        }
    }
    
    
    @IBAction func groupChangeBtnTapped(_ sender: Any) {
        studyPickerView.reloadAllComponents()
        studyPickerView.isHidden = false
        studyDoneView.isHidden = false
        
        studyPickerView.alpha = 0.0
        studyDoneView.alpha = 0.0
        UIView.animate(withDuration: 0.3) {
            self.studyPickerView.alpha = 1.0
            self.studyDoneView.alpha = 1.0
        }
        
        addBtn.isHidden = true
    }
    
    @objc func studyDoneBtnTapped(){
        studyPickerView.isHidden = true
        studyDoneView.isHidden = true
        print(self.studyGroupID)
        if let studyGroupText = studyGroup{
            if studyGroupText == ""{
                groupLabel.text = ""
                groupTimerList = []
                self.setGroupTimer()
            }
            else{
                groupLabel.text = studyGroupText
                StudyGroupManager.getGroupTimer(groupID: studyGroupID!) { res in
                    switch res{
                    case .success(let timers):
                        self.groupTimerList = timers
                        self.setGroupTimer()
                    case .failure(_):
                        print("그룹 타이머 불러오기 실패...")
                    }
                }
            }
        }
        else{
            groupLabel.text = nil
            groupTimerList = []
            self.setGroupTimer()
        }
        
        UIView.animate(withDuration: 0.3) {
            self.studyPickerView.alpha = 0.0
            self.studyDoneView.alpha = 0.0
        }
        
        addBtn.isHidden = false
        //api 호출로 스터디 그룹원들 타이머 설정해주기
    }
    
    func getTimerData(){
        TimerManager.readTimer { timerdatas in
            switch timerdatas{
            case.success(let timers):
                print("타이머 조회 성공")
                self.array = timers
                print(timers)
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
        print("타이머 갯수:\(array.count)")
        return array.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        print("*****")
        print(indexPath.row)
        //타이머 시간을 "HH:mm:ss" 형식으로 바꾸기 위해 dateformatter 사용
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        //GMT 기준으로 시간을 출력해야 00:00:00부터 시작한다.
        formatter.timeZone = TimeZone(identifier: "GMT")
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "TimerTableViewCell", for: indexPath) as! TimerTableViewCell
        cell.delegate = self
        
        let temp = array[indexPath.row]
        print("*****")
        print(indexPath.row)
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

extension StudyManageViewController: UIPickerViewDelegate, UIPickerViewDataSource{
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return studyGroupData.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return studyGroupData[row].studygroupName
    }

    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if row == 0{
            self.studyGroup = ""
            self.studyGroupID = -1
        }
        else{
            self.studyGroup = studyGroupData[row].studygroupName
            self.studyGroupID = studyGroupData[row].id
            print(self.studyGroupID)
        }
    }
}
