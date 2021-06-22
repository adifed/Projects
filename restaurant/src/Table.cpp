#include "../include/Table.h"
#include <iostream>
using namespace std;

Table::Table(int t_capacity): capacity(t_capacity) ,open(false) ,customersList(),orderList() {}

//rule of 5

//copy constructor
Table::Table(const Table &table_):capacity(table_.capacity) ,open(table_.open),customersList() ,orderList(table_.orderList)
{ copy_customersList(table_);} //inaitilaze the customersList


Table & Table:: operator=(const Table &table_){//operator=
    if(this == &table_)
        return *this;
    clear2();
    capacity = table_.capacity ;
    open = table_.open ;
    for(unsigned int i = 0 ; i < table_.orderList.size() ; i++) {
        orderList.push_back(table_.orderList.at(i));
    }
    copy_customersList(table_);//copy the customersList
    return *this;
}


Table::~Table() { //destructor
    clear2();
}


Table:: Table (Table && rhs):capacity() ,open(false) ,customersList(),orderList(){//move_copy
    steal(rhs);
}


Table& Table::operator=(Table && rhs){//move operator=
    clear2();
    steal(rhs);
    return *this;
}


void Table:: copy_customersList(const Table &table_){ //copy the customersList
    for(unsigned int i = 0 ; i < table_.customersList.size() ; i++ )
        customersList.push_back(table_.customersList.at(i)->clone());
}

void Table:: steal (Table &table_){// move the pointers of the objects
    vector<Customer*> curr_Cust =this->getCustomers();
    vector<Customer*> table_Cust = table_.getCustomers();

    for (unsigned int i = 0 ; i < table_Cust.size() ; i++)//steal the pointers
        curr_Cust.push_back(table_Cust.at(i));

    for ( unsigned int i = 0 ; i < table_Cust.size(); i++ ) {//delete the pointers
        table_Cust.at(i) = nullptr;
        table_Cust.clear();
    }
}


void Table:: clear2(){ // delete the vector customersList
    for (unsigned int i = 0 ; i<customersList.size() ; i++){
        if(customersList[i]!= nullptr) {
            delete customersList.at(i);
            customersList.at(i) = nullptr;
        }
    }
    if( (int) customersList.size() != 0)
        customersList.clear();
}

//rule of 5

int Table:: getCapacity() const{

    return capacity;
}

void Table:: addCustomer(Customer* customer){
    if((int) customersList.size() !=  capacity )
        customersList.push_back(customer);
}

//check if a cell is null
void Table:: removeCustomer(int id){
    for(unsigned int i = 0 ; i < customersList.size();i++){
        if(customersList.at(i)->getId() == id){//found the customer you want to remove
            customersList.erase(customersList.begin()+i);
        }
    }

}

Customer* Table:: getCustomer(int id){ // get a pointer to a customer in the customer vector
    bool flag = true;
    Customer* customer = nullptr;
    for(unsigned int i = 0 ; i < customersList.size() && flag ;i++){
        if(customersList.at(i)->getId()== id){
            flag=false;
            customer=customersList.at(i);
        }
    }
    return customer;
}

vector<Customer*>& Table:: getCustomers(){//ask what is returned
    return customersList;
}

vector<OrderPair>& Table:: getOrders(){
    return orderList;
}

Dish Table::find_dish(int id_dish, const vector<Dish> &menu ){// given dish id return the object dish
    for(unsigned int i = 0 ;i< menu.size() ; i++){
        if(menu.at(i).getId() == id_dish){
            return menu.at(i);
        }
    }
    return Dish();
}

void Table::order(const vector<Dish> &menu){
    for( unsigned int i = 0 ; i < customersList.size() ; i++) {
        vector<int> input_vec = customersList.at(i)->order(menu); // received vector of ids of the type
        for (unsigned int j = 0; j < input_vec.size(); j++) {
            if (input_vec.at(j) != -1) {//meaning the customer ordered
                Dish dish = find_dish(input_vec.at(j), menu); // received a dish
                OrderPair o = OrderPair(customersList.at(i)->getId(), dish);// ask if it insert the pair object
                orderList.push_back(o);
                cout << customersList.at(i)->getName() + " ordered " + dish.getName() << endl;
            }
        }
    }
}

void Table::setOrders(vector<OrderPair> vec){//delete the curr filed of orderlist and initialize it in a new vector
    orderList.clear();
    for (unsigned int i = 0 ; i < vec.size() ; i++){
        orderList.push_back(vec.at(i));
    }
}

void Table:: openTable(){
    open=true;
}

void Table:: closeTable(){
    open = false;
}

int Table:: getBill(){
    int bill = 0;
    for(unsigned int i = 0 ; i < orderList.size();i++) {
        bill= bill + orderList.at(i).second.getPrice();
    }
    return bill;
}

bool Table:: isOpen(){
    return open;
}

