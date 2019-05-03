package misaka.nemesiss.com.findlostthings.Utils;

import java.util.concurrent.ConcurrentHashMap;

public class EventProxy<TKey> {
    private ConcurrentHashMap<TKey, Object> events;
    private ConcurrentHashMap<TKey,EventStatus> eventsStatus;
    private int count;
    private EventResult<TKey> finishCallback;
    public EventProxy() {
        events = new ConcurrentHashMap<>();
        eventsStatus = new ConcurrentHashMap<>();
    }

    public void all(EventResult<TKey> callback,TKey... keys)
    {
        finishCallback = callback;
        count = keys.length;
        for (int i = 0; i < keys.length; i++) {
            eventsStatus.put(keys[i],EventStatus.InProgress);
        }
    }

    public enum EventStatus
    {
        Finish,Fail,InProgress
    }
    public void emit(TKey key,EventStatus status,Object value)
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
        count --;
        if(count == 0 && finishCallback != null)
        {
            finishCallback.handle(events,eventsStatus);
        }
    }

    public interface EventResult<TKey> {
        void handle(ConcurrentHashMap<TKey, Object> evs,ConcurrentHashMap<TKey, EventStatus> evStatus);
    }
}
