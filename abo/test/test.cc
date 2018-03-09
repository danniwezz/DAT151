int fun (int i, int j, int k) {
  return ++k;
}

int main () {
  int x = 0;
  printInt(fun(x, 0, 1));
  return x++;
}
