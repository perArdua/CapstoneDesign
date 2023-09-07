//
//  RankingViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/05.
//

import UIKit
import Alamofire

class RankingViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    let dateFormatter = DateFormatter()
    let dateFormatter2 = DateFormatter()

    @IBOutlet weak var weekLabel: UILabel!
    
    let arr: [String] = ["4등. 남궁00", "5등. 이00"]
    var rankingID: Int?
    var rankingList: [RankingContent] = []
    var calendar = Calendar.current

    var todayString: String?
    let currentDate = Date()
    
    var startOfWeek: Date?
    var endOfWeek: Date?
    
    var lastWeekEnd: Date?
    var curWeekStart: Date?
    var curWeekEnd: Date?
    var nextWeekEnd: Date?
    
    var canToogle: Bool = true
        
    @IBOutlet weak var segmentControl: UISegmentedControl!
    var month:Int?
    var week:Int?
    var components: DateComponents?
    
    var studyGroup:[MyStudyGroupDetails] = []
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var bronzePersonLabel: UILabel!
    @IBOutlet weak var silverPersonLabel: UILabel!
    @IBOutlet weak var goldPersonLabel: UILabel!
    @IBOutlet weak var toggleBtn: UIButton!
    @IBOutlet weak var prevBtn: UIButton!
    @IBOutlet weak var nextBtn: UIButton!
    
    @IBOutlet weak var rankingTypeBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        calendar.firstWeekday = 2
        dateFormatter.dateFormat = "YYYY-MM-dd"
        dateFormatter2.dateFormat = "MM.dd"
        todayString = dateFormatter.string(from: Date())
        startOfWeek = calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: currentDate))
        endOfWeek = calendar.date(byAdding: .day, value: 6, to: startOfWeek!)
        
        lastWeekEnd = calendar.date(byAdding: .day, value: -1, to: startOfWeek!)
        
        curWeekEnd = calendar.date(byAdding: .day, value: 6, to: startOfWeek!)
        curWeekStart = calendar.date(byAdding: .day, value: -6, to: curWeekEnd!)
        nextWeekEnd = calendar.date(byAdding: .day, value: 7, to: endOfWeek!)
        
        prevBtn.setTitle("", for: .normal)
        nextBtn.setTitle("", for: .normal)
        
        goldPersonLabel.text = "1. -"
        silverPersonLabel.text = "2. -"
        bronzePersonLabel.text = "3. -"
        
        weekLabel.text = "[ \(dateFormatter2.string(from: curWeekStart!)) ~ \(dateFormatter2.string(from: curWeekEnd!)) ]"
        tableView.delegate = self
        tableView.dataSource = self
        
        nextBtn.isEnabled = false
        rankingTypeBtn.setTitle("공부시간", for: .normal)
        
//        segmentControl.isHidden = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("랭킹 ")
        RankingManager.getPersonalStudyRanking {res in
            switch res{
            case .success(let data):
                self.rankingList = data
                self.setRanking()
                self.tableView.reloadData()
            case .failure(let err):
                print(err)
            }
        }
        print("appear 종료")
    }
    
    @IBAction func segmentControlChanged(_ sender: Any) {
        print("세그 컨트롤")
        initDate()
        rankingList.removeAll()
        setRanking()
        tableView.reloadData()
        
        if segmentControl.selectedSegmentIndex == 0{
            canToogle = true
            rankingTypeBtn.isEnabled = true
            RankingManager.getPersonalStudyRanking{ res in
                switch res{
                case .success(let data):
                    self.rankingList = data
                    self.setRanking()
                    self.tableView.reloadData()
                case .failure(_):
                    print("그룹 -> 개인 토글 실패")
                }
            }
        }
        else{
            canToogle = false
            rankingTypeBtn.isEnabled = false
            rankingTypeBtn.setTitle("공부시간", for: .normal)
            print("######")
            //그룹별 랭킹 api
            getGroupRanking {
                print(self.rankingList)

            }
            print("###")
        }
    }
    
    @IBAction func rankingTypeBtnTapped(_ sender: UIButton) {
        if sender.titleLabel?.text == "공부시간"{
            sender.setTitle("질의응답", for: .normal)
            RankingManager.getPersonalQuesRanking { res in
                switch res{
                case .success(let data):
                    print("토글 성공")
                    self.rankingList = data
                    self.setRanking()
                    self.tableView.reloadData()
                case .failure(_):
                    print("공부시간 -> 질의응답 토글 실패")
                }
            }
        }
        else{
            sender.setTitle("공부시간", for: .normal)
            RankingManager.getPersonalStudyRanking{ res in
                switch res{
                case .success(let data):
                    print("토글 성공")
                    self.rankingList = data
                    self.setRanking()
                    self.tableView.reloadData()
                case .failure(_):
                    print("질의응답 -> 공부시간 토글 실패")
                }
            }
        }
    }
    
    
    func initDate(){
        startOfWeek = calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: currentDate))
        endOfWeek = calendar.date(byAdding: .day, value: 6, to: startOfWeek!)
        
        lastWeekEnd = calendar.date(byAdding: .day, value: -1, to: startOfWeek!)
        
        curWeekEnd = calendar.date(byAdding: .day, value: 6, to: startOfWeek!)
        curWeekStart = calendar.date(byAdding: .day, value: -6, to: curWeekEnd!)
        nextWeekEnd = calendar.date(byAdding: .day, value: 7, to: endOfWeek!)
        weekLabel.text = "[ \(dateFormatter2.string(from: curWeekStart!)) ~ \(dateFormatter2.string(from: curWeekEnd!)) ]"
    }
    
    //그룹 랭킹 생성
    func getGroupRanking(completion: @escaping () -> Void){
        let dispatchGroup = DispatchGroup()
        
        dispatchGroup.enter()
        StudyGroupManager.showMyStudyGroup { res in
            switch res{
            case .success(let data):
                self.studyGroup = data
            case .failure(_):
                print("스터디 그룹 목록 불러오기 실패")
            }
            dispatchGroup.leave()
        }
        
        //각 그룹 랭킹 생성
        
        for i in 0..<studyGroup.count{
            dispatchGroup.enter()
            RankingManager.createGroupRanking(dateString: dateFormatter.string(from: Date()), groupID: studyGroup[i].id){res in
                switch res{
                case .success(_):
                    print("그룹 랭킹 생성 성공")
                case .failure(_):
                    print("그룹 랭킹 생성 실패")
                }
                dispatchGroup.leave()
            }
        }
        
        //그룹 랭킹 불러오기
        dispatchGroup.enter()
        RankingManager.getGroupRanking { res in
            switch res{
            case .success(let data):
                self.rankingList = data
                print(data)
                self.setRanking()
                self.tableView.reloadData()
                print("그룹 랭킹 불러오기 성공")
            case .failure(let err):
                print(err)
                print("그룹 랭킹 불러오기 실패")
            }
            dispatchGroup.leave()
        }
    }
    
    @IBAction func prevBtnTapped(_ sender: Any) {
        //저번주
        if canToogle{
            if dateFormatter.string(from: endOfWeek!) == dateFormatter.string(from: lastWeekEnd!){
                if rankingTypeBtn.titleLabel?.text == "공부시간"{
                    RankingManager.getPersonalStudyRanking {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                else{
                    RankingManager.getPersonalQuesRanking {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
            }else{
                let paramString = dateFormatter.string(from: lastWeekEnd!)
                print("prevprevprevc")
                print(paramString)
                if rankingTypeBtn.titleLabel?.text == "공부시간"{
                    RankingManager.prevPersonalStudyRanking(paramString: paramString) {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                else{
                    RankingManager.prevPersonalQuesRanking(paramString: paramString) {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                
            }
        }
        else{
            let dispatchGroup = DispatchGroup()
            let paramString = dateFormatter.string(from: lastWeekEnd!)
            let param: Parameters = ["localDate" : paramString]
            
            dispatchGroup.enter()
            StatisticsManager.createStatistics(param: param) { res in
                switch res{
                case .success(let suc):
                    print("\(paramString)통계 생성 성공")
                case .failure(let err):
                    print("\(paramString)통계 생성 실패")
                }
                dispatchGroup.leave()
            }
            
            for i in 0..<studyGroup.count{
                dispatchGroup.enter()
                RankingManager.createGroupRanking(dateString: paramString, groupID: studyGroup[i].id) { res in
                    switch res{
                    case .success(_):
                        print("그룹 랭킹 생성 성공")
                    case .failure(_):
                        print("그룹 랭킹 생성 실패")
                    }
                    dispatchGroup.leave()
                }
            }
            
            //특정 주차 그룹 랭킹 불러오기
            
        }
        
        curWeekEnd = lastWeekEnd
        lastWeekEnd = calendar.date(byAdding: .day, value: -7, to: curWeekEnd!)
        nextWeekEnd = calendar.date(byAdding: .day, value: 7, to: curWeekEnd!)
        curWeekStart = calendar.date(byAdding: .day, value: -6, to: curWeekEnd!)
        
        weekLabel.text = "[ \(dateFormatter2.string(from: curWeekStart!)) ~ \(dateFormatter2.string(from: curWeekEnd!)) ]"
        
        if dateFormatter.string(from: endOfWeek!) < dateFormatter.string(from: nextWeekEnd!){
            nextBtn.isEnabled = false
        }
        else{
            nextBtn.isEnabled = true
        }
    }
    
    
    @IBAction func nextBtnTapped(_ sender: Any) {
        if canToogle{
            if dateFormatter.string(from: endOfWeek!) == dateFormatter.string(from: nextWeekEnd!){
                if rankingTypeBtn.titleLabel?.text == "공부시간"{
                    RankingManager.getPersonalStudyRanking {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                else{
                    RankingManager.getPersonalQuesRanking {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
            }else{
                let paramString = dateFormatter.string(from: nextWeekEnd!)
                print("prevprevprevc")
                print(paramString)
                if rankingTypeBtn.titleLabel?.text == "공부시간"{
                    RankingManager.prevPersonalStudyRanking(paramString: paramString) {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                else{
                    RankingManager.prevPersonalQuesRanking(paramString: paramString) {res in
                        switch res{
                        case .success(let data):
                            self.rankingList = data
                            self.setRanking()
                            self.tableView.reloadData()
                            
                        case .failure(let err):
                            print(err)
                        }
                    }
                }
                
            }
        }
        else{
            let dispatchGroup = DispatchGroup()
            let paramString = dateFormatter.string(from: nextWeekEnd!)
            let param: Parameters = ["localDate" : paramString]
            
            dispatchGroup.enter()
            StatisticsManager.createStatistics(param: param) { res in
                switch res{
                case .success(_):
                    print("\(paramString)통계 생성 성공")
                case .failure(_):
                    print("\(paramString)통계 생성 실패")
                }
                dispatchGroup.leave()
            }
            
            for i in 0..<studyGroup.count{
                dispatchGroup.enter()
                RankingManager.createGroupRanking(dateString: paramString, groupID: studyGroup[i].id) { res in
                    switch res{
                    case .success(_):
                        print("그룹 랭킹 생성 성공")
                    case .failure(_):
                        print("그룹 랭킹 생성 실패")
                    }
                    dispatchGroup.leave()
                }
            }
            
            //특정 주차 그룹 랭킹 불러오기
            
        }
        curWeekEnd = nextWeekEnd
        curWeekStart = calendar.date(byAdding: .day, value: -6, to: curWeekEnd!)
        lastWeekEnd = calendar.date(byAdding: .day, value: -7, to: curWeekEnd!)
        nextWeekEnd = calendar.date(byAdding: .day, value: 7, to: curWeekEnd!)
        
        weekLabel.text = "[ \(dateFormatter2.string(from: curWeekStart!)) ~ \(dateFormatter2.string(from: curWeekEnd!)) ]"
        
        
        if dateFormatter.string(from: endOfWeek!) <= dateFormatter.string(from: nextWeekEnd!){
            nextBtn.isEnabled = false
        }
        else{
            nextBtn.isEnabled = true
        }
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 7
    }
    
    func setRanking(){
        if rankingList.count > 0{
            goldPersonLabel.text = "1. \(rankingList[0].name)"
        }
        if rankingList.count > 1{
            silverPersonLabel.text = "2. \(rankingList[1].name)"
        }
        if rankingList.count > 2{
            bronzePersonLabel.text = "3. \(rankingList[2].name)"
        }
        
        tableView.reloadData()
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "RankingTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! RankingTableViewCell
        
        if (indexPath.row) + 3 < rankingList.count{
            cell.rankingName.text = "\(indexPath.row + 3). \(rankingList[indexPath.row + 3].name)"
        }else{
            cell.rankingName.text = "\(indexPath.row + 3). -"
        }
        
        cell.rankingImage.image = UIImage(named: "person")
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("tap~!@!~#~!#@!~!@#~!#~!@#~!@#~@!#!#~!@#")
        print(")(*&(*7298&$298742974298470ㅂ98749187491827")
    }
}
