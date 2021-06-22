//
// Created by adifed@wincs.cs.bgu.ac.il on 1/1/19.
//

#ifndef BOOST_ECHO_CLIENT_TASK_H
#define BOOST_ECHO_CLIENT_TASK_H
#include <iostream>
//#include <boost/thread.hpp>
#include <connectionHandler.h>
#include <massageEncoderDecoder.h>


class Task {
private:
    int _id;
    ConnectionHandler &connectionHandler;
    massageEncoderDecoder &m;
    bool &isLogin;
    bool &isTerminated;

public:
    Task(int number,ConnectionHandler &connectionHandler,massageEncoderDecoder &m,bool &isLogin,bool &isTerminated);

    void operator()();
};
#endif //BOOST_ECHO_CLIENT_TASK_H