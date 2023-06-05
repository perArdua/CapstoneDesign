//
//  StatisticsViewController.swift
//  CampusIn
//
//  Created by 이동현 on 2023/06/04.
//

import UIKit
import FSCalendar
import Alamofire

class StatisticsViewController: UIViewController {

    @IBOutlet weak var calendar: FSCalendar!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var studyTimeLabel: UILabel!
    @IBOutlet weak var questionLabel: UILabel!
    @IBOutlet weak var adoptedLabel: UILabel!
    @IBOutlet weak var answerLabel: UILabel!

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
        let today = Date()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        let formattedDate = dateFormatter.string(from: today)
        var param:Parameters = [:]
        
        param["localDate"] = dateFormatter.string(from: today)
        getStatistics(param: param, date:today)
    }
    
    func getStatistics(param: Parameters, date: Date){
        StatisticsManager.getStatistics(params: param) { data in
            
            let formatter = DateComponentsFormatter()
            formatter.allowedUnits = [.hour, .minute, .second]
            formatter.unitsStyle = .positional
            formatter.zeroFormattingBehavior = .pad
            
            let formattedSecond = formatter.string(from: TimeInterval(data.totalElapsedTime))!
            
            self.studyTimeLabel.text = formattedSecond
            self.questionLabel.text = String(data.numberOfQuestions)
            self.answerLabel.text = String(data.numberOfAnswers)
            self.adoptedLabel.text = String(data.numberOfAdoptedAnswers)
        }
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY년 MM월 dd일"
        dateLabel.text = dateFormatter.string(from: date)
        
    }
}

extension StatisticsViewController: FSCalendarDelegate, FSCalendarDataSource, FSCalendarDelegateAppearance{
    // 날짜 선택 시 콜백 메소드
    func calendar(_ calendar: FSCalendar, didSelect date: Date, at monthPosition: FSCalendarMonthPosition) {
        
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd"
        
        let dateFormatter2 = DateFormatter()
        dateFormatter2.dateFormat = "YYYY년 MM월 dd일"
        var param: Parameters = [:]
        
        param["localDate"] = dateFormatter.string(from: date)
        getStatistics(param: param, date: date)

        
    }
    
}
