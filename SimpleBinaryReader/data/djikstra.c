#include <stdio.h>

#define MAX 10
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

int sfe_main(int cost[][MAX], int startID, int endID)
{
  int selected[MAX]={0};
  int preced[MAX]={0},distance[MAX];
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
  int cost[MAX][MAX]= 
{{0,10,30,38,15,12,39,3,12,15},
{10,0,28,27,20,38,28,13,10,12},
{30,28,0,29,5,22,27,4,21,3},
{38,27,29,0,13,17,29,16,23,1},
{15,20,5,13,0,11,25,33,38,4},
{12,38,22,17,11,0,15,25,17,5},
{39,28,27,29,25,15,0,35,21,23},
{3,13,4,16,33,25,35,0,10,39},
{12,10,21,23,38,17,21,10,0,35},
{15,12,3,1,4,5,23,39,35,0},
};



  
  
 int i;
  int ret = sfe_main(cost, 0, 4);
  //for(i=0;i<MAX;i++)
  //printf("%d\n",distance[i]);
  //printf("\nreturned value: %d", ret); printf("\n");
  return 0;
}
