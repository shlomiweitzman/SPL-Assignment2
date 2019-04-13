package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> messageBus = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> subscribers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Event<?>, Future<?>> results = new ConcurrentHashMap<>();

    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribeMessage(type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribeMessage(type, m);
    }

    private <T> void subscribeMessage(Class<? extends Message> type, MicroService m) {

        subscribers.putIfAbsent(type, new LinkedBlockingDeque<MicroService>());
        subscribers.get(type).add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = (Future<T>) results.get(e);
        future.resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        int size = subscribers.get(b.getClass()).size();
        for (int i = 0; i < size; i++) {
            MicroService m = subscribers.get(b.getClass()).poll();
            subscribers.get(b.getClass()).add(m);
            messageBus.get(m).add(b);
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (subscribers.get(e.getClass())==null){
            return null;
        }
        synchronized (subscribers.get(e.getClass())) {//protects the case which different microservices unregister between these lines
            if (subscribers.get(e.getClass()) == null || subscribers.get(e.getClass()).isEmpty()) {
                return null;
            }
            MicroService micro = subscribers.get(e.getClass()).poll();
            if (micro == null) {
                return null;
            }
            Future<T> future = new Future<>();
            results.put(e, future);
            subscribers.get(e.getClass()).add(micro);
            messageBus.get(micro).add(e);
            return future;
        }
    }

    @Override
    public void register(MicroService m) {
        BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>() {
        };
        messageBus.put(m, queue);
    }

    @Override
    public void unregister(MicroService m) {
        for (Class<? extends Message> e : subscribers.keySet())
            subscribers.get(e).remove(m);
        for (Message message:messageBus.get(m)) {
            if (message.getClass() == Event.class) {
                results.get(message).resolve(null);
            }
        }
        messageBus.remove(m);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> lst_msg = messageBus.get(m);
        if (lst_msg == null)
            throw new IllegalStateException("interrupted while a waiting");
        return messageBus.get(m).take();
    }
}