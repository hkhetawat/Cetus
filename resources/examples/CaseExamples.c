int main1(){
	int A, B, C;
  #pragma protect Check(Checker(A), Checker(B))  Recover(Load(A),Load(B)) 
	C = Compute(A, B);
	
   return 0;
}


int main2(){
	int A, B, C;
  #pragma protect Check(Checker(A), Checker(B))  Recover(Load(A),Load(B)) 
	C = Compute(A, B);
#pragma protect Check(Checker(C)) Recover(Recovery(C))
	Store(C);
   return 0;
}