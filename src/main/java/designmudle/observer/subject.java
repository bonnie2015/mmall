package designmudle.observer;

import org.apache.commons.collections.CollectionUtils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class subject {
    List<Observer> observerList = new ArrayList<>();
    private int status;
    public void setStatus(int status){
        this.status = status;
        notifyAllObserver();
    }
    public int getStatus(){
        return this.status;
    }
    public void attach(Observer observer){
        observerList.add(observer);
    }

    public void notifyAllObserver(){
        if (CollectionUtils.isNotEmpty(observerList)){
            for(Observer oItem : observerList){
                oItem.update();
            }
        }
    }
}
