//
//  ReportedCommentTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import UIKit

class ReportedCommentTableViewCell: UITableViewCell {

    @IBOutlet weak var commentLabel: UILabel!
    @IBOutlet weak var nicknameLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        
    }

}
