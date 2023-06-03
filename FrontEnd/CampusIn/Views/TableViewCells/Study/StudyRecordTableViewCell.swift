//
//  StudyRecordTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/03.
//

import UIKit

class StudyRecordTableViewCell: UITableViewCell {

    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var menuBtn2 : UIButton!
    @IBOutlet weak var menuBtn : UIButton!
    
    
    var menuBtnTappedClosure: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        menuBtn2.addTarget(self, action: #selector(menuBtnTapped), for: .touchUpInside)
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @objc func menuBtnTapped(){
        print("메뉴 버튼 클릭")
        menuBtnTappedClosure?()
    }

}
