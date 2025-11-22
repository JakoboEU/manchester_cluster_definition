# Describe clusters
Clusters availble along with image vision resources are in `src/main/resources`
This application uses LLM to describe clusters using the following workflow:
* Find all plots in each cluster
* Create summary stats for each cluster based on NDVI, habitat complexity, herbaceous/shrub/small tree/large tree cover
* Pass summary stats for all clusters to LLM, and ask LLM to define names and descriptions for clusters.
* Next, for each cluster, give all image descriptions from image vision to LLM and ask to describe habitat based on those images, and also to select a representation image

This workflow is run twice, one for [birds](./bird-clusters.json), and once for [insects and plants](./insect-plant-clusters.json)
