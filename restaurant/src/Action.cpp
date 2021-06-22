#include "../include/Action.h"
#include "../include/Table.h"
#include "../include/Restaurant.h"
#include <iostream>
#include <Action.h>

using namespace std;
extern Restaurant* backup;


BaseAction::BaseAction(): errorMsg(""),status(ActionStatus::PENDING)
{}

ActionStatus BaseAction::getStatus() const {
    return status;
}

void BaseAction::complete() {
    status=ActionStatus ::COMPLETED;
}

void BaseAction::error(std::string errorMsg) {
    status = ActionStatus ::ERROR;
    this->errorMsg = errorMsg;
}

string BaseAction::getErrorMsg() const {
    return errorMsg;
}

BaseAction::~BaseAction(){}



/////OpenTable

//constructor
OpenTable::OpenTable(int id, std::vector<Customer *> &customersList):BaseAction(),tableId(id), customers(customersList), error_massage() //constructor
{}

//copy constructor
OpenTable::OpenTable(const OpenTable &other):BaseAction(other),tableId(other.tableId) ,customers(), error_massage(){
    for(unsigned int i = 0 ; i < other.customers.size() ; i++ )
        customers.push_back(other.customers.at(i)->clone());
}


//distructor

OpenTable::~OpenTable() {
    clear_opentable();
}



void OpenTable:: clear_opentable(){ // delete the vector customersList
    for (unsigned int i = 0 ; i<customers.size() ; i++){
        if(customers[i]!= nullptr) {
            delete customers[i];
            customers[i] = nullptr;
        }
    }
    customers.clear();
}


BaseAction* OpenTable::clone() {

    return new OpenTable(*this);

}

bool OpenTable::IsLegalTable(int table_Id,Restaurant &restaurant){ //check if the table is open/doesn't exist
    bool flag=true; // will help us know if the table exist/close
    if(restaurant.getTable(tableId)== nullptr)//the table doesnt exist
        flag=false;
    if(restaurant.getTable(table_Id)->isOpen())//the table is open
        flag=false;
    return flag;
}
void OpenTable::act(Restaurant &restaurant) {//abstract method- opens a table
    bool flag=IsLegalTable(tableId,restaurant);
    if(!flag){ //means the table is open or doesnt exist
        error("Error: Table does not exist or is already open");
        cout << getErrorMsg() <<endl;
        error_massage = restaurant.getError_massage(); //get the line : Jon,alc
    }
    else{
        restaurant.getTables().at(tableId)->openTable();
        complete();
        for (unsigned int i = 0; i <customers.size() ; ++i) {
            restaurant.getTable(tableId)->addCustomer(customers[i]->clone());
        }

    }

}

string OpenTable::toString() const {
    string s;
    s = "open " + std::to_string(tableId) + " ";
    for (unsigned int i = 0; i < customers.size(); i++) {
        s = s + customers.at(i)->getName() + "," + customers.at(i)->toString()  +" ";
    }
    if(getStatus()== ActionStatus ::COMPLETED)
        s = s + "Completed";
    if(getStatus()== ActionStatus ::ERROR)
        s = s + error_massage + " Error: Table does not exist or is already open" ;

    return s;
}

/////Order
Order::Order(int id): tableId(id)
{}

BaseAction* Order::clone() {
    return new Order(*this);
}


void Order::act(Restaurant &restaurant) {
    bool flag = IsLegalTable(tableId, restaurant);
    if (!flag) {
        error("Error: Table does not exist or is not open");
        cout << getErrorMsg() << endl;
    }
    else{
        restaurant.getTable(tableId)->order(restaurant.getMenu());// takes an order for this table
        complete();
    }
}


bool Order::IsLegalTable(int table_Id,Restaurant &restaurant){ //check if the table is open/doesn't exist
    bool flag=true; // will help us know if the table exist/close
    if(restaurant.getTable(tableId)== nullptr)//the table doesnt exist
        flag=false;
    if(! restaurant.getTable(table_Id)->isOpen())//the table is open
        flag=false;
    return flag;
}

string Order::toString() const {
    string s=  "order " + std::to_string(tableId);
    if(getStatus()==COMPLETED)
        s=  s +  " Completed" ;
    if(getStatus()== ERROR)
        s= s+ " Error: Table does not exist or is not open";
    return s;
}

/////MoveCustomer
MoveCustomer::MoveCustomer(int src, int dst, int customerId) :srcTable(src), dstTable(dst),id(customerId)
{}

BaseAction* MoveCustomer::clone() {
    return new MoveCustomer(*this);
}

void MoveCustomer::act(Restaurant &restaurant) {

    vector<OrderPair> without_cust;
    vector<OrderPair> only_cust;
    vector<OrderPair> &allOrders = restaurant.getTable(srcTable)->getOrders();
    vector<OrderPair> dst_orders = restaurant.getTable(dstTable)->getOrders();
    // if the tables src or dst are close/dont exist/or the id is not in the table
    bool is_id_exist_src = IsIdExist(restaurant , srcTable ,id ); //return true if exist
    bool is_avl_seats_dst = (int) restaurant.getTable(dstTable)->getCustomers().size() <  restaurant.getTable(dstTable)->getCapacity(); // check for available seats in the table

    if ( ! is_id_exist_src  ||! IsLegalTable(srcTable , dstTable, restaurant) || ! is_avl_seats_dst ) {
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    }
    else {
        for (unsigned int i = 0; i < allOrders.size(); i++) {
            if (allOrders.at(i).first == id) { // if this is the customer we want to move
                only_cust.push_back(allOrders.at(i));// save the orders of the customer we want to move
            }
            else
                without_cust.push_back(allOrders.at(i));//copy the old vec to a new one without the customer we want to move
        }
        restaurant.getTable(srcTable)->setOrders(without_cust);

        without_cust.clear();
        for (unsigned int i = 0 ; i < only_cust.size() ; i++){
            dst_orders.push_back(only_cust[i]);
        }

        restaurant.getTable(dstTable)->setOrders(dst_orders);


        restaurant.getTable(dstTable)->addCustomer(restaurant.getTable(srcTable)->getCustomer(id));//add the customer from the customerList of the table
        restaurant.getTable(srcTable)->removeCustomer(restaurant.getTable(srcTable)->getCustomer(id)->getId()); // remove the customer from the customerList of the table

        if (restaurant.getTable(srcTable)->getCustomers().size() == 0)//if there is no seats in the scr table close it
            restaurant.getTable(srcTable)->closeTable();

        complete();
    }

}

std::string MoveCustomer::toString() const {
    string s;
    if(getStatus()==COMPLETED)
        s ="move " + std::to_string(srcTable) +" " + std::to_string(dstTable) +" " + std::to_string(id)+" " + "Completed" ;
    if(getStatus()==ERROR)
        s = "move " + std::to_string(srcTable) + " " + std::to_string(dstTable) + " "+ std::to_string(id)+ " Error: Cannot move customer";

    return s;
}

bool MoveCustomer::IsLegalTable(int src, int dst, Restaurant &restaurant) {
    bool flag = true; // will help us know if the table exist/close
    Table *src_table = restaurant.getTable(src);
    Table *dst_table = restaurant.getTable(dst);
    if (src_table == nullptr || dst_table == nullptr)//one of the tables src or dst doesnt exist
        flag = false;
    if (!(src_table->isOpen()) || !(dst_table->isOpen()))//one of the tables src or dst are close
        flag = false;
    return flag;
}

bool MoveCustomer::IsIdExist(Restaurant &restaurant , int id_src ,int id_customer ){//if the customer id exist in the table the function return true, else false
    bool flag = false;
    vector<Customer*> table_customers= restaurant.getTable(id_src)->getCustomers();
    for(unsigned int i = 0 ; i < table_customers.size() ; i++){
        if(table_customers.at(i)->getId() == id_customer){ // if we found one customer with this id return true
            flag = true ;
        }
    }
    return flag;
}

//close
Close::Close(int id): tableId(id)
{}

BaseAction* Close::clone() {
    return new Close(*this);
}

void Close::act(Restaurant &restaurant) {
    if ( ! IsLegalTable(tableId, restaurant )){
        error("Error: Table does not exist or is not open");
        cout << getErrorMsg() << endl;
    }
    else{
        cout<< "Table " + std::to_string(tableId) + " was closed. Bill " + std::to_string(restaurant.getTable(tableId)->getBill()) + "NIS" << endl;
        restaurant.getTable(tableId)->getOrders().clear();
        restaurant.getTable(tableId)->closeTable();//close the table and delete customerList
        for (unsigned int i = 0 ; i < restaurant.getTable(tableId)->getCustomers().size() ; i++ ) {
          //  int id_c = restaurant.getTable(tableId)->getCustomers()[i]->getId();
            delete restaurant.getTable(tableId)->getCustomers()[i];
           // restaurant.getTable(tableId)->removeCustomer(id_c);
        }
        restaurant.getTable(tableId)->getCustomers().clear();
        complete();
    }


}

bool Close::IsLegalTable(int table_Id, Restaurant &restaurant) {
    bool flag=true; // will help us know if the table exist/close
    if(restaurant.getTable(tableId)== nullptr)//the table doesnt exist
        flag=false;
    if(! (restaurant.getTable(tableId)->isOpen()))//the table is close
        flag=false;
    return flag;
}

std::string Close::toString() const {
    string s;
    if(getStatus()==COMPLETED)
        s= "close " + std::to_string(tableId) + " Completed" + "";
    if(getStatus()== ERROR)
        s= "close " + std::to_string(tableId) + " Error: Table does not exist or is not open" ;
    return s;
}

//CloseAll
CloseAll:: CloseAll()
{}

BaseAction* CloseAll::clone() {
    return new CloseAll(*this);
}

void CloseAll:: act(Restaurant &restaurant){
    int NumOfTables = restaurant.getNumOfTables();
    for(int i=0; i < NumOfTables ;i++){
        if(restaurant.getTable(i)->isOpen())
            cout << "Table " + std::to_string(i) + " was closed." + " Bill " + std::to_string(restaurant.getTable(i)->getBill()) + "NIS"<< endl;
    }

}

string CloseAll:: toString() const{
    return "closeall Completed " ;
}

//PrintMenu
PrintMenu::PrintMenu()
{} //constructor to PrintMenu


BaseAction* PrintMenu::clone() {
    return new PrintMenu(*this);
}

void PrintMenu:: act(Restaurant &restaurant){ // prins the menu
    for(unsigned int i=0;i<restaurant.getMenu().size();i++){
        Dish dish=restaurant.getMenu()[i];
        cout << dish.getName() + " " + restaurant.DishToString(dish.getType())+ " " + std::to_string(dish.getPrice())+ "NIS" << endl;
    }
    complete();

}

string PrintMenu:: toString() const{
    return "menu Completed ";
}


PrintTableStatus:: PrintTableStatus(int id):tableId(id) //constructor
{}

BaseAction* PrintTableStatus::clone() {
    return new PrintTableStatus(*this);
}

void PrintTableStatus:: act(Restaurant &restaurant){
    if(restaurant.getTable(tableId)->isOpen()== false)
        cout << "Table " + std::to_string(tableId) + " status: closed" << endl;
    else {
        cout << "Table " + std::to_string(tableId) + " status: open" << endl;
        cout << "Customers:" << endl;
        for (unsigned int i = 0; i < restaurant.getTable(tableId)->getCustomers().size(); i++) {
            int id = restaurant.getTable(tableId)->getCustomers().at(i)->getId();//id of the customer
            //first we will print the names of the customers, then the dishes
            cout << std::to_string(id) + " " + restaurant.getTable(tableId)->getCustomers().at(i)->getName() << endl;

        }
        vector<OrderPair> vec = restaurant.getTable(tableId)->getOrders();
        cout << "Orders:" << endl;
        for (unsigned int i = 0; i < vec.size(); i++) {
            int id = vec[i].first;//id of the customer
            //running on the order list
            cout << vec[i].second.getName() +" "+ std::to_string(vec[i].second.getPrice()) + "NIS " + std::to_string(id) << endl;
        }
        cout << "Current Bill: " + std::to_string(restaurant.getTable(tableId)->getBill()) + "NIS " << endl;
        complete();
    }
}


std::string PrintTableStatus:: toString() const{
    return  "status " + std::to_string(tableId) + " Completed";
}

//PrintActionLog
PrintActionsLog::PrintActionsLog()
{}

BaseAction* PrintActionsLog::clone() {
    return new  PrintActionsLog(*this);
}

void PrintActionsLog:: act(Restaurant &restaurant){
    for(unsigned int i=0;i<restaurant.getActionsLog().size();i++){
        cout << restaurant.getActionsLog()[i]->toString() << endl;
    }
    complete();
}


string PrintActionsLog::toString() const
{
    return "log Completed ";
}

//BackupRestaurant
BackupRestaurant:: BackupRestaurant()
{}

BaseAction* BackupRestaurant::clone() {
    return new  BackupRestaurant(*this);
}


void BackupRestaurant:: act(Restaurant &restaurant){
    if(backup == nullptr) {
       backup = new Restaurant(restaurant);
    }
    else {
        *backup = restaurant;
    }
    complete();
}
std::string BackupRestaurant::toString() const{
    return "backup Completed ";
}

//RestoreRastaurant
RestoreResturant::RestoreResturant(){}

BaseAction* RestoreResturant::clone() {
    return new  RestoreResturant(*this);
}

void RestoreResturant:: act(Restaurant &restaurant){
    if(backup== nullptr) {
        error("Error: No backup available ");
        cout << getErrorMsg() << endl ;
    }
    else {
        restaurant = *backup;
        complete();
    }

}

string RestoreResturant:: toString() const{
    string s = "restore ";
    if(getStatus()==ERROR)
        return s + getErrorMsg();
    if(getStatus()==COMPLETED)
        return s+ "Completed ";
    return s;
}
