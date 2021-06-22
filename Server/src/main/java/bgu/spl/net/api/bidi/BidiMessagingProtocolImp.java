package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Database;

import java.util.LinkedList;


public class BidiMessagingProtocolImp implements BidiMessagingProtocol<Message> {

    private Database database;
    private int connectionId;
    private Connections<Message> connections;

    public BidiMessagingProtocolImp(Database database){
        this.database = database;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    public String getUserName(){ //find the username
        String userName = database.getIdAndusername().get(connectionId);
        return userName;
    }

    public boolean isLogged(){ //check if the user is logged
        if(database.getLogin().contains(getUserName()))
            return true;
        return false;
    }

    @Override
    public void process(Message message) {
        if (message instanceof REGISTER) {
            String userName = ((REGISTER) message).getUsername();
            String password = ((REGISTER) message).getPassword();

            synchronized (database.getUsernameANDpassword()) {
                synchronized (database.getIdAndusername()) {
                    if (!database.getUsernameANDpassword().containsKey(userName) &&
                            !database.getIdAndusername().containsKey(connectionId)) {// check if the username exists
                        database.getUsernameANDpassword().put(userName, password);// if not, put username and password in map
                        database.getuANDp().put(userName, password);
                        ACK ack = new ACK(1); //then send ack
                        connections.send(connectionId, ack);
                    } else {
                        ERROR error = new ERROR(1);
                        connections.send(connectionId, error);
                    }
                }
            }
        }

        if (message instanceof LOGIN) {
            String userName = ((LOGIN) message).getUsername();

            synchronized (database.getLogin()) {
                synchronized (database.getIdAndusername()) {
                    if (database.getLogin().contains(userName) || database.getIdAndusername().containsKey(connectionId)) { //if username is already logged
                        ERROR error = new ERROR(2);
                        connections.send(connectionId, error);
                        return;
                    }
                }
            }

            String password = ((LOGIN) message).getPassword();
            boolean isLogin = true;

            synchronized (database.getUsernameANDpassword()) {
                if (!database.getUsernameANDpassword().containsKey(userName)) //if username is not registered
                    isLogin = false;

                //if password doesnt match
                if (database.getUsernameANDpassword().get(userName) == null || !database.getUsernameANDpassword().get(userName).equals(password)) {
                    isLogin = false;
                }
            }

            if (isLogin) {
                database.getIdAndusername().put(connectionId, userName); //save the id and username
                database.getUsernameAndid().put(userName, connectionId);  //save the id and username
                database.getLogin().add(userName); //add user to the login users

                synchronized (database.getWaitingNotifications()) {
                    if (database.getWaitingNotifications().get(userName) != null) { //if he has a list of messages
                        for (Message m : database.getWaitingNotifications().get(userName)) { //take 1

                            if (m instanceof PM) {
                                NOTIFICATION notification = new NOTIFICATION("PM", ((PM) m).getUserName(), ((PM) m).getContent());
                                connections.send(connectionId, notification);
                            } else {
                                NOTIFICATION notification = new NOTIFICATION("Public", database.getPostsANDusername().get(m), ((POST) m).getContent());
                                connections.send(connectionId, notification);
                            }
                        }
                        database.getWaitingNotifications().get(userName).remove();
                    }
                }

                ACK ack = new ACK(2); //create new ack message
                connections.send(connectionId, ack);
            } else {
                ERROR error = new ERROR(2); //create new error message
                connections.send(connectionId, error);
            }
        }

        if (message instanceof LOGOUT) {
            if (!isLogged()) {
                ERROR error = new ERROR(3); //if the user is not logged in he cant logout
                connections.send(connectionId, error);
                return;
            }

            String userName = getUserName();//get user name

            database.getLogin().remove(userName); //remove user from login list
            database.getIdAndusername().remove(connectionId);
            database.getUsernameAndid().remove(userName);

            ACK ack = new ACK(3); //new ack message
            connections.send(connectionId, ack);
        }

        if (message instanceof FOLLOW) {
            int numOFSuccsess = 0; //number of succsess follow/unfollow
            boolean isFollow = false;
            String userName = getUserName();
            LinkedList<String> listOfSuccess = new LinkedList(); //all the users we will follow/unfollow

            if (((FOLLOW) message).isFollow() == 0) //The command is to follow
                isFollow = true;

            if (!isLogged()) { //if user is not login then error
                ERROR error = new ERROR(4);
                connections.send(connectionId, error);
                return;
            }

            for (String user : ((FOLLOW) message).getUserNameList()) {
                synchronized (database.getUsernameANDpassword()) {
                    synchronized (database.getUserFollows()) {
                        synchronized (database.getUserfollowers()) {
                            if (database.getUsernameANDpassword().containsKey(user) && isFollow) {//user is registered and command is to follow
                                //database.getUserFollows()
                                if (!database.getUserFollows().containsKey(userName)) { //if username is not following anyone yet
                                    LinkedList l = new LinkedList(); //create new list
                                    database.getUserFollows().put(userName, l);
                                    l.add(user); //we add the user to the follow list of username

                                    //we want to username to the followers list of each user
                                    LinkedList<String> followers = new LinkedList<>();
                                    //database.getUserfollowers()
                                    if (!database.getUserfollowers().containsKey(user)) { //check if the user is in map
                                        database.getUserfollowers().put(user, followers);
                                        followers.add(userName);
                                    } else {
                                        database.getUserfollowers().get(user).add(userName);
                                    }
                                    numOFSuccsess++;
                                    listOfSuccess.add(user);

                                } else if (!database.getUserFollows().get(userName).contains(user)) { //if the follow list doesn't contain user
                                    database.getUserFollows().get(userName).add(user);

                                    LinkedList<String> followers = new LinkedList<>();
                                    if (!database.getUserfollowers().containsKey(user)) { //check if the user is in map
                                        database.getUserfollowers().put(user, followers);
                                        followers.add(userName);
                                    } else {
                                        database.getUserfollowers().get(user).add(userName);
                                    }

                                    numOFSuccsess++;
                                    listOfSuccess.add(user);
                                } else {
                                    ERROR error = new ERROR(4);
                                    connections.send(connectionId, error);
                                    return;
                                }
                            } else if (database.getUsernameANDpassword().containsKey(user) && !isFollow) {//user is registered and command is to Unfollow
                                if (database.getUserFollows().get(userName) != null) {
                                    if (database.getUserFollows().get(userName).contains(user)) {
                                        database.getUserFollows().get(userName).remove(user);

                                        if (database.getUserfollowers().get(user).contains(userName)) { //remove username from the followers list of each user
                                            database.getUserfollowers().get(user).remove(userName);
                                        }
                                        numOFSuccsess++;
                                        listOfSuccess.add(user);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (numOFSuccsess == 0) {
                ERROR error = new ERROR(4);
                connections.send(connectionId, error);
            } else {
                ACK ack = new ACK(4, numOFSuccsess, listOfSuccess);
                connections.send(connectionId, ack);
            }
        }

        if (message instanceof POST) {
            String userName = getUserName();
            LinkedList<String> usersList = new LinkedList();
            if (!isLogged()) {
                ERROR error = new ERROR(5);
                connections.send(connectionId, error);
                return;
            }

            String content = ((POST) message).getContent(); //find the usernames with @
            int index = content.indexOf('@');
            if (index != -1) {
                String[] s = content.split("@");
                for (int i = 1; i < s.length; i++) {
                    int index2 = s[i].indexOf(" ");
                    String user = "";
                    if (index2 != -1) {
                        user = s[i].substring(0, s[i].indexOf(" "));
                    } else {
                        user = s[i];
                    }

                    synchronized (database.getUsernameANDpassword()) {
                        if (database.getUsernameANDpassword().containsKey(user)) { //check if the user is registered
                            usersList.add(user);
                        }
                    }
                }
            }
            synchronized (database.getUserfollowers()) {
                if (database.getUserfollowers().containsKey(userName)) {
                    for (String s : database.getUserfollowers().get(userName)) { //add to @ users
                        if (!usersList.contains(s))
                            usersList.add(s);
                    }
                }
            }

            NOTIFICATION notification = new NOTIFICATION("Public", userName, content);
            for (String u : usersList) {
                synchronized (database.getLogin()) {
                    synchronized (database.getWaitingNotifications()) {
                        if (!database.getLogin().contains(u)) { // if u is not login
                            if (!database.getWaitingNotifications().containsKey(u)) {
                                LinkedList l = new LinkedList();
                                database.getWaitingNotifications().put(u, l);
                                l.add(message);
                            } else {
                                database.getWaitingNotifications().get(u).add(message);
                            }
                        } else {
                            int id = database.getUsernameAndid().get(u);
                            connections.send(id, notification);
                        }
                    }
                }
            }
            database.getPostsANDpm().add(message);

            LinkedList<Message> posts = new LinkedList<>();
            synchronized (database.getUsernameANDposts()) {
                if (database.getUsernameANDposts().get(userName) == null) {  //add the post to the username list
                    database.getUsernameANDposts().put(userName, posts);
                    posts.add(message);
                } else {
                    database.getUsernameANDposts().get(userName).add(message);
                }
            }

            database.getPostsANDusername().put(message, userName); //added 4.1

            ACK ack = new ACK(5); //create new ack message
            connections.send(connectionId, ack);
        }

        if (message instanceof PM) {
            if (!isLogged()) {
                ERROR error = new ERROR(6);
                connections.send(connectionId, error);
                return;
            }
            String content = ((PM) message).getContent();
            String reciepient = ((PM) message).getUserName();

            //    database.postsANDpm.add(message);
            NOTIFICATION notification = new NOTIFICATION("PM", getUserName(), content);
            synchronized (database.getLogin()) {
                synchronized (database.getWaitingNotifications()) {
                    if (!database.getLogin().contains(reciepient)) { // if u is not login
                        if (!database.getWaitingNotifications().containsKey(reciepient)) {
                            LinkedList l = new LinkedList();
                            database.getWaitingNotifications().put(reciepient, l);
                            l.add(message);
                        } else {
                            database.getWaitingNotifications().get(reciepient).add(message);
                        }
                    } else {
                        connections.send(database.getUsernameAndid().get(reciepient), notification);
                    }
                }
            }

            ACK ack = new ACK(6); //create new ack message
            connections.send(connectionId, ack);
        }

        if (message instanceof USERLIST) {
            if (!isLogged()) {
                ERROR error = new ERROR(7);
                connections.send(connectionId, error);
                return;
            }
            LinkedList str = new LinkedList();

            synchronized (database.getuANDp()) {
                for (String s : database.getuANDp().keySet()) {
                    str.add(s);
                }
            }

            int numOfUsers = str.size();

            ACK ack = new ACK(7, numOfUsers, str);
            connections.send(connectionId, ack);
        }

        if (message instanceof STAT) {
            String username = ((STAT) message).getUsername();
            int numOfPosts = 0;
            int userfollowing = 0;
            int userfollowers = 0;

            synchronized (database.getUsernameANDpassword()) {
                if (!database.getUsernameANDpassword().containsKey(username) || !isLogged()) {
                    ERROR error = new ERROR(8);
                    connections.send(connectionId, error);
                    return;
                }
            }

            synchronized (database.getUsernameANDposts()) {
                if (database.getUsernameANDposts().containsKey(username)) {
                    numOfPosts = database.getUsernameANDposts().get(username).size();
                }
            }

            synchronized (database.getUserFollows()) {
                if (database.getUserFollows().containsKey(username)) {
                    userfollowing = database.getUserFollows().get(username).size();    // how many users I follow
                }
            }
            synchronized (database.getUserfollowers()) {
                if (database.getUserfollowers().containsKey(username)) {
                    userfollowers = database.getUserfollowers().get(username).size(); //how many users follow me
                }
            }

            ACK ack = new ACK(8, numOfPosts, userfollowers, userfollowing);
            connections.send(connectionId, ack);
        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }



}


