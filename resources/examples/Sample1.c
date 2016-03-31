int main()
{
int A, B, C;
A=10;
B=15;
#pragma protect Check(Checker(A), Checker(B))  Recover(Load(A),Load(B)) 
C = Compute(A, B);
A = 5;
return 0;
}
