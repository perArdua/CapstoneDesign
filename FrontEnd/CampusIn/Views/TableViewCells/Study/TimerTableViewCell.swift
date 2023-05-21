//
//  TimerTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/20.
//

import UIKit

class TimerTableViewCell: UITableViewCell {

    
    @IBOutlet weak var startBtn: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timerLabel: UILabel!
    
    weak var timer: Timer?
    weak var delegate: TimerTableViewCellDelegate?
    var flag: Bool = false
    var cnt:Int = 0
    
    override func awakeFromNib() {
        super.awakeFromNib()
        startBtn.addTarget(self, action: #selector(timerBtnTapped), for: .touchUpInside)
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
    
    @objc func timerBtnTapped(){
        delegate?.timerBtnTapped(in: self)
        
        print("timer btn tapped")
        

        
    }
    

}
