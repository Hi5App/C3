#include <iostream>
#include "Score.h"
using namespace std;

int main() {
	Score s;
	cout << s.getLastLoginDay() << endl;
	cout << s.getLastLoginYear() << endl;
	return 0;
}