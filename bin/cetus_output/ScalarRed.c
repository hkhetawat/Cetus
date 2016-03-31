/*
Scalar, Additive Reduction

  The loop contains a scalar, additive reduction operation.


*/
int main()
{
	float a[10000], sum;
	int i, n;
	int _ret_val_0;
	n=10000;
	#pragma cetus private(i) 
	#pragma loop name main#0 
	#pragma cetus reduction(+: sum) 
	#pragma cetus parallel 
	#pragma omp parallel for private(i) reduction(+: sum)
	for (i=1; i<n; i ++ )
	{
		sum=(sum+a[i]);
	}
	_ret_val_0=0;
	return _ret_val_0;
}
