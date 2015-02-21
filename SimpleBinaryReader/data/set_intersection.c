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
  int num=50;
  int arr1[] = {1,1,2,2,2,3,4,4,4,7,8,9,9,9,10,10,11,11,11,12,12,13,13,14,14,16,17,17,18,18,19,20,21,21,22,22,23,25,26,26,26,27,27,28,31,32,35,36,37,38};
  int arr2[] = {1,2,3,4,7,7,10,14,14,15,15,15,15,16,16,16,17,18,19,19,20,20,21,22,23,23,24,24,26,27,27,28,28,29,30,30,31,31,31,32,34,35,35,35,35,36,38,38,38,40};

  int total = sfe_main(arr1, arr2, num, num);
  //printf("total %d\n", total);
  
  return 0;
}

