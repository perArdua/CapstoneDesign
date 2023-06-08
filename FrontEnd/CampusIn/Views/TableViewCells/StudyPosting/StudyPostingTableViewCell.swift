//
//  StudyPostingTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/08.
//

import UIKit

class StudyPostingTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var commentLabel: UILabel!
    
    //cell에서 viewDidLoad와 비슷한 역할을 하는 코드
    //만약 스토리보드로 만들때 밑 함수에 원하는 코드 작성. 모두 코드로 할거면 사용 x
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }

}
