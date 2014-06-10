package ot;

public class Matrix {

	int[][] data;
	int row;
	int col;
	public Matrix(int row, int col) {
		data = new int[row][col];
		this.row = row;
		this.col = col;
	}
	
	static public void transpose(Matrix a, Matrix b){
		b = new Matrix(a.col, a.row);
		for(int i = 0; i < b.row; ++i)
			for(int j = 0; j < b.col; ++j)
				b.data[i][j] = a.data[j][i];
	}
	
	static public Matrix COtranspose(Matrix a)
	{
		Matrix b = new Matrix(a.col, a.row);
		COtranspose(a, b, 0, 0, a.row, a.col);
		return b;
	}
	
	static public void COtranspose(Matrix a, Matrix b, int startx, int starty, int endx, int endy){
		System.out.print(startx+" "+starty+" "+endx+" "+endy);

		if(endy - starty == 1 && endx - startx == 1)
		{
			b.data[starty][startx] = a.data[startx][starty];
			return;
		}
		else if (endy-starty < endx-startx) {

			int midx = (startx + endx)/2;
			COtranspose(a, b, startx, starty, midx, endy);
			COtranspose(a, b, midx, starty, endx, endy);
		}
		else{
			int midy = (starty + endy)/2;
			COtranspose(a, b, startx, starty, endx, midy);
			COtranspose(a, b, startx, midy, endx, endy);
		}
	}
	
	public void print(){
		for(int i = 0; i < row; ++i){
			for(int j = 0; j < col; ++j)
				System.out.print(data[i][j]+" ");
			System.out.print("\n");
		}
	}
	
	public  static void main(String[] args){
		int cnt = 0;
		Matrix a = new Matrix(3, 3);
		for(int i = 0; i < a.row; ++i)
			for(int j = 0; j < a.col; ++j){
				a.data[i][j] = cnt;
				cnt = (cnt+1)%10;
			}
		
		a.print();
		
		Matrix b = COtranspose(a);
		b.print();
	}

}