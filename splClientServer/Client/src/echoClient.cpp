#include <stdlib.h>
#include <connectionHandler.h>
#include <Task.h>
#include <massageEncoderDecoder.h>

#include <thread>
//#include <mingw.thread.h>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    std::cout<<"recived host and port.."<<std::endl;

    massageEncoderDecoder m;

    bool isLogin(false);
    bool isTerminated(false);

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    //boost::thread th1(&Task::run, &task1);
	//boost::thread *th1 = new boost::thread(&Task::run, &task1);
	//Task task1(1, connectionHandler,m);

    Task task1(1, connectionHandler,m, isLogin, isTerminated);

    std::thread th1(std::ref(task1));

    while (!isTerminated){
        std::string answer = m.decode(&connectionHandler);
        std::cout << answer << std::endl;

        if(answer == "ACK 2"){ //if login
            isLogin = true;
        }
    }

    th1.join();

    return 0;
}
