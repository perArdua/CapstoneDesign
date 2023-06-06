//
//  RankingViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/05.
//

import UIKit

class RankingViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    let arr: [String] = ["4등. 남궁00", "5등. 이00"]
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
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "RankingTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! RankingTableViewCell
        cell.rankingName.text = arr[indexPath.row]
        cell.rankingImage.image = UIImage(named: "person")
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("tap~!@!~#~!#@!~!@#~!#~!@#~!@#~@!#!#~!@#")
        print(")(*&(*7298&$298742974298470ㅂ98749187491827")
    }
    
}
