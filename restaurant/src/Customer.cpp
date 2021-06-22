#include "../include/Customer.h"
#include <iostream>
using namespace std;


Customer::Customer(string c_name, int c_id):name(c_name),id(c_id)
{}

int Customer::getId() const {
    return id;
}

string Customer::getName() const {
    return name;
}
Customer::~Customer(){}

VegetarianCustomer::VegetarianCustomer(string name, int id):Customer(name,id){}

Customer* VegetarianCustomer::clone(){
    return new VegetarianCustomer(*this);
}

vector<int> VegetarianCustomer:: order(const vector<Dish> &menu){
    vector<int> veg={-1 , -1};
    int min_id_veg_dish = INT8_MAX;//smallest id veg dish
    int max_price_bev=-1; //most expensive beverage
    int id_bev = INT8_MAX; // id of the most expensive beverage

    for(unsigned int i = 0; i<menu.size();i++){
        int  id_Dish= menu.at(i).getId();
        int price_Dish = menu.at(i).getPrice();

        if(menu.at(i).getType()==VEG && min_id_veg_dish > id_Dish)//look for the smallest id veg dish
            min_id_veg_dish = id_Dish;

        if(menu.at(i).getType() == BVG && max_price_bev < price_Dish ) {//look for the most expensive  beverage
            max_price_bev = price_Dish;
            id_bev = id_Dish;
        }

        if(menu.at(i).getType() == BVG && max_price_bev == price_Dish ) {//in case that the prices are equal
            if (id_bev > id_Dish) {
                max_price_bev = price_Dish;
                id_bev = id_Dish;
            }
        }
    }
    if(min_id_veg_dish !=INT8_MAX && id_bev != INT8_MAX )//if he completed his order
        return veg={min_id_veg_dish,id_bev};
    return veg;
}

string VegetarianCustomer::toString() const {
    return "veg";
}

CheapCustomer::CheapCustomer(std::string name, int id):Customer(name,id) , numOfOrdes(0){}

Customer *CheapCustomer::clone() {
    return new CheapCustomer( *this);
}

vector<int> CheapCustomer::order(const std::vector<Dish> &menu){
    vector<int> Cheap_vec={-1};
    int min= INT8_MAX;
    int id_min = -1;
    if(numOfOrdes == 0) {
        for (unsigned int i = 0; i < menu.size(); i++) {
            if (menu.at(i).getPrice() < min) { //checking who is the cheapest meal
                min = menu.at(i).getPrice();
                id_min = menu.at(i).getId();
            }
        }
    }
    if(id_min != INT8_MAX){ // make sure the customer ordered a dish
        Cheap_vec[0]=id_min;
        numOfOrdes++;
    }
    return Cheap_vec; // vector that contains the id of the meal ordered
}

string CheapCustomer::toString() const {
    return "chp";
}


SpicyCustomer::SpicyCustomer(std::string name, int id):Customer(name,id),numOfOrder(0){}

Customer *SpicyCustomer::clone() {
    return new SpicyCustomer(*this);
}

vector<int> SpicyCustomer:: order(const std::vector<Dish> &menu) {
    vector<int> Spicy_vec={-1};

    if (numOfOrder == 0) { // if it's the first order
        int max_price = -1; //the price of the most expensive dish
        int exp_id = INT8_MAX; //the id of the most expensive dish

        for (unsigned int i = 0; i < menu.size(); i++) {
            int price = menu.at(i).getPrice();//the price of the dish
            int id_dish = menu.at(i).getId();// the id of the dish

            if (price == max_price && menu.at(i).getType() == SPC) {//check if the price is equal
                if (id_dish < exp_id) // choosing the smallest id
                    exp_id = id_dish;
            }
            else if (price > max_price && menu.at(i).getType() == SPC) { //checking who is the Spiciest meal
                max_price = price;
                exp_id = id_dish;
            }
        }
        if(exp_id != INT8_MAX) { // make sure the customer ordered a dish
            Spicy_vec.push_back(exp_id);
            numOfOrder++ ;
        }
    }

    else {  // further orders
        int min = INT8_MAX;
        int id_min = INT8_MAX;

        for (unsigned int i = 0; i < menu.size(); i++) {
            int price = menu.at(i).getPrice();//the price of the meal
            int id_d = menu.at(i).getId();// the id of the meal

            if (price == min && menu.at(i).getType() == BVG) { //check if the price is equal
                if (id_d < id_min) // choosing the smallest id
                    id_min = id_d;
            }
            else if (price < min && menu.at(i).getType() == BVG) { //checking who is the cheapest meal
                min = price;
                id_min = id_d;
            }
        }
        if(id_min != INT8_MAX) {// make sure the customer ordered a dish
            Spicy_vec[0]=id_min;
            numOfOrder++;
        }
    }
    return Spicy_vec ; // vector that contains the id of the meal ordered
}

string SpicyCustomer:: toString() const{
    return "spc";
}


AlchoholicCustomer::AlchoholicCustomer(std::string name, int id):Customer(name,id), last_ordered_id(-1) , last_ordered_price(-1),all_orders_id(){}

Customer *AlchoholicCustomer::clone() {
    return new AlchoholicCustomer(*this);
}

vector<int> AlchoholicCustomer:: order(const vector<Dish> &menu){
    int min_id = INT8_MAX; //if the price is the same, we will choose according to the id
    int min_price=INT8_MAX; //we need to find the cheapest dish;
    vector<int>vec={-1};
    bool found = false; // help us check if the id exist in all orders id vec
    for(unsigned int i=0; i<menu.size();i++) {
        if (menu[i].getType() == ALC) {
            if (menu[i].getPrice() < min_price && menu[i].getPrice() > last_ordered_price) {
                //the dish is cheaper and its not the dish we already ordered
                    min_id = menu[i].getId();
                    min_price = menu[i].getPrice();
                }
            if (menu[i].getPrice() < min_price && menu[i].getPrice() == last_ordered_price){
                if (menu[i].getId() > last_ordered_id){
                    min_id = menu[i].getId();
                    min_price = menu[i].getPrice();
                }
            }
            else if (menu[i].getPrice() == min_price && menu[i].getId() > last_ordered_id && menu[i].getPrice() >= last_ordered_price) { //the dish is equal in price
                if (menu[i].getId() < min_id) {  //so we check which id is smaller
                    min_id = menu[i].getId();
                    min_price = menu[i].getPrice();

                }
            }


        }
    }
    last_ordered_price = min_price;
    last_ordered_id = min_id;



       for(unsigned int i = 0 ; i < all_orders_id.size() ; i++){
        if(last_ordered_id == all_orders_id.at(i))
            found = true;
        }
    if(!found)
        all_orders_id.push_back(last_ordered_id);

    if(min_id != INT8_MAX )
        vec[0]=last_ordered_id;
    if(found)
        vec[0] = -1;

    return vec;
}

string AlchoholicCustomer:: toString() const{
    return "alc";
}

