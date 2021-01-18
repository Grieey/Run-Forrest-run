### 一、什么是ThreadLocal

ThreadLocal用于保存线程全局变量，以方便调用。即，当前线程独有，不与其他线程共享；可在当前线程任何地方获取到该变量。

### 二、ThreadLocal的使用

####  1、如何保存内容

创`ThreadLocal`实例，并调用`set`函数，保存`中国`字符串，分别在当前线程和`new-thread`线程获取该值。通过打印结果可以看到，虽然引用的是同个对象，但`new-thread`线程获取到的值却是`null`。

![use](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115104823.png)

运行结果:

```
main 中国
MainActivity: new-thread null
```

这是什么情况呢？

在`ThreadLocal`的`set`函数中，获取当前线程的`ThreadLocalMap`实例，如何当前线程第一次使用`ThreadLocal`,则需要创建`ThreadLocalMap`实例，否则直接通过`ThreadLocalMap`实例的`set`函数进行保存。

![set](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115103657.png)



#### 2、如何获取内容

由于`main`线程前面`set`函数将内容保存到`ThreadLocalMap`实例中，已经可以获取到`中国`字符串。而在`new-thread`线程中，由于是第一次使用`ThreadLocalMap`，所以此时`map`是`null`，并调用`setInitialValue`函数。

![get](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115111259.png)

在`setInitialValue`函数中,调用了`initialValue`函数，该函数直接返回了`null`，这就是为什么在`new-thread`线程获取的值是`null`。因此`setInitialValue`函数主要为当前线程创建`ThreadLocalMap`对象。

![setInitialValue](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115112648.png)

#### 3、ThreadLocalMap

`ThreadLocalMap`内部持有一个数组`table`，用于保存`Entry`元素。`Entry`继承至`WeakReference`,并以`ThreadLcoal`实例作为`key`，和保存内容 T作为`value`。当发生GC时，`key`就会被回收,从而导致该Entry过期。

![Entry](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118144659.png)

每一个线程都持有一个`ThreadLocalMap`局部变量`threadLocas`，如下图所示。

![image-20210118113312896](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118113312.png)

##### 3.1 ThreadLocalMap的创建

ThreadLocalMap对象的创建，也就是ThreadLocal 对象调用了自身的`createMap`函数。

![createMap](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115114648.png)

ThreadLocalMap的构造函数，创建了一个保存Entry对象的table数组，默认大小16。并通过`threadLocal`的`threadLocalHashCode`属性计算出Entry在数组的小标，进行保存，并计算出阈值`INITIAL_CAPACITY`的2/3。



`threadLocalHashCode`属性在ThreaLocal对象创建时会自动计算得出.

![threadLocalHashCode](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210115145913.png)

`threadLocalHashCode`作为ThreadLocal的唯一实例变量，在不同的实例中是不同的，通过`nextHashCode.getAndAdd`已经定义了下一个ThreadLcoal的实例的`threadLocalHashCode`值，而第一个ThreadLocal的`threadLocalHashCode`值则是从0开始，与下一个`threadLocalHashCode`间隔`HASH_INCREMENT`。

通过`threadLocalHashCode & (len-1)`计算出来的数组下标，分发很均匀，减少冲突。但是呢，冲突时还是会出现，如果发生冲突，则将新增的Entry放到后侧`entry=null`的地方。

### 三、源码分析

#### 1、ThreadLocalMap的set函数

在上一节中，分析了`ThreadLocal`实例的`set`函数，最终是调用了`ThreadLocalMap`实例的`set`函数进行保存。

![mapset](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116153208.png)

通过代码分析可知，`ThreadLocalMap`的`set`函数主要分为三个主要步骤：

1. 计算出当前`ThreadLocal`在`table`数组的位置，然后向后遍历，直到遍历到的`Entry`为`null`则停止，遍历到`Entry`的`key`与当前`threadLocal`实例的相等，直接更替value；

2. 如果遍历到`Entry`已过期（`Entry`的`key`为`null`），则调用`replaceStaleEntry`函数进行替换。

3. 在遍历结束后，未出现1和2两种情况，则直接创建新的`Entry`，保存到数组最后侧没有Entry的位置。


在第2步骤和最后都会清理过期的`Entry`，这个稍后分析，先看看第2步骤，在检测到过期的Entry，会调用`replaceStaleEntry`函数进行替换。

   ![replaceStaleEntry](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116152455.png)

`replaceStaleEntry`函数，主要分为两次遍历，以当前过期的Entry为分割线，一次向前遍历，一次向后遍历。

在向前遍历过程，如果发现有过期的`Entry`，则保留其位置`slotToExpunge`，直到有`Entry`为`null`为止。这里只是判断`staleSlot`前方是否有过期的`Entry`，然后方便后面进行清理。

在向后遍历过程，如果发现有`key`相同的`Entry`，直接与`staleSlot`位置的`Entry`交换`value`（上图注释有问题）。如果没有碰到相同的`key`，则创建新的`Entry`保存到`staleSlot`位置。与此同时，如果向前遍历没有发现过期Entry，而在向后遍历发现过期的`ntry`，则需要更新过期位置`slotToExpunge`，因为后面的清除内容是需要`slotToExpunge`。

#### 2、ThreadLocalMap清除过期Entry

在上一小节中，会通过`expungeStaleEntry`函数和`cleanSomeSlots`函数清理过期的Entry，它们又是如何实现呢？

`expungeStaleEntry`函数清理过期`Entry`过程被称为：**探测式清理**。函数传递进来的参数是过期的`Entry`的位置，工作过程是先将该位置置为`null`，然后遍历数组后侧所有位置的Entry，如果遍历到有`Entry`过期,则直接置`null`，否则将它移到合适的位置：`hash`计算出来的位置或离该`hash`位置最近的位置。

![expungeStaleEntry](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116165619.png)

经过这么一次经历，`staleSlot`位置到后侧最近`entry=null`的位置就不存在过期的`entry`，而每个`entry`要么在原有`hash`位置，要么离原有`hash`位置最近。

`expungeStaleEntry`函数的工作范围：

![expungeStaleEntry (1)](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116174219.png)

`expungeStaleEntry`函数一开始会将起点，即数组第3的位置设置为`null`。然后开始遍历数组后侧元素，4和5位置无论是否在它的`hash`位置，在这里都保持不变。遍历到第6时，发现`entry`已过期，将第6设置为`null`。此时3和6位置变成白色了。

![image-20210116175944199](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116175944.png)

A、遍历到第7的时候，假设`h != i`成立，那么第7位置的`entry`将被移到第6位置，空出第7位置。

![image-20210116180006575](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210116180006.png)

B、接着遍历到第8位置，假设`h != i`不成立，则第8的`entry`的位置不变。

接着继续遍历后侧元素，重复着A和B步骤，直到碰到entry为null，退出遍历。例如这里的第10位置，entry=null。

由于探测性清理，碰到`entry=null`的情况就会结束。而通过`cleanSomeSlots`函数进行**启发式清理**，碰到`entry=null`不停止，而是由控制条件n决定，而在这个过程中，碰到过期`entry`，n又恢复到数组长度,加大清理范围。

![clean](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118101626.png)

在启发式清理过程，如果碰到过期`Entry`，会导致控制条件`n`恢复到数组长度`len`，从而导致循环次数增加，则往后`nextIndex`次数增加，从而增加清理范围。这种方式也不一定能完整清理后面所有过期元素，例如在控制`n`右移所有过程中，没有碰到过期的`entry`，就结束了。

####  3、ThreadLocalMap的扩容机制

在第1节，调用`ThreadLocalMap`的`set`函数最后，会调用`reHash`函数进行扩容。

![rehash](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118110300.png)

在外层进行启发式清理后，如果`size>threshold`则会进行rehash，而在`rehash`中，会清理整个数组的过期`Entry`，如果清理后，数组长度还大于`3/4*threshod`，则进行扩容`resize`。

![resize](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118111623.png)

`resize`函数直接创建新的数组，长度为旧数组的两倍。然后重新计算旧数组元素在新数组的位置，复制。

### 四、内存泄露

正常情况下，用完ThreadLocal实例,将其置为null，在发生GC时，ThreadLocal对象就会被回收。但是此时如果线程还存活(例如线程池线程的复用)，就会导致Entry的value对象得不到释放，会造成内存泄露。所以，在使用完ThreadLocal实例后，调用`remove`函数清除一下。

#### 疑惑
发生GC的时候，Key会被回收么，还能获取到值么？

正常情况下，如果ThreadLocal实例同时被强引用，所以在发生GC的时候，是不会回收的，也就是此时`WeakReference.get`是有返回值的，不会被回收。

![gc](https://cdn.jsdelivr.net/gh/Android-XXM/dio@main/img/20210118174100.png)

[推荐阅读：Java引用与ThreadLocal](https://zhuanlan.zhihu.com/p/58931565?utm_source=wechat_session&utm_medium=social&utm_oi=1182794502313586688)

