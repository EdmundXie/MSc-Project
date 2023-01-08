# MSc-Project
## Overview
In this project, we address the problem of scheduling long-running applications (LRAs) in a cluster in order to maximize efficiency and stability. We consider a simplified version of the Integer Linear Programming (ILP) problem and use both item-centric and bin-centric heuristic algorithms to solve it. We also evaluate the performance of these algorithms using data from Alibaba's open source cluster trace and compare their results to the lower bound of optimal solutions. Our ultimate goal is to identify the most suitable algorithm for this specific dataset

## Implementation of experiments
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
