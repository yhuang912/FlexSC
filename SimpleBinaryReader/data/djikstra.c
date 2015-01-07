#include <stdio.h>

#define MAX 3
#define INFINITE 998

int allselected(int *selected)
{
  int i;

  for(i=0;i<MAX;i++){
    if(selected[i]==0)
      return 0;
  }
  return 1;
}

int sfe_main(int cost[][MAX],int *preced,int *distance, int startID, int endID)
{
  int selected[MAX]={0};
  int current=0,i,k,dc,smalldist,newdist;
  for(i=0;i<MAX;i++)
    distance[i]=INFINITE;
  selected[startID]=1;
  distance[startID]=0;
  current=startID;
  while(!allselected(selected))
  {
    smalldist=INFINITE;
    dc=distance[current];
    for(i=0;i<MAX;i++)
    {
      if(selected[i]==0)
      {                                             
        newdist=dc+cost[current][i];
        if(newdist<distance[i])
        {
          distance[i]=newdist;
          preced[i]=current;
        }
        if(distance[i]<smalldist)
        {
          smalldist=distance[i];
          k=i;
        }
      }
    }
    current=k;
    selected[current]=1;
   }
  return distance[endID];
}

int main()
{
  /* int cost[MAX][MAX]= 
     {{INFINITE,2,4,7,INFINITE,5,INFINITE},
      {2,INFINITE,INFINITE,6,3,INFINITE,8},
      {4,INFINITE,INFINITE,INFINITE,INFINITE,6,INFINITE},
      {7,6,INFINITE,INFINITE,INFINITE,1,6},
      {INFINITE,3,INFINITE,INFINITE,INFINITE,INFINITE,7},
      {5,INFINITE,6,1,INFINITE,INFINITE,6},
      {INFINITE,8,INFINITE,6,7,6,INFINITE}};*/

 int cost[MAX][MAX]= 
   {{INFINITE, 6, 9},
    {5, INFINITE, 2},
    {9, INFINITE, INFINITE}};

  int i,preced[MAX]={0},distance[MAX];
  int ret = sfe_main(cost,preced,distance, 1, 2);
  for(i=0;i<MAX;i++)
    printf("%d\n",distance[i]);
  printf("\nreturned value: %d", ret); printf("\n");
  return 0;
}
