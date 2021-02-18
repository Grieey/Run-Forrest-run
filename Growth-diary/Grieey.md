[TOC]

## 2021/01/03

- 初始化了学习记录用的仓库

- 完成7道算法题

- LeakCanary原理学习，在2.0之前，leakCanary的初始化是使用的**IdleHandler**，它会判断是否在主线程，是主线程就会使用这个，等待主线程空闲时进行初始化，并只初始化一次；2.0之后是利用的**ContenProvider**来出初始化的，这个会在Application之前调用。监测生命周期利用的是弱引用和引用队列。

### 归并排序

核心思想就是分治，将大问题分解为小问题治理，主要的方法依靠的是递归，将数组每次从低位**lo**和高位**hi**的中间分开，然后分别对左右两边排序；

最主要的处理在最后合并的时候，我们可以从最小的单位来梳理逻辑代码，也就是最小划分要么左边一个数，右边两个数（或者反过来左边两个，右边一个），要么是左右各一个数进行排序，后者是前者的特殊情况，分析前者就行了。整个数组三个数，重新排序，index从左到有，依次比较左右的大小，谁小谁排前面，拷贝的时候，小的那边就加一，进行遍历。当index大于了右边的时候，最小单位的排序也就完了。最小单位的情况分析完了就往上一层走，对于左右两边，如果left已经大于了mid，说明左边的排序已经完成了，直接就可以开始拷贝右边了，如果right大于了hi，说明右边的排序处理完了，就可以拷贝左边了。

这个算法是稳定的算法，时间复杂度是$O(n\log{n})$，空间复杂度是$O(1)$。 

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/mergeSort.png)

### 快速排序

快排是在归并的基础上进一步优化，在归并排序中，因为每次都选的中间位置开始划分数组，所以划分的次数是一定的，这是它稳定的原因。快排通过每次分割数组的点随机化来打破这种稳定。

这样要是运气好，随机到的点正好遍历一次就可以把数组排好序，那就是$O(n)$，要是运气不好，那最差也就是回到稳定排序的程度，和归并排序的复杂度一样$O(n \log{n})$

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/quick.png)

### 寻找数组中第K大的数

这个题目要求是给了一个无序的数组，要找到第k大的那个数，直接一点能想到的解法就是先对数组排序，然后根据下标就能获取到。但是这样的思路还可以用快排来更进一步优化，我们需要寻找的是第k大的数，也就是说只需要排序好前k个数就行了，当快排随机的基点等于前k的大小时，这次排序好后的数组的**基点**值真好就是第K大的数了（因为快排每次会把小于等于基点的数排在基点左边，大于的排在右边）

代码如下:

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/lastKWithQuick.png)

还有一种巧妙的思路，就是利用最小堆，最小堆每次添加元素时，会按照大小排序，寻找第k大的数，将堆大小维护在K值，在添加完数组数据后，位置堆定的就是我们需要的了，这个代码非常简洁易懂。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/lastKWithPriority.png)

## 2021/01/04

- 完成三道算法题
- 看了一遍小专栏的Handler源码分析，想想自己写的时候需要的大纲

###[节点间通路](https://leetcode-cn.com/problems/route-between-nodes-lcci/)

这个题目一看题目，首先是懵逼的，题都不太懂，主要是没有明白这个`graph`数组所代表的意思，后来搜了一下知道，数组中的每一个数组代表的是从多少到多少的连接，没有方向。这下思路就开朗了。使用**BFS**进行遍历不就完了，结果调试的数据是没有问题的，提交上去就超时，一看用例。。。。十万个节点的图(这用例我拷贝到idea中，idea直接崩了=。=)，那就是应该没有优化遍历的问题。我观察数组，发现子数组第一个值是有序递增的，那就是记录开始的位置进行遍历，后来一想，不对呀，随着遍历的深入，前一个点连接的后一个点是不知道连接到哪儿的，如果用**BFS**一个一个添加，那么在数组中的位置就不是挨着遍历的。。。

```
节点间通路。给定有向图，设计一个算法，找出两个节点之间是否存在一条路径。

示例1:

 输入：n = 3, graph = [[0, 1], [0, 2], [1, 2], [1, 2]], start = 0, target = 2
 输出：true
示例2:

 输入：n = 5, graph = [[0, 1], [0, 2], [0, 4], [0, 4], [0, 1], [1, 3], [1, 4], [1, 3], [2, 3], [3, 4]], start = 0, target = 4
 输出 true
```

最后看了答案，知道了[邻接表](https://zh.wikipedia.org/wiki/%E9%82%BB%E6%8E%A5%E8%A1%A8)这个东西，他基本就是我前面思路的实现方案了，通过建立邻接表，将节点的起点都放在一起，去遍历就方便多了。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/whetherExist.png)

### [ 特定深度节点链表](https://leetcode-cn.com/problems/list-of-depth-lcci/)

这道题一开始想的是用递归，发现好像不行，递归无法知道兄弟节点的子树情况。所以改变思路用的**BFS**，**BFS**处理起来思路就比较简单了，将每一层的**parent**节点添加到队列中，然后遍历，一次添加到链表中即可。对我自己来说，需要注意的就是这个头结点的用法，因为第一遍写的时候，用了很多判断来出来头结点的问题。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/listOfDeath.png)

## 2021/01/05

- 完成了三道算法题
- 读了一遍hashmap和concurrenthashmap的源码，整理了源码的注释，了解到了UnSafe工具包
- 了解了下内存泄漏的一些方案。

### [检查平衡性](https://leetcode-cn.com/problems/check-balance-lcci/)

这个题对于练习二叉树的遍历有比较好的价值，所以记录一下，题目要求是检查二叉树是否是平衡的（任意左右子树的高度差不超过1），看到这道题的思路就是，递归计算左右子树的高度，但是也有个问题，就是当你在某个子树判断时，已经得出这棵树非平衡二叉树了，怎么结束递归呢？开始我想的是返回` Boolean`，但这样又没法计算高度，所以就用一个负值来代替`false`的情况。

代码如下

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/isBalanced.png)

### [合法二叉搜索树](https://leetcode-cn.com/problems/legal-binary-search-tree-lcci/)

这个题我知道思路，需要借助额外的参数来比较子树与根root的大小，但是细节上还是没控制好，下面的代码逻辑比较巧妙的地方在于，他没有在当前节点去比较左右子树和当前root的合法性（这其实是书上一开始指出的错误思路，而我就是这样做的，就陷入了死胡同），而是先比较了当前root和父节点的合法性，这样就保证了到当前root为止，都是符合**BST(二叉搜索树)**的规则的，接下来需要做的事就是向下遍历去判断子树是否也符合了，而且因为数字左右是递减和递增的关系，那么前面的合法性判断就能很好的传承下去（当前root假设是左子树的节点，已经通过了`max != null && root.val >= max.val`这句的判断，那对子树来说，左右子树的`val`肯定是都需要符合小于`root.val`，这样判断就是依次递减的一个逻辑，只要出现了大于父节点的`root.val`，就非法了）

```kotlin
if (min != null && root.`val` <= min.`val`) return false // 这句是右子树的判断条件，因为从root开始，min赋值的是root
if (max != null && root.`val` >= max.`val`) return true // 这句是左子树的判断条件，因为从root开始，max赋值的是root
```

下面的代码从`root`开始想就能理解了，一开始左右的都是null，然后`root`分别赋值给了`max、min`，赋值`max`就是用来判断左子树的，赋值`min`就是用来判断右子树的。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/isValidBST.png)

### [后继者](https://leetcode-cn.com/problems/successor-lcci/)

这个题目一开始我想的是，像检查合法性一样，利用递归遍历每个节点，然后找到目标节点后，去拿到他的下一个节点，但是在写代码的过程中就发现事情没有这么简单，因为是中序遍历的后继节点，那么目标节点的位置就可以分好几种情况，例如目标节点正好的某个父节点的时候，需要判断右子树的情况，如果右子树为null，就是它的父节点，但是在递归中拿父节点是很不好操作的，到这里，这个思路基本就可以宣告无法执行了。

题目还有条件是这是一颗**BST**，利用它的性质可以搞事情。我们先以寻找目标节点为目的，如果目标节点的值小于当前节点，说明在左子树，继续遍历；大于了，就是在右子树，也继续遍历；等于了呢？等于的话结果就出来了，就是右子树最左边的节点，这个时候就需要一直赋值左子树的值了，那这个操作和前面的遍历左子树有什么区别么？并没有区别，你想想看遍历左子树的目的是什么，是寻找目标值，之所以在左子树寻找是因为当前节点的值大于了目标值，而寻找右子树最左边的节点，这个节点有什么性质，性质就是它正好是整颗右子树中第一个比目标值大的节点。

最有意思的是遍历的退出条件，对于需要寻找的节点来说，目标节点可能位于左叶子、右叶子和根。那么最开始我们疑虑的如果是右叶子如何去寻找父节点的父节点的问题，就可以通过两个指针来解决，先将结果指向父节点的父节点，另一个指针指向父节点，然后去判断父节点的情况，如果目标值小于父节点了，极端情况就是它就是左叶子，那么这个时候两个指针同时向左前一步，结果节点指向了父节点，另一个指针指向了左叶子，这个相等了，赋值右节点，是个**null**，退出循环；如果目标值等于父节点，那么结果是右子树的左边的左叶子，情况和左子树的遍历一样；如果目标值大于了父节点，也极端一点就是右叶子了，结果要么是右叶子的右节点，要么就是父节点的父节点，也就是现在结果指针指向的节点，这两种情况就在循环的判断条件里了，如果无右节点，赋值为**null**那么就退出了循环了，如果有，就会继续向下，重复等于的逻辑。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/inorderSuccessor.png)

## 2020/01/06

- 完成三道算法题

### [ 检查子树](https://leetcode-cn.com/problems/check-subtree-lcci/)

检查子树就是常规的二叉树的遍历，使用递归的方法，先找到目标子树，找到后再对比遍历就可以了。需要提一下的是这里是普通的二叉树，如果是二叉搜索树，还可以进一步优化搜索目标子树的逻辑，第二个是这存在一颗子树的节点一样，如果树中有多个节点的值和目标子树一样，需要用数组来保存所有的子树节点，然后遍历数组比较。相对来说，这道题思路比较简单。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/checkSubTree.png)

### [二叉搜索树序列](https://leetcode-cn.com/problems/bst-sequences-lcci/)

题目要求解出所有能够组成目标**BST**的可能数组，一旦题目中出现**所有**这个字眼儿，就可以考虑回溯算法，全排列的套路了。全排列的模板如下

```kotlin
fun backTrace() {
  if (xxx) {
  	// 结束条件，添加结果
      result.add(path)
  	return 
  }
  
  for ( 剩余选择) {
  	// 添加选择后的结果
      path.add(xx)
      // 向下遍历
  	backTrace()  
  	// 回溯
      path.remove()
  }
}
```

**剩余选择**的对象就是我们的叶子节点，所以这里需要一个队列来维护剩余节点，通过遍历剩余节点来遍历整颗树。当剩余节点为0时，说明树已经遍历完了，此时就是一个**路径**。接下来就是回溯，这个得分析下例题

```
示例：
给定如下二叉树

        2
       / \
      1   3
返回：

[
   [2,1,3],
   [2,3,1]
]

```

对于示例中这样的树，我们根据上面的模板能想到`[2,1,3]`这样的结果，首先添加的是`root`，这个时候的剩余选择是其两个子节点`1,3`，那么把剩余选择添加到队列中进行遍历，然后遍历顺序就是`1`，这个停下来想想流程，在一个**for**循环中，先拿到队列中的`1`节点，然后执行的是继续遍历`backTrace`，这个时候的队列中就只剩下了`3`，也就是说`3`在路径中的添加不是在第二层`1,3`的遍历中，而是在第二层遍历到`1`时进行下一层遍历时被添加的，然后才是添加`3`并进入`3`的下一层，这个时候队列为空，就添加了`[2,1,3]`。接下来回溯，要得到`[2,3,1]`，因为队列为空，回溯的目的肯定是将走过的路再添加回去，再看看前面的逻辑，有一点遗漏了，那就是在第二层遍历时，只处理完`1`就得到了一个路径，接下来遍历的应该是`3`，这样才会有`[2,3,1]`的结果，所以可以从这里去倒推逻辑，在处理第一个路径后，停留在`3`，此时队列为空，我们需要把数据插回队列以便后续的遍历，同时路径回退，而插回的顺序正好就是`[3,1]`(`3`插回后，回到第二层处理`1`处，这个时候也是把`1`插回队列)，这样在`1`结束后，遍历第二个数的时候就是`3`了，这个时候可能有疑问，此时的队列为`[3,1]`，第二个数不是`1`么，这个就是一个细节了，在全排列的遍历中，每一层的队列遍历代表的是次数，而不是从队列中根据索引去获取值，对每一个根节点来说，他遍历的次数最多两次（左右子树的选择），最少就是零次（叶子节点的下一层），对于每一层来说，遍历的次数就是这一层的所有节点说，所有从这里来解释，其实全排列的模板就是在树上一层一层的处理，每一层从左开始往叶子节点前进，直到这一层所有的节点都到达叶子节点。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/BSTSequences.png)

### [首个共同祖先](https://leetcode-cn.com/problems/first-common-ancestor-lcci/)

对于公共祖先节点，有两种情况，第一是这个节点的左右子树分别包含了`p、q`两个节点，那么这个节点就是公共节点，第二种情况就是这个节点本身等于`p或者q`，则另一个节点只能在左子树或者右子树了。所以换成代码就是，对于当前节点满足`root.val == p.val || root.val == q.val`，那包含这个节点的子树满足了情况一，或者对应情况二的前置条件，接下来就是分别判断情况一和情况二的后置条件了。情况一中，假设当前节点已经被包含在节点`x`的左子树或者右子树，需要判断的是另一个节点是否也包含在这个`x`节点的另一颗子树上，可以将包含作为遍历的结果返回，就是只需要判断哪个节点的左右子树，都满足包含`p或者q`，那这个节点就是公共祖先了。情况二的理解就更容易了，当前节点是`p、q`之一，只需要判断其子树之一是否包含剩下的节点就可以知道该节点是不是公共祖先了。

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/lowestCommonAncestor.png)

## 2021/01/07

- 完成了一道算法题
- 开始编写Handler的总结，从梳理代码逻辑开始，然后准备复盘到总结架构图、流程图和前置知识
- 在**Typora**中配置了图片上传代理，支持`PicGo`直接上传图床

### [求和路径](https://leetcode-cn.com/problems/paths-with-sum-lcci/)

这道题，一开始看完题目，我第一想到的使用回溯进行全排列，但是直接全排列没法满足任意一个节点开始。所以这里需要先使用遍历节点，以每个节点开始进行全排列来计算。

代码如下：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/pathSum_2Dfs.png)

这种算法的时间复杂度比较高$O(n^2)$，属于一种暴力解法。

答案区有个很精妙的答案，使用**前缀和+回溯算法**，前缀和就是达到当前节点的路径之和，那么把树上从`root`到当前节点的一条路径展开为线段，使用**当前节点A的路径和**减去**目标和**，这个差值如果是这一条路径上的**某个节点B的路径和**，那就说明**在节点A和节点B之间节点的节点和为我们的目标和**，用式子表示：`节点A路径和 - 目标和 = 节点B路径和`，而**节点B起(不包含节点B)到节点A，这一路径就是节点和等于目标和的路径**。在这种算法中，只需要遍历一次，存储所有节点的路径和，所有时间复杂度和空间复杂度都是$O(n)$。

做了一个动图，更直观的展示这一流程：

![](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/prefix_PathSum.gif)

 ## 2021/01/08

- **Kotlin**中对于二进制和十进制的转换

  ![trans_between_2_and_10](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/trans_between_2_and_10.png)

- 完成**Handler**流程分析的初稿，还有整体架构、扩展内容未完成。

### [三步问题](https://leetcode-cn.com/problems/three-steps-problem-lcci/)

经典对于斐波那契数列的应用，用动态规划来做。

![waysToStep](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/waysToStep.png)

## 2021/01/09

- **《Android源码设计模式》**的代理模式阅读，结合`AIDL`和`Retrofit`去理解静态代理模式和动态代理模式。[文章传送门](https://grieey.github.io/2021/01/09/%E7%BB%93%E6%9E%84%E5%9E%8B%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F-%E4%BB%A3%E7%90%86%E6%A8%A1%E5%BC%8F/)

- 进行了OPPO外包内置应用开发的二面。

## 2021/01/10

- 做了两道算法题。
- **《Android源码设计模式》**的装饰模式阅读。
- 做了**Rxjava**的流程动画和技术分享

## 2021/01/11

- 重新梳理了脑图，根据面试题增加了一些内容，整理了本周计划

- 文字描边效果实现方案：**Paint.Style.FillAndStroke**这个风格是会在文字填充的情况下，在外部再加一圈描边，其实对于**Paint**的这几种**style**，首先文字可以理解为1px的框绘制完成的，如下图的黑色线，**Fill**的效果就是用黄色填充的样子，而**Stroke**就是红色。结合前面的知识，要实现的描边（也就是**Stroke**的颜色和**Fill**不一样），可以考虑绘制两个文字，底部的用**Stroke**，顶部的用**Fill**

  <img src="https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/drawH.png" alt="drawH" style="zoom: 33%;" />

- t1，t2，t3三个线程按照顺序打印1到100的代码示例，可以使用`join()`方法实现

  ![thread_print_inorder](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/thread_print_inorder.png)

- Glide的缓存策略源码分析，理清楚了逻辑

## 2021/01/12

- 了解了recyclerview的缓存机制、内部处理点击事件冲突的逻辑
- 重新复习了一遍之前的笔记，发现在很多知识点上有细节不到位的情况
- 去书声科技一面，整理了面试题

## 2021/01/13

- gradle配置本地镜像`file:///Users/griee/Documents/Workspace/GradleHome/gradle-6.8-bin.zip`
- 一个新奇的想法，写一个脚本，定期（看gradle release是否有订阅服务）去gradle release下载不同版本的gradle，然后配置本地gradle，在拉取distributionUrl时进行替换
- 基本清晰了**ArrayMap**和**SparseArray**的实现原理和扩容机制。及和**HashMap**的对比。
- 梳理了一遍从**Luncher点击应用图标开始**到启动**Activity**的源码流程

## 2021/01/14

- 复盘写过的算法思路，一些经典的代码，如归并排序、快速排序等。

- Android多进程配置中，带**:**代表的是私有进程，否则是全局进程。

- 判断回文链表的思路，这个代码实现细节稍微复杂，

  - 利用快慢指针，找到链表的中部，
  - 从中部开始，翻转链表
  - 接着快指针回到头部，和慢指针一起移动，判断是否一致
  - 一致后，快指针停留在中部，继续调用翻转链表的方法还原后半部链表

  整个思路不需要额外的空间，时间复杂度也还好，没有嵌套循环，是$O(n)$。

### [股票的最大利润](https://leetcode-cn.com/problems/gu-piao-de-zui-da-li-run-lcof/)

这个题，经典的使用动态规划进行解决，有一系列的股票问题，先说说当前的这个。

题目是给了一个数组，元素代表当天的股价，只能买卖一次，能获取的最大利润是多少。

因为只能买卖一次，所以不会涉及到**K**的问题（买卖K次），整个解法的维度就从三维降低到了二维（进行数据压缩后可以到一维）。这里面，对于每一天来说，选择的就是**买**或者**卖**这两种行为，接下来状态的转移就是买卖后的利润变化。对于买来说，利润减少了当天的股价，但这道题只能买卖一次，所以没有前面利润的积累，只会减少当天的股价（在股票的最大利润系列题目中，第二题就是无限次买卖，这个时候买就需要加上前一天的利润了）；对于卖来说，利润增加了当天的股价。

这里贴一下原始的状态转移方程

```java
原始的动态转移方程，没有可化简的地方
dp[i][k][0] = max(dp[i-1][k][0], dp[i-1][k][1] + prices[i]) // 第i天，最多允许k笔后没有持有股票的利润
dp[i][k][1] = max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i]) // 第i天，最多允许k笔后仍持有股票后的利润
```

这里对**K**需要说明一下，**i**和**0**代表都是某一刻的状态，即是在整个穷举过程中的一个变化，而**K**是这一刻状态中需要穷举的量。

还有两种情况就是，**含冷冻期和手续费**：

- 冷冻期的解决方案就是：卖出的时候不是**dp[i - 1]**，而是**dp[i - 2]**；而且需要注意数组下标问题，i需要大于2，前两天是没法卖的，都是0。
- 手续费更简单：在卖出的时候多减一些，**dp[i - 1] - prices[i] - free**就OK了。

![max_profit](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_profit.png)

但是对于**K = 2**的情况，从上面的状态转移方程，始终没有想明白对**K**的穷举，所以这里还是用官方的思路吧，比较清晰易懂。对需要交易2笔，在第i天时存在以下几种状态：

- 不进行任何操作；
- 在今天买入，此时的利润为**-prices[i]**；
- 在今天卖出，此时的利润为**prices[i]**；
- 在已经交易了一次的基础上，再买入，则利润为**前一次卖出时的利润-prices[i]**；
- 在已经交易了一次的基础上，再卖出，则利润为**第二次买入后的利润+prices[i]**。

这几种状态就把第i天描述完了，那么如何根据第i-1天来推导第i天呢，其实，对于新的一天，都可以在不做任何操作和以上五种状态中选一种，谁最大，就是谁了。

以下是代码：

![max_profit_2](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_profit_2.png)

当**K**为任意数的时候，这里直接说解决方案：

主要是处理**K**很大的时候的海量计算问题，当**K**大于了`prices.size / 2`，其效果和**K**正无穷一样，所以就是上面无限次交易那样省略**K**；当小于时，其实就是状态转移方程，进行穷举了。

## 2021/01/17

- 把Android多线程、线程间交互、多线程原理的视频看了一遍

- 尝试写demo去验证多线程优化的逻辑，开始想的使用生产者和消费者来处理。后面编码发现不行，最后的思路就是在一个子线程中，提交任务去获取对应的消息列表，然后使用**CountDownLaunch**去控制这些子任务。伪代码如下：

  ```kotlin
  fun run () {
    var startId = 0
    cdl = CountDownLunch(coreNum)
    while (startId == curId) {
         for ( i in 1..coreNum) {
    	  // getMessage是一个内部类，在完成任务后，调用cdl.countDown()
    	  val run = GetMessage(startId)
    	  startId += count
    	  pool.submit(run)
    	}
    	cdl.await()
    }
  }
  
  class GetMessage() {
    fun run() {
      val list = getMessage()
      res.add(list)
      cdl.countDown()
    }
  }
  ```

## 2021/01/18

- 完成两道算法题。之前练习过的剑指offer的题目很多又忘了，需要重新刷

### [重建二叉树](https://leetcode-cn.com/problems/zhong-jian-er-cha-shu-lcof/)

根据前序和中序遍历的结果来恢复二叉树，对于一个最简单的二叉树，前序就是**中左右**，中序就是**左中右**这样的顺序。根据这样的规律可以得出，在前序遍历的数组中，第一个位置的就是**root**节点，而在中序遍历的数组中，**root**节点的左边的数据，就是左子树的节点们，而右边的就是右子树的节点们。这样第一层的划分就完成了。接下来就是再将左右的区间下放到下一层再进行一次这样的操作就能完成整个二叉树的重建了。

关键点在于，用前序去确定**root**节点，用中序去划分左右子树。

代码如下：

![build_tree](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/build_tree.png)

上面这种属于常规的分析题目后能得到的正常思路，其实从判断条件可以看出，还是挺复杂的。

这里还有一种思路更简单一点的：利用中序遍历的特点，在上面我们是使用start和end来划定的范围，还需要进行一次中序的位置定位，而接下来的思路就是将这种定位的操作放在了每一次的遍历中。对于中序来说，**root**索引的位置就是构建左子树的停止位置。当前的stop值等于中序中的值，那就说明对于**root**来说，左子树已经构建完成。梳理下这个过程，例如是1,2,3,4这样一颗数的中序遍历，3是**root**，那么构建左子树需要调用3次方法进行**TreeNode**的生成，这样也正好将索引增加到了3的位置。其实理解了上面的代码，这里应该不难理解，用的是同一种方法来处理左子树的构建的结束条件。至于右子树，构建时只要在数组范围内或者stop范围内，就可以了。

![build_tree_simply](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/build_tree_simply.png)

重新来理解下`if (inorder[inStop] == stop)` 这句代码中的逻辑，同样是代码注释中的例子：对于`root = 4`来说，左子树的`stop`值是`4`，但是对于更下面的节点来说就不是了，比如以`root = 2`这颗子树来说，走到左子树`1`时，当`root = 1`这颗树遍历时，调用`buildTree`的方法去赋值它的左子树时，此时的`inStop = 0，inorder[inStop] = 1`，而`buildTree`的参数传入的`stop`就是`1`。所以这里的`inStop`的意义就是每当遇到最左边的一个子树的遍历到头（这个头就是它的`root`在中序的索引）时，就会前进一步。

对于二叉树构建的更难的版本是二叉树的序列化。

### [反转链表](https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/)

这个题记录在这里是因为，之前自己写的时候，一直对链表处理的有问题，但是今天再写的时候就很顺畅。不知道当时写的时候哪个细节出错了，反正在这里记录下，题目本身很简单的。

![reverse_list](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/reverse_list.png)

### [合并两个有序数组](https://leetcode-cn.com/problems/merge-sorted-array/)

这个和下面的合并链表是同一类型的题目，合并数组因为知道大小，所以直接使用双指针就是比较快的做法。

![merge_array](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/merge_array.png)

### [合并两个排序的链表](https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/)

合并链表因为没法知道大小，不能像数组那样很快的倒序合并。这里仍然是`dummy`这种头指针的用法。最后返回`dummy.next`就可以了。

![merge_link](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/merge_link.png)

## 2021/01/19

- 回顾昨天的算法

### [从上到下打印二叉树](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-lcof/)

这个是一个系列的算法题，本质上属于对二叉树的遍历，思想上还是使用的**BFS**去进行的遍历。这一套流程的思路就是通过一个队里来不断的增加向下搜索的可选择的数据。在遍历到每一个节点时，根据我们的条件来看是否需要把他添加到队列中。

比如说这个题，需要从上到下的打印整颗二叉树，对于队列来说，每一个节点的可选值就是她的左右孩子。条件也很简单，左右孩子不为空，就添加进入。由于是队列，会按照添加的顺序来打印，那么对于同一层的节点，在依次遍历队列时就是一个按着取的过程，可以想象为将二叉树拉平为一条直线。

代码如下:

![print_bst_1](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/print_bst_1.png)

### [从上到下打印二叉树 II](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-ii-lcof/)

第二个在打印上和第一个的区别就是增加了层数的限制，你需要明确的分出每一层来，其实可以结合之前计算二叉树的高度的思想，在每一层中再加一个for循环；这个for循环的意义就是，当一次循环遍历结束，就是处理完成了一层的节点。仔细想想，当`root`时，有两个节点，下一次循环就是2次，这两个节点处理完，下一次循环时，就应该是4次，因为每一次循环都会`poll()`所以处理完一层，剩下的size就是下一层的数目了。

![print_bst_2_2](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/print_bst_2_2.png)

### [从上到下打印二叉树 III](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-iii-lcof/)

第三题比第二题难处理的点在于从右遍历这个怎么解决呢，其实换个角度，我可以不改变遍历的方式，改变结果的展示方式不就行了：对于从左到右的遍历，在添加结果到数组中时，就正常的添加在尾部，这样结果的顺序和遍历的顺序一致。当从右往左的遍历的时候，其实我们还是从左往右遍历，只是添加结果的时候是每次将结果添加到前面，来达到结果是从右往左的显示。

![print_bst_3](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/print_bst_3.png)

### [删除链表的倒数第 N 个结点](https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/)

还是快慢指针，这是我第一时间能想到的，但是细节处理很不到位。想的是用删除第n个节点的方法来删除，发现始终都有问题。还是头结点的使用，建立dummy节点，这样slow指针指向的下一个节点才是需要删除的。

![remove_n_from_end](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/remove_n_from_end.png)

## 2021/01/20

- git修改默认分支名称的命令`git config --global init.defaultBranch main`，该命令需要在`git --version 2.28`以上才有用，如果使用`brew upgrdate git`之后，仍然提示git版本不对，`which -a git`来查看下有哪些版本，一般情况下有**MacOS**自带的和**HomeBrew**安装的两个版本，所以需要在**PATH**中配置；`vim ~/.bash_profile`中添加**brew**对应的路径，一般是`/usr/local/bin`这样的，将这个添加到**PATH**中去。`export PATH=$PATH:/usr/local/bin`这样的。
- 看了[高爷的Systrace教程](https://www.androidperformance.com/2019/12/21/Android-Systrace-CPU/)的前半部分，醍醐灌顶。

## 2021/01/21

- 阅读《Java并发编程实战》的第2章，想想对于**volatile**这个关键字的理解，正如它的定义一样，它是用来保证**写-读**这样一个操作的透明，即对其他线程可见。它无法保证的是**写-写**这样的操作，所以这样的操作肯定会带来脏数据。因此，才会有锁机制来保证**写-写**操作的安全性。
- 继续看**Systrace**的文章，从**Systrace**的角度去看系统的事件分发和绘制流程。
- 对于弱引用的理解，这个东西是临时性使用的，当短时间内有大量的数据使用，可以用弱引用来创建临时的对象。还有一种是结合另一个引用来使用（关于这点还有疑惑）。

### [ I. 二叉搜索树的最近公共祖先](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-zui-jin-gong-gong-zu-xian-lcof/)

这个题目和之前做的题目的唯一差别就是，这是一颗**BST**，所以可以利用**BST**的性质。如果求两个节点在一颗**BST**中的公共祖先，那它一定满足的条件就是，`p.val <= root.val && q.val >= root.val` ，其中`p、q`的位置可以互换。这个条件对应在**BST**就更明显了，因为对于**BST**来说，`left.val < root.val && right.val > root.val`，是不是一致了。

![lowest_common_ancestor_bst](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/lowest_common_ancestor_bst.png)

### [判定是否互为字符重排](https://leetcode-cn.com/problems/check-permutation-lcci/)

这个题目比较简单，有多种做法可以解决，我这里用的滑动窗口的简版。滑动窗口的本质，是比较频数，即一个字符在字符串中出现的次数。对于本题而言，两个可以互相重排的字符串，本质上同频数的字符的随机组合而来。例如`abac`，这个字符串中，`a`出现2次，`b,c`各出现一次；而`bcaa`，这个字符也是一样，`a`2次，`b,c`各一次，他们的字符在频数上是一致的。这个题目中，时间复杂度至少都是$O(n)$，这个没的说，肯定得遍历一次。而空间复杂度，有些解法是用asc码来解决，那么需要的数组大小就是26个字符，而对于滑动窗口来说，最坏也就是26，最好可以达到1。

代码如下：

![check_permutation](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/check_permutation.png)

### [URL化](https://leetcode-cn.com/problems/string-to-url-lcci/)

这个题目做过一次了，之前是看的答案，这次是自己做出来的。再次贴出来是提醒下**String**的一个方法`String(array, 0, end)`

![replace_spaces](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/replace_spaces.png)

## 2021/01/22

- 在gradle文件中无法读取都`local.propertires`中定义的变量，创建文件流去读取

  ![gradle_get_properties](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/gradle_get_properties.png)

  这个问题不常见，可能是不同版本的gradle插件导致的问题，因为其他的版本的我看直接使用`properties.get()`可以直接获取到的。

- 使用`bintrayUpload`命令报错**HTTP:401**这种错误就是`bintray.user、bintray.apikey`没有设置好或者配置读取没有成功，参照上面的读取，配置就是在`local.properties`中设置就行了。格式是`bintray.user=xxxx`没有引号。

- 想要不同的**task**按照一定顺序执行，可以使用`mustRunAfter`，代码如下：

  ![order_running_task](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/order_running_task.png)

### [用两个栈实现队列](https://leetcode-cn.com/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/)

  这其实也是有一个系列的题型的，就是使用栈来实现队列的数据结构。这个题比较简单，贴出来做一个抛砖引玉的效果。双栈的思想就是，利用好入栈和出栈的顺序，我们首先想想队列，先进先出，然后栈是先进后出。那要是把两个栈的屁股连在一起，打通，这样压入栈A的元素出现在栈B的底部，想想这个场景，假设两个栈的底部是相通的，然后元素进入栈A时，也就是到了栈B的底部，然后第二个元素入栈，对栈A来说，从栈顶到栈底是`2,1`，对栈B来说就是`1,2`。所以基于此，针对这道题就是，当有元素入栈，就只入栈A，当出栈时，先看栈B是否有元素，有就出栈，没有就把栈A的元素依次放入栈B中，这样就到达了上面说的连通的效果。

![two_stack_for_queue2](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/two_stack_for_queue2.png)

### [旋转数组的最小数字](https://leetcode-cn.com/problems/xuan-zhuan-shu-zu-de-zui-xiao-shu-zi-lcof/)

这道题第二次做的时候，虽然有思路了，但是细节零分。。。。仍然错了很多次，特别贴出来。

说一下思路，基础还是基于二分查找来做，关键在于判断。首先这样一个旋转（或非旋转的数组中），因为有序，我把整个数组以旋转点位置为界限，分为前半部分和后半部分，例如`3,4,5,6,1,2`这样一组数据，那么`3,4,5,6`就是前半部分，`1,2`就是后半部分。可以得出一个要点就是`left`对应的值是后半部分的最大值，`right`对应的值是前半部分的最小值，如果计算了`mid`，比后半部分的最大值(`right`)还大，那`mid`肯定坐落于前半部分的闭区间内，及`mid` 为`3，4，5，6`的其中一个，反之，要是`mid` 比前半部分的最小值(`left`)还小，那就位于后半部分中。根据二分查找的思路，如果坐落前半部分的区间，应该移动左区间来缩小范围，反之亦然，坐落后半部分就移动右区间。如果`mid`即不大于后半部分的最大值，又不小于前半部分的最小值，就只有一种情况:`2,1,2,2,2`这种。这种就只有遍历，找出从`left` 到`right`中的最小值。

![min_array](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/min_array.png)

## 2021/01/23

- 四个线程，其中第四个线程需要等前三个线程完成计算后去统计他们的结果。除了使用**CountDownLatch**外，还可以使用**BlockingQueue**，其插入、删除和检查队列的方法有以下的这些：

  |         | Throws Exception |  特殊值  |  阻塞  |            超时             |
  | :-----: | :--------------: | :------: | :----: | :-------------------------: |
  | Insert  |      add(o)      | offer(o) | put(o) | offer(o, timeout, timeunit) |
  | Remove  |    remove(o)     |  poll()  | take() |   poll(timeout, timeunit)   |
  | Examine |    element()     |  peek()  |        |                             |

  其中，不同的方法带来的效果不一样：

  - **Throws Exception：**调用方法后，该方法不能立刻发生，则抛出异常；
  - **特殊值：**调用方法后，该方法不能立刻发生，则返回特殊值（`true和false`）；
  - **阻塞：**调用方法后，该方法不能立刻发生，那么就阻塞；
  - **超时：**调用方法后，该方法不能立刻发生，就阻塞，但是有超时的设定。

  基于这个思路，将前三个线程看做生产者，最后一个线程看做消费者，那么也可以实现消费者等待前三个线程生产结束后再统计。伪码如下：

  ```kotlin
  val queue = BlockingQueue<Int>()
  thread {
    queue.put(1)
  }
  thread {
    queue.put(2)
  }
  thread {
    queue.put(3)
  }
  thread {
    val a = queue.take() // 阻塞，等待计算
    val b = queue.take()
    val c = queue.take()
    val res = a + b + c
  }
  ```

  

### [二叉树的右视图](https://leetcode-cn.com/problems/binary-tree-right-side-view/)

这个题翻译一下就是找每一层的最右边的节点，一看到每一层这几个字就知道和一层层遍历的算法是同一类型的，前面是每一层遍历打印，这次是找最右边的值，所以相对还简单一些。一样使用队列去添加每一层的节点，每一次的循环就是每一层的遍历，然后依次的赋值左右节点进行覆盖，最后留下的就是最右边的节点值。

需要提一下的是不要忘了`root`节点的值，因为是在循环外部添加的，所以容易忘。

![right_side_view](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/right_side_view.png)

## 2021/01/24

- 简单的复习了下剑指offer中的思路，很多题还是一脸懵逼。

## 2021/01/25

- 配置好了core-ui的自动上传文件。
- 完成了缓存的配图。

### [左旋转字符串](https://leetcode-cn.com/problems/zuo-xuan-zhuan-zi-fu-chuan-lcof/)

简单的题，往往只需要简单的思路...这个题目思路就错了，用字符串拼接，一个遍历就出来了。

![reverse_left_words](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/reverse_left_words.png)

### [滑动窗口的最大值](https://leetcode-cn.com/problems/hua-dong-chuang-kou-de-zui-da-zhi-lcof/)

第二次遇到这个题，仍然滑铁卢，不过好在第二次终于想明白了。

我一开始的思路眉头还是正确的，这个题本质上和包含min函数的题是一样的，通过一个额外的数据结构来维护最大值到最小值的过程，只不过min函数的题是使用的栈来实现的，而这里需要用到的是双端队列。在双端队列中，队首用来存储最大值，队尾用来存储当前窗口中的最小值。

根据题目，可以分为两个阶段，一个是窗口未移动时，一个是窗口移动时，两者的区别主要是移动时需要移除窗口前的那个数，而这个数有可能是上一次窗口中的最大值，所以这种情况下需要去维护队列，达到维护最大值的情况。

接下来就是维护最小值，当我们窗口移动一步，此时就新添加了一个数，这个数需要插入合适的位置才能保证队首是最大值，而队尾是最小值。现在有两个思路，一个是每次新添加一个数，就遍历队列，在合适位置插入来保证队列的有序，这样一来又有新的问题，一个是这种中间插入会移动后边的数，时间复杂度高；二是如果窗口移动过程中，窗口前的数在队列中部，这样窗口都移走了，但是窗口前的数仍然在队列中，肯定会对后续的判断造成问题。所以基于上面的考虑，有这样的一种解法，就是只维护当前新加的数为队列的最小值，按照这种做法，添加完当前值后，整个队列中无非两种情况：一是队首是最大值，队尾是当前值，这样窗口移动过程中，窗口前的数如果是队首，移除队首后第二个数要么比队尾大，要么是队尾，下一次的判断又是循环这个流程，窗口前的数如果不是队首，那就不管呀，反正它不在窗口内了也不在队列内了，回想上面的问题，如果窗口前那个数，正好是队列中部的数，怎么办？事实上，这不可能，首先在移动前，队列保证的是最小值就是当前窗口的最后一位，那么下一次要移除的就不能是这个数，除非这窗口大小是1；第二种情况就是队首即队尾，其实更简单，就像刚刚说的，除非窗口大小是1，队列的有序仍然保持。

以下是代码：

![max_sliding_window](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_sliding_window.png)

### [队列的最大值](https://leetcode-cn.com/problems/dui-lie-de-zui-da-zhi-lcof/)

这个题和上一个题是解法同类型的。求队列中的最大值，所以肯定需要一个双端队列来维护最大值和最小值，同时还要保证出队的顺序，那么就只有用两个队列了。

基本思路和上面一样，代码如下：

![max_queue](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_queue.png)

### [连续子数组的最大和](https://leetcode-cn.com/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/)

动态规划的类型，一个连续子数组的和，对于某一个数来说，有两个状态可以选择，一个是本身，而是和前面的数相加。

![max_sum_of_subArray](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_sum_of_subArray.png)

## 2021/01/26

- 完善了handler的知识总结

## 2021/01/27

- 复习下剑指offer的简单题思路
- 开始尝试向core-ui中添加类别

## 2021/01/28

- 编写通知的UI，初步完成
- 编写core-ext的lib

## 2021/01/29

### [求1+2+…+n](https://leetcode-cn.com/problems/qiu-12n-lcof/)

这个题目是中等难度，但是答案让人很意外，利用的是判断语句的优化

![sum_nums](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/sum_nums.png)

### [ 数组中数字出现的次数 II](https://leetcode-cn.com/problems/shu-zu-zhong-shu-zi-chu-xian-de-ci-shu-ii-lcof/)

题解中使用的是有限状态机来解决的该问题，可以让空间复杂度降低到$O(1)$。我自己写的话是用频数统计来做的，思路比较容易理解一些，不过空间复杂度是$O(n)$

![single_number](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/single_number.png)

### [ 复杂链表的复制](https://leetcode-cn.com/problems/fu-za-lian-biao-de-fu-zhi-lcof/)

这个题目比较难的是处理随机指针指向的位置。常规的直接使用新指针进行拷贝，那么无法处理随机指针的位置，因为随机指针的指向也需要拷贝，但是这个指向就很乱了。

如下：

![](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/01/09/e1.png)

一个一个的拷贝的时候，如果把random指向原来的节点，那就还需要二次遍历，来一个一个寻找随机指针指向源节点的复制节点，这样就很麻烦。

有一个新思路就是将复制节点添加在源节点的`next`上。这样在处理`random`的节点时，只需要找到源节点的`random.next`的就行了。最后再把链表根据`next`一分为二就可以了。

分开链表还是使用头指针的法，很方便，最后返回头指针的`next`就好了。

![copy_random_list](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/copy_random_list.png)

## 2021/01/30

### [礼物的最大价值](https://leetcode-cn.com/problems/li-wu-de-zui-da-jie-zhi-lcof/)

典型的动态规划的问题，首先题目中明确了只能向右或者向下，那么某一点的礼物价值就只能是累加上一行或者左一列的值，可以设置`dp`数组的含义是：`dp[i][j]`为到第i行j列的礼物的最大值。起点就是0,0。这个位置是没有累加的，另外对于第一行和第一列来说也比较特殊，因为第一行只能是从左边来，没法从上面来，第一列也是如此，只能从上面遍历过来没法从左边。其他的位置就可能从上面的一行向下走到达或者左边一列向右走到达，取个最大值。

![max_value_of_gif](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/max_value_of_gif.png)

## 2021/01/31

- 看协程相关的内容

### [二叉搜索树与双向链表](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-yu-shuang-xiang-lian-biao-lcof/)

将二叉搜索树转化为双向链表，使用双指针，对树的遍历使用中序遍历，这样能保证头节点是最左边的节点，尾节点是最右边的节点。

所以根据框架，在中间进行操作，也就是将双指针指向树，使用`pre`指针游走。

![tree_to_doubly_list](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/tree_to_doubly_list.png)

### [丑数](https://leetcode-cn.com/problems/chou-shu-lcof/)

说实话，这个题的思路不怎么明白。因为丑数的定义是质数只包含`2,3,5`的倍数，所以

![nth_ugly_number](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/nth_ugly_number.png)

### [栈的压入、弹出序列](https://leetcode-cn.com/problems/zhan-de-ya-ru-dan-chu-xu-lie-lcof/)

我的思路是根据出栈序列来模拟出栈。从入栈序列中一直添加数据，直到添加到出栈的那个值，代表此时那个值进行了一次出栈操作，则我们也在下面出栈。再继续根据入栈序列添加数据，去找下一个出栈的值。最后根据stack里的值是不是完全出栈来判断。因为不是正确的出栈序列，这个模拟的过程肯定无法成立，就导致stack数据无法出栈完。

![validate_stack_sequences](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/validate_stack_sequences.png)

## 2021/02/01

- 数值类型的操作都需要考虑溢出的问题。
- **LeakCanary**的实现原理：首先是注册，通过注册**ApplicationLifeCycleCallback**来获取**ActivityOnDestory**的回调，然后在子线程进行观察**Activity**对象，将**Activity**包装到弱引用中，在子线程中不断轮询弱引用队列，查看对应的弱引用是否存在，如果存在，证明被回收了，没有则继续尝试，尝试一定次数后，仍然存在，说明发生了泄漏。

### [构建乘积数组](https://leetcode-cn.com/problems/gou-jian-cheng-ji-shu-zu-lcof/)

废话不多说，上题解图

![](https://pic.leetcode-cn.com/6056c7a5009cb7a4674aab28505e598c502a7f7c60c45b9f19a8a64f31304745-Picture1.png)

不使用除法，就是分别求当前值的左右乘积，则可以列出上列的表格来，所以每一行的左右乘积就是对角线的左右乘积。分别来计算左右，再相乘即可：

- 对于左边，即$A(0)*A(1)...A(i - 2)*A(i - 1)$，而$A(i - 1)$前面的乘积正好是$B(i - 1)$的左边部分的乘积值，所以$B(i) = B(i - 1) * A(i - 1)$构成了左边部分的乘积；
- 对于右边，即$A(i + 1) * A(i + 2)...A(N - 1) * A(N)$，可以从`N - 1`开始求，$B(N - 1) = A(N) * 1$，$B(N - 2) = B(N - 1) * A(N - 1)$。

将上面的规律翻译为代码如下，两次遍历，一次求左边的乘积，一次求右边的乘积和总的乘积。

![construct_arr](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/construct_arr.png)

### [二叉树中和为某一值的路径](https://leetcode-cn.com/problems/er-cha-shu-zhong-he-wei-mou-yi-zhi-de-lu-jing-lcof/)

这道题看题目还以为和上面的路径之和的题目一样，想用前缀和，结果不是，还是使用全排列来做。注意题目条件是从根节点出发，到达叶子节点的路径，所以还相对简单一些。结束条件肯定就是`root == null`，添加的条件就是`track`中的和满足等于`target`。这里有一个小技巧，就是每一层都传入剩下需要的和，这样就不用重复计算`track.sum()`了。

总体思路还是，记住每一层需要做的事，做完了就移除进行回溯。

![path_sum_of_bst](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/path_sum_of_bst.png)

## 2021/02/05

### [好数对的数目](https://leetcode-cn.com/problems/number-of-good-pairs/)

使用数学上的方法来做这道题，需要额外的空间辅助，分析题目中的例子，一个相同的数的数对个数计算公式为$s = (v * (v - 1)) / 2$。所以首先通过`HashMap`来计算每个重复的数组的频数，再计算每组数对的个数和就可以了。

时间复杂度和空间复杂度都是$O(n)$。

![num_identical_pairs](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/num_identical_pairs.png)

### [消失的数字](https://leetcode-cn.com/problems/missing-number-lcci/)

这个题有两个思路，一个是数学上的，包含从0-n一共n个数，正常情况下这些数的总和为$sum = n * (n + 1) / 2$，现在用这个总和去依次减掉数组中的值，剩下那个就是缺少的。

第二种是采用异或运算，因为0和其他数的异或等于本身，而相同的数异或等于0，同样的，上面的数据如果正常不缺少，依次和下标和0异或后应该为0，例如：下标为1，值为1，那么`1 ^ 1 ^ 0 = 0`，第一个1是下标，第二个1是某个下中的值为1。

![missing_number](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/missing_number.png)

### [找到所有数组中消失的数字](https://leetcode-cn.com/problems/find-all-numbers-disappeared-in-an-array/)

这个题是上面的题的进阶版，消失的数目是不定的，但是题目最关键的条件还是所有数的范围是`1-n`，假设我们将数据排好序，且不重复，那么`index + 1 = num`，`num`是下标对应的值。这里可以使用负数来标记存在值的索引，例如，某个下标中有值`1`，那我们将下标`1`对应的值修改为负数，这样当遍历到下标`1`时，只要它的值为负数，就代表数组中某个下标的值为`1`。最后只需要遍历出哪些值不为负数就知道哪些下标的值在数组中不存在了。这样操作的时间复杂度为$O(n)$，空间复杂度为$O(1)$。

![find_disappeared_numbers](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/find_disappeared_numbers.png)

### [主要元素](https://leetcode-cn.com/problems/find-majority-element-lcci/)

这个题目，在$O(n)$的空间复杂度下比较好做，使用一个map来进行频数统计，最后遍历，找到频数最大的那个值就可以了。

如果要实现$O(1)$的空间复杂度，可以使用`times`来遍历统计(**摩尔投票**)，之前有个题目和这个类似，但是区别是那个题目是肯定存在主要数的，这个题目说了不一定，不存在时需要返回`-1`，所以采用左右遍历，如果存在，则两次遍历找到的应该是同一个主要数，如果不存在，则左右遍历的结果会不一致的。

![majority_element](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/majority_element.png)

## 2021/02/06

### [快乐数](https://leetcode-cn.com/problems/happy-number/)

这个题目读完我一直在想，这个无限循环如何结束的问题。确实没有想到这最后会是一个圈，其实道理也很简单。如果一个数的数位平方和一直加下去，是不可能无限的增长的。既然会是个死循环，那么说明肯定是一个圈的。

一个圈就可以用快慢指针来找出相交点，要是这个点存在，说明是个圈，不存在，则最后的尽头就是1了。

![is_happy](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/is_happy.png)

## 2021/02/07

- Android中的`flag_clear_top`标志的使用，如果需要启动的目标Activity在任务栈中，则需要先看该Activity的启动模式，要是标准模式，就会将目标Activity本身及其上的所有Activity都移出栈，再生成一个新的目标实例。如果不想这样可以再添加`flag_single_top`的标志，或者将目标Activity的启动模式修改了，这样就不会移除目标Activity，而是会回调`onNewIntent()`方法。

## 2021/02/18

### [宝石与石头](https://leetcode-cn.com/problems/jewels-and-stones/)

这是一道简单的题目，本身确实没有啥难度，利用哈希表集合就可以解决这个题目，常规的思路就是，将宝石的类型一一添加到`set`集合中，再去遍历所有的石头就可以计算出你拥有的宝石个数了。时间复杂度为$O(m+n)$，空间复杂度为$O(m)$。

评论区给出了另一种思路，不需要判断，也不错，将空间复杂度降为了$O(1)$。利用ASC码的特性作为索引，每一个字母对应的ASC码值就是数组的下标，然后遍历宝石将对于的字母下标设置为1，再遍历石头时，依次相加就可以得出了。

![num_jewels_in_stones](https://cdn.jsdelivr.net/gh/Grieey/ImgHosting@main/img/num_jewels_in_stones.png)