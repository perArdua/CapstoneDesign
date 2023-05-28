//
//  ToDoTableViewCell.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/25.
//

import UIKit

class ToDoTableViewCell: UITableViewCell {
    
    
    @IBOutlet weak var todoBtn: UIButton!
    
    @IBOutlet weak var todoLable: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        todoBtn.setTitle("", for: .normal)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    @IBAction func todoBtnTapped(_ sender: UIButton) {
        //self.todoBtn.setImage(UIImage(systemName: "checkmark.square"), for: .normal)
    }
    
//    override func prepareForReuse() {
//        todoBtn.setImage(UIImage(systemName: "square"), for: .normal)
//    }
//    override func prepareForReuse() {
//        super.prepareForReuse()
//        todoBtn.setImage(UIImage(systemName: "square"), for: .normal)
//    }
}
