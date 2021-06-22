//
// Created by adifed@wincs.cs.bgu.ac.il on 1/1/19.
//

#include <iostream>
//#include <boost/thread.hpp>
#include <Task.h>
//#include "connectionHandler.cpp"
#include <connectionHandler.h>


Task::Task(int number,ConnectionHandler &connectionHandler ,massageEncoderDecoder &m, bool &isLogin, bool &isTerminated):
        _id(number), connectionHandler(connectionHandler), m(m),isLogin(isLogin),isTerminated(isTerminated){};

void Task::operator()(){
    while (!isTerminated) {
        const short bufsize = 1024;
        char buf[bufsize];

        std::cin.getline(buf, bufsize);

        std::string line(buf);
        int len = (int) line.length();


        if(isLogin && line == "LOGOUT"){
            isTerminated = true;
        }

        int m_size=0;
        char *c = m.encode(line,&m_size);

        if (!connectionHandler.sendBytes(c, m_size)) {
            std::cout << "Disconnected. Exiting..." << std::endl;
            break;
        }
        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
     //   std::cout << "Sent " << len + 1 << " bytes to server" << std::endl;
    }
};
