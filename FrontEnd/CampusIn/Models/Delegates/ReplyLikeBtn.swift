//
//  ReplyLikeBtn.swift
//  CampusIn
//
//  Created by 최다경 on 2023/09/09.
//

import Foundation

protocol ReplyLikeBtn: AnyObject{
    func likeBtnTapped(in cell: ReplyCommentTableViewCell)
}
