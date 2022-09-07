# MSc-Project
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
