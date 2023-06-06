//
//  BookDetailViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/06/06.
//

import UIKit

class BookDetailViewController: UIViewController {

    @IBOutlet weak var bookTitle: UILabel!
    var bookName: String?
    var sellerName: String?
    var bookPrice: String?
    var bookImg: UIImage?
    @IBOutlet weak var detailPrice: UILabel!
    @IBOutlet weak var detailImg: UIImageView!
    @IBOutlet weak var msgBtn: UIButton!
    @IBOutlet weak var detailSellerName: UILabel!
    @IBOutlet weak var detailName: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        bookTitle.textColor = .white
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print("will appear")
        print(bookName)
        msgBtn.tintColor = UIColor(named: "btnColor")
        detailImg.image = bookImg
        detailPrice.text = bookPrice
        detailName.text = bookName
        detailSellerName.text = sellerName
    }

    @IBAction func sendMsgBtnTapped(_ sender: UIButton) {
        
    }
    
}
