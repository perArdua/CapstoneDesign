//
//  StudyGroupTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/04.
//

import UIKit

class StudyGroupTableViewCell: UITableViewCell {

    @IBOutlet weak var groupName: UILabel!
    @IBOutlet weak var groupSize: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
