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
    
    var commentID: Int?
    //인증마크 여부
    var is_check: Bool?
    var childComments: [CommentDataContent]?
    
    weak var delegate: QuestionReplyBtnDelegate?
    
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
    
}
