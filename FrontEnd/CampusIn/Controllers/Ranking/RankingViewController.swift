//
//  RankingViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/05.
//

import UIKit

class RankingViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    let arr: [String] = ["4등. 남궁00", "5등. 이00"]
    var rankingID: Int?
    var rankingList: [RankingContent] = []
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var bronzePersonLabel: UILabel!
    @IBOutlet weak var silverPersonLabel: UILabel!
    @IBOutlet weak var goldPersonLabel: UILabel!
    @IBOutlet weak var toggleBtn: UIButton!
    @IBOutlet weak var prevBtn: UIButton!
    @IBOutlet weak var nextBtn: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        prevBtn.setTitle("", for: .normal)
        nextBtn.setTitle("", for: .normal)
        
        goldPersonLabel.text = "-"
        silverPersonLabel.text = "-"
        bronzePersonLabel.text = "-"
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        RankingManager.createRanking { [self] res in
            switch res{
            case .success(let id):
                rankingID = id
            case .failure(let err):
                print(err)
            }
        }
        
        getCurPersonalStudyRank()
    }
    
    // MARK: - 랭킹 조회 함수
    //현재 주차 개인 공부 시간 랭킹 조회
    func getCurPersonalStudyRank(){
        RankingManager.getPersonalStudyRanking { res in
            switch res{
            case .success(let data):
                self.rankingList = data
                self.setRanking()
            case .failure(let err):
                print(err)
            }
        }
    }
    
    //이전 주차 개인 공부 시간 랭킹 조회
    func getPrevPersonalStudyRank(){

    }
    
    //현재 주차 개인 질의 응답 랭킹 조회
    func getCurPersonalQuesRank(){
        
    }
    
    //이전 주차 개인 질의 응답 랭킹 조회
    func getPrevPersonalQuesRank(){
        
    }
    
    //이전 주차 그룹 공부 시간 랭킹 조회
    func getPrevGroupStudyRank(){
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 7
    }
    
    func setRanking(){
        if rankingList.count > 1{
            goldPersonLabel.text = rankingList[0].name
        }
        if rankingList.count > 2{
            silverPersonLabel.text = rankingList[1].name
        }
        if rankingList.count > 3{
            bronzePersonLabel.text = rankingList[2].name
        }
        
        tableView.reloadData()
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "RankingTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! RankingTableViewCell
        
        if (indexPath.row + 1) + 3 > rankingList.count{
            cell.rankingName.text = rankingList[indexPath.row + 3].name
        }
        
        cell.rankingImage.image = UIImage(named: "person")
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("tap~!@!~#~!#@!~!@#~!#~!@#~!@#~@!#!#~!@#")
        print(")(*&(*7298&$298742974298470ㅂ98749187491827")
    }
}
