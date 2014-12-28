int foo(int a) {
	return a;
}
int bar();
int sfe_main(int a, int b) {
	return foo(a)+bar(b);
}
int bar(int a) {
	return a;
}
int main(int argc, char **argv) {
	sfe_main(1,2);
	return 0;
}
