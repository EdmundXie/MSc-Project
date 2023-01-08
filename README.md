# MSc-Project
## Overview
In this project, we address the problem of scheduling long-running applications (LRAs) in a cluster in order to maximize efficiency and stability. We consider a simplified version of the Integer Linear Programming (ILP) problem and use both item-centric and bin-centric heuristic algorithms to solve it. We also evaluate the performance of these algorithms using data from Alibaba's open source cluster trace and compare their results to the lower bound of optimal solutions. Our ultimate goal is to identify the most suitable algorithm for this specific dataset

## Project Aim
The project aims to minimise the number of nodes used when allocating all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other and each node does not exceed the capacity in all dimensions in a specific dataset. 
### Bin packing problem(BP) and its variants 
BP are complex discrete combinatorial optimisation problems. The classical bin packing problem requires that a certain number of items are placed in several bins with equal capacity, the sum of the sizes of the items in each bin does not exceed the capacity of the bin and that the number of bins used is minimised (Vijay V, 2001). 
On top of this, there is also the vector bin packing problem with conflicts (VB〖PC〗_d) due to conflicts between items (two items cannot be placed in the same bin) and the multidimensional requirement (d≥2)  . (e.g.size, weight of the items)
### Scheduling LRAs in shared clusters 
The LRA scheduling problem is similar to VB〖PC〗_d in that the aim is to deploy all the LRAs with a minimum number of nodes without exceeding the constraints. Still, it has some specific requirements [R1]-[R4] as follows:
[R1] multi-dimensional resource requirements
In running LRAs in a node, shared resources such as CPU cache and memory of the nodes need to be occupied, and other LRAs cannot use the occupied resources. This means that, in analogy to the items in the bin packing problem, LRAs cannot be placed in nodes in parallel in the same dimension.
[R2] LRA conflict constraints
Conflict usually refers to the relatedness of two LRAs, i.e. they can not be placed in the same node.
LRAs with conflict should be placed in different nodes to avoid interfering. For example, LRAs that require a fast response in the same node can increase network latency and affect performance.
[R3] Optimisation requirements in large clusters
Clusters are often large in scale, and as the volume of data increases, some algorithms (described in detail later) suffer from performance degradation. Finding a better algorithm that meets the actual requirements is particularly important.
[R4] Replicas of LRAs 
A replica is an object that can be dispatched to a node and is part of an LRA. Just as an application can have multiple processes on a computer, an LRA has multiple replicas that can be deployed in different nodes. Therefore, when scheduling LRAs, it must be ensured that all replicas for all LRAs are deployed on the nodes.
Thus, to meet the above requirements, in large-scale cluster management, the following problems are usually considered to optimise cluster utilisation and stability (Mondal et al., 2021) [P1]-[P3]：
[P1] Minimise inter-application conflicts
Conflicts exist between applications (e.g. fast response conflicts). Minimising conflicts can reduce non-essential resource consumption and thus improve cluster performance.
[P2] Maximising cluster utilisation
When the cluster is often at a low peak, i.e. when the workload is much less than the amount of resources Workload should be concentrated on a few nodes to prevent wasted resources.
[P3] Minimise the number of nodes used
When the workload is constant and less than the overall cluster resources, the number of nodes should be minimised to reduce resource costs. 
The scheduling problem for LRAs has many variants from these fundamental three aspects, which is difficult because of conflicts between the aspects.

## Problem Statement
We consider the scheduling problem of LRAs as [P3] considering [R1]-[R3] requirements.
The problem is a d-dimensional vector bin packing problem with conflicts (VB〖PC〗_d), it is NP-hard for every d (Panigrahy et al., 2011). This means that when d=1, it is widely believed that there is no algorithm for finding the optimal solution in polynomial time. 
The scheduling of LRAs in nodes usually considers the nodes’ capacity, such as CPU and memory capacity, in analogy to the size of the bin in different dimensions. However, they must be accumulated in each dimension when placed. This means an LRA will occupy a separate CPU and memory buffer at runtime, not shared with other LRAs. There are also conflicts between LRAs that mutually exclusive LRAs cannot be placed in the same container.
Garefalakis et al. (2018), Muritiba et al. (2009) and Clement et al. (2022) used the Integer Liner programming (ILP) model as a formulation for the bin packing problem with conflicts and achieved good results. Inspired by them, we formulate it as a simplified version of the ILP introduced by Clement et al. (2022). Specifically, it deals with [P3] with satisfying all constraints [R1]-[R4].
Notations used in scheduling problems of LRAs are as follows:
L denotes the set of LRAs,
I_l denotes the set of replicas that belongs to an LRA l, l∈L,
N denotes the set of nodes,
F denotes the set of pairs (l_1,l_2) of LRAs that have conflicts
C_h denotes the capacity of each node in dimension h, 1≤h≤d
¯C_hn denotes the remaining capacity of the activated node n in dimension h, y_n=1, 1≤h≤d
s_lh denotes the size of the LRA l in dimension h, l∈L, 1≤h≤d
y_n denotes whether node n is used, equals to 1 if it is used, 0 otherwise, n∈N, y_n∈{0,1}
x_(i_l n) denotes whether replica i_l of the LRA l is assigned to node n, equals to 1 if it is assigned, 0 otherwise, i_l∈I_l, n∈N, x_(i_l n)∈{0,1}
z_ln denotes whether one replica of LRA l is assigned to node n, equals to 1 if it is assigned, 0 otherwise, l∈L, n∈N, x_(i_l n)∈{0,1}
Specifically, the problem is to allocate all LRA replicas among multiple nodes such that the LRAs in all nodes do not conflict with each other, each node does not exceed the capacity in d dimensions, and ultimately the number of nodes is minimised. It is formulated to ILP as follows: 
min    ∑_(n∈N)▒y_n                                                                                                                       (a)
s.t.     ∑_(n∈N)▒〖x_(i_l n)=1〗                                                               〖i_l∈I〗_l,l∈L,                            (b)
          ∑_(l∈L)▒〖(s〗_lh ∙∑_(i∈I_l)▒x_(i_l n) )≤C_h∙y_n                                       n∈N,1≤h≤d,                    (c)
          ∑_(i∈I_l)▒x_(i_l n) ≤〖(min〗⁡{min┬(1≤h≤d)⁡{⌊C_h/s_lh ⌋},|I_l |}∙z_ln)                          n∈N, l∈L,                            (d)
          ∑_(i∈I_l)▒x_(i_l n) ≥z_ln                                                            n∈N, l∈L,                             (e)
          ∑_(i∈I_(l_1 ))▒x_(i_(l_1 ) n) ≤〖(min〗⁡{min┬(1≤h≤d)⁡{⌊C_h/s_(l_1 h) ⌋},|I_(l_1 ) |}∙(1-z_(l_2 n)))     n∈N, (l_1,l_2)∈F,                   (f)
In this ILP, the aim (a) is to minimize the number of nodes used. (b) represents all replicas of all LRAs allocated to nodes. (c) means resources in d dimensions used in nodes not outweighed the capacity of each node. Furthermore, in (d) min⁡{min┬(1≤h≤d)⁡{⌊C_h/s_lh ⌋},|I_l |} is the maximum number of LRA replicas can be placed in a node ignoring conflict restrictions. So (d) and (e) ensure that the resource size required for a replica in a dimension cannot exceed the capacity of that dimension. In addition, the number of placements exceeds the number of LRA replicas that cannot be provided, and at least one replica of an LRA is placed on a node. In the end, (f) represents conflict constraints between LRAs.
 
Figure 2.1.  Project problem statement —— Since the problem is a vector bin packing problem with conflicts we formulate it as an ILP in order to solve VB〖PC〗_d
![image](https://user-images.githubusercontent.com/41847989/211219573-9bd8bd4c-7338-4771-9c1a-477da6eac879.png)



## Implementation of project
All data are from Alibaba traces which are sampled from one of their production clusters, avaliable on https://tianchi.aliyun.com/dataset/dataDetail?dataId=6287.
The experiment has three stpes as in following figure.
<div align=center><img src="https://user-images.githubusercontent.com/41847989/186922971-a8bedcee-0247-4727-8a82-33b7152a8d0e.png"></div>
<!-- <img width="250" alt="image" src="https://user-images.githubusercontent.com/41847989/186922971-a8bedcee-0247-4727-8a82-33b7152a8d0e.png"> -->

## Running experiments
### Data Formulation and Analysis
The original data is in `src/data/input`. Formulate analyze the data using `Analysis_Generate_data.ipynb` which Generates `processed_data` in that file.
### Alorithms implementation
Run `src/main/java/binpacklib/Main`. In the main function select the location of the input and output files and choose the algorithm to be deployed.
### Alorithms performance visualization
The results are in `src/data/results`. Running `Result_Analysis.ipynb` to analyse the results.
