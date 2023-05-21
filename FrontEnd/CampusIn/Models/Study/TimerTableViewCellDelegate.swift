//
//  TimerTableViewCellDelegate.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/20.
//

import Foundation

protocol TimerTableViewCellDelegate: AnyObject {
    func timerBtnTapped(in cell: TimerTableViewCell)
}
