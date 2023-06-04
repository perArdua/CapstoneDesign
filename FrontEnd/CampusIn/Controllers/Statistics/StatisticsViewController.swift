//
//  StatisticsViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/04.
//

import UIKit
import FSCalendar

class StatisticsViewController: UIViewController {

    @IBOutlet weak var calendar: FSCalendar!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var studyTimeLabel: UILabel!
    @IBOutlet weak var questionLabel: UILabel!
    @IBOutlet weak var adoptedLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        calendar.delegate = self
        calendar.dataSource = self
        setupCalendar()
        setUp()
        
        // Do any additional setup after loading the view.
    }
    
    // MARK: - calendar UI 설정
    func setupCalendar(){
        //켈린더 헤더 설정
        calendar.appearance.headerDateFormat = "YYYY년 MM월"
        calendar.appearance.headerTitleAlignment = .center
        calendar.appearance.headerTitleColor = .black
        calendar.appearance.headerMinimumDissolvedAlpha = 0.0
        
        //요일 글씨 색 설정
        calendar.appearance.titleDefaultColor = .gray  // 평일
        calendar.appearance.titleWeekendColor = .red    // 주말
    }
    
    
    // MARK: - viewDidLoad 실행 시 calendar 외의 UI 설정
    func setUp(){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY년 MM월 dd일"
        let date = Date()
        let formattedDate = dateFormatter.string(from: date)
        
        //오늘 날짜의 통계 데이터를 불러오는 API콜
        
        dateLabel.text = formattedDate
    }
    
}

extension StatisticsViewController: FSCalendarDelegate, FSCalendarDataSource, FSCalendarDelegateAppearance{
    // 날짜 선택 시 콜백 메소드
    func calendar(_ calendar: FSCalendar, didSelect date: Date, at monthPosition: FSCalendarMonthPosition) {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        
        let dateFormatter2 = DateFormatter()
        dateFormatter2.dateFormat = "YYYY년 MM월 dd일"
        
        dateLabel.text = dateFormatter2.string(from: date)
        
        //API콜을 통해서 선택한 날짜의 통계 Data를 가져오기
        
        
    }
    
}
