#include <stdio.h>

#define MAX 15
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
{{0,39,10,12,20,33,14,35,18,39},
{39,0,35,35,32,3,7,20,32,34},
{10,35,0,28,26,26,25,20,37,20},
{12,35,28,0,1,13,27,36,15,14},
{20,32,26,1,0,3,5,9,1,29},
{33,3,26,13,3,0,5,26,34,1},
{14,7,25,27,5,5,0,2,34,1},
{35,20,20,36,9,26,2,0,6,28},
{18,32,37,15,1,34,34,6,0,18},
{39,34,20,14,29,1,1,28,18,0},
};



  
  
 int i;
  int ret = sfe_main(cost, 0, 4);
  //for(i=0;i<MAX;i++)
  //printf("%d\n",distance[i]);
  //printf("\nreturned value: %d", ret); printf("\n");
  return 0;
}
