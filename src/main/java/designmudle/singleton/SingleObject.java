package designmudle.singleton;

// 饿汉式 浪费内存，但效率高
public class SingleObject {
    private SingleObject(){}
    private static SingleObject instance = new SingleObject();
    public static SingleObject getInstance(){
        return instance;
    }
    public void showMessage(){
        System.out.println("hello Singel");
    }
}


/* 懒汉式 适用于单例效率对程序影响不大时
public class SingleObject {
    private static SingleObject instance;
    private SingleObject(){}

    public static synchronized SingleObject getInstance(){
        if (instance == null){
            instance = new SingleObject();
        }
        return instance;
    }
}
 */

/* 双重锁，线程安全且高效
public class SingleObject {
    private static SingleObject instance;
    private SingleObject(){}
    public static SingleObject getInstance(){
        if (instance == null){
            synchronized (SingleObject.class){
                if (instance == null){
                    instance = new SingleObject();
                }
            }
        }
        return instance;
    }
}

 */

