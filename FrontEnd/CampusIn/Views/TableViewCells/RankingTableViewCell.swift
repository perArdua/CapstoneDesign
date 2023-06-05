//
//  RankingTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/05.
//

import UIKit

class RankingTableViewCell: UITableViewCell {

    @IBOutlet weak var rankingName: UILabel!
    @IBOutlet weak var rankingImage: UIImageView!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
