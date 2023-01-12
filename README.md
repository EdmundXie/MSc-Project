# Scheduling of Long Running Applications in Shared Clusters
## Overview
In this project, we address the problem of scheduling long-running applications (LRAs) in a cluster in order to maximize efficiency and stability. We consider a simplified version of the Integer Linear Programming (ILP) problem and use both item-centric and bin-centric heuristic algorithms to solve it. We also evaluate the performance of these algorithms using data from Alibaba's open source cluster trace and compare their results to the lower bound of optimal solutions. Our ultimate goal is to identify the most suitable algorithm for this specific dataset.

## Project Aim
The project aims to minimise the number of nodes used when allocating all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other and each node does not exceed the capacity in all dimensions in a specific dataset. 
### Bin packing problem(BP) and its variants 
BP are complex discrete combinatorial optimisation problems. The classical bin packing problem requires that a certain number of items are placed in several bins with equal capacity, the sum of the sizes of the items in each bin does not exceed the capacity of the bin and that the number of bins used is minimised (Vijay V, 2001). 

On top of this, there is also the vector bin packing problem with conflicts $(VB{PC}_d)$ due to conflicts between items (two items cannot be placed in the same bin) and the multidimensional requirement $\left(d\geq2\right)\$ . (e.g.size, weight of the items)

### Scheduling LRAs in shared clusters 
The LRA scheduling problem is similar to $(VB{PC}_d)$ in that the aim is to deploy all the LRAs with a minimum number of nodes without exceeding the constraints. Still, it has some specific requirements [R1]-[R4] as follows:
#### [R1] multi-dimensional resource requirements
In running LRAs in a node, shared resources such as CPU cache and memory of the nodes need to be occupied, and other LRAs cannot use the occupied resources. This means that, in analogy to the items in the bin packing problem, LRAs cannot be placed in nodes in parallel in the same dimension.
#### [R2] LRA conflict constraints
Conflict usually refers to the relatedness of two LRAs, i.e. they can not be placed in the same node.

LRAs with conflict should be placed in different nodes to avoid interfering. For example, LRAs that require a fast response in the same node can increase network latency and affect performance.
#### [R3] Optimisation requirements in large clusters
Clusters are often large in scale, and as the volume of data increases, some algorithms (described in detail later) suffer from performance degradation. Finding a better algorithm that meets the actual requirements is particularly important.
#### [R4] Replicas of LRAs 
A replica is an object that can be dispatched to a node and is part of an LRA. Just as an application can have multiple processes on a computer, an LRA has multiple replicas that can be deployed in different nodes. Therefore, when scheduling LRAs, it must be ensured that all replicas for all LRAs are deployed on the nodes.

Thus, to meet the above requirements, in large-scale cluster management, the following problems are usually considered to optimise cluster utilisation and stability (Mondal et al., 2021) [P1]-[P3]：
#### [P1] Minimise inter-application conflicts
Conflicts exist between applications (e.g. fast response conflicts). Minimising conflicts can reduce non-essential resource consumption and thus improve cluster performance.
#### [P2] Maximising cluster utilisation
When the cluster is often at a low peak, i.e. when the workload is much less than the amount of resources Workload should be concentrated on a few nodes to prevent wasted resources.
#### [P3] Minimise the number of nodes used
When the workload is constant and less than the overall cluster resources, the number of nodes should be minimised to reduce resource costs. 

The scheduling problem for LRAs has many variants from these fundamental three aspects, which is difficult because of conflicts between the aspects.

## Problem Statement
We consider the scheduling problem of LRAs as [P3] considering [R1]-[R3] requirements.

The problem is a d-dimensional vector bin packing problem with conflicts VBPd, it is NP-hard for every d (Panigrahy et al., 2011). This means that when d=1, it is widely believed that there is no algorithm for finding the optimal solution in polynomial time. 

The scheduling of LRAs in nodes usually considers the nodes’ capacity, such as CPU and memory capacity, in analogy to the size of the bin in different dimensions. However, they must be accumulated in each dimension when placed. This means an LRA will occupy a separate CPU and memory buffer at runtime, not shared with other LRAs. There are also conflicts between LRAs that mutually exclusive LRAs cannot be placed in the same container.

Garefalakis et al. (2018), Muritiba et al. (2009) and Clement et al. (2022) used the Integer Liner programming (ILP) model as a formulation for the bin packing problem with conflicts and achieved good results. Inspired by them, we formulate it as a simplified version of the ILP introduced by Clement et al. (2022). Specifically, it deals with [P3] with satisfying all constraints [R1]-[R4].

Notations used in scheduling problems of LRAs are as follows:

<div align=left><img width="728" alt="截屏2023-01-08 21 34 26" src="https://user-images.githubusercontent.com/41847989/211220267-1508d437-61ee-4944-a193-f225657f23fa.png"></div>

Specifically, the problem is to allocate all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other, each node does not exceed the capacity in d dimensions, and ultimately the number of nodes is minimised. It is formulated to ILP as follows:

<div align=left><img width="725" alt="截屏2023-01-08 21 36 56" src="https://user-images.githubusercontent.com/41847989/211220351-bf9b630a-bbce-4aaa-b92e-125993fa0f14.png"></div>

In this ILP, the aim (a) is to minimize the number of nodes used. (b) represents all replicas of all LRAs allocated to nodes. (c) means resources in d dimensions used in nodes not outweighed the capacity of each node. Furthermore, in (d) <img width="182" alt="截屏2023-01-10 21 31 08" src="https://user-images.githubusercontent.com/41847989/211666651-67472a46-9a9e-4c9e-bc28-48a51918df79.png"> is the maximum number of LRA replicas can be placed in a node ignoring conflict restrictions. So (d) and (e) ensure that the resource size required for a replica in a dimension cannot exceed the capacity of that dimension. In addition, the number of placements exceeds the number of LRA replicas that cannot be provided, and at least one replica of an LRA is placed on a node. In the end, (f) represents conflict constraints between LRAs.

## Methods and Techniques
There are various solutions to the scheduling problem of LRAs, heuristic algorithms and their variants and deep reinforcement learning.

Possible solutions for scheduling problems of LRAs —— ILP solver is to solve [P1]
<div align=center><img width="775" alt="image" src="https://user-images.githubusercontent.com/41847989/211660016-e268e39e-bce9-4df4-b2f4-b6b5a7f826e2.png"></div>

As mentioned above, we formulate the scheduling problems of LRAs with conflicts as the ILP and use heuristic algorithms and the ILP solver to deal with it, satisfying requirements [R1]-[R3]. To be more specific, we use item-centric heuristics (e.g. FFDProd, FFDSum, Best Fit Average Sum (BFAvgSum), etc.), and bin-centric heuristics (e.g. FFD-Dotproduct and FFD-L2Norm) to solve this problem, run several experiments on similar datasets of different magnitudes to select the group of algorithms that use fewer nodes or consume less time. Then we adjust the relevant parameters before conducting further experiments to choose the best-performing algorithm.

### Item-centric Heuristics

In the scheduling problems of LRAs, Similar to $(VB{PC}_d)$, the replica of the LRA in question is analogous to an item, and the node where the LRA replica is placed is similar to a bin. But the conflicts in scheduling problems of LRAs are between LRAs instead of replicas, so there is a difference when applying the heuristics described above to this problem. Here is the pseudocode of the item-centric heuristic:

<div align=left><img width="616" alt="截屏2023-01-10 20 57 39" src="https://user-images.githubusercontent.com/41847989/211661058-2a782194-4a55-471f-8eb5-a4fbe12d67df.png"></div>

Item-centric heuristics can be generated by this pseudocode. It starts with activating the first node from the node-set $N$, and sorts all LRAs in order by ${score}_1$. The order there decides if it is a decreasing algorithm, and ${score}_1$ determines how to calculate the weight of an LRA (e.g. Sum, Product, etc.). Then select the LRA with the highest ${score}_1$, and place the maximum number of replicas of the LRA in the activated node picked by sorting the ${score}_2$, if none of them can fit, activate next node. If all replicas of the LRA $l$ are allocated, delete $l$ from $L$.


### Bin-centric Heuristics

The pseudocode of bin-centric heuristics is as follows. It starts with activating the first node from the node-set $N$, and then picking the first LRA $l$ from $L$. If none of its replicas $i_l$ can fit in the current node, activate the next node. If not, the algorithm picks an LRA $l$ in $L$ in an assigned order with the highest ${score}_3$. It uses Dot-Product (5) and L2-Norm (6) to calculate ${score}_3$ as follows: ${score}_3={score}_1\bullet{score}_2$ (Dot-Product)

### Conclusion
In general, we choose item-centric and bin-centric heuristics as the methods. Each method requires calculating a score to be used as the priority of the node or LRA at the time of placement.

In these methods, ${score}_1$ and ${score}_3$ are the criteria for priority placement of LRAs, and ${score}_2$ is the criterion used to prioritise activated nodes. In addition, heuristic algorithms, such as BF, NF, WF, etc., will have many more variants when combined with the multidimensional requirements. We use the following algorithms to calculate ${score}_1$, ${score}_2$, and ${score}_3$.

<div align=center><img width="739" alt="截屏2023-01-10 21 49 18" src="https://user-images.githubusercontent.com/41847989/211669744-5e4fa9f8-03d5-4c9c-84eb-b82017f7e2fe.png"></div>


## Implementation of project
All data are from Alibaba traces which are sampled from one of their production clusters, avaliable on https://tianchi.aliyun.com/dataset/dataDetail?dataId=6287. 
The experiment has three stpes as in following figure.
<div align=center><img src="https://user-images.githubusercontent.com/41847989/186922971-a8bedcee-0247-4727-8a82-33b7152a8d0e.png"></div>




## Running experiments
### Data Formulation and Analysis
The original data is in `src/data/input`. Formulate analyze the data using `Analysis_Generate_data.ipynb` which Generates `processed_data` in that file.
### Alorithms implementation
Run `src/main/java/binpacklib/Main`. In the main function select the location of the input and output files and choose the algorithm to be deployed.
### Alorithms performance visualization
The results are in `src/data/results`. Running `Result_Analysis.ipynb` to analyse the results.

## Results of the Empirical Investigation
The main goal of this project is to find a valid placing while minimising the number of nodes used when allocating all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other and each node does not exceed the capacity in all dimensions. We deploy the experiment with different heuristic algorithms (item-centric and bin-centric) provided by Clement et al. (2022)  on the Alibaba Tianchi dataset. The data we used contains 9k LRAs with 68k replicas, 641 LRA pairs with conflicts, and the nodes with 32 CPU cores and 64 GB memory. And we compare the time consumption and the number of nodes used in each algorithm. For further evaluation in the next chapter, there are two sections of the result of this empirical investigation as follows.

### The time consumption
In this section, we recorded the execution time of each algorithm module in the program when placing all LRA replicas in multiple nodes. The results are as table 4.1.
<div align=center><img width="741" alt="截屏2023-01-12 15 21 27" src="https://user-images.githubusercontent.com/41847989/212106824-a04cc1fa-20e2-40ff-9214-519a1d6fc3a7.png"></div>

### The number of nodes used
In this section, we recorded the number of nodes used in each algorithm when placing all 
LRA replicas in multiple nodes. The results are as table 4.2.
<div align=center><img width="739" alt="截屏2023-01-12 15 22 38" src="https://user-images.githubusercontent.com/41847989/212107290-d5f551b6-1237-45e0-a1ae-05b83fcb5603.png"></div>
<div align=center><img width="736" alt="截屏2023-01-12 15 22 51" src="https://user-images.githubusercontent.com/41847989/212107390-608fe1cf-e23a-4f88-87c5-3ba81a4ba53b.png"></div>

According to tables 4.1 and 4.2, item-centric heuristics (FF and its variants FFDAvgSum and FFDExpSum) consume much less time than other algorithms, and use fewer nodes to allocate all replicas of all LRAs. However, bin-centric heuristics (FFDDotProduct and FFDL2-Norm) consume a much longer time while using more nodes.

Specifically, in item-centric heuristics, FF consumed the least amount of time with 503 msand used the least number of nodes with only 11016 nodes among its variants, while FFDL2-Norm consumed the most time and used the most nodes, with 191263 ms and 12548 nodes.

## Validation of Results
To compare the effectiveness of the algorithms, we use two performance metrics: the deviation from the lower bound of the optimal solution and the time consumed. Both can be recorded while the algorithm is running. In particular, the lower bound of the optimal solution can be expressed as follows by calculating a strong lower bound on the bin packing problem mentioned by Vazirani(Vijay V, 2001)：
<div align=center><img width="744" alt="截屏2023-01-12 15 24 38" src="https://user-images.githubusercontent.com/41847989/212107864-05d9881c-0771-4f2d-8ca2-d275ad979e23.png"></div>

Then we calculate the deviation from  of each algorithm. Finally, we use bar charts and a scatter chart to visualize the results discussed in chapter 4.

<div align=center><img width="671" alt="截屏2023-01-12 15 25 12" src="https://user-images.githubusercontent.com/41847989/212107979-f2c50ae9-75a4-4e57-b83f-8f14fc564be0.png"></div>

<div align=center><img width="744" alt="截屏2023-01-12 15 25 35" src="https://user-images.githubusercontent.com/41847989/212108084-dfc065f0-23ac-4c90-ad2e-2c5598dc1202.png"></div>

In general, in this experiment, the item-centric heuristics (FF and its variants FFDAvgSum and FFDExpSum) are better-performing than the bin-centric heuristics (FFDDotProduct and FFDL2-Norm). Firstly, FF performed the best consuming 503 ms with a 9.63% deviation.And the other two item-centric heuristics(FFDAvgSum and FFDExpSum) performed similarlywithin a second, and find a valid placing with an 11% deviation on average. However, in the bin-centric heuristics, FFDDotProduct consumed 10 s with a 24.4% deviation, and FFDL2-Norm performed the worst consuming 20 s with a 24.8% deviation.

## Conclusions and Future Work

### Conclusions
The aim of the project is to minimise the number of nodes used when allocating all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other and each node does not exceed the capacity in all dimensions in a specific dataset. 

It is the $(VB{PC}_d)$ with specific requirements [R1]-[R4]. And it is NP-hard for every $d$. Inspiredby Garefalakis et al. (2018), Muritiba et al. (2009) and Clement et al. (2022), we formulate it as a simplified version of the ILP introduced by Clement et al. (2022). We reviewed previous research on similar problems of scheduling LRAs and found out that they have covered [P1]-[P3] with [R1]-[R3], but each study focuses on different problems and fulfils different requirements. This report is intended to find an algorithm which should satisfy [R1]-[R4] to solve [P3]. Then we chose item-centric (FFD and its variants FFDAvgSum and FFDExpSum) and bin-centric heuristics (FFDDotProduct and FFDL2-Norm) as the methods to solve this ILP. In addition, we generate and analyse the data from Alibaba's open source cluster trace and implement all the algorithms provided by Clement. Finally, we evaluate the algorithms by the time they consume in the experiment and the deviation between them and the lower bound of optimal solutions. The result of this investigation shows that item-centric heuristic algorithms perform better than bin-centric heuristics in scheduling replicas of LRAs on the Alibaba dataset.

### Future Work
This report is limited by the dataset and the complexity of the problem. The original data only contains 9k LRAs with 68k replicas and 641 LRA pairs with conflicts. Due to the small proportion of LRAs with conflicts, the advantages of the variant algorithm are difficult to be shown. Furthermore, only this one data set was used for this experiment, so there is a lack of controls for the test results. Further work needs to be done to find a better algorithm to solve the LRA scheduling problem with more requirements.

The first step is to expand the original dataset. Not only does the number of LRAs need to be expanded, but also the number of LRAs with conflicts should be increased.

Then the requirements should be expanded to include the resource requirements of LRAs 
over time, affinity constraints between LRAs, etc.

Finally, the algorithm library should be expanded and comparisons should be made with 
previous studies.
