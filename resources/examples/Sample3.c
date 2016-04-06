int Checker(int A);
int Load(int A);

int main()
{
	int A, B, C, D, E, F;
	A=10;
	B=15;
  #pragma protect Check(Checker(A), Checker(B))  Recover(Load(A), Recovery(A) ,Load(B), Recovery(B)) 
	C = Compute(A, B);
	D = 7;
	Compute(A, B);
#pragma protect Check(Checker(C)) Recover(Recovery(C), Recovery2(C)) 
	E = Compute(C, D);
	Store(C);
	D = 12;
	#pragma protect Check(Checker(F)) Recover(Recovery(F))
	Store(F);
	D = 8;
   return 0;
}

int Checker(int A)
{
	return 0;
}

int Load(int A)
{
	return 0;
}
