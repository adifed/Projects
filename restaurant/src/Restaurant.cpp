
#include "../include/Restaurant.h"
#include "../include/Action.h"
#include "../include/Dish.h"
#include <iostream>
#include "../include/Table.h"
#include <fstream>
#include <sstream>


using namespace std;

//Rule of five

//Default constructor
Restaurant::Restaurant() : open(false),tables(),menu(),actionsLog(),num_of_Table(0), error_massage() {}


Restaurant::Restaurant(const std::string &configFilePath): open(false),tables(),menu(),actionsLog(),num_of_Table(0),error_massage() {
    ifstream file;
    string line;
    int count = 0;// tell me which part I read , 1- num of tables , 2 tables descript , 3 menu
    int id_dish = 0;
    file.open(configFilePath);
    while (getline(file , line)){//check if it stops the loop
        if( int(line.find("\r")) !=  (-1) )
            line.erase(line.length()-1);
        if (line.size() == 0 || line.at(0) == '#' ) {}//skip the lines that have # or are *empty*
        else if (line.at(0) != '#' && count == 0 && check(line)) {// if you found a line which is not a commend ,read
            num_of_Table = stoi(line);
            count++;
        }

        else if( line.at(0) != '#' && count == 1 && check(line)){
            vector<string> tables_dec;
            for (unsigned int i = 0 ; i< split(line , ',').size() ; i++)// split the string and insert to tabels_dec
                tables_dec.push_back(split(line , ',').at(i));

            for (unsigned int i = 0 ; i < tables_dec.size() ; i++)
                tables.push_back(new Table(stoi(tables_dec.at(i))));// initialize the filed tables
            count++;
        }

        else if(line.at(0) != '#' && count == 2 && check(line)){
            vector<string> menuInput;
            for (unsigned int i = 0 ; i< split(line , ',').size() ; i++)// split the string and insert to tabels_dec
                menuInput.push_back(split(line , ',').at(i));

            //initailaze menu in Dish
            if(type(menuInput[1]) == DishType::SPC) {
                Dish dish = Dish(id_dish, menuInput[0], stoi(menuInput[2]), DishType::SPC);
                menu.push_back(dish);
            }
            else if(type(menuInput[1]) == DishType::BVG) {
                Dish dish = Dish(id_dish, menuInput[0], stoi(menuInput[2]), DishType::BVG);
                menu.push_back(dish);
            }
            else if(type(menuInput[1]) == DishType::ALC) {
                Dish dish = Dish(id_dish, menuInput[0], stoi(menuInput[2]), DishType::ALC);
                menu.push_back(dish);
            }
            else if(type(menuInput[1]) == DishType::VEG) {
                Dish dish = Dish(id_dish, menuInput[0], stoi(menuInput[2]), DishType::VEG);
                menu.push_back(dish);
            }
            id_dish++;//count the dishes thatinsert to menu
        }
    }
}



//Copy constructor
Restaurant::Restaurant(const Restaurant &other):  open(other.open),tables(),menu(),actionsLog(), num_of_Table(other.num_of_Table),error_massage()
{
    for (unsigned int i = 0; i <other.menu.size() ; ++i) { //copy the dishes in the menu to another vector
        menu.push_back(other.menu[i]);
    }

    for(unsigned int i=0;i<other.tables.size();i++){
        Table* table = new Table(*(other.tables[i]));
        tables.push_back(table);
    }

    for(unsigned int i=0;i<other.actionsLog.size();i++){
        actionsLog.push_back(other.actionsLog[i]->clone());

    }
}

//void Restaurant:: setStatus(ActionStatus status , Restaurant ){


//Distructor
Restaurant:: ~Restaurant() {
    menu.clear();
    for (unsigned int i = 0 ; i<tables.size() ; i++){
        delete tables.at(i);
        tables.at(i)= nullptr;
    }
    tables.clear();

    for (unsigned int i = 0; i <actionsLog.size() ; ++i) {
        delete actionsLog.at(i);
        actionsLog.at(i) = nullptr;
    }
    actionsLog.clear();
}

//Copy assignment Operator
Restaurant& Restaurant:: operator=(const Restaurant &other){
    if(this == &other)
        return *this;
    //delete to the complex variables

    menu.clear();
    for (unsigned int i = 0 ; i<tables.size() ; i++){
        delete tables.at(i);
    }
    tables.clear();

    for (unsigned int i = 0; i <actionsLog.size() ; ++i) {
        delete actionsLog.at(i);
    }
    actionsLog.clear();
    // initialize the complex variables
    open = other.open;
    num_of_Table = other.num_of_Table ;
    for (unsigned int i = 0; i <other.menu.size() ; ++i) {
        this->menu.push_back(other.menu[i]);
    }

    for(unsigned int i=0;i<other.tables.size();i++){
        Table* table = new Table(*(other.tables[i]));
        tables.push_back(table);
    }

    for(unsigned int i=0;i<other.actionsLog.size();i++){
        actionsLog.push_back(other.actionsLog[i]->clone());
    }

    return *this;
}

//move_copy
Restaurant::Restaurant (Restaurant && rhs):open(false),tables(),menu(),actionsLog(),num_of_Table(0), error_massage() {
    steal_tables(rhs);
    steal_actions(rhs);
}

//move operator=
Restaurant& Restaurant::operator=(Restaurant && rhs){
    clear_tables();
    clear_actions();
    steal_tables(rhs);
    steal_actions(rhs);
    return *this;
}

void Restaurant:: steal_tables(Restaurant &other) {
    vector <Table*> curr_tables = this->getTables();
    vector <Table*> other_tables = other.getTables();

    for(unsigned int i=0;i<other_tables.size();i++){
        curr_tables.push_back(other_tables[i]);
    }
    for(unsigned int i=0;i<other_tables.size();i++){
        other_tables[i]= nullptr;
        other_tables.clear();
    }
}

void Restaurant:: steal_actions(Restaurant &other) {
    vector <BaseAction*> curr_actions = this->getActionsLog();
    vector <BaseAction*> other_actions = other.getActionsLog();

    for(unsigned int i=0;i<other_actions.size();i++){
        curr_actions .push_back(other_actions[i]);
    }
    for(unsigned int i=0;i<other_actions.size();i++){
        other_actions[i]= nullptr;
        other_actions.clear();
    }
}

void Restaurant:: clear_tables(){
    for(unsigned int i = 0; i < tables.size(); i++){
        if(tables[i]!= nullptr){
            delete tables[i];
            tables[i] = nullptr;
        }
    }
    tables.clear();
}

void Restaurant::clear_actions() {
    for(unsigned int i = 0; i < actionsLog.size(); i++){
        if(actionsLog[i]!= nullptr){
            delete actionsLog[i];
            actionsLog[i] = nullptr;
        }
    }
    actionsLog.clear();
}


vector<string> Restaurant::split(const string &s, char delimiter) { //split the string
    vector<string> tokens;
    string token;
    std::istringstream tokenStream(s);
    while (std::getline(tokenStream, token, delimiter)) {
        tokens.push_back(token);
    }
    return tokens;
}

bool Restaurant::check (string s){ //check if the line of the config start with legal arguments
    if(s.at(0) >= 'A' || s.at(0)<= 'z' || s.at(0) >= 0 || s.at(0) <=9 )
        return true;
    else return false;

}

void Restaurant::start(){
    open=true;
    string action;
    cout << "Restaurant is now open!" << endl;
    getline(cin , action);
    int ind = action.find(' '); //find index of ""
    string sub = action.substr(0,ind); //find the action
    int id_customer = 0;

    while(sub!= "closeall"){ //when we reach closeall we will close the program

        if(sub=="open"){
            action = action.substr(ind+1); // the string without the name of the action
            ind = action.find(' ');
            int tableID = stoi(action.substr(0,ind)); //find which table ordered
             action = action.substr(ind + 1); //
             vector<string> vec = split(action, ' ');//function that returns vector(name,type name,type)
             vector<Customer *> customers;

             if(getTable(tableID)->isOpen()) // if the table is already open we will copy the line
                 error_massage = action;
             for (unsigned int i = 0; i < vec.size() && !getTable(tableID)->isOpen(); i++) {
                 vector<string> vec_customer = split(vec[i], ',');
                 if (vec_customer[1] == "veg") {
                     VegetarianCustomer *vg = new VegetarianCustomer(vec_customer[0], id_customer);//creat a poiner to the customer
                     customers.push_back(vg);
                 }
                else if (vec_customer[1] == "chp") {
                     CheapCustomer *ch = new CheapCustomer(vec_customer[0], id_customer);//creat a poiner to the customer
                     customers.push_back(ch);//insert to the vector of customers
                 }
                else if (vec_customer[1] == "alc") {
                     AlchoholicCustomer *al = new AlchoholicCustomer(vec_customer[0], id_customer);//creat a poiner to the customer
                     customers.push_back(al);//insert to the vector of customers
                 }
                else if (vec_customer[1] == "spc") {
                     SpicyCustomer *sp = new SpicyCustomer(vec_customer[0], id_customer);//creat a poiner to the customer
                     customers.push_back(sp);
                  }
                 id_customer++;
             }

             OpenTable *openTable = new OpenTable(tableID, customers);
             openTable->act(*this);
             actionsLog.push_back(openTable);
        }


        if(sub=="order") {
            int tableID = stoi(action.substr(ind+1)); //find which table ordered
            Order* order = new Order(tableID);
            order->act(*this);
            actionsLog.push_back(order);

        }

        if(sub=="move"){
            action=action.substr(ind+1); //
            ind = action.find(' '); //find the next index of ""
            int src = stoi(action.substr(0,ind)); // src
            action=action.substr(ind+1); //
            ind = action.find(' '); //find the next index of ""
            int dst = stoi(action.substr(0,ind));
            action=action.substr(ind+1); //
            ind = action.find(' '); //find the next index of ""
            int id = stoi(action.substr(0,ind));

            MoveCustomer* move = new MoveCustomer(src,dst,id);
            move->act(*this);
            actionsLog.push_back(move);
        }

        if(sub=="status"){
            int tableID = stoi(action.substr(ind+1)); //find which table ordered
            PrintTableStatus* status = new PrintTableStatus(tableID);
            status-> act(*this);
            actionsLog.push_back(status);
        }


        if(sub=="close"){
            int tableID = stoi(action.substr(ind+1)); //find which table ordered
            Close* close = new Close(tableID);
            close->act(*this);
            actionsLog.push_back(close);
        }

        if(sub=="menu"){
            PrintMenu* menu = new PrintMenu();
            menu->act(*this);
            actionsLog.push_back(menu);
        }

        if(sub=="log"){
            PrintActionsLog* log = new PrintActionsLog();
            log->act(*this);
            actionsLog.push_back(log);
        }

        if(sub=="backup"){
            BackupRestaurant* backup1 = new BackupRestaurant();
            backup1->act(*this);
            actionsLog.push_back(backup1);
        }

        if(sub=="restore"){
            RestoreResturant* restore = new RestoreResturant();
            restore->act(*this);
            actionsLog.push_back(restore);
        }
        getline(cin , action);
        ind = action.find(' '); //find index of ""
        sub = action.substr(0,ind); //find the action
    }

    if(sub == "closeall") {
        CloseAll closeAll = CloseAll();
        closeAll.act(*this);
    }
}

Table* Restaurant::getTable(int ind) {
    return tables[ind];// return the table which is in index-ind
}

vector<Dish>& Restaurant:: getMenu(){
    return menu; // return the vector menu
}

const vector<BaseAction*>& Restaurant:: getActionsLog() const{ // check if its correct with the reference
    return actionsLog;
}

const vector<Table*>& Restaurant:: getTables() const{
    return tables;
}

int Restaurant:: getNumOfTables() const{
    return  num_of_Table;
}

DishType Restaurant::type(std::string s) {// convert the string to a DishType
    if (s == "VEG")
        return DishType::VEG;

    if (s == "ALC")
        return DishType::ALC;

    if (s == "SPC")
        return DishType::SPC;

    if (s == "BVG")
        return DishType::BVG;
    return DishType::VEG;
}

string Restaurant::DishToString(DishType type){
    if(DishType::VEG == type)
        return "VEG";
    if(DishType::ALC == type)
        return "ALC";
    if(DishType::BVG== type)
        return "BVG";
    if(DishType::SPC== type)
        return "SPC";
    return " ";
}

std::string  Restaurant::getError_massage(){
    return error_massage;
}