//
//  TimerManager.swift
//  CampusIn
//
//  Created by 이동현 on 2023/05/20.
//

import Foundation

class TimerManager{
    let defaults = UserDefaults.standard
    let encoder = JSONEncoder()
    let decoder = JSONDecoder()
    
    func getArray() -> [TimerData]{
        if let savedArray = defaults.object(forKey: "TimerData") as? Data{
            if let array = try? decoder.decode([TimerData].self, from: savedArray){
                return array
            }
        }
        return []
    }
    
    func updateArray(array: [TimerData]){
//        let newTimer = TimerData(label: title, cnt: 0)
//        array.append(newTimer)
        if let encodeArray = try? encoder.encode(array){
            defaults.set(encodeArray, forKey: "TimerData")
        }
    }
}
