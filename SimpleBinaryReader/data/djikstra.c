#include <stdio.h>

#ifndef MAX
#define MAX 20
#endif
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
{
#if MAX == 3
{0,10,25},
{10,0,24},
{25,24,0},
#elif MAX == 4
{0,14,30,16},
{14,0,14,10},
{30,14,0,15},
{16,10,15,0},
#elif MAX == 5
{0,11,10,9,35},
{11,0,17,19,11},
{10,17,0,7,29},
{9,19,7,0,3},
{35,11,29,3,0},
#elif MAX == 6
{0,30,16,3,20,26},
{30,0,18,37,37,14},
{16,18,0,19,38,9},
{3,37,19,0,4,6},
{20,37,38,4,0,35},
{26,14,9,6,35,0},
#elif MAX == 7
{0,15,40,1,22,22,2},
{15,0,7,10,3,37,24},
{40,7,0,24,29,9,19},
{1,10,24,0,28,17,26},
{22,3,29,28,0,33,15},
{22,37,9,17,33,0,24},
{2,24,19,26,15,24,0},
#elif MAX == 8
{0,9,40,4,15,11,1,20},
{9,0,16,11,2,32,4,18},
{40,16,0,39,15,3,14,7},
{4,11,39,0,31,1,29,8},
{15,2,15,31,0,36,24,8},
{11,32,3,1,36,0,38,2},
{1,4,14,29,24,38,0,24},
{20,18,7,8,8,2,24,0},
#elif MAX == 9
{0,37,33,20,19,5,26,25,26},
{37,0,40,15,18,37,7,12,4},
{33,40,0,9,40,15,22,14,5},
{20,15,9,0,9,7,27,27,14},
{19,18,40,9,0,36,6,3,18},
{5,37,15,7,36,0,16,18,40},
{26,7,22,27,6,16,0,2,34},
{25,12,14,27,3,18,2,0,13},
{26,4,5,14,18,40,34,13,0},
#elif MAX == 10
{0,23,1,5,11,21,40,2,25,18},
{23,0,31,26,15,20,16,24,31,9},
{1,31,0,17,15,29,17,29,30,35},
{5,26,17,0,37,19,12,25,18,40},
{11,15,15,37,0,6,25,30,29,8},
{21,20,29,19,6,0,17,19,16,15},
{40,16,17,12,25,17,0,5,4,5},
{2,24,29,25,30,19,5,0,33,17},
{25,31,30,18,29,16,4,33,0,1},
{18,9,35,40,8,15,5,17,1,0},
#elif MAX == 11
{0,34,4,7,7,25,10,37,25,8,19},
{34,0,10,35,37,4,4,4,1,12,14},
{4,10,0,7,3,6,7,22,36,28,40},
{7,35,7,0,23,19,34,18,11,22,30},
{7,37,3,23,0,35,38,17,8,18,36},
{25,4,6,19,35,0,10,29,8,15,29},
{10,4,7,34,38,10,0,11,11,7,13},
{37,4,22,18,17,29,11,0,14,7,27},
{25,1,36,11,8,8,11,14,0,2,37},
{8,12,28,22,18,15,7,7,2,0,36},
{19,14,40,30,36,29,13,27,37,36,0},
#elif MAX == 12
{0,18,26,35,12,4,1,10,17,24,18,23},
{18,0,37,28,15,15,14,14,19,4,25,6},
{26,37,0,13,25,39,3,37,5,22,16,12},
{35,28,13,0,26,37,27,9,14,12,6,38},
{12,15,25,26,0,26,37,20,28,21,15,36},
{4,15,39,37,26,0,31,22,22,4,30,3},
{1,14,3,27,37,31,0,4,39,15,15,9},
{10,14,37,9,20,22,4,0,6,10,23,31},
{17,19,5,14,28,22,39,6,0,26,4,6},
{24,4,22,12,21,4,15,10,26,0,21,7},
{18,25,16,6,15,30,15,23,4,21,0,19},
{23,6,12,38,36,3,9,31,6,7,19,0},
#elif MAX == 13
{0,35,28,8,27,6,20,1,34,21,38,36,21},
{35,0,14,14,35,14,14,3,14,3,35,19,30},
{28,14,0,22,19,26,38,36,5,34,11,1,10},
{8,14,22,0,25,11,40,22,16,22,8,22,37},
{27,35,19,25,0,30,3,4,29,29,8,31,5},
{6,14,26,11,30,0,12,25,34,15,13,34,30},
{20,14,38,40,3,12,0,7,29,39,17,23,27},
{1,3,36,22,4,25,7,0,10,18,23,38,5},
{34,14,5,16,29,34,29,10,0,37,8,21,19},
{21,3,34,22,29,15,39,18,37,0,28,17,17},
{38,35,11,8,8,13,17,23,8,28,0,2,30},
{36,19,1,22,31,34,23,38,21,17,2,0,17},
{21,30,10,37,5,30,27,5,19,17,30,17,0},
#elif MAX == 14
{0,14,34,26,9,9,29,34,24,24,39,35,13,25},
{14,0,37,6,8,35,32,25,11,30,8,22,39,32},
{34,37,0,30,38,33,31,6,40,6,29,1,15,11},
{26,6,30,0,36,5,16,29,36,36,22,33,8,35},
{9,8,38,36,0,34,33,24,40,27,27,20,39,6},
{9,35,33,5,34,0,14,17,27,20,12,37,36,5},
{29,32,31,16,33,14,0,39,19,34,28,13,20,3},
{34,25,6,29,24,17,39,0,10,34,22,36,38,40},
{24,11,40,36,40,27,19,10,0,18,30,37,12,15},
{24,30,6,36,27,20,34,34,18,0,22,25,7,17},
{39,8,29,22,27,12,28,22,30,22,0,7,38,17},
{35,22,1,33,20,37,13,36,37,25,7,0,21,5},
{13,39,15,8,39,36,20,38,12,7,38,21,0,30},
{25,32,11,35,6,5,3,40,15,17,17,5,30,0},
#elif MAX == 15
{0,26,23,25,14,29,4,24,12,5,32,40,39,14,24},
{26,0,6,7,28,36,11,11,19,15,12,16,20,33,5},
{23,6,0,30,10,38,14,13,35,7,22,38,40,3,1},
{25,7,30,0,7,8,3,10,34,17,30,16,25,40,16},
{14,28,10,7,0,30,27,17,18,27,6,28,31,4,32},
{29,36,38,8,30,0,37,40,34,12,13,39,32,34,18},
{4,11,14,3,27,37,0,27,13,14,1,11,37,20,27},
{24,11,13,10,17,40,27,0,29,11,8,33,40,26,37},
{12,19,35,34,18,34,13,29,0,17,13,8,32,6,29},
{5,15,7,17,27,12,14,11,17,0,1,3,20,3,20},
{32,12,22,30,6,13,1,8,13,1,0,16,4,20,7},
{40,16,38,16,28,39,11,33,8,3,16,0,14,27,26},
{39,20,40,25,31,32,37,40,32,20,4,14,0,34,33},
{14,33,3,40,4,34,20,26,6,3,20,27,34,0,22},
{24,5,1,16,32,18,27,37,29,20,7,26,33,22,0},
#elif MAX == 16
{0,26,5,20,40,5,21,20,2,8,20,25,37,6,17,38},
{26,0,25,10,30,27,28,18,5,27,21,33,39,30,39,35},
{5,25,0,4,39,9,33,3,37,31,1,22,3,25,3,12},
{20,10,4,0,39,38,3,35,31,23,2,14,5,18,24,22},
{40,30,39,39,0,26,27,15,12,28,35,11,39,12,24,15},
{5,27,9,38,26,0,36,34,1,18,31,10,7,15,12,7},
{21,28,33,3,27,36,0,29,11,31,19,39,28,20,19,40},
{20,18,3,35,15,34,29,0,20,37,35,38,32,19,13,20},
{2,5,37,31,12,1,11,20,0,8,32,28,9,21,23,35},
{8,27,31,23,28,18,31,37,8,0,2,13,4,35,21,9},
{20,21,1,2,35,31,19,35,32,2,0,37,21,38,24,28},
{25,33,22,14,11,10,39,38,28,13,37,0,34,36,15,23},
{37,39,3,5,39,7,28,32,9,4,21,34,0,10,10,11},
{6,30,25,18,12,15,20,19,21,35,38,36,10,0,25,29},
{17,39,3,24,24,12,19,13,23,21,24,15,10,25,0,31},
{38,35,12,22,15,7,40,20,35,9,28,23,11,29,31,0},
#elif MAX == 17
{0,22,37,18,11,21,9,34,16,26,10,8,19,28,16,17,18},
{22,0,14,10,10,4,10,1,28,16,34,3,36,22,32,1,8},
{37,14,0,8,12,22,3,36,38,16,15,30,16,18,27,35,11},
{18,10,8,0,33,26,5,33,18,19,9,32,23,17,15,26,17},
{11,10,12,33,0,30,23,7,28,5,18,36,7,14,40,5,4},
{21,4,22,26,30,0,24,32,26,9,31,23,21,36,10,31,10},
{9,10,3,5,23,24,0,3,4,32,23,9,10,9,40,25,17},
{34,1,36,33,7,32,3,0,27,38,2,30,23,39,22,3,23},
{16,28,38,18,28,26,4,27,0,26,8,7,24,23,14,10,11},
{26,16,16,19,5,9,32,38,26,0,21,1,30,8,35,23,16},
{10,34,15,9,18,31,23,2,8,21,0,16,17,18,5,15,19},
{8,3,30,32,36,23,9,30,7,1,16,0,25,8,16,23,33},
{19,36,16,23,7,21,10,23,24,30,17,25,0,11,29,37,36},
{28,22,18,17,14,36,9,39,23,8,18,8,11,0,16,10,1},
{16,32,27,15,40,10,40,22,14,35,5,16,29,16,0,23,24},
{17,1,35,26,5,31,25,3,10,23,15,23,37,10,23,0,4},
{18,8,11,17,4,10,17,23,11,16,19,33,36,1,24,4,0},
#elif MAX == 18
{0,3,10,16,30,24,20,14,31,11,40,23,21,30,35,5,6,38},
{3,0,34,13,30,27,11,39,3,7,18,24,13,40,10,36,7,19},
{10,34,0,33,29,8,23,20,22,36,32,37,32,17,2,24,23,2},
{16,13,33,0,2,22,26,35,3,9,25,5,24,30,21,25,39,2},
{30,30,29,2,0,39,36,29,31,14,15,36,8,37,6,9,29,28},
{24,27,8,22,39,0,28,7,35,16,23,20,26,8,4,20,31,14},
{20,11,23,26,36,28,0,13,11,19,9,31,14,15,35,5,5,32},
{14,39,20,35,29,7,13,0,29,2,5,14,23,15,2,14,1,32},
{31,3,22,3,31,35,11,29,0,19,25,33,19,24,36,5,9,13},
{11,7,36,9,14,16,19,2,19,0,36,27,5,11,22,18,2,10},
{40,18,32,25,15,23,9,5,25,36,0,22,20,31,25,33,39,5},
{23,24,37,5,36,20,31,14,33,27,22,0,25,26,31,1,12,31},
{21,13,32,24,8,26,14,23,19,5,20,25,0,12,39,17,9,17},
{30,40,17,30,37,8,15,15,24,11,31,26,12,0,29,38,3,21},
{35,10,2,21,6,4,35,2,36,22,25,31,39,29,0,20,18,23},
{5,36,24,25,9,20,5,14,5,18,33,1,17,38,20,0,36,1},
{6,7,23,39,29,31,5,1,9,2,39,12,9,3,18,36,0,14},
{38,19,2,2,28,14,32,32,13,10,5,31,17,21,23,1,14,0},
#elif MAX == 19
{0,2,28,5,28,17,2,26,9,36,16,33,4,23,33,21,38,6,14},
{2,0,40,22,20,34,27,1,2,22,12,21,14,5,35,13,34,37,38},
{28,40,0,18,26,4,39,37,30,18,2,15,17,36,1,13,25,25,26},
{5,22,18,0,11,22,1,11,38,17,22,10,1,24,11,11,19,10,6},
{28,20,26,11,0,15,12,8,24,38,37,9,11,11,9,28,37,38,35},
{17,34,4,22,15,0,4,2,37,12,18,5,6,13,2,22,8,17,34},
{2,27,39,1,12,4,0,3,8,3,2,5,3,16,1,28,10,39,3},
{26,1,37,11,8,2,3,0,5,20,12,39,32,27,35,28,17,8,28},
{9,2,30,38,24,37,8,5,0,1,8,3,9,14,39,23,14,20,16},
{36,22,18,17,38,12,3,20,1,0,14,10,17,20,39,30,19,13,28},
{16,12,2,22,37,18,2,12,8,14,0,25,20,33,6,14,2,35,26},
{33,21,15,10,9,5,5,39,3,10,25,0,10,24,29,26,36,40,17},
{4,14,17,1,11,6,3,32,9,17,20,10,0,19,30,33,36,18,16},
{23,5,36,24,11,13,16,27,14,20,33,24,19,0,38,12,12,37,3},
{33,35,1,11,9,2,1,35,39,39,6,29,30,38,0,14,18,15,11},
{21,13,13,11,28,22,28,28,23,30,14,26,33,12,14,0,1,19,20},
{38,34,25,19,37,8,10,17,14,19,2,36,36,12,18,1,0,27,18},
{6,37,25,10,38,17,39,8,20,13,35,40,18,37,15,19,27,0,26},
{14,38,26,6,35,34,3,28,16,28,26,17,16,3,11,20,18,26,0},
else
	WTF
#endif
};

 int i;
  int ret = sfe_main(cost, 3, 4);
  //for(i=0;i<MAX;i++)
  //printf("%d\n",distance[i]);
  printf("\nreturned value: %d", ret); printf("\n");
  return 0;
}
