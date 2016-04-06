int main()
{
	int A, B, C, D;
	int _ret_val_0;
	bool completed[2];
	A=10;
	B=15;
	completed[1]=false;
	completed[0]=false;
	while (completed[1]==false)
	{
		bool check[1];
		while (completed[0]==false)
		{
			bool check[2];
			do
			{
				C=Compute(A, B);
				check[0]=Checker(A);
				if (check[0]==false)
				{
					if (Load(A)==false)
					{
						throw unrecoverable;
					}
				}
				check[1]=Checker(B);
				if (check[1]==false)
				{
					if (Load(B)==false)
					{
						throw unrecoverable;
					}
				}
			}while((check[0]==false)||(check[1]==false));
			
			if ((check[0]==true)&&(check[1]==true))
			{
				completed[0]=true;
			}
		}
		do
		{
			Store(C);
			check[0]=Checker(C);
			if (check[0]==false)
			{
				if (Recovery(C)==false)
				{
					throw unrecoverable;
				}
			}
		}while(check[0]==false);
		
		if (check[0]==true)
		{
			completed[1]=true;
		}
	}
	D=7;
	while (true)
	{
		D=13;
		A=12;
	}
	_ret_val_0=0;
	return _ret_val_0;
}
