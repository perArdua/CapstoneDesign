//
//  QuestionPostingCommentTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/12.
//

import UIKit

class QuestionPostingCommentTableViewCell: UITableViewCell {

    @IBOutlet weak var userImg: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var likeCnt: UILabel!
    
    @IBOutlet weak var adoptBtn: UIButton!
    
    @IBOutlet weak var badgeImg: UIImageView!
    var commentID: Int?
    //인증마크 여부
    var is_check: Bool?
    var childComments: [CommentDataContent]?
    
    weak var delegate: QuestionReplyBtnDelegate?
    weak var adoptDelegate: AdoptBtnDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func replyBtnTapped(_ sender: UIButton) {
        print("답글")
        delegate?.replyBtnTapped(in: self)
    }
    
    @IBAction func adoptBtnTapped(_ sender: UIButton) {
        print("채택 버튼눌림")
        adoptDelegate?.adoptBtnTapped(in: self)
    }

    
}
