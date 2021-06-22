//
// Created by adien@wincs.cs.bgu.ac.il on 12/30/18.
//

//#include <boost/asio/ip/tcp.hpp>
#include <string>
#include <iostream>
#include <massageEncoderDecoder.h>
#include <connectionHandler.h>
//#include "Task.cpp"

//#include <thread>

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using std::vector;


massageEncoderDecoder::massageEncoderDecoder() {}


char bytesArr [1024]; //which size?
char *b = new char[1024];

char* massageEncoderDecoder::encode(string s,int *p_size) {

    vector <string > split = massageEncoderDecoder::split(s,' ');
    string first = split[0];

    //  std::vector<char> tosend;

    int val = 0;
    int *index = &val;

    if(first == "REGISTER"){
        shortToBytes(1,bytesArr);// 1 to bytes
        *index = 2;
        char username[split[1].size()+0x10]; ///todo: what about pointers?
        strcpy(username, split[1].c_str()); //copying the contents of the string to char array

        char password[split[2].size()+0x10];
        strcpy(password, split[2].c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,username,index);
        bytesArr[*index] = 0;
        (*index)++;

        addTObytes(bytesArr,password,index);
        bytesArr[*index] = 0;
        (*index)++;

    //    cout<< "register"<<endl;

        *p_size = *index;
        return bytesArr;
    }
    if(first == "LOGIN"){
        shortToBytes(2,bytesArr);// 2 to bytes
        *index = 2;

        char username[split[1].size()+0x10]; ///todo: what about pointers?
        strcpy(username, split[1].c_str()); //copying the contents of the string to char array

        char password[split[2].size()+0x10];
        strcpy(password, split[2].c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,username,index);
        bytesArr[*index] = 0;
        (*index)++;

        addTObytes(bytesArr,password,index);
        bytesArr[*index] = 0;
        (*index)++;

    //    cout<< "login"<<endl;
        *p_size = *index;
        return bytesArr;

    }
    if(first == "LOGOUT"){
        shortToBytes(3,bytesArr);// 3 to bytes
        *index = 2;

  //      cout<< "logout"<<endl;
        *p_size = *index;
        return bytesArr;
    }
    if(first == "FOLLOW"){
        shortToBytes(4,bytesArr);// 4 to bytes
        *index = 2;

        if(split[1] == "1"){
            bytesArr[*index] = 1;
        } else{
            bytesArr[*index] = 0;
        }

        *index = 3;

        char arr[2];
        short s = (short) std::stoi(split[2]);
        shortToBytes(s, arr);

        bytesArr[*index] = arr[0];
        (*index)++;
        bytesArr[*index] = arr[1];
        (*index)++;
//
//        addTObytes(bytesArr,followunfollow,index);
//        addTObytes(bytesArr,numOFusers,index);

        for(int i=3; i<split.size(); i++){
            char string[split[i].size() +0x10 ];
            strcpy(string, split[i].c_str()); //copying the contents of the string to char array
            addTObytes(bytesArr,string,index);
            bytesArr[*index] = 0;
            (*index)++;
        }

    //    cout<< "follow"<<endl;
        *p_size = *index;
        return bytesArr;

    }
    if(first == "POST"){
        shortToBytes(5,bytesArr);// 5 to bytes
        *index = 2;

        string tmp = s.substr(5);

        char content[split[1].size()+0x10];
        strcpy(content, tmp.c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,content,index);
        bytesArr[*index] = 0;
        (*index)++;

      //  cout<< "post"<<endl;
        *p_size = *index;
        return bytesArr;

    }
    if(first == "PM"){
        shortToBytes(6,bytesArr);// 6 to bytes
        *index = 2;

        char username[split[1].size()+0x10];
        strcpy(username, split[1].c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,username,index);
        bytesArr[*index] = 0;
        (*index)++;

        int n = 4 + split[1].size();
        string tmp = s.substr(n);

       char content[split[2].size()+0x10];
       strcpy(content, tmp.c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,content,index);
        bytesArr[*index] = 0;
        (*index)++;

    //    cout<< "pm"<<endl;
        *p_size = *index;
        return bytesArr;

    }
    if(first == "USERLIST"){
        shortToBytes(7,bytesArr);// 7 to bytes
        *index = 2;

        //return
     //   cout<< "userlist"<<endl;
        *p_size = *index;
        return bytesArr;
    }
    if(first == "STAT"){
        shortToBytes(8,bytesArr);// 8 to bytes
        *index = 2;

        char username[split[1].size()+0x10];
        strcpy(username, split[1].c_str()); //copying the contents of the string to char array

        addTObytes(bytesArr,username,index);
        bytesArr[*index] = 0;
        (*index)++;

     //   cout<< "stat"<<endl;
        *p_size = *index;
        return bytesArr;
    }

    *p_size = *index;
    return 0;
}


std::string massageEncoderDecoder::decode(ConnectionHandler *connectionHandler) {
    int val2 = 0;
    int *ind = &val2;


    connectionHandler->getBytes(b,2); //recieve from the server 2 bytes -- opcode
    short opcode = bytesToShort(b);
    string output = "";
    string username="";
    string content = "";
    string listusers = "";
    string notification = "";


    if (opcode == 9){ //IF NOTIFICATION
        connectionHandler->getBytes(&b[2],1); //GIVE ME 1 BYTE
        char p = b[2]; //FOR public or pm
        *ind = 3;

        for(int i=0; i<2; i++) {
            while (true) {
                char last_char;
                connectionHandler->getBytes(&b[*ind], 1);
                last_char = b[*ind];
                (*ind)++;
                if (last_char == 0)
                    break;
                notification = notification + last_char;
            }
            notification += ' ';
        }


        if(p == 1) { //TODO: WHY
            output = "NOTIFICATION Public " + notification;
        }
        else {
            output = "NOTIFICATION PM " + notification;
        }

        return output;
    }
    if(opcode == 10){ //IF ACK
        connectionHandler->getBytes(&b[2],2); //GIVE ME 2 BYTES
        short o = bytesToShort(&b[2]); //CONVERT TO SHORT THE OPCODE OF THE MESSAGE

        if(o == 4 || o == 7){ //IF FOLLOW OR USERLIST
            connectionHandler->getBytes(&b[4],2); //GIVE ME 2 BYTS
            short numofuser = bytesToShort(&b[4]);//CONVERT TO SHORT: NUM OF USERS
            *ind = 6;

           // connectionHandler->getFrameAscii(listusers,'\0');

            for(int i=0; i<numofuser; i++) {
                while (true) {
                    char last_char;
                    connectionHandler->getBytes(&b[*ind], 1);
                    last_char = b[*ind];
                    (*ind)++;
                    if (last_char == 0)
                        break;
                    listusers = listusers + last_char;
                }
                listusers += ' ';

            }

            if(o == 4)
                output = "ACK 4 " + std::to_string(numofuser) +" "+ listusers;
            else if(o == 7)
                output = "ACK 7 " + std::to_string(numofuser) +" "+ listusers;
        }
        else if(o == 8){ //IF STAT
          // connectionHandler->getBytes(&b[2],2); //GIVE ME 2 BYTES
           // short o = bytesToShort(&b[2]); //CONVERT TO SHORT

            connectionHandler->getBytes(&b[2],2);
            short numPosts =  bytesToShort(&b[2]);

            connectionHandler->getBytes(&b[4],2);
            short numFollowers =  bytesToShort(&b[4]);

            connectionHandler->getBytes(&b[6],2);
            short numFollowing =  bytesToShort(&b[6]);

            output = "ACK 8 "+std::to_string(numPosts)+" "+std::to_string(numFollowers)+" "+std::to_string(numFollowing);
        }
        else {
            output = "ACK " + std::to_string(o);
        }

        return output;

    }
    if(opcode == 11){
        connectionHandler->getBytes(&b[2],2);
        short o = bytesToShort(&b[2]);
        string s = "ERROR " + std::to_string(o);
        return s;
    }

}


short massageEncoderDecoder::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

std::vector<std::string> massageEncoderDecoder::split(const std::string &s, char delimiter) {
    std::vector<std::string> tokens;
    std::string token;
    std::istringstream tokenStream(s);
    while (std::getline(tokenStream, token, delimiter))
    {
        tokens.push_back(token);
    }
    return tokens;
}

void massageEncoderDecoder::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

void massageEncoderDecoder::addTObytes(char *bytearry, char *add, int *index) {
    for (int i=0; i < strlen(add); i++){
        bytearry[*index] = add[i];
        (*index)++;
    }

}






