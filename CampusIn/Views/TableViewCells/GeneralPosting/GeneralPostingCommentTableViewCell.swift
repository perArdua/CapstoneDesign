//
//  GeneralPostingCommentTableViewCell.swift
//  CapstonDesign
//
//  Created by 이동현 on 2023/04/10.
//

import UIKit

class GeneralPostingCommentTableViewCell: UITableViewCell {

    @IBOutlet weak var userImg: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var likeCnt: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
