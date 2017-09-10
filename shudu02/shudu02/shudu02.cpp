// shudu02.cpp: 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#pragma warning(disable:4996)
#include<stdio.h>
#include<iostream>
#include<set>
#include<stdlib.h>
#include<time.h>
#include<fstream>
#include<cstring>
using namespace std;

int Array[9][9];
int btnArray[9];

int MAX_Time = 200;//产生随机数组的函数的最大次数
int times = 0;//产生随机数组的函数使用次数

void print() {
	for (int i = 0; i < 9; i++) {
		for (int j = 0; j < 9; j++) {
			char num = '0' + Array[i][j];
			putchar(num); putchar(' ');
		}
		puts("");
	}
	puts("");
}

//产生随机1-9数组btnArray
void creatArray() {
	times++;
	for (int i = 0; i<9; i++) {
		btnArray[i] = i + 1;
	}

	//根据需求，首位为（ 2 + 4 ) % 9 + 1 = 7;
	btnArray[0] = 7;
	btnArray[6] = 1;
	for (int i = 0; i<20; i++) {
		int t = rand() % 8 + 1;
		int temp = btnArray[1];
		btnArray[1] = btnArray[t];
		btnArray[t] = temp;
	}

}

//判断某个数字放入后是否正确
bool checkone(int col, int row) {

	/*
	//同行不可重复
	for (int i = 0; i < row; i++) {
	if (Array[col][row] == Array[col][i])return false;
	}
	*/

	//同九宫格不可重复
	for (int i = (col / 3) * 3; i < col; i++) {
		for (int j = (row / 3) * 3; j < (row / 3) * 3 + 3; j++) {
			if (Array[col][row] == Array[i][j])return false;
		}
	}

	//同列不可重复
	for (int i = 0; i < (col / 3) * 3; i++) {
		if (Array[col][row] == Array[i][row])return false;
	}

	return true;

}

//判断当前生成数组可不可以填入某行
bool checkbtnArray(int col) {
	int judge;

	for (int i = 0; i < 9; i++) {
		judge = 0;
		
		for (int j = 0; j < 9; j++) {
			if (btnArray[j] != 0) {
				Array[col][i] = btnArray[j];
				if (checkone(col, i)) {
					judge = 1;
					btnArray[j] = 0;//表示此数字已经使用过 
					break;
				}
			}
		}
		//如果数组数字都不可以填入当前行，说明当前数组不可填入
		if (judge == 0) {
			for (int k = 0; k <= i; k++) {
				Array[col][k] = 0;
			}
			return false;
		}

	}
	return true;
}

void getshudu() {

	times = 0;
	for (int col = 0; col < 9;) {
		//如果是首行，可以直接放入
		if (col == 0) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					Array[i][j] = 0;
				}
			}

			creatArray();
			for (int i = 0; i<9; i++) {
				Array[0][i] = btnArray[i];
			}
			col++;
		}
		else {
			//使用次数超过最大限度，整个数独重新生成
			if (times > MAX_Time) {
				col = 0;
				times = 0;
			}
			else {
				//产生随机数组，判断是否可以放入
				creatArray();
				//如果可以放入，下一行，不可以，继续本行
				if (checkbtnArray(col)) {
					col++;
				}
			}

		}
	}
	print();

}

int main(int argc, char* argv[]) {
	int N = 0;
	
	//判断输入是否合法
	if (argc != 3) {
		cout << "input error" << endl;
	}
	else {
		string s = argv[1];
		if (s != "-c") {
			cout << "input error" << endl;
		}
		else {
			int len = strlen(argv[2]);
			for (int i = 0; i < len; ++i) {
				if (argv[2][i] < '0' || argv[2][i] > '9') {
					cout << "input error" << endl;
					return 0;
				}
				N = N * 10 + argv[2][i] - '0';

			}
			//重定向输出文件
			freopen("./sudoku.txt", "w", stdout);
			for (int i = 0; i < N; i++)
				getshudu();

		}

	}

	return 0;

}
