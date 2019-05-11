package misaka.nemesiss.com.findlostthings.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventProxy<TKey> {
    private ConcurrentHashMap<TKey, Object> events;
    private ConcurrentHashMap<TKey,EventStatus> eventsStatus;
    private EventResult<TKey> finishCallback;

    private AtomicBoolean HaveDoneAllTasks = new AtomicBoolean(false);
    public EventProxy() {
        events = new ConcurrentHashMap<>();
        eventsStatus = new ConcurrentHashMap<>();
    }

    public void all(EventResult<TKey> callback,TKey... keys)
    {
        finishCallback = callback;
        for (int i = 0; i < keys.length; i++) {
            eventsStatus.put(keys[i],EventStatus.InProgress);
        }
    }

    public enum EventStatus
    {
        Finish,Fail,InProgress
    }
    public synchronized void emit(TKey key,EventStatus status,Object value)
    {
        if(status == EventStatus.InProgress) {
            throw new UnsupportedOperationException("Re-enter InProgress status is not allowed!");
        }
        EventStatus oldStatus = eventsStatus.get(key);
        if(oldStatus != EventStatus.InProgress)
        {
            throw new UnsupportedOperationException("Cannot emit an event twice!");
        }
        eventsStatus.replace(key,status);
        events.put(key,value);
        boolean AllFinish = true;
        for (EventStatus evs : eventsStatus.values()) {
            if(evs==EventStatus.InProgress) {
                AllFinish = false;
            }
        }
        if(AllFinish && !HaveDoneAllTasks.get())
        {
            finishCallback.handle(events,eventsStatus);
            HaveDoneAllTasks.set(true);
        }
    }

    public synchronized void tryemit(TKey key,EventStatus status,Object value)
    {
        if(status == EventStatus.InProgress) {
            throw new UnsupportedOperationException("Re-enter InProgress status is not allowed!");
        }

        eventsStatus.replace(key,status);
        events.put(key,value);
        boolean AllFinish = true;
        for (EventStatus evs : eventsStatus.values()) {
            if(evs==EventStatus.InProgress) {
                AllFinish = false;
            }
        }
        if(AllFinish && !HaveDoneAllTasks.get())
        {
            finishCallback.handle(events,eventsStatus);
            HaveDoneAllTasks.set(true);
        }
    }
    public synchronized void revoke(TKey key){
        events.remove(key);
        eventsStatus.remove(key);
    }

    public synchronized void reset(TKey key) {
        events.remove(key);
        eventsStatus.replace(key,EventStatus.InProgress);
    }

    public interface EventResult<TKey> {
        void handle(ConcurrentHashMap<TKey, Object> evs,ConcurrentHashMap<TKey, EventStatus> evStatus);
    }
}
