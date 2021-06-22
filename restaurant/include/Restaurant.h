
#ifndef RESTAURANT_H_
#define RESTAURANT_H_

#include <vector>
#include <string>
#include "Dish.h"
#include "Table.h"
#include "Action.h"



class Restaurant{
public:
    virtual~Restaurant(); // Distructor
    Restaurant();//default
    Restaurant(const Restaurant &restaurant);//copy
    Restaurant& operator=(const Restaurant &other);//copy assignment operator
    Restaurant (Restaurant && rhs); //move copy
    Restaurant& operator=(Restaurant && rhs);//move operator=
    Restaurant(const std::string &configFilePath);
    void start();
    int getNumOfTables() const;
    Table* getTable(int ind);
    const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    const std::vector<Table*>& getTables() const;
    std::vector<Dish>& getMenu();
    std:: string DishToString(DishType type);// convert the string to a DishType
    std::string getError_massage(); // **

private:
    bool open;
    std::vector<Table*> tables;
    std::vector<Dish> menu;
    std::vector<BaseAction*> actionsLog;
    int num_of_Table;
    std::vector<std::string> split(const std::string &s, char delimiter);//split the string
    DishType type(std::string s);// convert the string to a DishType
    bool check (std::string s); //check if the line of the config start with legal arguments
    void steal_tables(Restaurant &other);
    void steal_actions(Restaurant &other);
    void clear_tables();
    void clear_actions();
    std::string error_massage; // ***
};

#endif
