package bgu.spl.net.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Database {

   private ConcurrentHashMap<String, String> usernameANDpassword; //user name, password- when register
    private LinkedHashMap<String, String> uANDp; /// for userlist- save a ordered list of registered

    private ConcurrentHashMap<Integer, String> idAndusername; //connection id, user name -when login
    private ConcurrentHashMap<String, Integer> usernameAndid; //user name, connection id -when login
    private ConcurrentLinkedQueue<String> login;

    private ConcurrentHashMap<String, LinkedList<String>> userFollows; //user name, users which he follows
    private ConcurrentHashMap<String, LinkedList<String>> userfollowers; //user name, users which follow user name

    private ConcurrentLinkedQueue<Message> postsANDpm ;
    private ConcurrentHashMap<String, LinkedList<Message>> usernameANDposts; //user name and his posts
    private ConcurrentHashMap<Message , String> postsANDusername; // post and who post it
    private ConcurrentHashMap<String, LinkedList<Message>> waitingNotifications; //posts and pm when the user is logoff


    public Database(){
        this.usernameANDpassword = new ConcurrentHashMap<>();
        this.idAndusername = new ConcurrentHashMap<>();
        this.userFollows = new ConcurrentHashMap<>();
        this.postsANDpm = new ConcurrentLinkedQueue<>();
        this.usernameANDposts = new ConcurrentHashMap<>();
        this.userfollowers = new ConcurrentHashMap<>();
        this.waitingNotifications = new ConcurrentHashMap<>();
        this.login = new ConcurrentLinkedQueue<>();
        this.usernameAndid = new ConcurrentHashMap<>();
        this.postsANDusername = new ConcurrentHashMap<>();
        this.uANDp = new LinkedHashMap<>();

    }

    public ConcurrentHashMap<String, String> getUsernameANDpassword() {
        return usernameANDpassword;
    }

    public ConcurrentHashMap<Integer, String> getIdAndusername() {
        return idAndusername;
    }

    public ConcurrentHashMap<String, Integer> getUsernameAndid() {
        return usernameAndid;
    }

    public ConcurrentLinkedQueue<String> getLogin() {
        return login;
    }

    public ConcurrentHashMap<String, LinkedList<String>> getUserFollows() {
        return userFollows;
    }

    public ConcurrentHashMap<String, LinkedList<String>> getUserfollowers() {
        return userfollowers;
    }

    public ConcurrentLinkedQueue<Message> getPostsANDpm() {
        return postsANDpm;
    }

    public ConcurrentHashMap<String, LinkedList<Message>> getUsernameANDposts() {
        return usernameANDposts;
    }

    public ConcurrentHashMap<Message, String> getPostsANDusername() {
        return postsANDusername;
    }

    public ConcurrentHashMap<String, LinkedList<Message>> getWaitingNotifications() {
        return waitingNotifications;
    }

    public LinkedHashMap<String, String> getuANDp() {
        return uANDp;
    }
}
