#include <stdio.h>
int sfe_main(int arr1[], int arr2[], int m, int n)
{
  int i = 0, j = 0, total=0;
  while(i < m && j < n)
  {
    if(arr1[i] < arr2[j])
      i++;
    else if(arr2[j] < arr1[i])
      j++;
    else /* if arr1[i] == arr2[j] */
    {
      //     printf(" %d ", arr2[j++]);
      i++;
      total++;
    }
  }
  return total;
}
 
/* Driver program to test above function */
int main()
{
  int i;
  int num=20;
  int arr1[] = {4,33,54,57,65,70,75,83,111,113,118,124,129,132,144,155,170,175,187,189};
  int arr2[] = {5,19,21,38,46,60,64,65,72,73,77,78,80,120,144,148,156,175,190,196};

  int total = sfe_main(arr1, arr2, num, num);
  printf("total %d\n", total);
  
  return 0;
}

