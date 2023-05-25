//
//  GeneralPostingCommentAddTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/25.
//

import UIKit

class GeneralPostingCommentAddTableViewCell: UITableViewCell {

    
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var addBtn: UIButton!
    
    weak var delegate: GeneralCommentCellDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        addBtn.addTarget(self, action: #selector(addBtnTapped), for: .touchUpInside)
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    @objc func addBtnTapped(){
        delegate?.addBtnTapped(in: self)
        print(KeyChain.read(key: "token"))
        print("comment add Btn tapped")
    }

}
