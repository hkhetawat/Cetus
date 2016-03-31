/*
Examples of Loops with Induction Variables

  The first loop includes a basic, linear induction variable ind.  

  The second loop includes a more generalized induction variable, which uses a
  linear induction variable as the increment.


*/
int main()
{
	float a[10000], b[10000];
	int i, n, ind, ind2;
	printf("HelloWorld!");
	int _ret_val_0;
	n=10000;
	ind=123;
	#pragma cetus private(i) 
	#pragma loop name main#0 
	#pragma cetus parallel 
	#pragma omp parallel for private(i)
	for (i=1; i<n; i ++ )
	{
		a[123+(2*i)]=b[i];
	}
	ind+=19998;
	ind2=5;
	ind=234;
	#pragma cetus private(i) 
	#pragma loop name main#1 
	#pragma cetus parallel 
	#pragma omp parallel for if((10000<(-2L+(3L*n)))) private(i)
	for (i=1; i<n; i ++ )
	{
		a[(5+(235*i))+(i*i)]=b[i];
	}
	ind2+=((-234+(233*n))+(n*n));
	ind+=(-2+(2*n));
	_ret_val_0=0;
	return _ret_val_0;
}
