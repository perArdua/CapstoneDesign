//
//  BookStoreTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/06.
//

import UIKit

class BookStoreTableViewCell: UITableViewCell {

    @IBOutlet weak var sellerName: UILabel!
    @IBOutlet weak var bookPrice: UILabel!
    @IBOutlet weak var bookName: UILabel!
    @IBOutlet weak var bookImgView: UIImageView!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
