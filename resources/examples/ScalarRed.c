/* Scalar, Additive Reduction

  The loop contains a scalar, additive reduction operation.

*/
int main(){

  float a[10000], sum;
  int i, n;
  
  n = 10000;
  for (i=1; i<n; i++) {
    sum = sum + a[i];
  }
	
   return 0;
}
