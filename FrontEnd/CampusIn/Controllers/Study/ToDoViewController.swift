//
//  ToDoViewController.swift
//  CampusIn
//
//  Created by 최다경 on 2023/05/25.
//

import UIKit

class ToDoViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    var todoItems: [Todo] = []
    override func viewDidLoad() {
        super.viewDidLoad()
        let defaults = UserDefaults.standard
        // 테이블뷰 설정
        tableView.delegate = self
        tableView.dataSource = self

}
    override func viewWillAppear(_ animated: Bool) {
        prepareTableView()
    }
    
    //MARK: - set tableView
    func prepareTableView() {
            TodoManager.getAllTodoList { [weak self] result in
                switch result {
                case .success(let todoList):
                    // Todo 목록을 성공적으로 가져온 경우
                    self?.todoItems = todoList.reversed()
                    DispatchQueue.main.async {
                        self?.tableView.reloadData()
                    }
                case .failure(let error):
                    // Todo 목록을 가져오는데 실패한 경우
                    print("Error: \(error)")
                }
            }
        }
    
    //MARK: - scroll tableView to bottom
    func scrollToBottom() {
        guard todoItems.count > 1 else { return }
        let lastIndexPath = IndexPath(row: todoItems.count - 1, section: 0)
        tableView.scrollToRow(at: lastIndexPath, at: .bottom, animated: true)
    }

    //MARK: - implement action when add button tapped
    @IBAction func addBtnTapped(_ sender: UIButton) {
        let defaults = UserDefaults.standard
        
        let userId: Int? = defaults.value(forKey: "userId") as! Int
        let alertController = UIAlertController(title: "Add Todo", message: nil, preferredStyle: .alert)
            alertController.addTextField { textField in
                textField.placeholder = "Enter Todo"
            }
            
        let addAction = UIAlertAction(title: "Add", style: .default) { [weak self] _ in
            if let todoText = alertController.textFields?.first?.text {
                TodoManager.addTodoItem(title: todoText) { [weak self] result in
                    switch result {
                    case .success(let todoId):
                        let todo = Todo(userId: userId!, todoId: todoId, title: todoText, completed: false)
                        self?.todoItems.append(todo)
                        DispatchQueue.main.async {
                            self?.tableView.reloadData()
                            self?.scrollToBottom()
                        }
                    case .failure(let error):
                        print("Error: \(error)")
                    }
                }
            }
        }
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        alertController.addAction(addAction)
        alertController.addAction(cancelAction)
        present(alertController, animated: true, completion: nil)
    }
    
    //MARK: - implement action when done buttom tapped
    @IBAction func doneBtnTapped(_ sender: UIButton) {
        var flag: Bool = true
        if let cell = sender.superview?.superview as? ToDoTableViewCell, let indexPath = tableView.indexPath(for: cell) {
            if todoItems[indexPath.row].completed{
                flag = false
            }else{
                flag = true
            }
            TodoManager.updateTodoItem(title: cell.todoLable.text ?? "", isCompleted: flag, todoId: todoItems[indexPath.row].todoId) { result in
                switch result {
                case .success:
                    self.prepareTableView()
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                    }
                case .failure(let error):
                    print(error)
                }
            }
        }
    }
    
}


extension ToDoViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return todoItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "ToDoTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! ToDoTableViewCell
       
        let todo = todoItems[indexPath.row]
        cell.todoLable?.text = todo.title
        
        // Completed 상태에 따라 버튼 이미지 설정
        let imageName = todo.completed ? "checkmark.square" : "square"
        let image = UIImage(systemName: imageName)
        cell.todoBtn.setImage(image, for: .normal)
        
        //Completed 상태에 따라 text 색상 조정
        cell.todoLable?.textColor = todo.completed ? UIColor.gray : UIColor.black
        
    
        cell.selectionStyle = .none
       
        return cell
    }
    
    //MARK: - implement swipe delete and edit action
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let deleteAction = UIContextualAction(style: .destructive, title: "삭제") { [weak self] (_, _, completionHandler) in
            let todo = self?.todoItems[indexPath.row]
            
            let alertController = UIAlertController(title: "Todo 삭제하기", message: "정말 삭제하시겠습니까?", preferredStyle: .alert)
            
            let confirmAction = UIAlertAction(title: "확인", style: .destructive) { [weak self] _ in
                // TodoManager를 사용하여 Todo 항목 삭제
                TodoManager.deleteTodoItem(todoId: todo?.todoId ?? 0) { result in
                    switch result {
                    case .success:
                        // Todo 삭제에 성공한 경우, 배열에서 해당 항목을 제거하고 테이블 뷰를 업데이트
                        self?.todoItems.remove(at: indexPath.row)
                        DispatchQueue.main.async {
                            tableView.deleteRows(at: [indexPath], with: .fade)
                        }
                    case .failure(let error):
                        print("Error: \(error)")
                    }
                }
            }
            
            let cancelAction = UIAlertAction(title: "취소", style: .cancel, handler: nil)
            
            alertController.addAction(confirmAction)
            alertController.addAction(cancelAction)
            
            self?.present(alertController, animated: true, completion: nil)
            
            completionHandler(true)
        }
        
        deleteAction.backgroundColor = .red
        
        let editAction = UIContextualAction(style: .normal, title: "수정") { [weak self] (_, _, completionHandler) in
            let alertController = UIAlertController(title: "Todo 수정", message: nil, preferredStyle: .alert)
            alertController.addTextField { textField in
                textField.placeholder = "Enter New Todo"
            }
            
            let saveAction = UIAlertAction(title: "수정", style: .default) { _ in
                if let newTodoText = alertController.textFields?.first?.text {
                    // TodoManager를 사용하여 Todo 항목 수정
                    TodoManager.updateTodoItem(title: newTodoText, isCompleted: false, todoId: self?.todoItems[indexPath.row].todoId ?? 0) { result in
                        switch result {
                        case .success:
                            // Todo 수정에 성공한 경우, 해당 항목의 제목을 업데이트하고 테이블 뷰를 리로드합니다.
                            self?.todoItems[indexPath.row].title = newTodoText
                            DispatchQueue.main.async {
                                tableView.reloadData()
                            }
                        case .failure(let error):
                            print("Error: \(error)")
                        }
                    }
                }
            }
            
            let cancelAction = UIAlertAction(title: "취소", style: .cancel, handler: nil)
            
            alertController.addAction(saveAction)
            alertController.addAction(cancelAction)
            
            self?.present(alertController, animated: true, completion: nil)
            
            completionHandler(true)
        }
        
        editAction.backgroundColor = .blue
        
        let configuration = UISwipeActionsConfiguration(actions: [deleteAction, editAction])
        return configuration
    }

}

