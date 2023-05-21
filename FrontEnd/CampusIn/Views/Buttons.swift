//
//  Buttons.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/21.
//

import UIKit


let addBtn: UIButton = {
    let btn = UIButton(frame: CGRect(x: 0, y: 0, width: 600, height: 600))
    btn.layer.masksToBounds = true
    btn.layer.cornerRadius = 30
    btn.tintColor = .white
    btn.backgroundColor = .gray
    btn.layer.shadowRadius = 6
    btn.layer.shadowOpacity = 0.3
    btn.setImage(UIImage(systemName: "pencil.tip.crop.circle"), for: .normal )
    btn.setPreferredSymbolConfiguration(.init(pointSize: 30, weight: .regular, scale: .default), forImageIn: .normal)
    
    return btn
}()
