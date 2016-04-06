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
	#pragma protect Check(Checker(A),Checker(B),Checker(CD),Checker(EF)) Recover(Load(A),Load(B),Loader(A),Loaded(A),Recover(B),Recovery(CD),Loader(EF)) 
	n=10000;
	ind=123;
	#pragma loop name main#0 
	for (i=1; i<n; i ++ )
	{
		a[123+(2*i)]=b[i];
	}
	ind+=19998;
	ind2=5;
	ind=234;
	#pragma loop name main#1 
	for (i=1; i<n; i ++ )
	{
		a[(5+(235*i))+(i*i)]=b[i];
	}
	ind2+=((-234+(233*n))+(n*n));
	ind+=(-2+(2*n));
	return 0;
}
