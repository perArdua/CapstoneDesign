//
//  CommetCellDelegate.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/06.
//

import Foundation

protocol GeneralCommentCellDelegate: AnyObject {
    func addBtnTapped(in cell: GeneralPostingCommentAddTableViewCell)
}

protocol ReplyBtnDelegate: AnyObject {
    func replyBtnTapped(in cell: GeneralPostingCommentTableViewCell)
}


