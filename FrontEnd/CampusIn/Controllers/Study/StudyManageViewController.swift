//
//  StudyManageViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/15.
//

import UIKit

class StudyManageViewController: UIViewController{

    var array: [TimerData] = []
    var timerManager = TimerManager()
    var timerFlag: Bool?
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var collectionView: UICollectionView!
    
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

       // array = timerManager.getArray()
        tableView.dataSource = self
        tableView.delegate = self
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.register(TimerCollectionViewCell.self, forCellWithReuseIdentifier: "TimerCollectionViewCell")
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

//        array = timerManager.getArray()
        view.addSubview(addBtn)
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        addBtn.frame = CGRect(x: view.frame.size.width - 75, y: view.frame.size.height - 200, width: 60, height: 60)
    }
    
    // MARK: - 타이머 추가 버튼을 눌렀을때 작동하는 함수
    @objc func addBtnTapped(){
        let alert = UIAlertController(title: "이름을 입력하세요.", message: "", preferredStyle: .alert)
        
        let ok = UIAlertAction(title: "추가", style: .default) { _ in
            guard let textField = alert.textFields?.first, let name = textField.text?.trimmingCharacters(in: .whitespacesAndNewlines) else {return}
            let newTimer = TimerData(label: name, cnt: 0)
            self.array.append(newTimer)
//            self.timerManager.updateArray(array: self.array)
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
            ok.isEnabled = !text.isEmpty && !(array.contains(where: { $0.label == text }) )
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
        cell.titleLabel.text = temp.label
        cell.cnt = temp.cnt
        let date = Date(timeIntervalSinceReferenceDate: TimeInterval(cell.cnt))
        let formattedCnt = formatter.string(from: date)
        cell.timerLabel.text = formattedCnt
        
        return cell
    }
    
    // MARK: - 테이블 뷰 셀 밀어서 삭제하는 함수
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let delete_action = UIContextualAction(style: .destructive, title: nil) { action, view, completion in
            self.array.remove(at: indexPath.row)
//            self.timerManager.updateArray(array: self.array)
            tableView.reloadData()
            completion(true)
        }
        delete_action.title = "삭제"
//        delete_action.backgroundColor = .red
        let swipe_configuration = UISwipeActionsConfiguration(actions: [delete_action])
        return swipe_configuration
    }
}

extension StudyManageViewController: UICollectionViewDelegate,UICollectionViewDataSource{
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 5
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "TimerCollectionViewCell", for: indexPath) as! TimerCollectionViewCell
        
        return cell
    }
    
    
}

extension StudyManageViewController: TimerTableViewCellDelegate{

    func timerBtnTapped(in cell: TimerTableViewCell) {
        //현재 다른 타이머가 작동 중인 상태라면 (timerflag 가 true인 상태)
        if timerFlag!{
            if !cell.flag{return}
        }
        
        cell.flag.toggle()
        //타이머가 활성화 된 상태하면
        if(cell.flag){
            timerFlag!.toggle()
            cell.startBtn.setImage(UIImage(systemName: "stop.circle.fill"), for: .normal)
            cell.timer?.invalidate()
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm:ss"
            formatter.timeZone = TimeZone(identifier: "GMT")
            
            cell.timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true, block: { timer in
                cell.cnt  += 1
                let date = Date(timeIntervalSinceReferenceDate: TimeInterval(cell.cnt))
                let formattedCnt = formatter.string(from: date)
                cell.timerLabel.text = formattedCnt
            })
        }
        //타이머가 비활성화 된 상태라면
        else{
            timerFlag!.toggle()
            cell.startBtn.setImage(UIImage(systemName: "play.circle.fill"), for: .normal)
            cell.timer?.invalidate()
        }
        
    }
}
