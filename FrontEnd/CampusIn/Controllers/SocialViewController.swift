//
//  SocialViewController.swift
//  CapstonDesign
//
//  Created by 최다경 on 2023/04/02.
//

//http://localhost:8080/oauth2/authorization/google
//http://localhost:8080/login/oauth2/code/google

import UIKit
import WebKit
import Alamofire

class SocialViewController: UIViewController, WKNavigationDelegate{
    
    
    @IBOutlet weak var webView: WKWebView!
    var socialURL : String?
    

    override func viewWillAppear(_ animated: Bool) {
        let defaults = UserDefaults.standard
        let check = defaults.value(forKey: "isLoggedIn")
        if check as! Bool{
            self.dismiss(animated: false, completion: nil)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        webView.navigationDelegate = self
        
        WKWebsiteDataStore.default()// 웹 쿠키 삭제
              .fetchDataRecords(
                ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()
              ) { records in
                records
                  .forEach {
                    WKWebsiteDataStore.default()
                      .removeData(
                        ofTypes: $0.dataTypes,
                        for: [$0],
                        completionHandler: {}
                      )
                  }
              }
        
        // 구글이 웹뷰에서 OAuth 더이상 안되게 막아서 custom agent로 우회
        let customUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.4 Mobile/15E148 Safari/604.1"
        if #available(iOS 9.0, *) {
            webView.customUserAgent = customUserAgent
        } else {
            // iOS 8 이하에서는 사용자 정의 에이전트 문자열을 설정할 수 없습니다.
            // 대신 HTTP 요청 헤더에서 에이전트 문자열을 설정할 수 있습니다.
            let userAgentDict = ["UserAgent": customUserAgent]
            UserDefaults.standard.register(defaults: userAgentDict)
        }
        guard let socialURL = socialURL else { return }
        loadWeb(socialURL)
    }
    
    func loadWeb(_ url: String){
        let myUrl = URL(string: url)
        let request = URLRequest(url: myUrl!)
        webView.load(request)
    }
    
    //MARK: - web에서 response 받을 때 마다 호출되는 함수
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        if let response = navigationResponse.response as? HTTPURLResponse {
            //print(response)
            
            let raw_token = response.url // token이 포함된 url
            if let url = raw_token {
                let urlString = url.absoluteString // 해당 url을 string 형태로 바꿔줌
                
                if urlString.count >= 34{//토큰을 포함한 유효한 url인지 검증하는 과정
                    //해당 url이 token 키워드를 포함하는지 검증
                    let checkIdxStart = urlString.index(urlString.startIndex, offsetBy: 23)
                    let checkIdxEnd = urlString.index(urlString.startIndex, offsetBy: 28)
                    let checkStr = urlString[checkIdxStart..<checkIdxEnd]
                    
                    print("***CHECK***")
                    if checkStr == "token"{//token 포함시
                        print("***token_check_passed***")
                        let tokenIdx = urlString.index(urlString.startIndex, offsetBy: 29)
                        let token = urlString[tokenIdx...] // token이 포함된 url애서 token만 분리
                        let defaults = UserDefaults.standard
                        
                        //KeyChain에 토큰 저장, 로그아웃시 삭제 요망
                        KeyChain.create(key: "token", token: String(token))
                        print("key chain value")
                        print(KeyChain.read(key: "token"))
                        
                        // user default에 로그인 여부 저장
                        defaults.set(true, forKey: "isLoggedIn")
                        print("login succeed")
                        print("token save succeed")
                        
                        // 로그인 완료 메시지 출력
                        let alert = UIAlertController(title: "로그인 결과", message: "성공하셨습니다.", preferredStyle: .alert)
                        // 확인 버튼 클릭 시 액션 구현
                        let action1 = UIAlertAction(title: "확인", style: .default) { (action: UIAlertAction!) -> Void in
                            BoardManager.initBoard()
                            //닉네임 입력 화면 이동
                            UserManager.isExistingMember(){ result in
                                switch result{
                                case .success(let isExisting):
                                    if isExisting {
                                        self.dismiss(animated: false, completion: nil)
                                    } else{
                                        let nickVC = self.storyboard!.instantiateViewController(withIdentifier: "NickNameViewController") as! NickNameViewController
                                        nickVC.modalTransitionStyle = .coverVertical
                                        nickVC.modalPresentationStyle = .fullScreen
                                        self.present(nickVC, animated: false, completion: nil)
                                    }
                                case .failure(let error):
                                    print("error: \(error)")
                                }
                                
                            }
                            

                        }
                        alert.addAction(action1)
                        present(alert, animated: false, completion: nil)
                        
                    }
                }
                
            } else {
                print("nil error")
            }
        }
        decisionHandler(.allow)
    }
    
    @IBAction func backBtnTapped(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
}
