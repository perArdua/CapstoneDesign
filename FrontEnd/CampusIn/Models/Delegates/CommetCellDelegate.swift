//
//  CommetCellDelegate.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/06.
//

import Foundation

protocol GeneralReplyBtnDelegate: AnyObject {
    func replyBtnTapped(in cell: GeneralPostingCommentTableViewCell)
}

protocol StudyReplyBtnDelegate: AnyObject {
    func replyBtnTapped(in cell: StudyPostingCommentTableViewCell)
}

protocol QuestionReplyBtnDelegate: AnyObject {
    func replyBtnTapped(in cell: QuestionPostingCommentTableViewCell)
}

protocol AdoptReplyBtnDelegate: AnyObject {
    func replyBtnTapped(in cell: AdoptCommentTableViewCell)
}

protocol AdoptBtnDelegate: AnyObject {
    func adoptBtnTapped(in cell: QuestionPostingCommentTableViewCell)
}

