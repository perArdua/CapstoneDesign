//
//  StudyRecordDelegate.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/03.
//

import Foundation

protocol StudyRecordDelegate: AnyObject {
    func menuBtnTapped(in cell: StudyRecordTableViewCell)
}
