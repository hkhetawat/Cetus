int main()
{
	int A, B, C, D;
	A=10;
	B=15;
  #pragma protect Check(Checker(A), Checker(B))  Recover(Load(A),Load(B)) Continue
	C = Compute(A, B);
D = 7;
#pragma protect Check(Checker(C)) Recover(Recovery(C)) 
	Store(C);
   return 0;
}
