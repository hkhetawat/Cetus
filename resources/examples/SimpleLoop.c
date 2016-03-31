/*  Very Simple Parallelizable Loop Example
*/

int main(){

  float a[10000], b[10000];
  int i;
  
  for (i=1; i<10000; i++) {
    a[i]= b[i];
  }
	
   return 0;
}
