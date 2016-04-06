int Checker(int A);
int Load(int A);
int main()
{
	int A, B, C, D, E, F;
	int _ret_val_0;
	bool completed[3];
	A=10;
	B=15;
	completed[0]=false;
	completed[1]=false;
	completed[2]=false;
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
					if ((Load(A)==false)&&(Recovery(A)==false))
					{
						throw unrecoverable;
					}
				}
				check[1]=Checker(B);
				if (check[1]==false)
				{
					if ((Load(B)==false)&&(Recovery(B)==false))
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
		D=7;
		Compute(A, B);
		do
		{
			E=Compute(C, D);
			check[0]=Checker(C);
			if (check[0]==false)
			{
				if ((Recovery(C)==false)&&(Recovery2(C)==false))
				{
					completed[0]=false;
				}
			}
		}while(check[0]==false);
		
		if (check[0]==true)
		{
			completed[1]=true;
		}
	}
	Store(C);
	D=12;
	while (completed[2]==false)
	{
		bool check[1];
		do
		{
			Store(F);
			check[0]=Checker(F);
			if (check[0]==false)
			{
				if (Recovery(F)==false)
				{
					throw unrecoverable;
				}
			}
		}while(check[0]==false);
		
		if (check[0]==true)
		{
			completed[2]=true;
		}
	}
	D=8;
	_ret_val_0=0;
	return _ret_val_0;
}

int Checker(int A)
{
	int _ret_val_0;
	_ret_val_0=0;
	return _ret_val_0;
}

int Load(int A)
{
	int _ret_val_0;
	_ret_val_0=0;
	return _ret_val_0;
}
