int main()
{
	int A, B, C;
	int _ret_val_0;
	bool completed[1];
	A=10;
	B=15;
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
	_ret_val_0=0;
	return _ret_val_0;
}
