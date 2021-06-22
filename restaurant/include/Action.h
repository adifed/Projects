
#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"

enum ActionStatus{
	PENDING, COMPLETED, ERROR
};


//Forward declaration
class Restaurant;

class BaseAction{
public:
	BaseAction();
	ActionStatus getStatus() const;
	virtual void act(Restaurant& restaurant)=0;
	virtual std::string toString() const=0;
	virtual BaseAction* clone()=0;
	virtual~BaseAction();
protected:
	void complete();
	void error(std::string errorMsg);
	std::string getErrorMsg() const;
private:
	std::string errorMsg;
	ActionStatus status;
};


class OpenTable : public BaseAction {
public:
	OpenTable(int id, std::vector<Customer *> &customersList);
	OpenTable(const OpenTable &other); //copy constructor
	//OpenTable & operator=(const OpenTable &other); //operator=
	virtual ~OpenTable(); //distructor
	void act(Restaurant &restaurant);
	std::string toString() const;
	bool IsLegalTable(int table_Id,Restaurant &restaurant);
	BaseAction* clone();

private:
	const int tableId;
	std::vector<Customer *> customers;
	void clear_opentable();
	std::string error_massage; //**

};


class Order : public BaseAction {
public:
	Order(int id);
	void act(Restaurant &restaurant);
	std::string toString() const;
	bool IsLegalTable(int table_Id,Restaurant &restaurant);
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
	const int tableId;
	//std::string dishName(int id_dish, Restaurant &restaurant);

};


class MoveCustomer : public BaseAction {
public:
	MoveCustomer(int src, int dst, int customerId);
	void act(Restaurant &restaurant);
	std::string toString() const override;
	bool IsLegalTable(int src, int dst, Restaurant &restaurant);
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
	const int srcTable;
	const int dstTable;
	const int id;
	bool IsIdExist(Restaurant &restaurant , int id_src ,int id_customer );
};


class Close : public BaseAction {
public:
	Close(int id);
	void act(Restaurant &restaurant);
	std::string toString() const;
	bool IsLegalTable(int table_Id, Restaurant &restaurant);
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
	const int tableId;
};


class CloseAll : public BaseAction {
public:
	CloseAll();
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
};


class PrintMenu : public BaseAction {
public:
	PrintMenu();
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
};


class PrintTableStatus : public BaseAction {
public:
	PrintTableStatus(int id);
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
	const int tableId;
};


class PrintActionsLog : public BaseAction {
public:
	PrintActionsLog();
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
};


class BackupRestaurant : public BaseAction {
public:
	BackupRestaurant();
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);
private:
};


class RestoreResturant : public BaseAction {
public:
	RestoreResturant();
	void act(Restaurant &restaurant);
	std::string toString() const;
	BaseAction* clone();
    void setStatus(ActionStatus status);

};


#endif

