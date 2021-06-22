package bgu.spl.mics;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//private List <MicroService> microServices;
	private ConcurrentHashMap <Class<? extends Event<?>>, Vector<MicroService>> mapE;//For each event we have a list of MS
	private ConcurrentHashMap <Class<? extends Broadcast>, Vector<MicroService>> mapB ;//For each broadcast we have a list of MS
	private ConcurrentHashMap <MicroService, BlockingQueue<Message>> microServiceQueueHashMap; //For each MS we have a Queue
	private ConcurrentHashMap <Event<?>,Future > events;//For each Event we have a Future

	private static MessageBusImpl instance = null;

	private MessageBusImpl(){ //constructor
		mapE = new ConcurrentHashMap<>();
		mapB = new ConcurrentHashMap<>();
		microServiceQueueHashMap = new ConcurrentHashMap<>();
		events= new ConcurrentHashMap<>();

	}

	public static MessageBusImpl getInstance(){ //singleton
		if(instance == null)
			instance = new MessageBusImpl();
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!mapE.containsKey(type)){ //if this broadcast is not exist
			Vector <MicroService> l = new Vector<>();
			l.add(m); //adding the right
			mapE.put(type, l);
		}
		else {
			Vector <MicroService> l = mapE.get(type);
			l.add(m); //adding the MS to the right list
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!mapB.containsKey(type)) { //the event is not found in the hashMap
			Vector <MicroService> l = new Vector<>();
			mapB.put(type, l);
			l.add(m);
		}
		else {
			mapB.get(type).add(m); //adding the MS to the right list

		}
	}

	@Override
	public  <T> void complete(Event<T> e, T result) {
		Future<T> future = null;
		synchronized (events)
		{
			if (events.containsKey(e))
			{
				future = (Future<T>) events.get((Message) e);
				events.remove((Message) e);
			}
		}
		if(future!=null)
		{
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(mapB.get(b.getClass()) != null) {
			for (MicroService m : mapB.get(b.getClass())) {
				try {
					microServiceQueueHashMap.get(m).put(b);//add the broadcast to the queue of all the MS which subscribed
				}
				catch (InterruptedException e) {
				}

			}
		}
	}


	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if(mapE.get(e.getClass()) == null) {
			return null;
		}
		Future<T> future = new Future<>();
		events.put(e, future);
		Vector<MicroService> l = mapE.get(e.getClass());
		MicroService m;
		synchronized(l)
		{
			m = l.remove(0);
			l.add(m);
		}
		//if(microServiceQueueHashMap.get(m) == null)//{
			//return null;
		microServiceQueueHashMap.get(m).add(e);
		return future;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueueHashMap.put(m, new LinkedBlockingQueue<>());
	}


	@Override
	public synchronized void unregister(MicroService m) {
		for(Message m1: microServiceQueueHashMap.get(m)){
			if( m1 instanceof Event)
				mapE.get(m1).remove(m);
			else
				mapB.get(m1).remove(m);
		}
		microServiceQueueHashMap.remove(m);

	}

	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> q = microServiceQueueHashMap.get(m);
		Message message = null;
		if(q == null) // if the microService doesnt have a queue
			return null;
		synchronized (q) {
			message = q.take();




		}
		return message;
	}



}