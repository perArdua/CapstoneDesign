//
//  AdoptCommentTableViewCell.swift
//  CampusIn
//
//  Created by 이동현 on 2023/09/08.
//

import UIKit

class AdoptCommentTableViewCell: UITableViewCell {

    @IBOutlet weak var userImg: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var likeCnt: UILabel!
    
    @IBOutlet weak var likeBtn: UIButton!
    @IBOutlet weak var checkMark: UIImageView!
    
    @IBOutlet weak var replyBtn: UIButton!
    @IBOutlet weak var resLabel: UILabel!
    var commentID: Int?
    //인증마크 여부
    var is_check: Bool?
    var childComments: [CommentDataContent]?
    
    weak var delegate: AdoptReplyBtnDelegate?
    
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
