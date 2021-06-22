#include "../include/Dish.h"
#include <iostream>
using namespace std;



Dish::Dish():id(-1), name(""), price(-1), type()//default constructor
{}

Dish::Dish(int d_id, string d_name, int d_price, DishType d_type) :
        id(d_id) ,name(d_name) ,price(d_price) ,type(d_type)
{}



int Dish:: getId() const{
    return id;
}

string Dish:: getName() const{
    return name;
}

int Dish :: getPrice() const{
    return price;
}

DishType Dish:: getType() const{
    return type;
}

