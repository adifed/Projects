#ifndef TABLE_H_
#define TABLE_H_

#include <vector>
#include "Customer.h"
#include "Dish.h"

typedef std::pair<int, Dish> OrderPair;

class Table{
public:
    Table(int t_capacity);
    Table(const Table &table_);//copy constructor
    Table& operator= (const Table &table_);//operator=
    Table (Table && rhs);// move_copy
    Table& operator=(Table && rhs);//move operator=

    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const std::vector<Dish> &menu);
    void openTable();
    void closeTable();
    int getBill();
    bool isOpen();
    virtual~Table();
    void setOrders(std::vector<OrderPair> vec);
private:
    int capacity;
    bool open;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order in a table - (customer_id, Dish)
    Dish find_dish(int id_dish, const std::vector<Dish> &menu );// given dish id return the object dish
    void clear2();// delete the vector customersList
    void copy_customersList(const Table &table_);//copy the customersList
    void steal(Table &table_);// move the pointers of the objects
};


#endif
